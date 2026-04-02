package com.newsarticle.db;

import com.newsarticle.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public int findIdByName(String name) {
        String sql = "SELECT id FROM category WHERE name = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("  Error finding category: " + e.getMessage());
        }
        return 0;
    }

    public List<Category> findAll() {
        String sql = "SELECT * FROM category ORDER BY name";
        List<Category> categories = new ArrayList<>();

        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setDescription(rs.getString("description"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("  Error listing categories: " + e.getMessage());
        }
        return categories;
    }
}
