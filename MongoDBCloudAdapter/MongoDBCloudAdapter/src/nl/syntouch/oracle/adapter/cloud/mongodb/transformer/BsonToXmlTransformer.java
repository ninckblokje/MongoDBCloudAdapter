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

import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import org.bson.Document;

import org.bson.types.ObjectId;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BsonToXmlTransformer implements Transformer<Document,Node> {
    
    private String dataNamespace;
    private String rootNamespace;
    private String wrapperElementName;
    
    protected void transform(org.w3c.dom.Document xml, Node parentXml, Document bson, String namespace) {
        Set<String> keys = bson.keySet();
        for(String key: keys) {
            Object o = bson.get(key);
            
            Element elem = xml.createElementNS(namespace, key);
            parentXml.appendChild(elem);
            
            if (o instanceof String
                    || o instanceof ObjectId) {
                elem.appendChild(xml.createTextNode(o.toString()));
            } else {
                transform(xml, elem, (Document) o, namespace);
            }
        }
    }
    
    @Override
    public Node transform(Document t) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document xml = documentBuilder.newDocument();
            
            Element elem = xml.createElementNS(rootNamespace, "Document");
            if (wrapperElementName != null) {
                Element wrapperElem = xml.createElementNS(rootNamespace, wrapperElementName);
                wrapperElem.appendChild(elem);
                xml.appendChild(wrapperElem);
            } else {            
                xml.appendChild(elem);
            }
            
            transform(xml, elem, t, dataNamespace);
            
            return xml;
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }


    public BsonToXmlTransformer setDataNamespace(String dataNamespace) {
        this.dataNamespace = dataNamespace;
        return this;
    }

    public String getDataNamespace() {
        return dataNamespace;
    }

    public BsonToXmlTransformer setRootNamespace(String rootNamespace) {
        this.rootNamespace = rootNamespace;
        return this;
    }

    public String getRootNamespace() {
        return rootNamespace;
    }

    public BsonToXmlTransformer setWrapperElementName(String wrapperElementName) {
        this.wrapperElementName = wrapperElementName;
        return this;
    }

    public String getWrapperElementName() {
        return wrapperElementName;
    }
}
