package nl.syntouch.oracle.adapter.cloud.mongodb.bson;

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

import java.util.Set;

import javax.xml.namespace.QName;

import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.DataType;
import oracle.tip.tools.ide.adapters.cloud.api.model.ObjectCategory;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudDataObjectNodeImpl;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.FieldImpl;

import org.bson.Document;
import org.bson.types.ObjectId;

public class BSONUtil {
    
    public static Document getUnstructuredDocument() {
        return new Document().append("_id", new ObjectId());
    }
    
    public static CloudDataObjectNode parseToDataObjectNode(Document bson, String namespace) {
        QName qName = new QName(namespace, "Document");
        CloudDataObjectNode bsonNode = new CloudDataObjectNodeImpl(null, qName, ObjectCategory.CUSTOM, DataType.OBJECT);
        
        Set<String> keys = bson.keySet();
        for (String key: keys) {
            CloudDataObjectNode type = BSONDataTypeMapper.getDataObjectNode(bson.get(key));
            // new FieldImpl(String name, CloudDataObjectNode fieldType, boolean array, boolean required, boolean nullable, boolean custom)
            bsonNode.addField(new FieldImpl(key, type, false, false, true, true));
        }
        
        CloudDataObjectNode unstructuredType = new CloudDataObjectNodeImpl(null, new QName("http://www.w3.org/2001/XMLSchema", "anyType"), ObjectCategory.BUILTIN);
        bsonNode.addField(new FieldImpl("_unstructured", unstructuredType, false, false, true, true));
        
        return bsonNode;
    }
}
