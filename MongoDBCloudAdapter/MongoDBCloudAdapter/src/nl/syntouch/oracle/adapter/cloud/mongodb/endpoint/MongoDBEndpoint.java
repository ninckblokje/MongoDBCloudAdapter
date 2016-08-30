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
import oracle.cloud.connector.api.Endpoint;
import oracle.cloud.connector.api.EndpointObserver;
import oracle.cloud.connector.api.RemoteApplicationException;

import org.bson.Document;

import org.w3c.dom.Node;

public class MongoDBEndpoint implements Endpoint {
    
    private CloudAdapterLoggingService logger;
    
    private MongoDBConnection connection;
    private CloudInvocationContext ctx;
    private List<EndpointObserver> observers = new ArrayList<>();
    
    protected void initializeConnectionProperties(CloudInvocationContext cloudInvocationContext) {
        cloudInvocationContext.getCloudConnectionProperties().put(Constants.MONGO_URI_KEY, new MongoDBCredentialStore(cloudInvocationContext).getUrl());
    }
    
    protected CloudMessage invokeInsert(Document requestBson) throws CloudInvocationException {
        connection.getCollection().insertOne(requestBson);
        
        Document responseBson = new Document()
                .append("_id", requestBson.getObjectId("_id"));
        
        Node responseXml = new BsonToXmlTransformer().setNamespace(null).transform(responseBson);
        return CloudMessageFactory.newInstance().createCloudMessage(responseXml);
    }

    @Override
    public CloudMessage invoke(CloudMessage requestMsg) throws RemoteApplicationException, CloudInvocationException {
        String operationName = ctx.getTargetOperationName();
        Node xml = requestMsg.getMessagePayloadAsDocument();
        Document bson = new XmlToBsonTransformer().transform(xml);
        
        CloudMessage responseMsg;
        switch(operationName) {
            case "insert":
                responseMsg = invokeInsert(bson);
                break;
            default:
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
        ctx = cloudInvocationContext;
        
        logger = ctx.getLoggingService();
        initializeConnectionProperties(ctx);
        
        String mongoUri = (String) ctx.getCloudConnectionProperties().get(Constants.MONGO_URI_KEY);
        String mongoDb = (String) ctx.getCloudConnectionProperties().get(Constants.MONGO_DB_KEY);
        String mongoCollection = (String) ctx.getCloudConnectionProperties().get(Constants.MONGO_COLLECTION_KEY);
        
        System.err.println("ctx conn");
        System.err.println(ctx.getCloudConnectionProperties());
        System.err.println("ctx opp");
        System.err.println(ctx.getCloudOperationProperties());
        
        logger.log(CloudAdapterLoggingService.Level.DEBUG, "Initializing endpoint for MongoDB");
        
        connection = new MongoDBConnection(ctx);
        connection
            .setMongoUri(mongoUri)
            .setMongoDb(mongoDb)
            .setMongoCollection(mongoCollection)
            .connect();
    }

    @Override
    public void destroy() {
        logger.log(CloudAdapterLoggingService.Level.DEBUG, "Destroying endpoint for MongoDB");
        connection.close();
    }
}
