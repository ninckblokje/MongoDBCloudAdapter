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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoDatabase;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import nl.syntouch.oracle.adapter.cloud.mongodb.logging.CloudAdapterLogger;

import nl.syntouch.oracle.adapter.cloud.mongodb.logging.CloudAdapterLoggingServiceWrapper;

import nl.syntouch.oracle.adapter.cloud.mongodb.logging.LoggerServiceWrapper;

import oracle.cloud.connector.api.CloudInvocationContext;

import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;

import org.bson.Document;

public class MongoDBConnection implements AutoCloseable {
    
    private final CloudAdapterLogger logger;
    
    private String mongoCollection;
    private String mongoDb;
    private String mongoUri;

    private MongoClient client = null;
    private MongoDatabase db = null;
    private MongoCollection<Document> collection = null;
    
    public MongoDBConnection(AdapterPluginContext ctx) {
        logger = new LoggerServiceWrapper(ctx.getServiceRegistry().getService(LoggerService.class));
    }
    
    public MongoDBConnection(CloudInvocationContext ctx) {
        logger = new CloudAdapterLoggingServiceWrapper(ctx.getLoggingService());
    }
    
    public MongoCollection<Document> getCollection() {
        return collection;
    }
    
    public String getNamespace() {
        return collection.getNamespace().getFullName();
    }
    
    public MongoDBConnection setMongoCollection(String mongoCollection) {
        this.mongoCollection = mongoCollection;
        return this;
    }
    
    public MongoDBConnection setMongoDb(String mongoDb) {
        this.mongoDb = mongoDb;
        return this;
    }
    
    public MongoDBConnection setMongoUri(String mongoUri) {
        this.mongoUri = mongoUri;
        return this;
    }

    public void connect() {
        logger.log(CloudAdapterLogger.Level.INFO, "Connecting to [" + mongoUri + "]");
        MongoClientURI uri = new MongoClientURI(mongoUri);
        client = new MongoClient(uri);

        logger.log(CloudAdapterLogger.Level.INFO,
                   "Retrieving collection [" + mongoCollection + "] from database [" + mongoDb + "]");
        db = client.getDatabase(mongoDb);
        collection = db.getCollection(mongoCollection);
    }

    @Override
    public void close() {
        logger.log(CloudAdapterLogger.Level.DEBUG, "Closing connection");
        if (client != null)
            client.close();
        
        collection = null;
        db = null;
        client = null;
    }
}
