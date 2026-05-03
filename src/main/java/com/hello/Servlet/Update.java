package com.hello.Servlet;

import com.hello.classes.ErrorLogUtil;
import com.hello.classes.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

public class Update extends HttpServlet {

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
        String newUsername = extractJson(body, "new_username");
        String newPassword = extractJson(body, "new_password");

        if (username == null || password == null ||
                newUsername == null || newPassword == null) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"status\":400,\"message\":\"tüm alanlar zorunlu\"}");
            return;
        }

        try {
            boolean updated = UserDAO.updateUser(username, password, newUsername, newPassword);

            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"status\":200,\"message\":\"Güncelleme başarılı\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"status\":401,\"message\":\"Kullanıcı adı veya şifre hatalı\"}");
            }

        } catch (SQLException e) {

            ErrorLogUtil.errorLog(e, username, req);

            // duplicate username vs
            if ("23505".equals(e.getSQLState())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"status\":409,\"message\":\"Yeni kullanıcı adı zaten kullanılıyor\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"status\":500,\"message\":\"Veritabanı hatası\"}");
            }

        } catch (Exception e) {

            ErrorLogUtil.errorLog(e, username, req);

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":500,\"message\":\"Beklenmeyen hata\"}");
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