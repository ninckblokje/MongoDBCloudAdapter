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

import java.util.Map;

import oracle.cloud.connector.api.CloudApplicationConnection;
import oracle.cloud.connector.api.CloudApplicationConnectionFactory;
import oracle.cloud.connector.api.CloudConnectorException;

public class MongoDBApplicationConnectionFactory implements CloudApplicationConnectionFactory {
    
    private Map<String, String> properties;

    @Override
    public void setConnectionFactoryProperties(Map<String, String> map) {
        this.properties = map;
    }

    @Override
    public Map<String, String> getConnectionFactoryProperties() {
        return properties;
    }

    @Override
    public CloudApplicationConnection getConnection() throws CloudConnectorException {
        return new MongoDBApplicationConnection();
    }

    @Override
    public String getCloudConnectorClassName() {
        return MongoDBApplicationConnection.class.getName();
    }
}
