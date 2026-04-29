package com.hello.Servlet;

import com.hello.classes.PostgresUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

public class Login extends HttpServlet {



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

        String ipAddress = req.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) ipAddress = req.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) ipAddress = "127.0.0.1";

        try {
            boolean valid = PostgresUtil.checkUser(username, password);

            if (valid) {
                String token = java.util.UUID.randomUUID().toString();
                PostgresUtil.saveToken(token, username, ipAddress);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{"
                        + "\"status\": 200,"
                        + "\"message\": \"Giriş başarılı\","
                        + "\"token\": \"" + token + "\","
                        + "\"username\": \"" + username + "\","
                        + "\"ip\": \"" + ipAddress + "\""
                        + "}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"status\": 401, \"message\": \"Hatalı kullanıcı adı veya şifre\"}");
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