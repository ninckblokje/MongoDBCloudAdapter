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

import oracle.cloud.connector.api.CloudInvocationContext;
import oracle.cloud.connector.api.CloudInvocationException;
import oracle.cloud.connector.api.CloudMessage;
import oracle.cloud.connector.api.CloudMessageHandler;

public class MongoDBMessageHandler implements CloudMessageHandler {

    @Override
    public boolean handleRequestMessage(CloudInvocationContext cloudInvocationContext,
                                        CloudMessage cloudMessage) throws CloudInvocationException {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean handleResponseMessage(CloudInvocationContext cloudInvocationContext,
                                         CloudMessage cloudMessage) throws CloudInvocationException {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean handleErrorMessage(CloudInvocationContext cloudInvocationContext,
                                      CloudMessage cloudMessage) throws CloudInvocationException {
        // TODO Implement this method
        return false;
    }
}
