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

import com.sun.org.apache.xml.internal.serialize.OutputFormat;

import java.io.IOException;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamResult;

import org.bson.Document;

import org.bson.types.ObjectId;

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Node;

import org.xml.sax.SAXException;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.DifferenceEvaluators;

public class TestBsonToXmlTransformer {
    
    private static final String resourcePath = "/nl/syntouch/oracle/adapter/cloud/mongodb/transformer";
    
    protected org.w3c.dom.Document readInputFile(String fileName) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        return builder.parse(this.getClass().getResourceAsStream(resourcePath + "/" + fileName));
    }
    


    protected String toString(Node xml) throws TransformerConfigurationException, TransformerException {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(xml), new StreamResult(sw));
        return sw.toString();
    }
    
    @Test
    public void testTransformSimple() throws Exception {
        Document bson = new Document()
            .append("_id", new ObjectId())
            .append("rootElement", new Document()
                    .append("elementOne", "value1")
                    .append("elementTwo", new Document()
                            .append("elementThree", "value2")));
        
        Node xml = new BsonToXmlTransformer()
            .setNamespace("")
            .transform(bson);
        Assert.assertNotNull(xml);
        
        Diff diff = DiffBuilder
            .compare(readInputFile("testBsonToXmlSimple.xml"))
            .withTest(xml)
            .checkForSimilar()
            .ignoreWhitespace()
            .withDifferenceEvaluator(
                DifferenceEvaluators.chain(
                    DifferenceEvaluators.Default,
                    new ObjectIdDifferenceEvaluator()))
            .build();
        
        for (Difference foundDiff: diff.getDifferences()) {
            System.err.println(foundDiff);
        }
        
        Assert.assertFalse(diff.hasDifferences());
    }
}

class ObjectIdDifferenceEvaluator implements DifferenceEvaluator {
    
    protected Node getControlNode(Comparison comparison) {
        return comparison.getControlDetails().getTarget();
    }
    
    protected Node getTestNode(Comparison comparison) {
        return comparison.getTestDetails().getTarget();
    }
    
    protected boolean isEvaluatorApplicable(Comparison comparison) {
        return comparison.getControlDetails().getTarget() != null
            && comparison.getControlDetails().getTarget().getNodeType() == Node.TEXT_NODE
            && "_id".equals(comparison.getControlDetails().getTarget().getParentNode().getLocalName())
            && comparison.getTestDetails().getTarget() != null
            && comparison.getTestDetails().getTarget().getNodeType() == Node.TEXT_NODE
            && "_id".equals(comparison.getTestDetails().getTarget().getParentNode().getLocalName());
    }

    @Override
    public ComparisonResult evaluate(Comparison comparison, ComparisonResult comparisonResult) {
        if (!isEvaluatorApplicable(comparison)) {
            return comparisonResult;
        }
        
        Node controlNode = getControlNode(comparison);
        Node testNode = getTestNode(comparison);
        
        if ("*".equals(controlNode.getTextContent())) {
            return ComparisonResult.SIMILAR;
        } else if (controlNode.getTextContent().equals(testNode.getTextContent())) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT;
        }
    }
}
