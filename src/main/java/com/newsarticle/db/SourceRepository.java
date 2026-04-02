package com.newsarticle.db;

import com.newsarticle.model.Source;

import java.sql.*;

public class SourceRepository {

    public int findOrCreate(String apiSourceId, String name) {
        if (name == null || name.isBlank()) return 0;

        String selectSql = "SELECT id FROM source WHERE name = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(selectSql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("  Error finding source: " + e.getMessage());
        }

        String insertSql = "INSERT INTO source (api_source_id, name) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(insertSql,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, apiSourceId);
            pstmt.setString(2, name);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("  Error creating source: " + e.getMessage());
        }
        return 0;
    }

    public String findNameById(int id) {
        String sql = "SELECT name FROM source WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("  Error finding source name: " + e.getMessage());
        }
        return null;
    }
}
