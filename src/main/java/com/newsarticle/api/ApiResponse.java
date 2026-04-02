package com.newsarticle.api;

import com.newsarticle.model.Article;

import java.util.List;

public class ApiResponse {

    private String status;
    private int totalResults;
    private List<Article> articles;
    private String errorCode;
    private String errorMessage;

    public ApiResponse() {
    }

    public ApiResponse(String status, int totalResults, List<Article> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public static ApiResponse error(String code, String message) {
        ApiResponse response = new ApiResponse();
        response.status = "error";
        response.errorCode = code;
        response.errorMessage = message;
        response.articles = List.of();
        response.totalResults = 0;
        return response;
    }

    public boolean isSuccess() {
        return "ok".equalsIgnoreCase(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
