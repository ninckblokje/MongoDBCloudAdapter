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

import com.mongodb.client.MongoCollection;

import java.util.Set;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;
import nl.syntouch.oracle.adapter.cloud.mongodb.endpoint.MongoDBConnection;

import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractCloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.connection.AuthenticationScheme;
import oracle.tip.tools.ide.adapters.cloud.api.connection.PingStatus;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;

import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;

import org.bson.Document;

public class MongoDBPing extends AbstractCloudConnection implements AutoCloseable {
    
    private final LoggerService logger;
    
    private AdapterPluginContext ctx;
    private MongoDBConnection connection;
    
    public MongoDBPing(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext.getReferenceBindingName());
        ctx = adapterPluginContext;
        connection = new MongoDBConnection(adapterPluginContext);
        
        logger = adapterPluginContext.getServiceRegistry().getService(LoggerService.class);
    }
    
    public void connect() {
        String mongoUri = getConnectionProperties().getProperty(Constants.MONGO_URI_KEY);
        String mongoDb = getConnectionProperties().getProperty(Constants.MONGO_DB_KEY);
        String mongoCollection = getConnectionProperties().getProperty(Constants.MONGO_COLLECTION_KEY);
        
        connection
            .setMongoUri(mongoUri)
            .setMongoDb(mongoDb)
            .setMongoCollection(mongoCollection)
            .connect();
        
        ctx.setContextObject(Constants.MONGO_NAMESPACE_KEY, connection.getNamespace());
    }
    
    public MongoCollection<Document> getCollection() {
        return connection.getCollection();
    }
    
    @Override
    public void close() {
        connection.close();
    }

    @Override
    public AuthenticationScheme getAuthenticationScheme() {
        return new MongoDBAuthenticationScheme(ctx, this);
    }
    
    @Override
    public Set<String> getPersistentPropertyNames() {
        Set<String> propNames = super.getPersistentPropertyNames();
        
        propNames.add(Constants.MONGO_DB_KEY);
        propNames.add(Constants.MONGO_COLLECTION_KEY);
        
        propNames.add(Constants.CONTEXT_MODE_KEY);
        propNames.add(Constants.CONTEXT_SAMPLE_FILE_KEY);
        
        return propNames;
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
