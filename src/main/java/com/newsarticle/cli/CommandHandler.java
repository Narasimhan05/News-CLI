package com.newsarticle.cli;

import com.newsarticle.api.ApiResponse;
import com.newsarticle.api.NewsApiClient;
import com.newsarticle.db.ArticleRepository;
import com.newsarticle.model.Article;
import com.newsarticle.util.Config;
import com.newsarticle.util.ConsoleFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Handles execution of parsed CLI commands.
 * Acts as the bridge between user input and application logic.
 */
public class CommandHandler {

    private final NewsApiClient apiClient;
    private final ArticleRepository repository;

    // Stores the last fetched articles for save-by-index functionality
    private List<Article> lastFetchedArticles;

    private static final Set<String> VALID_CATEGORIES = Set.of(
            "business", "entertainment", "general", "health",
            "science", "sports", "technology"
    );

    public CommandHandler() {
        this.apiClient = new NewsApiClient();
        this.repository = new ArticleRepository();
        this.lastFetchedArticles = new ArrayList<>();
    }

    /**
     * Execute a parsed command.
     */
    public void handle(CommandParser cmd) {
        switch (cmd.getCommand()) {
            case "latest"  -> handleLatest(cmd);
            case "search"  -> handleSearch(cmd);
            case "save"    -> handleSave(cmd);
            case "saved"   -> handleSaved(cmd);
            case "delete"  -> handleDelete(cmd);
            case "clear"   -> handleClear();
            case "help"    -> ConsoleFormatter.displayHelp();
            case "exit", "quit", "q" -> handleExit();
            default        -> ConsoleFormatter.printError(
                    "Unknown command: '" + cmd.getCommand() + "'. Type 'help' for available commands.");
        }
    }

    /**
     * Handle 'latest' command - fetch top headlines.
     */
    private void handleLatest(CommandParser cmd) {
        if (!checkApiKey()) return;

        String category = cmd.getFlag("category");
        if (category != null && !VALID_CATEGORIES.contains(category.toLowerCase())) {
            ConsoleFormatter.printError("Invalid category: '" + category + "'");
            ConsoleFormatter.printInfo("Valid categories: " + String.join(", ", VALID_CATEGORIES));
            return;
        }

        String header = "Latest Headlines";
        if (category != null) {
            header += " — " + capitalize(category);
        }

        ConsoleFormatter.printLoading("Fetching latest headlines");
        ApiResponse response = apiClient.getTopHeadlines(category);
        ConsoleFormatter.printLoadingDone();

        if (!response.isSuccess()) {
            ConsoleFormatter.printError("API Error [" + response.getErrorCode() + "]: "
                    + response.getErrorMessage());
            return;
        }

        lastFetchedArticles = response.getArticles();
        ConsoleFormatter.displayArticles(lastFetchedArticles, header, response.getTotalResults());

        if (!lastFetchedArticles.isEmpty()) {
            ConsoleFormatter.printInfo("Use 'save <number>' to save an article to your database.");
        }
    }

    /**
     * Handle 'search' command - search articles by keyword.
     */
    private void handleSearch(CommandParser cmd) {
        if (!checkApiKey()) return;

        String keyword = cmd.getArgsAsString();
        if (keyword == null || keyword.isBlank()) {
            ConsoleFormatter.printError("Please provide a search keyword.");
            ConsoleFormatter.printInfo("Usage: search <keyword> [--from YYYY-MM-DD] [--to YYYY-MM-DD]");
            return;
        }

        String fromDate = cmd.getFlag("from");
        String toDate = cmd.getFlag("to");

        // Validate date format
        if (fromDate != null && !isValidDate(fromDate)) {
            ConsoleFormatter.printError("Invalid --from date format. Use YYYY-MM-DD.");
            return;
        }
        if (toDate != null && !isValidDate(toDate)) {
            ConsoleFormatter.printError("Invalid --to date format. Use YYYY-MM-DD.");
            return;
        }

        StringBuilder header = new StringBuilder("Search: \"" + keyword + "\"");
        if (fromDate != null) header.append(" from ").append(fromDate);
        if (toDate != null) header.append(" to ").append(toDate);

        ConsoleFormatter.printLoading("Searching articles");
        ApiResponse response = apiClient.searchArticles(keyword, fromDate, toDate);
        ConsoleFormatter.printLoadingDone();

        if (!response.isSuccess()) {
            ConsoleFormatter.printError("API Error [" + response.getErrorCode() + "]: "
                    + response.getErrorMessage());
            return;
        }

        lastFetchedArticles = response.getArticles();
        ConsoleFormatter.displayArticles(lastFetchedArticles, header.toString(), response.getTotalResults());

        if (!lastFetchedArticles.isEmpty()) {
            ConsoleFormatter.printInfo("Use 'save <number>' to save an article to your database.");
        }
    }

