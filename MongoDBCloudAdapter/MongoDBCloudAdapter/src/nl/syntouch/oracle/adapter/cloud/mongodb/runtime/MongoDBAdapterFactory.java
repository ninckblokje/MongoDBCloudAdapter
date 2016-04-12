package nl.syntouch.oracle.adapter.cloud.mongodb.runtime;

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

import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.MongoDBAdapter;

import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapter;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterException;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterFactory;

public class MongoDBAdapterFactory implements CloudApplicationAdapterFactory {
    
    @Override
    public CloudApplicationAdapter createAdapter(AdapterPluginContext adapterPluginContext) throws CloudApplicationAdapterException {
        return new MongoDBAdapter(adapterPluginContext);
    }
}
