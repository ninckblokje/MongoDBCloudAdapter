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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import java.util.List;
import java.util.Locale;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.tip.tools.adapters.cloud.api.CloudAdapterException;
import oracle.tip.tools.adapters.cloud.api.CloudAdapterPageState;
import oracle.tip.tools.adapters.cloud.api.ICloudAdapterPage;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;
import oracle.tip.tools.presentation.uiobjects.sdk.EditField;
import oracle.tip.tools.presentation.uiobjects.sdk.SelectObject;
import oracle.tip.tools.presentation.uiobjects.sdk.UIError;
import oracle.tip.tools.presentation.uiobjects.sdk.UIFactory;
import oracle.tip.tools.presentation.uiobjects.sdk.UIObject;

import org.bson.Document;

public class MongoDBOperationsPage extends AbstractMongoDBPage implements ICloudAdapterPage {
    
    private static final String bsonDescriptionKey = "UI.BSON.DESCRIPTION";
    private static final String bsonEditField = "bsonEF";
    private static final String bsonHelpKey = "UI.BSON.HELP";
    private static final String bsonLabelKey = "UI.BSON.LABEL";
    
    private static final String modeDescriptionKey = "UI.MODE.DESCRIPTION";
    private static final String modeEditField = "modeEF";
    private static final String modeHelpKey = "UI.MODE.HELP";
    private static final String modeLabelKey = "UI.MODE.LABEL";
    
    private static final String modeStructured = "structured";
    private static final String modeUnstructured = "unstructured";
    private static final String[] modeArray = {modeStructured, modeUnstructured};
    
    private static final String operationsDescriptionKey = "UI.OPERATIONS.DESCRIPTION";
    private static final String operationsEditField = "operationsEF";
    private static final String operationsHelpKey = "UI.OPERATIONS.HELP";
    private static final String operationsLabelKey = "UI.OPERATIONS.LABEL";
    
    private static final String pageId = "operations";
    private static final String pageNameKey = "PAGE.OPERATIONS.NAME";
    private static final String pageTitleKey = "PAGE.OPERATIONS.TITLE";
    private static final String pageWelcomeKey = "PAGE.OPERATIONS.WELCOME";
    
    public MongoDBOperationsPage(AdapterPluginContext adapterPluginContext) {
        super(adapterPluginContext);
    }
    
    @Override
    public String getPageId() {
        return pageId;
    }

    @Override
    public String getPageName() {
        return getText(pageNameKey);
    }

    @Override
    public String getPageTitle() {
        return getText(pageTitleKey);
    }

    @Override
    public String getHelpId() {
        return pageId;
    }

    @Override
    public String getWelcomeText() {
        return getText(pageWelcomeKey);
    }

    @Override
    public LinkedList<EditField> getPageEditFields() throws CloudAdapterException {
        LinkedList<EditField> fields = new LinkedList<>();
        
        List<String> operationNames = new ArrayList<>();
        for (CloudOperationNode operation: getMetadataBrowser().getOperations()) {
            operationNames.add(operation.getName());
        }
        String[] operationNamesArray = {};
        operationNamesArray = operationNames.toArray(operationNamesArray);
        
        // String[] values, String[] formattedValues, String selected, int displayMode, boolean hasEvents
        UIObject operationsSelect = UIFactory.createSelectObject(operationNamesArray, operationNamesArray,
                                                                 operationNamesArray[0], 2, true);
        // String name, String label, String description, boolean isrequired, boolean isDisabled, UIObject object, EditField.LabelFieldLayout labelFieldLayout, String helpText, EditField.LabelFieldAlignment oneRowlabelFieldAlignment
        fields.add(UIFactory.createEditField(operationsEditField, getText(operationsLabelKey),
                                             getText(operationsDescriptionKey), true, false,
                                             operationsSelect, EditField.LabelFieldLayout.ONE_ROW_LAYOUT,
                                             getText(operationsHelpKey), EditField.LabelFieldAlignment.LEFT_LEFT));
        
        UIObject modeSelect = UIFactory.createSelectObject(modeArray, modeArray,
                                                                 modeArray[0], 2, true);
        fields.add(UIFactory.createEditField(modeEditField, getText(modeLabelKey),
                                             getText(modeDescriptionKey), true, false,
                                             modeSelect, EditField.LabelFieldLayout.ONE_ROW_LAYOUT,
                                             getText(modeHelpKey), EditField.LabelFieldAlignment.LEFT_LEFT));
        
        Document bson = (Document) getContext().getContextObject(Constants.CONTEXT_SAMPLE_DOCUMENT_KEY);
        UIObject bsonTextArea = UIFactory.createTextArea(bson.toJson(), 40, 15, false);
        fields.add(UIFactory.createEditField(bsonEditField, getText(bsonLabelKey),
                                             getText(bsonDescriptionKey), false, false,
                                             bsonTextArea, EditField.LabelFieldLayout.ONE_ROW_LAYOUT,
                                             getText(bsonHelpKey), EditField.LabelFieldAlignment.LEFT_LEFT));
        
        return fields;
    }

    @Override
    public LinkedHashMap<String, ICloudAdapterPage> getChildrenEditPages() {
        // TODO Implement this method
        return null;
    }

    @Override
    public CloudAdapterPageState updateBackEndModel(LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                                                    LinkedList<EditField> currentPageFields) throws CloudAdapterException {
        return null;
    }

    @Override
    public CloudAdapterPageState getUpdatedEditPages(LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                                                     LinkedList<EditField> currentPageFields,
                                                     String fieldName) throws CloudAdapterException {
        getLogger().log(LoggerService.Level.DEBUG, "Received event from [" + fieldName + "]");
        
        boolean refresh = false;
        EditField eventSource = findEditField(currentPageFields, fieldName);
        switch (fieldName) {
            case modeEditField:
                refresh = handleModeEvent(eventSource, currentPageFields);
                break;
            default:
                getLogger().log(LoggerService.Level.DEBUG, "Ignoring event from [" + fieldName + "]");
                break;
        };
        
        CloudAdapterPageState newState = new CloudAdapterPageState(refresh, wizardPages, currentPageFields);
        return newState;
    }

    @Override
    public LinkedHashMap<String, UIError[]> validatePage(LinkedHashMap<String, ICloudAdapterPage> wizardPages,
                                                         LinkedList<EditField> currentPageFields) throws CloudAdapterException {
        return null;
    }
}
