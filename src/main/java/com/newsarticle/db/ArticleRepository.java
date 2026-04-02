package com.newsarticle.db;

import com.newsarticle.model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleRepository {

    private final SourceRepository sourceRepository = new SourceRepository();

    public boolean save(Article article) {
        int sourceId = 0;
        if (article.getSource() != null && !article.getSource().isBlank()) {
            sourceId = sourceRepository.findOrCreate(null, article.getSource());
        }

        String sql = """
                INSERT IGNORE INTO articles 
                (title, description, content, url, author, published_at, fetched_at, source_id, category, url_to_image)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getContent());
            pstmt.setString(4, article.getUrl());
            pstmt.setString(5, article.getAuthor());
            pstmt.setString(6, article.getPublishedAt());

            if (article.getFetchedAt() > 0) {
                pstmt.setInt(7, article.getFetchedAt());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            if (sourceId > 0) {
                pstmt.setInt(8, sourceId);
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            pstmt.setString(9, article.getCategory());
            pstmt.setString(10, article.getUrlToImage());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("  Error saving article: " + e.getMessage());
            return false;
        }
    }

    public int saveAll(List<Article> articles) {
        int savedCount = 0;
        for (Article article : articles) {
            if (save(article)) {
                savedCount++;
            }
        }
        return savedCount;
    }

    public List<Article> findAll() {
        String sql = """
                SELECT a.*, s.name as source_name 
                FROM articles a 
                LEFT JOIN source s ON a.source_id = s.id 
                ORDER BY a.saved_at DESC
                """;
        return executeQueryWithSource(sql);
    }

    public List<Article> findByKeyword(String keyword) {
        String sql = """
                SELECT a.*, s.name as source_name 
                FROM articles a 
                LEFT JOIN source s ON a.source_id = s.id 
                WHERE a.title LIKE ? OR a.description LIKE ? 
                ORDER BY a.published_at DESC
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

    public List<Article> findByCategory(String category) {
        String sql = """
                SELECT a.*, s.name as source_name 
                FROM articles a 
                LEFT JOIN source s ON a.source_id = s.id 
                WHERE a.category = ? 
                ORDER BY a.published_at DESC
                """;

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

    public Article findById(int id) {
        String sql = """
                SELECT a.*, s.name as source_name 
                FROM articles a 
                LEFT JOIN source s ON a.source_id = s.id 
                WHERE a.id = ?
                """;

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

    private List<Article> executeQueryWithSource(String sql) {
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
        article.setTitle(rs.getString("title"));
        article.setDescription(rs.getString("description"));
        article.setContent(rs.getString("content"));
        article.setUrl(rs.getString("url"));
        article.setAuthor(rs.getString("author"));
        article.setPublishedAt(rs.getString("published_at"));
        article.setFetchedAt(rs.getInt("fetched_at"));
        article.setSourceId(rs.getInt("source_id"));
        article.setCategory(rs.getString("category"));
        article.setUrlToImage(rs.getString("url_to_image"));
        article.setSavedAt(rs.getString("saved_at"));

        try {
            String sourceName = rs.getString("source_name");
            article.setSource(sourceName);
        } catch (SQLException e) {
            // source_name not in result set
        }

        return article;
    }
}
