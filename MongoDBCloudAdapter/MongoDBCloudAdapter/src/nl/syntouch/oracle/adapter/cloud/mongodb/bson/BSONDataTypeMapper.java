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

import javax.xml.namespace.QName;

import oracle.tip.tools.ide.adapters.cloud.api.model.CloudDataObjectNode;
import oracle.tip.tools.ide.adapters.cloud.api.model.DataType;
import oracle.tip.tools.ide.adapters.cloud.api.model.ObjectCategory;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.CloudDataObjectNodeImpl;

public class BSONDataTypeMapper {
    
    public static CloudDataObjectNode getDataObjectNode(Object o) {
        DataType type = DataType.STRING;
        ObjectCategory category = ObjectCategory.BUILTIN;
        
        return new CloudDataObjectNodeImpl(null, new QName(type.getDataType()), category, type);
    }
}
