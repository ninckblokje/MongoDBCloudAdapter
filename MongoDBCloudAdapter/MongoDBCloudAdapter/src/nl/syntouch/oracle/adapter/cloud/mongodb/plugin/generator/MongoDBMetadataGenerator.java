package nl.syntouch.oracle.adapter.cloud.mongodb.plugin.generator;

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

import java.util.ArrayList;
import java.util.List;

import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.generator.wsdl.MongoDBWSDLGenerator;

import oracle.tip.tools.ide.adapters.cloud.api.generation.ArtifactGenerator;
import oracle.tip.tools.ide.adapters.cloud.api.generation.RuntimeGenerationContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.impl.generation.AbstractRuntimeMetadataGenerator;
import oracle.tip.tools.ide.adapters.cloud.impl.generation.wsdl.WSDLGenerator;

public class MongoDBMetadataGenerator extends AbstractRuntimeMetadataGenerator {
    
    private AdapterPluginContext ctx;
    
    public MongoDBMetadataGenerator(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
        ctx = adapterPluginContext;
    }

    @Override
    protected void initializeContext(RuntimeGenerationContext runtimeGenerationContext) {
        // TODO Implement this method
    }
}
