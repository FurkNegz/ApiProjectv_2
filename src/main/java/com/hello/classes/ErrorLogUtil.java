package com.hello.classes;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.http.HttpServletRequest;
import org.bson.Document;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class ErrorLogUtil {

    public static void errorLog(Exception e, String username, HttpServletRequest req) {

        try {
            MongoDatabase db = MongoConnection.getDatabase();
            MongoCollection<Document> col = db.getCollection("error_logs");

            Document doc = new Document("error_message", e.getMessage())
                    .append("stack_trace", getStackTrace(e))
                    .append("endpoint", req.getRequestURI())
                    .append("username", username)
                    .append("exception_type", e.getClass().getName())
                    .append("created_at", new java.util.Date());

            col.insertOne(doc);

            System.out.println("mongo log inserted");

        } catch (Exception ex) {
            System.out.println("Mongo log FAILED: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
