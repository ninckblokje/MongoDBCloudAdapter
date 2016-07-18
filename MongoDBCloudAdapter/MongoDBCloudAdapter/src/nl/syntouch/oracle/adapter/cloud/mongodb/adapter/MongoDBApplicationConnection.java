package nl.syntouch.oracle.adapter.cloud.mongodb.adapter;

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

import java.util.Collections;
import java.util.List;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import nl.syntouch.oracle.adapter.cloud.mongodb.endpoint.MongoDBEndpointFactory;

import oracle.cloud.connector.api.CloudMessageHandler;
import oracle.cloud.connector.api.EndpointFactory;
import oracle.cloud.connector.api.SessionManager;
import oracle.cloud.connector.impl.AbstractCloudApplicationConnection;

public class MongoDBApplicationConnection extends AbstractCloudApplicationConnection {
    
    public MongoDBApplicationConnection() {
        super();
        
        setEndpointFactory(new MongoDBEndpointFactory());
    }

    @Override
    protected List<CloudMessageHandler> getMessageHandlers() {
        // TODO Implement this method
        return Collections.emptyList();
    }

    @Override
    public String getEndpointType(String string) {
        return Constants.ENDPOINT_TYPE;
    }

    @Override
    public void close() {
        // TODO Implement this method
    }

    @Override
    public boolean isValid() {
        // TODO Implement this method
        return false;
    }

    @Override
    public SessionManager getSessionManager() {
        // TODO Implement this method
        return null;
    }
}
