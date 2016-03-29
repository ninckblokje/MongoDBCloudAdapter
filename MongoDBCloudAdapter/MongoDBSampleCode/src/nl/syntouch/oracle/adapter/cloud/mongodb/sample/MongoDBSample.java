package nl.syntouch.oracle.adapter.cloud.mongodb.sample;

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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoDBSample {
    public static void main(String[] args) {
        // connect to MongoDB at localhost:27017
        MongoClient mongoClient = new MongoClient();
        
        // switch to test db
        MongoDatabase database = mongoClient.getDatabase("test");
        
        // drop test collection (note the collection will always be created by the getCollection command)
        MongoCollection<Document> collection = database.getCollection("test");
        collection.drop();
        
        // create a new collection
        collection = database.getCollection("test");
        
        // create BSON document and save it
        Document doc = new Document()
                .append("field1", "value1");
        collection.insertOne(doc);
        System.out.println(doc.toString());
        
        Document queryDoc1 = new Document()
                .append("_id", doc.get("_id"));
        System.out.println(collection.find(queryDoc1).first().toString());
        
        Document queryDoc2 = new Document()
                .append("field1", doc.get("field1"));
        System.out.println(collection.find(queryDoc2).first().toString());
    }
}