    /**
     * Handle 'save' command - save an article by its display index.
     */
    private void handleSave(CommandParser cmd) {
        String indexStr = cmd.getFirstArg();
        if (indexStr == null) {
            ConsoleFormatter.printError("Please specify the article number to save.");
            ConsoleFormatter.printInfo("Usage: save <number>");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            ConsoleFormatter.printError("'" + indexStr + "' is not a valid number.");
            return;
        }

        if (lastFetchedArticles.isEmpty()) {
            ConsoleFormatter.printError("No articles to save. Fetch articles first with 'latest' or 'search'.");
            return;
        }

        if (index < 1 || index > lastFetchedArticles.size()) {
            ConsoleFormatter.printError("Invalid article number. Choose between 1 and " + lastFetchedArticles.size() + ".");
            return;
        }

        Article article = lastFetchedArticles.get(index - 1);

        if (repository.existsByUrl(article.getUrl())) {
            ConsoleFormatter.printWarning("This article is already saved in your database.");
            return;
        }

        boolean saved = repository.save(article);
        if (saved) {
            ConsoleFormatter.printSuccess("Article saved: \"" + truncate(article.getTitle(), 50) + "\"");
        } else {
            ConsoleFormatter.printError("Failed to save article (may be a duplicate).");
        }
    }

    /**
     * Handle 'saved' command - display all saved articles.
     */
    private void handleSaved(CommandParser cmd) {
        List<Article> savedArticles = repository.findAll();
        ConsoleFormatter.displaySavedArticles(savedArticles);
    }

    /**
     * Handle 'delete' command - delete a saved article by display index.
     */
    private void handleDelete(CommandParser cmd) {
        String indexStr = cmd.getFirstArg();
        if (indexStr == null) {
            ConsoleFormatter.printError("Please specify the article number to delete.");
            ConsoleFormatter.printInfo("Usage: delete <number> (use 'saved' to see article numbers)");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            ConsoleFormatter.printError("'" + indexStr + "' is not a valid number.");
            return;
        }

        List<Article> savedArticles = repository.findAll();
        if (savedArticles.isEmpty()) {
            ConsoleFormatter.printError("No saved articles to delete.");
            return;
        }

        if (index < 1 || index > savedArticles.size()) {
            ConsoleFormatter.printError("Invalid article number. Choose between 1 and " + savedArticles.size() + ".");
            return;
        }

        Article article = savedArticles.get(index - 1);
        boolean deleted = repository.deleteById(article.getId());
        if (deleted) {
            ConsoleFormatter.printSuccess("Deleted: \"" + truncate(article.getTitle(), 50) + "\"");
        } else {
            ConsoleFormatter.printError("Failed to delete article.");
        }
    }

    /**
     * Handle 'clear' command.
     */
    private void handleClear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        ConsoleFormatter.displayBanner();
    }

    /**
     * Handle 'exit' command.
     */
    private void handleExit() {
        ConsoleFormatter.printInfo("Goodbye! 👋");
        System.exit(0);
    }

    /**
     * Check if the API key is configured.
     */
    private boolean checkApiKey() {
        if (!Config.isApiKeyConfigured()) {
            ConsoleFormatter.printError("API key not configured!");
            ConsoleFormatter.printInfo("Edit src/main/resources/config.properties and set your NewsAPI key.");
            ConsoleFormatter.printInfo("Get a free key at: https://newsapi.org/register");
            return false;
        }
        return true;
    }

    /**
     * Validate date format (YYYY-MM-DD).
     */
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    /**
     * Capitalize the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Truncate a string to the given max length.
     */
    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() <= maxLen ? str : str.substring(0, maxLen - 3) + "...";
    }
}
