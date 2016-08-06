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

import java.security.AccessController;

import java.security.PrivilegedAction;

import java.util.HashMap;
import java.util.Map;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.cloud.connector.api.CloudAdapterLoggingService;
import oracle.cloud.connector.api.CloudInvocationContext;

import oracle.security.jps.JpsContext;
import oracle.security.jps.JpsContextFactory;
import oracle.security.jps.JpsException;
import oracle.security.jps.service.credstore.CredStoreException;
import oracle.security.jps.service.credstore.CredentialStore;
import oracle.security.jps.service.credstore.GenericCredential;

public class MongoDBCredentialStore {
    
    private final CloudAdapterLoggingService logger;

    private String csfkey;
    private String csfmap;

    public MongoDBCredentialStore(CloudInvocationContext cloudInvocationContext) {
        logger = cloudInvocationContext.getLoggingService();
        
        csfkey = (String) cloudInvocationContext.getCloudConnectionProperties().get("csfkey");
        csfmap = (String) cloudInvocationContext.getCloudConnectionProperties().get("csfMap");
    }
    
    public Map<String, String> getCredentials() {
        Map<String, String> credentialMap = new HashMap<>();
        
        try {
            JpsContextFactory jpsContextFactory = JpsContextFactory.getContextFactory();
            JpsContext jpsContext = jpsContextFactory.getContext();
            final CredentialStore store = jpsContext.getServiceInstance(CredentialStore.class);

             credentialMap.putAll(AccessController.doPrivileged(new PrivilegedAction<Map<String, String>> () {
                public Map<String, String> run() {
                    Map<String, String> credentials = new HashMap<>();
                    
                    try {
                        GenericCredential credential = (GenericCredential) store.getCredential(csfmap, csfkey);
                        credentials.put(Constants.MONGO_URI_KEY, (String) credential.getCredential());
                    } catch(CredStoreException ex) {
                        logger.logError("Unable to retrieve csfkey [" + csfkey + "] from csfmap [" + csfmap + "]", ex);
                    }
                    
                    return credentials;
                }
            }));
        } catch(JpsException ex) {
            logger.logError("Unable to retrieve csfkey [" + csfkey + "] from csfmap [" + csfmap + "]", ex);
        }
        
        return credentialMap;
    }

    public String getUrl() {
        return getCredentials().get(Constants.MONGO_URI_KEY);
    }
}
