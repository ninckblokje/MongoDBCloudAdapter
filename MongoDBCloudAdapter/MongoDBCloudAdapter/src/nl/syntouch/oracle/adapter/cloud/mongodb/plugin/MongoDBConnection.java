package nl.syntouch.oracle.adapter.cloud.mongodb.plugin;

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
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractCloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.connection.AuthenticationScheme;
import oracle.tip.tools.ide.adapters.cloud.api.connection.PingStatus;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;

import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;

import org.bson.Document;

public class MongoDBConnection extends AbstractCloudConnection implements AutoCloseable {
    
    private final LoggerService logger;
    
    private AdapterPluginContext ctx;
    
    private MongoClient client = null;
    private MongoDatabase db = null;
    private MongoCollection<Document> collection = null;
    
    public MongoDBConnection(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext.getReferenceBindingName());
        ctx = adapterPluginContext;
        
        logger = adapterPluginContext.getServiceRegistry().getService(LoggerService.class);
    }
    
    public void connect() {
        String mongoUri = getConnectionProperties().getProperty(Constants.MONGO_URI_KEY);
        String mongoDb = getConnectionProperties().getProperty(Constants.MONGO_DB_KEY);
        String mongoCollection = getConnectionProperties().getProperty(Constants.MONGO_COLLECTION_KEY);
        
        connect(mongoUri, mongoDb, mongoCollection);
    }
    
    public void connect(String mongoUri, String mongoDb, String mongoCollection) {
        close();
        
        logger.log(LoggerService.Level.INFO, "Connecting to [" + mongoUri + "]");
        MongoClientURI uri = new MongoClientURI(mongoUri);
        client = new MongoClient(uri);
        
        db = client.getDatabase(mongoDb);
        collection = db.getCollection(mongoCollection);
        
        ctx.setContextObject(Constants.MONGO_NAMESPACE_KEY, collection.getNamespace().getFullName());
    }
    
    public MongoCollection<Document> getCollection() {
        return collection;
    }
    
    @Override
    public void close() {
        logger.log(LoggerService.Level.DEBUG, "Closing connection");
        if (client != null) client.close();
    }

    @Override
    public AuthenticationScheme getAuthenticationScheme() {
        return new MongoDBAuthenticationScheme(ctx, this);
    }

    @Override
    public PingStatus ping() {
        try {
            connect();
            logger.log(LoggerService.Level.DEBUG, "Ping OK");
            
            return PingStatus.SUCCESS_STATUS;
        } catch (Exception ex) {
            logger.log(LoggerService.Level.WARNING, "Ping NOK");
            logger.log(LoggerService.Level.WARNING, ex.getMessage());
            
            ex.printStackTrace();
            
            return new PingStatus(ex);
        } finally {
            close();
        }
    }
}
