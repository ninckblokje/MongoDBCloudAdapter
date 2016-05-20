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
import java.util.Locale;

import java.util.Map;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.metadata.MongoDBMetadataBrowser;

import oracle.tip.tools.adapters.cloud.utils.CloudAdapterUtils;
import oracle.tip.tools.ide.adapters.cloud.api.connection.AbstractCloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.connection.CloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataBrowser;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.model.TransformationModelBuilder;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.SelectObject;

public abstract class AbstractMongoDBPage {
    
    private final LoggerService logger;
    
    private final AdapterPluginContext ctx;
    
    protected AbstractMongoDBPage(AdapterPluginContext adapterPluginContext) {
        ctx = adapterPluginContext;
        logger = adapterPluginContext.getServiceRegistry().getService(LoggerService.class);
    }
    
    protected EditField findEditField(LinkedList<EditField> currentPageFields, String fieldName) {
        EditField eventSource = null;
        
        for (EditField ef: currentPageFields) {
            if (fieldName.equals(ef.getName())) {
                eventSource = ef;
                break;
            }
        }
        
        return eventSource;
    }
    
    protected CloudConnection getCloudConnection() {
        return (CloudConnection) ctx.getContextObject("CA_UI_connection");
    }
    
    @SuppressWarnings("unchecked")
    protected Map<String, String> getConnectionProperties() {
        // only available in edit mode
        return (Map<String, String>) ctx.getContextObject("connectionProperties");
    }
    
    protected AdapterPluginContext getContext() {
        return ctx;
    }
    
    protected Locale getLocale() {
        return CloudAdapterUtils.getLocale(ctx);
    }
    
    protected LoggerService getLogger() {
        return logger;
    }
    
    protected MongoDBMetadataBrowser getMetadataBrowser() {
        return (MongoDBMetadataBrowser) CloudAdapterUtils.getMetadataBrowser(ctx);
    }
    
    protected String getText(String key) {
        return Constants.getText(getLocale(), key);
    }
    
    protected TransformationModelBuilder getTransformationModelBuilder() {
        return (TransformationModelBuilder) ctx.getContextObject("_ui_ModelBuilder");
    }
    
    protected void setTransformationModelBuilder(TransformationModelBuilder modelBuilder) {
        ctx.setContextObject("_ui_ModelBuilder", modelBuilder);
    }
}
