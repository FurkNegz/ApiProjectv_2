package com.hello.classes;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.*;

public class PostgresUtil {
    private static final String URL      =System.getenv("URLNAME");
    private static final String USER     = System.getenv("USERNAME");
    private static final String PASSWORD = System.getenv("PASSWORD");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Postgresql bulunmadı", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean insertUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO public.user_table(username, password, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
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

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUsername);
            stmt.setString(2, hashedPass);
            stmt.setString(3, username);

            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean checkUser(String username, String password) throws SQLException {
        String sql = "SELECT password FROM public.user_table WHERE username = ?";
        try (Connection conn = getConnection();
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

    public static void saveToken(String token, String username, String ipAddress) throws SQLException {
        int userId = getUserIdByUsername(username);
        if (userId == -1) return;

        String sql = "INSERT INTO public.token_table (user_id, token, ip_address, created_at, expires_at) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 minutes') "
                + "ON CONFLICT (user_id) DO UPDATE SET "
                + "token = EXCLUDED.token, "
                + "ip_address = EXCLUDED.ip_address, "
                + "created_at = CURRENT_TIMESTAMP, "
                + "expires_at = CURRENT_TIMESTAMP + INTERVAL '30 minutes'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, token);
            stmt.setString(3, ipAddress);
            stmt.executeUpdate();
        }
    }

    public static void deleteToken(String token) throws SQLException {
        String sql = "DELETE FROM public.token_table WHERE token = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        }
    }

    public static String getUserNameByToken(String token) throws SQLException {
        String sql = "SELECT username FROM public.user_table u "
                + "JOIN public.token_table t ON u.id = t.user_id "
                + "WHERE t.token = ? AND t.expires_at > CURRENT_TIMESTAMP";
        try (Connection conn = getConnection();
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
        try (Connection conn = getConnection();
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

    public static int getUserIdByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM public.user_table WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    public static int getFruitIdByName(String fruitName) throws SQLException {
        String sql = "SELECT id FROM public.fruit_table WHERE fruit_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fruitName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    public static void recordFruitRequest(int userId, int fruitId) throws SQLException {
        String sql = "INSERT INTO public.taken_fruit_table (user_id, fruit_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, fruitId);
            stmt.executeUpdate();
        }
    }

}
//pdo update  try catch içerisinde error table değiştilebilir olsun

