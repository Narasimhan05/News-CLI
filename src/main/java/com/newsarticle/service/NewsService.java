package com.newsarticle.service;

import com.newsarticle.api.ApiResponse;
import com.newsarticle.api.NewsApiClient;
import com.newsarticle.db.ArticleRepository;
import com.newsarticle.db.CategoryRepository;
import com.newsarticle.db.FetchLogRepository;
import com.newsarticle.db.SearchHistoryRepository;
import com.newsarticle.model.Article;
import com.newsarticle.util.Config;
import com.newsarticle.util.ConsoleFormatter;

import java.util.ArrayList;
import java.util.List;

public class NewsService {

    private final NewsApiClient apiClient;
    private final ArticleRepository repository;
    private final CategoryRepository categoryRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final FetchLogRepository fetchLogRepository;
    
    private List<Article> lastFetchedArticles;

    public NewsService() {
        this.apiClient = new NewsApiClient();
        this.repository = new ArticleRepository();
        this.categoryRepository = new CategoryRepository();
        this.searchHistoryRepository = new SearchHistoryRepository();
        this.fetchLogRepository = new FetchLogRepository();
        this.lastFetchedArticles = new ArrayList<>();
    }

    public void fetchLatest(String categoryArg) {
        String category = categoryArg.isEmpty() ? null : categoryArg;
        
        ConsoleFormatter.printLoading("Fetching latest headlines");
        ApiResponse response = apiClient.getTopHeadlines(category);
        ConsoleFormatter.printLoadingDone();

        if (!response.isSuccess()) {
            ConsoleFormatter.printError("API Error: " + response.getErrorMessage());
            return;
        }

        int catId = (category != null) ? categoryRepository.findIdByName(category) : 0;
        int fetchId = fetchLogRepository.logFetch(1, Config.getApiPageSize(), response.getTotalResults(), "ok", catId);

        lastFetchedArticles = response.getArticles();
        for (Article a : lastFetchedArticles) {
            a.setFetchedAt(fetchId);
        }

        String header = (category != null) ? "Latest Headlines - " + category.toUpperCase() : "Latest Headlines";
        ConsoleFormatter.displayArticles(lastFetchedArticles, header, response.getTotalResults());
    }

    public void searchNews(String keyword) {
        if (keyword.isEmpty()) {
            ConsoleFormatter.printError("Please provide a search keyword (e.g., 'search technology').");
            return;
        }

        ConsoleFormatter.printLoading("Searching articles");
        ApiResponse response = apiClient.searchArticles(keyword, null, null);
        ConsoleFormatter.printLoadingDone();

        if (!response.isSuccess()) {
            ConsoleFormatter.printError("API Error: " + response.getErrorMessage());
            return;
        }

        searchHistoryRepository.logSearch(keyword, null, null, response.getTotalResults(), 0);
        int fetchId = fetchLogRepository.logFetch(1, Config.getApiPageSize(), response.getTotalResults(), "ok", 0);

        lastFetchedArticles = response.getArticles();
        for (Article a : lastFetchedArticles) {
            a.setFetchedAt(fetchId);
        }

        ConsoleFormatter.displayArticles(lastFetchedArticles, "Search: " + keyword, response.getTotalResults());
    }

    public void saveArticle(String indexStr) {
        if (indexStr.isEmpty()) {
            ConsoleFormatter.printError("Please specify the article number (e.g., 'save 1').");
            return;
        }
        
        try {
            int index = Integer.parseInt(indexStr);
            if (index < 1 || index > lastFetchedArticles.size()) {
                ConsoleFormatter.printError("Invalid article number.");
                return;
            }
            
            Article article = lastFetchedArticles.get(index - 1);
            if (repository.existsByUrl(article.getUrl())) {
                ConsoleFormatter.printWarning("Article is already saved.");
            } else if (repository.save(article)) {
                ConsoleFormatter.printSuccess("Article saved successfully.");
            } else {
                ConsoleFormatter.printError("Failed to save article.");
            }
        } catch (NumberFormatException e) {
            ConsoleFormatter.printError("Please enter a valid number formatting.");
        }
    }

    public void viewSavedArticles() {
        List<Article> savedArticles = repository.findAll();
        ConsoleFormatter.displaySavedArticles(savedArticles);
    }

    public void deleteArticle(String indexStr) {
        if (indexStr.isEmpty()) {
            ConsoleFormatter.printError("Please specify the article number to delete (e.g., 'delete 1').");
            return;
        }
        
        try {
            int index = Integer.parseInt(indexStr);
            List<Article> savedArticles = repository.findAll();
            
            if (index < 1 || index > savedArticles.size()) {
                ConsoleFormatter.printError("Invalid article number.");
                return;
            }
            
            Article article = savedArticles.get(index - 1);
            if (repository.deleteById(article.getId())) {
                ConsoleFormatter.printSuccess("Article deleted.");
            } else {
                ConsoleFormatter.printError("Failed to delete article.");
            }
        } catch (NumberFormatException e) {
            ConsoleFormatter.printError("Please enter a valid number formatting.");
        }
    }
}
