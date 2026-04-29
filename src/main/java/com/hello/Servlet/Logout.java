package com.hello.Servlet;

import com.hello.classes.PostgresUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/logout")
public class Logout extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = null;

        // Bearer header'dan al
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // Cookie'den al
        if (token == null && req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("auth_token".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }

        try {
            if (token != null) PostgresUtil.deleteToken(token);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Cookie'yi temizle
        Cookie expired = new Cookie("auth_token", "");
        expired.setMaxAge(0);
        resp.addCookie(expired);

        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\": 200, \"message\": \"Çıkış başarılı\"}");
    }
}