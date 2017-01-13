package nl.syntouch.oracle.adapter.cloud.mongodb.plugin.metadata;

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

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;

import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Properties;

import javax.activation.DataSource;

import nl.syntouch.oracle.adapter.cloud.mongodb.bson.BSONUtil;
import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.bson.BSONDataSource;
import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import nl.syntouch.oracle.adapter.cloud.mongodb.plugin.MongoDBPing;

import oracle.tip.tools.ide.adapters.cloud.api.connection.CloudConnection;
import oracle.tip.tools.ide.adapters.cloud.api.metadata.MetadataParser;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudAPINode;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudApplicationModel;
import oracle.tip.tools.ide.adapters.cloud.api.model.CloudOperationNode;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.AdapterPluginContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterException;
import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;
import oracle.tip.tools.ide.adapters.cloud.impl.metadata.wsdl.AbstractMetadataBrowser;

import org.bson.Document;

public class MongoDBMetadataBrowser extends AbstractMetadataBrowser {
    
    private final LoggerService logger;
    
    private AdapterPluginContext ctx;
    
    public MongoDBMetadataBrowser(CloudConnection cloudConnection, AdapterPluginContext adapterPluginContext) {
        super(cloudConnection, adapterPluginContext);
        ctx = adapterPluginContext;
        
        logger = adapterPluginContext.getServiceRegistry().getService(LoggerService.class);
    }
    
    protected Document getSampleDocument(AdapterPluginContext adapterPluginContext) {
        CloudConnection cloudConnection = (CloudConnection) adapterPluginContext.getContextObject("adapterConnection");
        if (!(cloudConnection instanceof MongoDBPing)) {
            throw new RuntimeException("CloudConnection must be an instance of MongoDBConnection");
        }
        
        MongoDBPing connection = (MongoDBPing) cloudConnection;
        try {
            connection.connect();
            Document sample = connection.getCollection().find().first();
            if (sample == null) {
                sample = BSONUtil.getUnstructuredDocument();
            }
            
            return sample;
        } finally {
            connection.close();
        }
    }
    
    protected Document getStoredDocument(AdapterPluginContext adapterPluginContext) throws CloudApplicationAdapterException {
        try {
            String dirLocation = (String) adapterPluginContext.getContextObject("generatedBaseDir");
            URI dirUri = new URI(dirLocation);
            
            String fileName = (String) adapterPluginContext.getContextObject(Constants.CONTEXT_SAMPLE_FILE_KEY);
            if (fileName == null) return null;
            
            File dir = new File(dirUri);
            File f = new File(dir, fileName);
            
            byte[] data = Files.readAllBytes(Paths.get(f.toURI()));
            return Document.parse(new String(data, "UTF-8"));
        } catch (IOException | URISyntaxException ex) {
            throw new CloudApplicationAdapterException(ex);
        }
    }

    @Override
    protected String getVersion() {
        return Constants.VERSION;
    }

    @Override
    protected boolean filterByAPINodes() {
        return false;
    }

    @Override
    protected List<CloudAPINode> getAPINodes() {
        return Collections.emptyList();
    }

    @Override
    protected List<MetadataParser> getMetadataParsers() {
        List<MetadataParser> parsers = new ArrayList<>();
        
        parsers.add(new MongoDBMetadataParser(ctx));
        
        return parsers;
    }

    @Override
    protected void parseMetadata(MetadataParser metadataParser,
                                 AdapterPluginContext adapterPluginContext) throws CloudApplicationAdapterException {
        CloudApplicationModel cloudApplicationModel = getModel();
        Document bson = getStoredDocument(adapterPluginContext);
        if (bson == null) {
            bson = getSampleDocument(adapterPluginContext);
        }
        
        adapterPluginContext.setContextObject(Constants.CONTEXT_SAMPLE_DOCUMENT_KEY, bson);
        DataSource dataSource = new BSONDataSource(bson, StandardCharsets.UTF_8.toString());
        Properties props = new Properties();
        
        metadataParser.parse(dataSource, cloudApplicationModel, props);
    }

    @Override
    public CloudOperationNode getOperation(String operationName) {
        List<CloudOperationNode> operations = super.getOperations();
        
        for (CloudOperationNode operation: operations) {
            if (operationName.equals(operation.getName())) {
                return operation;
            }
        }
        
        return null;
    }
}
