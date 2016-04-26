package nl.syntouch.oracle.adapter.cloud.mongodb.plugin.metadata;

/*
Copyright 2016 SynTouch B.V.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataSource;

import javax.xml.namespace.QName;

import nl.syntouch.oracle.adapter.cloud.mongodb.bson.BSONDataSource;

import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParserException;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudApplicationModel;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.DataType;
import oracle.tip.tools.ide.adapters.cloud.api.model.InvocationStyle;
import oracle.tip.tools.ide.adapters.cloud.api.model.ObjectCategory;
import oracle.tip.tools.ide.adapters.cloud.api.model.OperationResponse;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.CloudApplicationModelImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudDataObjectNodeImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudOperationNodeImpl;

import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.FieldImpl;

import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.OperationResponseImpl;

import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDBMetadataParser implements MetadataParser {
    
    private static final String[] operationNames = { "insert", "find" };
    
    private final LoggerService logger;
    
    public MongoDBMetadataParser(AdapterPluginContext adapterPluginContext) {
        logger = adapterPluginContext.getServiceRegistry().getService(LoggerService.class);
    }
    
    protected void addData(Document bson, CloudApplicationModel cloudApplicationModel) {
        String id = getObjectId(bson);
        
        QName qName = new QName(id);
        CloudDataObjectNode bsonNode = new CloudDataObjectNodeImpl(null, qName, ObjectCategory.CUSTOM, DataType.OBJECT);
        
        Set<String> keys = bson.keySet();
        for (String key: keys) {
            QName fieldQName = new QName("Object");
            CloudDataObjectNode fieldType = new CloudDataObjectNodeImpl(null, fieldQName, ObjectCategory.CUSTOM, DataType.OBJECT);
            bsonNode.addField(new FieldImpl(key, fieldType, false, false, true, true));
        }
        
        logger.log(LoggerService.Level.DEBUG, "Adding data from BSON document with _id [" + id + "] to CloudApplicationModel");
        cloudApplicationModel.addDataObject(bsonNode);
    }
    
    protected void addOperation(CloudApplicationModel cloudApplicationModel, String operationName) {
        CloudOperationNodeImpl operation = new CloudOperationNodeImpl();
        operation.setName(operationName);
        operation.setInvocationStyle(InvocationStyle.REQUEST_RESPONSE);
        
        switch(operationName) {
            case "find":
                break;
            case "insert":
                operation.setResponse(getInsertResponse());
                break;
            default:
                logger.log(LoggerService.Level.SEVERE, "Unable to add unknown operation [" + operationName + "]");
                throw new RuntimeException("Unable to add unknown operation [" + operationName + "]");
        };
        
        logger.log(LoggerService.Level.INFO, "Adding operation [" + operationName + "] to CloudApplicationModel");
        cloudApplicationModel.addOperation(operation);
    }
    
    protected Document getDocumentFromDataSource(DataSource dataSource) {
        if (dataSource instanceof BSONDataSource) {
            return ((BSONDataSource) dataSource).getDocument();
        } else {
            throw new RuntimeException("DataSource must be an instance of BSONDataSource");
        }
    }
    
    protected OperationResponse getInsertResponse() {
        OperationResponseImpl response = new OperationResponseImpl();
        
        response.setDescription("ObjectId of newly insert document");
        response.setName("_id");
        response.setQualifiedName(new QName("InsertResponse"));
        response.setResponseObject(new CloudDataObjectNodeImpl(null, new QName("_id"), ObjectCategory.CUSTOM, DataType.ID));
        
        return response;
    }
    
    protected String getObjectId(Document bson) {
        ObjectId id = bson.getObjectId("_id");
        return id.toString();
    }
    
    @Override
    public CloudApplicationModel parse(DataSource dataSource, Properties properties) throws MetadataParserException {
        CloudApplicationModel cloudApplicationModel = new CloudApplicationModelImpl();
        parse(dataSource, cloudApplicationModel, properties);
        
        return cloudApplicationModel;
    }

    @Override
    public void parse(DataSource dataSource, CloudApplicationModel cloudApplicationModel,
                      Properties properties) throws MetadataParserException {
        logger.log(LoggerService.Level.INFO, "Adding [" + operationNames.length + "] operations with data to CloudApplicationModel");
        
        for(String operationName: operationNames) {
            addOperation(cloudApplicationModel, operationName);
        }
        
        Document bson = getDocumentFromDataSource(dataSource);
        addData(bson, cloudApplicationModel);
    }

    @Override
    public Set<String> getSupportedMediaTypes() {
        Set<String> mediaTypes = new HashSet<>();
        mediaTypes.add("application/xml");
        
        return mediaTypes;
    }
}
