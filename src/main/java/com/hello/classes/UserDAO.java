package com.hello.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {


    public static boolean insertUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO public.user_table(username, password, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    public static boolean updateUser(String username, String password,
                                     String newUsername, String newPassword) throws SQLException {

        boolean result = checkUser(username, password);
        if (!result) {
            return false;
        }

        String hashedPass = PasswordUtil.hashPassword(newPassword);
        String sql = "UPDATE public.user_table SET username = ?, password = ? WHERE username = ?";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUsername);
            stmt.setString(2, hashedPass);
            stmt.setString(3, username);

            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean checkUser(String username, String password) throws SQLException {
        String sql = "SELECT password FROM public.user_table WHERE username = ?";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return PasswordUtil.checkPassword(password, rs.getString("password"));
                }
            }
        }
        return false;
    }
    public static int getUserIdByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM public.user_table WHERE username = ?";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

}
