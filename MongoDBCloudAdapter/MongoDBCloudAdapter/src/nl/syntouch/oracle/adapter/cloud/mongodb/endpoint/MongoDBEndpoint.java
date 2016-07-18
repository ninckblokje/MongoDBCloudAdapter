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

import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudInvocationException;
import oracle.cloud.connector.api.CloudMessage;
import oracle.cloud.connector.api.Endpoint;
import oracle.cloud.connector.api.EndpointObserver;
import oracle.cloud.connector.api.RemoteApplicationException;

public class MongoDBEndpoint implements Endpoint {
    
    private MongoDBConnection connection;
    private List<EndpointObserver> observers = new ArrayList<>();

    @Override
    public CloudMessage invoke(CloudMessage cloudMessage) throws RemoteApplicationException, CloudInvocationException {
        // TODO Implement this method
        return null;
    }

    @Override
    public void addObserver(EndpointObserver endpointObserver) {
        observers.add(endpointObserver);
    }

    @Override
    public void initialize(CloudInvocationContext cloudInvocationContext) throws CloudInvocationException {
        connection = new MongoDBConnection(cloudInvocationContext);
        connection
            .setMongoUri(null)
            .setMongoDb(null)
            .setMongoCollection(null)
            .connect();
    }

    @Override
    public void destroy() {
        connection.close();
    }
}
