package nl.syntouch.oracle.adapter.cloud.mongodb.wizard;

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

import java.util.LinkedHashMap;
import java.util.Locale;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.MongoDBAdapter;

import nl.syntouch.oracle.adapter.cloud.mongodb.wizard.page.MongoDBConnectionPage;
import nl.syntouch.oracle.adapter.cloud.mongodb.wizard.page.MongoDBOperationsPage;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterConstants;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterFilter;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterPage;
import oracle.tip.tools.adapters.cloud.impl.AbstractCloudAdapterUIBinding;
import oracle.tip.tools.adapters.cloud.impl.CloudAdapterSummaryPage;
import oracle.tip.tools.adapters.cloud.impl.CloudAdapterWelcomePage;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.impl.plugin.AbstractCloudApplicationAdapter;

public class MongoDBUIBinding extends AbstractCloudAdapterUIBinding {
    
    public MongoDBUIBinding(CloudAdapterFilter cloudAdapterFilter, Locale locale) throws CloudAdapterException {
        super(cloudAdapterFilter, locale, Constants.ADAPTER_NAME);
        initUIBinding();
    }
    
    protected void initUIBinding() {
        AbstractCloudApplicationAdapter adapter = new MongoDBAdapter(this.context);
        context.setContextObject(CloudAdapterConstants.APPLICATION_ADAPTER, adapter);
        
        try {
            useApplicationAdapterToInitialise(adapter);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CloudAdapterException(ex);
        }
    }

    @Override
    public LinkedHashMap<String, ICloudAdapterPage> getEditPages(Object object) {
        CloudAdapterFilter filter = (CloudAdapterFilter) this.context.getContextObject(CloudAdapterConstants.UI_CLOUD_ADAPTER_FILTER);
        
        LinkedHashMap<String, ICloudAdapterPage> editPages = new LinkedHashMap<String, ICloudAdapterPage>();
        
        editPages.put(CloudAdapterConstants.WELCOME_PAGE_ID, new CloudAdapterWelcomePage(this.context));
        if (filter.isAddConnection()) {
            editPages.put(CloudAdapterConstants.CONNECTION_PAGE_ID, new MongoDBConnectionPage(this.context));
        }
        editPages.put(CloudAdapterConstants.OPERATIONS_PAGE_ID, new MongoDBOperationsPage(this.context));
        editPages.put(CloudAdapterConstants.SUMMARY_PAGE_ID, new CloudAdapterSummaryPage(this.context));
        
        return editPages;
    }

    @Override
    public AdapterPluginContext getAdapterConfiguration() {
        return this.context;
    }
}
