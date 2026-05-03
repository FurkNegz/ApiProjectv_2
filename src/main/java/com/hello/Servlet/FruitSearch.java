package com.hello.Servlet;

import com.hello.classes.ErrorLogUtil;
import com.hello.classes.FruitDAO;
import com.hello.classes.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

public class FruitSearch extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) sb.append(line);
        String body = sb.toString();

        String fruitName = extractJson(body, "fruit_name");
        String username = (String) req.getAttribute("username");

        if (fruitName == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"status\": 400, \"message\": \"fruit_name zorunlu\"}");
            return;
        }

        try {
            int fruitId = FruitDAO.getFruitIdByName(fruitName);
            int userId = UserDAO.getUserIdByUsername(username);

            if (fruitId != -1) {
                FruitDAO.recordFruitRequest(userId, fruitId);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"status\": 200, \"message\": \"" + fruitName + " bulundu ve kaydedildi.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"status\": 404, \"message\": \"" + fruitName + " veritabanında yok.\"}");
            }

        } catch (SQLException e) {
            ErrorLogUtil.errorLog(e,username,req);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\": 500, \"message\": \"Sunucu hatası\"}");
        }catch (Exception e) {
            ErrorLogUtil.errorLog(e,username,req);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\": 500, \"message\": \"bilinmeyen hata oluştu\"}");
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