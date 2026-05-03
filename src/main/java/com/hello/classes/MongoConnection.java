package com.hello.classes;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {

    private static final String CONNECTION_STRING =
            "mongodb://loguser:1234@localhost:27018/?authSource=admin";

    private static final String DATABASE_NAME = "log_db";

    private static final MongoClient mongoClient =
            MongoClients.create(CONNECTION_STRING);

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase(DATABASE_NAME);
    }
}