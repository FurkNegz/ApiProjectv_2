package com.hello.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenDAO {
    public static void saveToken(String token, String username, String ipAddress) throws SQLException {
        int userId = UserDAO.getUserIdByUsername(username);
        if (userId == -1) return;

        String sql = "INSERT INTO public.token_table (user_id, token, ip_address, created_at, expires_at) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 minutes') "
                + "ON CONFLICT (user_id) DO UPDATE SET "
                + "token = EXCLUDED.token, "
                + "ip_address = EXCLUDED.ip_address, "
                + "created_at = CURRENT_TIMESTAMP, "
                + "expires_at = CURRENT_TIMESTAMP + INTERVAL '30 minutes'";

        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, token);
            stmt.setString(3, ipAddress);
            stmt.executeUpdate();
        }
    }

    public static void deleteToken(String token) throws SQLException {
        String sql = "DELETE FROM public.token_table WHERE token = ?";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        }
    }

    public static String getUserNameByToken(String token) throws SQLException {
        String sql = "SELECT username FROM public.user_table u "
                + "JOIN public.token_table t ON u.id = t.user_id "
                + "WHERE t.token = ? AND t.expires_at > CURRENT_TIMESTAMP";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("username");
            }
        }
        return null;
    }

    public static boolean isTokenExpired(String token) throws SQLException {
        String sql = "SELECT expires_at FROM public.token_table WHERE token = ?";
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // token var ama dolmuş
                    return rs.getTimestamp("expires_at").before(new java.util.Date());
                }
            }
        }
        return false;
    }
}
