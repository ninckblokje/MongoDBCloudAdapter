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

import org.bson.Document;

import org.bson.types.ObjectId;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlToBsonTransformer implements Transformer<Node,Document> {
    
    private boolean removeRootNode = true;
    
    protected boolean isObjectIdNode(Node xml) {
        return isTextNode(xml)
            && "_id".equals(xml.getLocalName());
    }
    
    protected boolean isTextNode(Node xml) {
        return xml.hasChildNodes()
            && xml.getChildNodes().getLength() == 1
            && xml.getLastChild().getNodeType() == Node.TEXT_NODE;
    }
    
    protected void transformNode(Node t, Document bson) {
        NodeList xmlChildren = t.getChildNodes();
        if (xmlChildren == null ||
                xmlChildren.getLength() == 0) {
            return;
        }
        
        for (int i=0; i<xmlChildren.getLength(); i++) {
            Node xmlChild = xmlChildren.item(i);
            
            if (isObjectIdNode(xmlChild)) {
                bson.append("_id", new ObjectId(xmlChild.getLastChild().getTextContent()));
            } else if (isTextNode(xmlChild)) {
                bson.append(xmlChild.getLocalName(), xmlChild.getLastChild().getTextContent());
            } else if (xmlChild.getLocalName() != null) {
                Document subBson = new Document();
                
                transformNode(xmlChild, subBson);
                bson.append(xmlChild.getLocalName(), subBson);
            }
        }
    }

    @Override
    public Document transform(Node t) {
        Document bson = new Document();
        
        if (removeRootNode
                && t.hasChildNodes()) {
            for (int i=0; i<t.getChildNodes().getLength(); i++) {
                Node childXml = t.getChildNodes().item(i);
                transformNode(childXml, bson);
            }
        } else {
            transformNode(t, bson);
        }
        
        return bson;
    }

    public XmlToBsonTransformer setRemoveRootNode(boolean removeRootNode) {
        this.removeRootNode = removeRootNode;
        return this;
    }

    public boolean isRemoveRootNode() {
        return removeRootNode;
    }
}
