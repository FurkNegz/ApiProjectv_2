package com.hello.Servlet;

import com.hello.classes.PasswordUtil;
import com.hello.classes.PostgresUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class Register extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) sb.append(line);
        String body = sb.toString();

        String username = extractJson(body, "username");
        String password = extractJson(body, "password");

        if (username == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"status\": 400, \"message\": \"username ve password zorunlu\"}");
            return;
        }

        try {
            String hashed = PasswordUtil.hashPassword(password);
            boolean saved = PostgresUtil.insertUser(username, hashed);

            if (saved) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"status\": 201, \"message\": \"Kayıt başarılı\", \"username\": \"" + username + "\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"status\": 409, \"message\": \"Kullanıcı adı zaten var\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\": 500, \"message\": \"Sunucu hatası\"}");
        }
    }

    private String extractJson(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        int colon = json.indexOf(":", idx);
        int start = json.indexOf("\"", colon) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}