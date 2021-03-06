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

import java.util.Locale;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterFilter;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterUIProvider;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterUIBinding;

public class MongoDBUIProvider extends CloudAdapterUIProvider {

    @Override
    public ICloudAdapterUIBinding getCloudAdapterUIBinding(CloudAdapterFilter cloudAdapterFilter,
                                                           Locale locale) throws CloudAdapterException {
        return new MongoDBUIBinding(cloudAdapterFilter, locale);
    }

    @Override
    public String getLocalizedAdapterType(Locale locale) {
        return Constants.ADAPTER_NAME;
    }
}
