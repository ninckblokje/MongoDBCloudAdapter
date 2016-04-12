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

import java.awt.Frame;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.ide.Context;
import oracle.ide.model.Project;

import oracle.tip.tools.adapters.cloud.api.ICloudAdapterUIBinding;
import oracle.tip.tools.ide.adapters.designtime.adapter.JcaEndpointAbstract;
import oracle.tip.tools.ide.fabric.api.EndpointController;
import oracle.tip.tools.ide.fabric.api.EndpointInfo;
import oracle.tip.tools.ide.fabric.api.SCAEndpointOptions;
import oracle.tip.tools.ide.portablewizard.controller.WizardController;
import oracle.tip.tools.ide.portablewizard.controller.WizardUIHandler;
import oracle.tip.tools.ide.portablewizard.controller.WizardUIHandlerForJcaAdapters;

public class MongoDBJcaEndpoint extends JcaEndpointAbstract {

    @Override
    public EndpointInfo runCreateWizard(Frame frame, Project project, EndpointController endpointController,
                                        SCAEndpointOptions scaEndpointOptions, boolean isExternalReference) throws Exception {
        Context jcontext = Context.newIdeContext();
        WizardUIHandler uiHandler = new WizardUIHandlerForJcaAdapters(frame, Constants.ADAPTER_NAME, endpointController, this.endpointInfo, scaEndpointOptions, isExternalReference);

        ICloudAdapterUIBinding iCloudAdapterUIBinding = new MongoDBUIBinding(uiHandler.getFilter(), uiHandler.getLocale());

        String serviceName = WizardController.displayDialog(frame, jcontext, jdevProject, this.controller, scaEndpointOptions, null,
                                           null, isExternalReference, iCloudAdapterUIBinding, uiHandler);
        if (serviceName == null) {
            return null;
        }
        EndpointInfo endpointInfo = this.controller.getEndpointInfo(serviceName);

        return endpointInfo;
    }

    @Override
    public EndpointInfo runUpdateWizard(Frame frame, Project project, EndpointController endpointController,
                                        SCAEndpointOptions scaEndpointOptions, boolean isExternalReference) throws Exception {
        return runCreateWizard(frame, project, endpointController, scaEndpointOptions, isExternalReference);
    }
}
