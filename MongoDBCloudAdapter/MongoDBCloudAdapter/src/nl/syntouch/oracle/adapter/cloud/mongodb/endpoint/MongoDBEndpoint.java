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

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.cloud.connector.api.CloudAdapterLoggingService;
import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudInvocationException;
import oracle.cloud.connector.api.CloudMessage;
import oracle.cloud.connector.api.Endpoint;
import oracle.cloud.connector.api.EndpointObserver;
import oracle.cloud.connector.api.RemoteApplicationException;

import org.bson.Document;

public class MongoDBEndpoint implements Endpoint {
    
    private CloudAdapterLoggingService logger;
    
    private MongoDBConnection connection;
    private CloudInvocationContext ctx;
    private List<EndpointObserver> observers = new ArrayList<>();

    @Override
    public CloudMessage invoke(CloudMessage cloudMessage) throws RemoteApplicationException, CloudInvocationException {
        String operationName = (String) ctx.getContextObject("targetOperation");
        Document bson = (Document) cloudMessage.getMessagePayload();
        
        // TODO Implement this method
        return null;
    }

    @Override
    public void addObserver(EndpointObserver endpointObserver) {
        observers.add(endpointObserver);
    }

    @Override
    public void initialize(CloudInvocationContext cloudInvocationContext) throws CloudInvocationException {
        logger = cloudInvocationContext.getLoggingService();
        
        String mongoUri = (String) cloudInvocationContext.getCloudConnectionProperties().get(Constants.MONGO_URI_KEY);
        String mongoDb = (String) cloudInvocationContext.getCloudConnectionProperties().get(Constants.MONGO_DB_KEY);
        String mongoCollection = (String) cloudInvocationContext.getCloudConnectionProperties().get(Constants.MONGO_COLLECTION_KEY);
        
        logger.log(CloudAdapterLoggingService.Level.DEBUG, "Initializing endpoint for MongoDB");
        
        connection = new MongoDBConnection(cloudInvocationContext);
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
