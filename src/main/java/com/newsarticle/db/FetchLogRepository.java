package com.newsarticle.db;

import java.sql.*;

public class FetchLogRepository {

    public int logFetch(int pageNumber, int pageSize, int totalResults, String status, int categoryId) {
        String sql = """
                INSERT INTO fetch_log (page_number, page_size, total_results, status, category_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, pageNumber);
            pstmt.setInt(2, pageSize);
            pstmt.setInt(3, totalResults);
            pstmt.setString(4, status);
            if (categoryId > 0) {
                pstmt.setInt(5, categoryId);
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("  Error logging fetch: " + e.getMessage());
        }
        return 0;
    }
}
