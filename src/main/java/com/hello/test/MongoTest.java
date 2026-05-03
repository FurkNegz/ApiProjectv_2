
package com.hello.test;

import com.hello.classes.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoTest {

    public static void main(String[] args) {

        MongoDatabase db = MongoConnection.getDatabase();
        MongoCollection<Document> col = db.getCollection("error_logs");

        Document doc = new Document("test", "HELLO MONGO");

        col.insertOne(doc);

        System.out.println("TEST INSERT OK");
    }
}
