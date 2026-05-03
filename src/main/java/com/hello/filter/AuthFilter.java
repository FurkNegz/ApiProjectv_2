package com.hello.filter;

import com.hello.classes.TokenDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.equals("/login") || path.equals("/register") || path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractToken(req);

        try {
            if (token == null) {
                sendUnauthorized(res, "Token bulunamadı");
                return;
            }

            if (TokenDAO.isTokenExpired(token)) {
                sendUnauthorized(res, "Token süresi doldu");
                return;
            }

            String username = TokenDAO.getUserNameByToken(token);
            if (username == null) {
                sendUnauthorized( res, "Geçersiz token");
                return;
            }

            req.setAttribute("username", username);
            chain.doFilter(request, response);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractToken(HttpServletRequest req) {
        // Authorization: Bearer header'ına bak (Postman için)
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2. Cookie'ye bak (tarayıcı için)
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("auth_token".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse res, String message)
            throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"status\": 401, \"error\": \"" + message + "\"}");
    }
}