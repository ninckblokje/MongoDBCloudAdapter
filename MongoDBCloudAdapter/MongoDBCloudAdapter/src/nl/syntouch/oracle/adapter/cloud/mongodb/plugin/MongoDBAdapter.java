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

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.generator.MongoDBMetadataGenerator;
import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.metadata.MongoDBMetadataBrowser;

import oracle.tip.tools.ide.adapters.cloud.api.connection.CloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.generation.RuntimeMetadataGenerator;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.CloudMetadataBrowser;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;
import oracle.tip.tools.ide.adapters.cloud.impl.plugin.AbstractCloudApplicationAdapter;

public class MongoDBAdapter extends AbstractCloudApplicationAdapter {
    
    private final LoggerService logger;
    
    private final AdapterPluginContext ctx;
    
    public MongoDBAdapter(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
        ctx = adapterPluginContext;
        
        logger = adapterPluginContext.getServiceRegistry().getService(LoggerService.class);
    }

    @Override
    public CloudConnection getConnection() {
        return new MongoDBPing(ctx);
    }

    @Override
    public CloudMetadataBrowser getMetadataBrowser(CloudConnection cloudConnection) {
        return new MongoDBMetadataBrowser(cloudConnection, ctx);
    }

    @Override
    public RuntimeMetadataGenerator getRuntimeMetadataGenerator() {
        return new MongoDBMetadataGenerator(ctx);
    }

    @Override
    public String getName() {
        return Constants.ADAPTER_NAME;
    }
}
