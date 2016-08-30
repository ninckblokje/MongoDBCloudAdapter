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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlToBsonTransformer implements Transformer<Node,Document> {
    
    private boolean removeRootNode = true;
    
    protected boolean isTextNode(Node xml) {
        return xml.hasChildNodes()
            && xml.getChildNodes().getLength() == 1
            && xml.getLastChild().getNodeType() == Node.TEXT_NODE;
    }
    
    protected Document transformNode(Node t) {
        Document bson = new Document();
        
        NodeList xmlChildren = t.getChildNodes();
        if (xmlChildren == null ||
                xmlChildren.getLength() == 0) {
            return bson;
        }
        
        for (int i=0; i<xmlChildren.getLength(); i++) {
            Node xmlChild = xmlChildren.item(i);
            
            if (isTextNode(xmlChild)) {
                bson.append(xmlChild.getLocalName(), xmlChild.getLastChild().getTextContent());
            } else if (xmlChild.getLocalName() != null) {
                bson.append(xmlChild.getLocalName(), transformNode(xmlChild));
            }
        }
        
        return bson;
    }

    @Override
    public Document transform(Node t) {
        if (removeRootNode
                && t.hasChildNodes()) {
            return transformNode(t.getLastChild());
        } else {
            return transformNode(t);
        }
    }

    public XmlToBsonTransformer setRemoveRootNode(boolean removeRootNode) {
        this.removeRootNode = removeRootNode;
        return this;
    }

    public boolean isRemoveRootNode() {
        return removeRootNode;
    }
}
