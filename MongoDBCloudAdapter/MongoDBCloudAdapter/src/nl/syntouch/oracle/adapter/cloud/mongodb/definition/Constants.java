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
    
    public static final String ADAPTER_NAME = "MongoDBCloudAdapter";
    public static final String ENDPOINT_TYPE = "MongoDB";
    public static final String VERSION = "1";
    
    public static final String CSFKEY_KEY = "csfkey";
    
    public static final String MONGO_COLLECTION_KEY = "MongoDB.collection";
    public static final String MONGO_DB_KEY = "MongoDB.db";
    public static final String MONGO_NAMESPACE_KEY = "mongoNamespace";
    public static final String MONGO_URI_KEY = "MongoDB.uri";
    
    public static final String CONTEXT_MODE_KEY = "MongoDB.mode";
    public static final String CONTEXT_OPERATION_KEY = "MongoDB.operation";
    public static final String CONTEXT_PARSE_DOCUMENT_KEY = "MongoDB.parseBsonDocument";
    public static final String CONTEXT_SAMPLE_DOCUMENT_KEY = "MongoDB.sampleBsonDocument";
    public static final String CONTEXT_SAMPLE_FILE_KEY = "MongoDB.sampleBsonFile";
    
    public static final String MODE_STRUCTURED = "structured";
    public static final String MODE_UNSTRUCTURED = "unstructured";
    
    public static String getText(Locale locale, String key) {
        return ResourceBundle.getBundle(resourceBundle, locale).getString(key);
    }
}
