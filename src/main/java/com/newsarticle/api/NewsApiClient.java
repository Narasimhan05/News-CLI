package com.newsarticle.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newsarticle.model.Article;
import com.newsarticle.util.Config;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class NewsApiClient {

    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl;
    private final int pageSize;

    public NewsApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.apiKey = Config.getApiKey();
        this.baseUrl = Config.getBaseUrl();
        this.pageSize = Config.getApiPageSize();
    }

    public ApiResponse getTopHeadlines(String category) {
        String country = Config.getDefaultCountry();
        StringBuilder urlBuilder = new StringBuilder(baseUrl)
                .append("/top-headlines?country=").append(country)
                .append("&pageSize=").append(pageSize);

        if (category != null && !category.isBlank()) {
            urlBuilder.append("&category=").append(encode(category.trim().toLowerCase()));
        }

        return fetchWithPagination(urlBuilder.toString());
    }

    public ApiResponse searchArticles(String keyword, String fromDate, String toDate) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl)
                .append("/everything?q=").append(encode(keyword))
                .append("&pageSize=").append(pageSize)
                .append("&sortBy=publishedAt")
                .append("&language=en");

        if (fromDate != null && !fromDate.isBlank()) {
            urlBuilder.append("&from=").append(encode(fromDate.trim()));
        }

        if (toDate != null && !toDate.isBlank()) {
            urlBuilder.append("&to=").append(encode(toDate.trim()));
        }

        return fetchWithPagination(urlBuilder.toString());
    }

    private ApiResponse fetchWithPagination(String baseRequestUrl) {
        List<Article> allArticles = new ArrayList<>();
        int totalResults = 0;
        int currentPage = 1;
        int maxPages = 3;

        while (currentPage <= maxPages) {
            String url = baseRequestUrl + "&page=" + currentPage + "&apiKey=" + apiKey;

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("User-Agent", "NewsArticleCLI/1.0")
                        .timeout(Duration.ofSeconds(15))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    return parseErrorResponse(response.body(), response.statusCode());
                }

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                String status = json.get("status").getAsString();

                if (!"ok".equalsIgnoreCase(status)) {
                    String code = json.has("code") ? json.get("code").getAsString() : "unknown";
                    String message = json.has("message") ? json.get("message").getAsString() : "Unknown API error";
                    return ApiResponse.error(code, message);
                }

                totalResults = json.get("totalResults").getAsInt();
                JsonArray articlesArray = json.getAsJsonArray("articles");

                if (articlesArray == null || articlesArray.isEmpty()) {
                    break;
                }

                for (JsonElement element : articlesArray) {
                    JsonObject articleJson = element.getAsJsonObject();
                    Article article = parseArticle(articleJson);
                    if (article != null && article.getTitle() != null
                            && !article.getTitle().equals("[Removed]")) {
                        allArticles.add(article);
                    }
                }

                if (allArticles.size() >= totalResults || articlesArray.size() < pageSize) {
                    break;
                }

                currentPage++;

            } catch (IOException e) {
                return ApiResponse.error("network_error",
                        "Network error: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return ApiResponse.error("interrupted", "Request was interrupted");
            } catch (Exception e) {
                return ApiResponse.error("parse_error",
                        "Error parsing response: " + e.getMessage());
            }
        }

        return new ApiResponse("ok", totalResults, allArticles);
    }

    private Article parseArticle(JsonObject json) {
        try {
            String source = "";
            if (json.has("source") && !json.get("source").isJsonNull()) {
                JsonObject sourceObj = json.getAsJsonObject("source");
                if (sourceObj.has("name") && !sourceObj.get("name").isJsonNull()) {
                    source = sourceObj.get("name").getAsString();
                }
            }

            return new Article(
                    source,
                    getStringOrNull(json, "author"),
                    getStringOrNull(json, "title"),
                    getStringOrNull(json, "description"),
                    getStringOrNull(json, "url"),
                    getStringOrNull(json, "urlToImage"),
                    getStringOrNull(json, "publishedAt"),
                    getStringOrNull(json, "content"));
        } catch (Exception e) {
            System.err.println("  Warning: Could not parse article: " + e.getMessage());
            return null;
        }
    }

    private ApiResponse parseErrorResponse(String body, int statusCode) {
        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String code = json.has("code") ? json.get("code").getAsString() : String.valueOf(statusCode);
            String message = json.has("message") ? json.get("message").getAsString() : "HTTP " + statusCode;
            return ApiResponse.error(code, message);
        } catch (Exception e) {
            return ApiResponse.error(String.valueOf(statusCode),
                    "HTTP " + statusCode + " - " + body);
        }
    }

    private String getStringOrNull(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
