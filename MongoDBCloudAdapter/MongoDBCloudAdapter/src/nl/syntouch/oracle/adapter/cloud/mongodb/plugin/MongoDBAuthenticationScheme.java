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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractAuthenticationScheme;
import oracle.tip.tools.ide.adapters.cloud.api.connection.CloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.connection.PropertyDefinition;
import oracle.tip.tools.ide.adapters.cloud.api.connection.PropertyDefinitions;
import oracle.tip.tools.ide.adapters.cloud.api.connection.PropertyType;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;

public class MongoDBAuthenticationScheme extends AbstractAuthenticationScheme {
    
    public MongoDBAuthenticationScheme(AdapterPluginContext adapterPluginContext, CloudConnection cloudConnection) {
        super(adapterPluginContext, cloudConnection);
    }

    @Override
    public Set<String> getAuthenticationPropertyNames() {
        Set<String> names = new HashSet<String>();
        names.add(Constants.MONGO_URI_KEY);
        
        return names;
    }

    @Override
    public PropertyDefinitions getPropertyDefinitions() {
        PropertyDefinitions definitions = new PropertyDefinitions();
        
        definitions.getPropertyDefintions().add(createPropertyDefinition(Constants.MONGO_URI_KEY, true, PropertyType.STRING));
        definitions.getPropertyDefintions().add(createPropertyDefinition(Constants.CSFKEY_KEY, true, PropertyType.STRING));
        
        return definitions;
    }
    
    private PropertyDefinition createPropertyDefinition(String name, boolean required, PropertyType type) {
        PropertyDefinition definition = new PropertyDefinition();
        definition.setPropertyName(name);
        definition.setRequired(required);
        definition.setPropertyType(type);
        return definition;
    }
    
    public String getMongoDBURI() {
        return getAuthenticationProperties().getProperty(Constants.MONGO_URI_KEY);
    }
}
