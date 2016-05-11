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

import nl.syntouch.oracle.adapter.cloud.mongodb.bson.BSONDataTypeMapper;
import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParserException;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudApplicationModel;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.DataType;
import oracle.tip.tools.ide.adapters.cloud.api.model.InvocationStyle;
import oracle.tip.tools.ide.adapters.cloud.api.model.ObjectCategory;
import oracle.tip.tools.ide.adapters.cloud.api.model.OperationResponse;
import oracle.tip.tools.ide.adapters.cloud.api.model.RequestParameter;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.CloudApplicationModelImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudDataObjectNodeImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudOperationNodeImpl;

import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.FieldImpl;

import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.OperationResponseImpl;

import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.RequestParameterImpl;

import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDBMetadataParser implements MetadataParser {
    
    //private static final String[] operationNames = { "insert", "find" };
    private static final String[] operationNames = { "insert" };
    
    private final LoggerService logger;
    
    private AdapterPluginContext ctx;
    
    public MongoDBMetadataParser(AdapterPluginContext adapterPluginContext) {
        ctx = adapterPluginContext;
        logger = adapterPluginContext.getServiceRegistry().getService(LoggerService.class);
    }
    
    protected void addOperation(CloudApplicationModel cloudApplicationModel, String operationName, DataSource dataSource) {
        String mongoNamespace = (String) ctx.getContextObject(Constants.MONGO_NAMESPACE_KEY);
        String baseNamespace = Constants.ADAPTER_NAME + "/" + mongoNamespace;
        
        logger.log(LoggerService.Level.INFO, "Adding operation [" + operationName + "] to CloudApplicationModel");
        switch(operationName) {
            case "find":
                break;
            case "insert":
                cloudApplicationModel.addOperation(getInsertOperation(dataSource, baseNamespace));
                break;
            default:
                logger.log(LoggerService.Level.SEVERE, "Unable to add unknown operation [" + operationName + "]");
                throw new RuntimeException("Unable to add unknown operation [" + operationName + "]");
        };
    }
    
    protected CloudOperationNode getInsertOperation(DataSource dataSource, String baseNamespace) {
        CloudOperationNodeImpl operation = new CloudOperationNodeImpl();
        operation.setName("insert");
        operation.setInvocationStyle(InvocationStyle.REQUEST_RESPONSE);
        
        operation.getRequestParameters().add(getInsertRequest(dataSource, baseNamespace));
        operation.setResponse(getInsertResponse(baseNamespace));
        
        return operation;
    }
    
    protected CloudDataObjectNode getDataObjectNode(Document bson, String namespace) {
        QName qName = new QName(namespace, "Document");
        CloudDataObjectNode bsonNode = new CloudDataObjectNodeImpl(null, qName, ObjectCategory.CUSTOM, DataType.OBJECT);
        
        Set<String> keys = bson.keySet();
        for (String key: keys) {
            CloudDataObjectNode type = BSONDataTypeMapper.getDataObjectNode(bson.get(key));
            // new FieldImpl(String name, CloudDataObjectNode fieldType, boolean array, boolean required, boolean nullable, boolean custom)
            bsonNode.addField(new FieldImpl(key, type, false, false, true, true));
        }
        
        CloudDataObjectNode unstructuredType = new CloudDataObjectNodeImpl(null, new QName("http://www.w3.org/2001/XMLSchema", "anyType"), ObjectCategory.BUILTIN);
        bsonNode.addField(new FieldImpl("_unstructured", unstructuredType, false, false, true, true));
        
        return bsonNode;
    }
    
    protected Document getDocumentFromDataSource(DataSource dataSource) {
        if (dataSource instanceof BSONDataSource) {
            return ((BSONDataSource) dataSource).getDocument();
        } else {
            throw new RuntimeException("DataSource must be an instance of BSONDataSource");
        }
    }
    
    protected RequestParameter getInsertRequest(DataSource dataSource, String baseNamespace) {
        Document bson = getDocumentFromDataSource(dataSource);
        
        RequestParameterImpl request = new RequestParameterImpl();
        request.setDataType(getDataObjectNode(bson, baseNamespace + "/insert/request"));
        
        return request;
    }
    
    protected OperationResponse getInsertResponse(String baseNamespace) {
        OperationResponseImpl response = new OperationResponseImpl();
        
        Document bson = new Document().append("_id", new ObjectId());
        
        
        response.setDescription("ObjectId of newly insert document");
        response.setName("_id");
        response.setQualifiedName(new QName("InsertResponseDocument"));
        response.setResponseObject(getDataObjectNode(bson, baseNamespace + "/insert/response"));
        
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
            addOperation(cloudApplicationModel, operationName, dataSource);
        }
    }

    @Override
    public Set<String> getSupportedMediaTypes() {
        Set<String> mediaTypes = new HashSet<>();
        mediaTypes.add("application/xml");
        
        return mediaTypes;
    }
}
