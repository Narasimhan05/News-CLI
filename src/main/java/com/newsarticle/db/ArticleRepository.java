package com.newsarticle.db;

import com.newsarticle.model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for Article CRUD operations.
 * Handles saving, querying, and deleting articles in MySQL.
 * Uses INSERT IGNORE on URL to prevent duplicates.
 */
public class ArticleRepository {

    /**
     * Save an article to the database.
     * Returns true if inserted, false if duplicate (URL already exists).
     */
    public boolean save(Article article) {
        String sql = """
                INSERT IGNORE INTO articles 
                (source, author, title, description, url, url_to_image, published_at, content, category)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, article.getSource());
            pstmt.setString(2, article.getAuthor());
            pstmt.setString(3, article.getTitle());
            pstmt.setString(4, article.getDescription());
            pstmt.setString(5, article.getUrl());
            pstmt.setString(6, article.getUrlToImage());
            pstmt.setString(7, article.getPublishedAt());
            pstmt.setString(8, article.getContent());
            pstmt.setString(9, article.getCategory());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("  Error saving article: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save multiple articles. Returns count of newly inserted articles.
     */
    public int saveAll(List<Article> articles) {
        int savedCount = 0;
        for (Article article : articles) {
            if (save(article)) {
                savedCount++;
            }
        }
        return savedCount;
    }

    /**
     * Retrieve all saved articles, ordered by saved date (newest first).
     */
    public List<Article> findAll() {
        String sql = "SELECT * FROM articles ORDER BY saved_at DESC";
        return executeQuery(sql);
    }

    /**
     * Find articles by keyword in title or description.
     */
    public List<Article> findByKeyword(String keyword) {
        String sql = """
                SELECT * FROM articles 
                WHERE title LIKE ? OR description LIKE ? 
                ORDER BY published_at DESC
                """;

        List<Article> articles = new ArrayList<>();
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }
        } catch (SQLException e) {
            System.err.println("  Error searching articles: " + e.getMessage());
        }
        return articles;
    }

    /**
     * Find articles by category.
     */
    public List<Article> findByCategory(String category) {
        String sql = "SELECT * FROM articles WHERE category = ? ORDER BY published_at DESC";

        List<Article> articles = new ArrayList<>();
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, category);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }
        } catch (SQLException e) {
            System.err.println("  Error filtering articles: " + e.getMessage());
        }
        return articles;
    }

    /**
     * Find an article by its database ID.
     */
    public Article findById(int id) {
        String sql = "SELECT * FROM articles WHERE id = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToArticle(rs);
            }
        } catch (SQLException e) {
            System.err.println("  Error finding article: " + e.getMessage());
        }
        return null;
    }

    /**
     * Check if an article with the given URL already exists.
     */
    public boolean existsByUrl(String url) {
        String sql = "SELECT COUNT(*) FROM articles WHERE url = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, url);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("  Error checking article existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Delete an article by its database ID.
     */
    public boolean deleteById(int id) {
        String sql = "DELETE FROM articles WHERE id = ?";

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("  Error deleting article: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the count of saved articles.
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM articles";

        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("  Error counting articles: " + e.getMessage());
        }
        return 0;
    }

    // --- Private Helpers ---

    private List<Article> executeQuery(String sql) {
        List<Article> articles = new ArrayList<>();
        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }
        } catch (SQLException e) {
            System.err.println("  Error querying articles: " + e.getMessage());
        }
        return articles;
    }

    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(rs.getInt("id"));
        article.setSource(rs.getString("source"));
        article.setAuthor(rs.getString("author"));
        article.setTitle(rs.getString("title"));
        article.setDescription(rs.getString("description"));
        article.setUrl(rs.getString("url"));
        article.setUrlToImage(rs.getString("url_to_image"));
        article.setPublishedAt(rs.getString("published_at"));
        article.setContent(rs.getString("content"));
        article.setCategory(rs.getString("category"));
        article.setSavedAt(rs.getString("saved_at"));
        return article;
    }
}
