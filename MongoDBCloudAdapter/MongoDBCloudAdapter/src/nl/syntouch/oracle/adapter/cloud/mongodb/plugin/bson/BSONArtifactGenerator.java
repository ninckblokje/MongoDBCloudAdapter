package nl.syntouch.oracle.adapter.cloud.mongodb.plugin.bson;

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

import java.nio.file.Files;
import java.nio.file.Paths;

import java.nio.file.StandardOpenOption;

import nl.syntouch.oracle.adapter.cloud.mongodb.definition.Constants;

import oracle.tip.tools.ide.adapters.cloud.api.generation.ArtifactGenerator;
import oracle.tip.tools.ide.adapters.cloud.api.generation.DefaultRuntimeGenerationContext;
import oracle.tip.tools.ide.adapters.cloud.api.plugin.CloudApplicationAdapterException;

import org.bson.Document;

public class BSONArtifactGenerator implements ArtifactGenerator {

    public void generate(DefaultRuntimeGenerationContext defaultRuntimeGenerationContext) throws CloudApplicationAdapterException {
        try {
            String dirLocation = (String) defaultRuntimeGenerationContext.getContextObject("generatedBaseDir");
            URI dirUri = new URI(dirLocation);
            
            String fileName = (String) defaultRuntimeGenerationContext.getContextObject(Constants.CONTEXT_SAMPLE_FILE_KEY);
            Document bson = (Document) defaultRuntimeGenerationContext.getContextObject(Constants.CONTEXT_PARSE_DOCUMENT_KEY);
            
            File dir = new File(dirUri);
            dir.mkdirs();
            
            File f = new File(dir, fileName);
        
            Files.write(Paths.get(f.toURI()), bson.toJson().getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException | URISyntaxException ex) {
            throw new CloudApplicationAdapterException(ex);
        }
    }
}
