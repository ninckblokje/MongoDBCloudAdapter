package nl.syntouch.oracle.adapter.cloud.mongodb.endpoint;

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

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.List;

import nl.syntouch.oracle.adapter.cloud.mongodb.adapter.MongoDBCredentialStore;
import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import nl.syntouch.oracle.adapter.cloud.mongodb.transformer.BsonToXmlTransformer;
import nl.syntouch.oracle.adapter.cloud.mongodb.transformer.XmlToBsonTransformer;

import oracle.cloud.connector.api.CloudAdapterLoggingService;
import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudInvocationException;
import oracle.cloud.connector.api.CloudMessage;
import oracle.cloud.connector.api.CloudMessageFactory;
import oracle.cloud.connector.api.CloudRuntimeConstants;
import oracle.cloud.connector.api.Endpoint;
import oracle.cloud.connector.api.EndpointObserver;
import oracle.cloud.connector.api.RemoteApplicationException;

import oracle.xml.parser.schema.XMLSchema;

import org.bson.Document;

import org.w3c.dom.Node;

public class MongoDBEndpoint implements Endpoint {
    
    private CloudAdapterLoggingService logger;
    
    private MongoDBConnection connection;
    private List<EndpointObserver> observers = new ArrayList<>();
    private String operationName;
    private String rootNamespace;
    private String typeNamespace;
    
    protected void connect(CloudInvocationContext cloudInvocationContext) {
        String mongoUri = new MongoDBCredentialStore(cloudInvocationContext).getUrl();
        String mongoDb = (String) cloudInvocationContext.getCloudConnectionProperties().get(Constants.MONGO_DB_KEY);
        String mongoCollection = (String) cloudInvocationContext.getCloudConnectionProperties().get(Constants.MONGO_COLLECTION_KEY);
        
        connection = new MongoDBConnection(cloudInvocationContext);
        connection
            .setMongoUri(mongoUri)
            .setMongoDb(mongoDb)
            .setMongoCollection(mongoCollection)
            .connect();
    }
    
    protected void initializeNamespaces(CloudInvocationContext cloudInvocationContext) {
        XMLSchema inputSchema = (XMLSchema) cloudInvocationContext.getContextObject(CloudRuntimeConstants.TARGET_INPUT_SCHEMA);
        rootNamespace = inputSchema.getSchemaTargetNS();
        
        String adapterName = rootNamespace.split("/")[5];
        
        String mongoNamespace = connection.getNamespace();
        typeNamespace = adapterName + "/" + mongoNamespace + "/" + operationName;
        
        logger.log(CloudAdapterLoggingService.Level.DEBUG, "Set root namespace to [" + rootNamespace + "] and type namespace to [" + typeNamespace + "]");
    }
    
    protected CloudMessage invokeInsert(Document requestBson) throws CloudInvocationException {
        connection.getCollection().insertOne(requestBson);
        
        Document responseBson = new Document()
                .append("_id", requestBson.getObjectId("_id"));
        
        Node responseXml = new BsonToXmlTransformer()
            .setRootNamespace(rootNamespace)
            .setDataNamespace(typeNamespace + "/response")
            .setWrapperElementName("insertResponse")
            .transform(responseBson);
        return CloudMessageFactory.newInstance().createCloudMessage(responseXml);
    }

    @Override
    public CloudMessage invoke(CloudMessage requestMsg) throws RemoteApplicationException, CloudInvocationException {
        Node xml = requestMsg.getMessagePayloadAsDocument();
        Document bson = new XmlToBsonTransformer()
                .transform(xml)
                .get("Document", Document.class);
        
        CloudMessage responseMsg;
        switch(operationName) {
            case "insert":
                responseMsg = invokeInsert(bson);
                break;
            default:
                logger.log(CloudAdapterLoggingService.Level.ERROR, "Unknown operation [" + operationName + "]");
                throw new CloudInvocationException("Unknown operation [" + operationName + "]");
        }
        
        return responseMsg;
    }

    @Override
    public void addObserver(EndpointObserver endpointObserver) {
        observers.add(endpointObserver);
    }

    @Override
    public void initialize(CloudInvocationContext cloudInvocationContext) throws CloudInvocationException {
        logger = cloudInvocationContext.getLoggingService();
        
        logger.log(CloudAdapterLoggingService.Level.DEBUG, "Initializing endpoint for MongoDB");
        connect(cloudInvocationContext);
        
        operationName = cloudInvocationContext.getTargetOperationName();
        initializeNamespaces(cloudInvocationContext);
    }

    @Override
    public void destroy() {
        logger.log(CloudAdapterLoggingService.Level.DEBUG, "Destroying endpoint for MongoDB");
        connection.close();
    }
}
