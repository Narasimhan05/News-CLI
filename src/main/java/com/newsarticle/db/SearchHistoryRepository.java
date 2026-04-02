package com.newsarticle.db;

import java.sql.*;

public class SearchHistoryRepository {

    public void logSearch(String keyword, String fromDate, String toDate, int resultCount, int categoryId) {
        String sql = """
                INSERT INTO history (keyword, from_date, to_date, result_count, category_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, keyword);
            pstmt.setString(2, fromDate);
            pstmt.setString(3, toDate);
            pstmt.setInt(4, resultCount);
            if (categoryId > 0) {
                pstmt.setInt(5, categoryId);
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("  Error logging search history: " + e.getMessage());
        }
    }
}
