package nl.syntouch.oracle.adapter.cloud.mongodb.definition;

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

import java.util.Locale;
import java.util.ResourceBundle;

public class Constants {
    
    private static final String resourceBundle = "nl.syntouch.oracle.adapter.cloud.mongodb.definition.MongoDBCloudAdapterBundle";
    
    public static final String ADAPTER_NAME = "MongoDBAdapter";
    public static final String VERSION = "1";
    
    public static final String CSFKEY_KEY = "csfkey";
    
    public static final String MONGO_COLLECTION_KEY = "mongoCollection";
    public static final String MONGO_DB_KEY = "mongoDb";
    public static final String MONGO_URI_KEY = "mongoUri";
    
    public static final String CONTEXT_SAMPLE_DOCUMENT_KEY = "MongoDB.sampleBsonDocument";
    
    public static String getText(Locale locale, String key) {
        return ResourceBundle.getBundle(resourceBundle, locale).getString(key);
    }
}
