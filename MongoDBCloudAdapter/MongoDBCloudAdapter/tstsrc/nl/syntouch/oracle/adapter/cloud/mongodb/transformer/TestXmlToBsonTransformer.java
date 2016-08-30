package nl.syntouch.oracle.adapter.cloud.mongodb.transformer;

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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import org.bson.Document;

import org.junit.Assert;
import org.junit.Test;

import org.xml.sax.SAXException;

public class TestXmlToBsonTransformer {
    
    private static final String resourcePath = "/nl/syntouch/oracle/adapter/cloud/mongodb/transformer";
    
    protected void compareBson(Document bson, ExpectedBsonField[] expectations) {
        Assert.assertEquals(expectations.length, bson.keySet().size());
        
        for (ExpectedBsonField expectation: expectations) {
            Object o = bson.get(expectation.getName());
            
            Assert.assertNotNull(o);
            Assert.assertEquals(expectation.getType(), o.getClass());
            
            if (o instanceof Document) {
                compareBson((Document) o, (ExpectedBsonField[]) expectation.getValue());
            } else {
                Assert.assertEquals(expectation.getValue(), o);
            }
        }
    }
    
    protected org.w3c.dom.Document readInputFile(String fileName) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        return builder.parse(this.getClass().getResourceAsStream(resourcePath + "/" + fileName));
    }
    
    @Test
    public void testTransformSimple() throws Exception {
        org.w3c.dom.Document xml = readInputFile("test1.xml");
        Document bson = new XmlToBsonTransformer().transform(xml);
        
        Assert.assertNotNull(bson);
        
        ExpectedBsonField expectation = new ExpectedBsonField()
            .setName("rootElement")
            .setType(Document.class)
            .setValue(
                new ExpectedBsonField[] {
                    new ExpectedBsonField()
                        .setName("elementOne")
                        .setType(String.class)
                        .setValue("value1"),
                    new ExpectedBsonField()
                        .setName("elementTwo")
                        .setType(Document.class)
                        .setValue(new ExpectedBsonField[] {
                            new ExpectedBsonField()
                                .setName("elementThree")
                                .setType(String.class)
                                .setValue("value2")
                        })
                });
        
        compareBson(bson, new ExpectedBsonField[] {expectation});
    }
}

class ExpectedBsonField {
    private String name;
    private Class type;
    private Object value;


    public ExpectedBsonField setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public ExpectedBsonField setType(Class type) {
        this.type = type;
        return this;
    }

    public Class getType() {
        return type;
    }

    public ExpectedBsonField setValue(Object value) {
        this.value = value;
        return this;
    }

    public Object getValue() {
        return value;
    }
}
