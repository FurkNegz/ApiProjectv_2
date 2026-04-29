/*
package com.hello;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hello.classes.PostgresUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.hello.classes.PostgresUtil.getConnection;

public class helloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Hello, Servlet!</h1>");
        out.println("<form method='post' action=''>");
        out.println("Input: <input type='text' name='myInput' />");
        out.println("<button type='submit'>Gönder</button>");
        out.println("</form>");
        try {
            PostgresUtil.listTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = getConnection()) {

            System.out.println("DB URL: " + conn.getMetaData().getURL());

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT inet_server_addr(), inet_server_port(), current_database()"
            );

            while (rs.next()) {
                System.out.println(
                        rs.getString(1) + " | " +
                                rs.getString(2) + " | " +
                                rs.getString(3)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        String inputValue = req.getParameter("myInput");
        out.println("<h2>POST isteği alındı!</h2>");
        out.println("<p>Girilen değer: " + inputValue + "</p>");
        try {
            PostgresUtil.insertValue(inputValue);
            //PostgresUtil.listTables();
            out.println("<p>Veri kaydedildi: " + inputValue + "</p>");
        } catch (Exception e) {
            e.printStackTrace(out);
        }

    }

}
*/