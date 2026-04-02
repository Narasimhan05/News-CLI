package com.newsarticle.model;

public class SearchHistory {

    private int id;
    private String keyword;
    private String fromDate;
    private String toDate;
    private int resultCount;
    private String searchedAt;
    private int categoryId;

    public SearchHistory() {
    }

    public SearchHistory(String keyword, String fromDate, String toDate, int resultCount, int categoryId) {
        this.keyword = keyword;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.resultCount = resultCount;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public String getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(String searchedAt) {
        this.searchedAt = searchedAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "SearchHistory{id=" + id + ", keyword='" + keyword + "', resultCount=" + resultCount + "}";
    }
}
