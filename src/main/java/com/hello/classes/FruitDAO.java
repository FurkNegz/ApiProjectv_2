package com.hello.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FruitDAO {

    public static int getFruitIdByName(String fruitName) throws SQLException {
        String sql = "SELECT id FROM public.fruit_table WHERE fruit_name = ?";
        try (Connection conn = PostgreConnection.getConnection();
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
        try (Connection conn = PostgreConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, fruitId);
            stmt.executeUpdate();
        }
    }
}
