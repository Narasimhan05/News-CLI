package com.newsarticle.model;

public class Article {

    private int id;
    private String title;
    private String description;
    private String content;
    private String url;
    private String author;
    private String publishedAt;
    private int fetchedAt;
    private int sourceId;
    private String category;
    private String urlToImage;
    private String savedAt;

    // Transient fields for display (not stored as FK)
    private String sourceName;

    public Article() {
    }

    public Article(String source, String author, String title, String description,
                   String url, String urlToImage, String publishedAt, String content) {
        this.sourceName = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(int fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt = savedAt;
    }

    public String getSource() {
        return sourceName;
    }

    public void setSource(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", source='" + sourceName + '\'' +
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
