package com.hello.classes;

import java.sql.*;

public class PostgresUtil {
    private static final String URL      = "jdbc:postgresql://localhost:5432/local";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "!!!lol2631";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver bulunamadı!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);

    }
    public static void listTables() throws SQLException {
        String sql = "SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname = 'public'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Public şemasındaki tablolar:");
            while (rs.next()) {
                System.out.println(rs.getString("tablename"));
            }
        }
    }


    public static void insertValue(String value) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO public.test_table(value) VALUES (?)")) {
            stmt.setString(1, value);
            stmt.executeUpdate();
        }
    }
    public static boolean insertUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO public.user_table(username, password, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean checkUser(String username, String password) throws SQLException {
        String sql = "SELECT password FROM public.user_table WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return PasswordUtil.checkPassword(password, storedHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void saveToken(String token, String username) throws SQLException {
        int userId = getUserIdByUsername(username);
        if (userId != -1) {
            String upsertSql = "INSERT INTO public.token_table (user_id, token, created_at) " +
                    "VALUES (?, ?, CURRENT_TIMESTAMP) " +
                    "ON CONFLICT (user_id) " +
                    "DO UPDATE SET token = EXCLUDED.token, created_at = CURRENT_TIMESTAMP,"+
                                    "expires_at = CURRENT_TIMESTAMP + INTERVAL '2 minutes'";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(upsertSql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, token);
                stmt.executeUpdate();
            }
        } else {
            System.out.println("Hata: Kullanıcı bulunamadığı için token kaydedilemedi -> " + username);
        }
    }
    public static int getUserIdByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM public.user_table WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }
    public static  String getUserNameByToken(String token) throws SQLException {
        String sql= "SELECT username FROM public.user_table AS u JOIN public.token_table AS t ON u.id=t.user_id " +
                "WHERE t.token = ? AND t.expires_at > CURRENT_TIMESTAMP" ;
        try(Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }
}

