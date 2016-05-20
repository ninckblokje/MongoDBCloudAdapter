package nl.syntouch.oracle.adapter.cloud.mongodb.wizard.page;

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

import java.util.LinkedList;

import java.util.Map;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.tip.tools.adapters.cloud.impl.CloudAdapterConnectionPage;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.ITextBoxObject;

public class MongoDBConnectionPage extends CloudAdapterConnectionPage {
    
    private AdapterPluginContext ctx;
    
    public MongoDBConnectionPage(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
        
        ctx = adapterPluginContext;
    }
    
    protected void addMissingData(LinkedList<EditField> fields, String fieldName, String contextKey) {
        Map<String, String> connProps = getConnectionProperties();
        if (connProps == null) return;
        
        String value = connProps.get(contextKey);
        if (value == null) return;
        
        for (EditField field: fields) {
            if (fieldName.equals(field.getName())
                    && field.getObject() instanceof ITextBoxObject) {
                ITextBoxObject textBox = (ITextBoxObject) field.getObject();
                textBox.setValue(value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String> getConnectionProperties() {
        // only available in edit mode
        return (Map<String, String>) ctx.getContextObject("connectionProperties");
    }
    
    @Override
    public LinkedList<EditField> getPageEditFields() {
        LinkedList<EditField> fields = super.getPageEditFields();
        
        addMissingData(fields, Constants.MONGO_COLLECTION_KEY, Constants.MONGO_COLLECTION_KEY);
        addMissingData(fields, Constants.MONGO_DB_KEY, Constants.MONGO_DB_KEY);
        
        return fields;
    }
}
