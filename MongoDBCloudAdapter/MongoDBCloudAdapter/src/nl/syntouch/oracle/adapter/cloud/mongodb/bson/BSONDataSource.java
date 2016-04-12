package nl.syntouch.oracle.adapter.cloud.mongodb.bson;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.bson.Document;

public class BSONDataSource implements DataSource {
    
    private Document bson;
    private String encoding;
    
    public BSONDataSource(Document bson, String encoding) {
        this.bson = bson;
        this.encoding = encoding;
    }
    
    public Document getDocument() {
        return bson;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bson.toString().getBytes(encoding));
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new RuntimeException("No OutputStream available");
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public String getName() {
        return bson.getString("_id");
    }
}
