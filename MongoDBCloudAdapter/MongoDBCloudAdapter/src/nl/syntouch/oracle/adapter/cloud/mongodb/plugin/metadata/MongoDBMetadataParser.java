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
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataSource;

import javax.xml.namespace.QName;

import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.bson.BSONDataSource;

import nl.syntouch.oracle.adapter.cloud.mongodb.bson.BSONUtil;
import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.cloud.connector.api.CloudOperation;

import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParser;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParserException;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudApplicationModel;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.InvocationStyle;
import oracle.tip.tools.ide.adapters.cloud.api.model.OperationResponse;
import oracle.tip.tools.ide.adapters.cloud.api.model.RequestParameter;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.CloudApplicationModelImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudOperationNodeImpl;

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
        Document bson =  getDocument(dataSource);
        
        logger.log(LoggerService.Level.INFO, "Adding operation [" + operationName + "] to CloudApplicationModel");
        switch(operationName) {
            case "insert":
                cloudApplicationModel.addOperation(getInsertOperation(cloudApplicationModel, bson, getBaseNamespace()));
                break;
            default:
                logger.log(LoggerService.Level.SEVERE, "Unable to add unknown operation [" + operationName + "]");
                throw new RuntimeException("Unable to add unknown operation [" + operationName + "]");
        };
    }
    
    protected String getBaseNamespace() {
        String mongoNamespace = (String) ctx.getContextObject(Constants.MONGO_NAMESPACE_KEY);
        return Constants.ADAPTER_NAME + "/" + mongoNamespace;
    }
    
    protected CloudOperationNode getInsertOperation(CloudApplicationModel cloudApplicationModel, Document bson, String baseNamespace) {
        CloudOperationNodeImpl existingOperation = (CloudOperationNodeImpl) getExistingOperation(cloudApplicationModel, "insert");
        
        CloudOperationNodeImpl operation = (existingOperation == null) ? new CloudOperationNodeImpl() : existingOperation;
        operation.setName("insert");
        operation.setInvocationStyle(InvocationStyle.REQUEST_RESPONSE);
        
        operation.getRequestParameters().add(getInsertRequest(bson, baseNamespace));
        operation.setResponse(getInsertResponse(baseNamespace));
        
        return operation;
    }
    
    protected Document getDocument(DataSource dataSource) {
        if (dataSource instanceof BSONDataSource) {
            return ((BSONDataSource) dataSource).getDocument();
        } else {
            throw new RuntimeException("DataSource must be an instance of BSONDataSource");
        }
    }
    
    protected RequestParameter getInsertRequest(Document bson, String baseNamespace) {
        RequestParameterImpl request = new RequestParameterImpl();
        request.setDataType(BSONUtil.parseToDataObjectNode(bson, baseNamespace + "/insert/request"));
        
        return request;
    }
    
    protected OperationResponse getInsertResponse(String baseNamespace) {
        OperationResponseImpl response = new OperationResponseImpl();
        
        Document bson = BSONUtil.getUnstructuredDocument();
        
        response.setDescription("ObjectId of newly insert document");
        response.setName("_id");
        response.setQualifiedName(new QName("InsertResponseDocument"));
        response.setResponseObject(BSONUtil.parseToDataObjectNode(bson, baseNamespace + "/insert/response"));
        
        return response;
    }
    
    protected String getObjectId(Document bson) {
        ObjectId id = bson.getObjectId("_id");
        return id.toString();
    }
    
    protected CloudOperationNode getExistingOperation(CloudApplicationModel cloudApplicationModel, String operationName) {
        List<CloudOperationNode> operations = cloudApplicationModel.getOperations();
        for (CloudOperationNode operation: operations) {
            if (operationName.equals(operation.getName())) return operation;
        }
        
        return null;
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
