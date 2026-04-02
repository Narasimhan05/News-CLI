package com.newsarticle.model;

/**
 * Represents a news article fetched from NewsAPI.
 * Maps to both the API JSON response and the database schema.
 */
public class Article {

    private int id;               // Database ID (auto-increment)
    private String source;        // Source name (e.g., "BBC News")
    private String author;        // Author name
    private String title;         // Article title
    private String description;   // Short description/summary
    private String url;           // Full article URL (used as unique key)
    private String urlToImage;    // Article image URL
    private String publishedAt;   // Publication date (ISO 8601)
    private String content;       // Truncated article content
    private String category;      // Category (if applicable)
    private String savedAt;       // When the article was saved locally

    public Article() {
    }

    public Article(String source, String author, String title, String description,
                   String url, String urlToImage, String publishedAt, String content) {
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt = savedAt;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return url != null && url.equals(article.url);
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
