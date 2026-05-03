package com.hello.classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreConnection {

    private static final String URL      = System.getenv("URLNAME");
    private static final String USER     = System.getenv("USERNAME");
    private static final String PASSWORD = System.getenv("PASSWORD");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Postgresql driver bulunamadı", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}