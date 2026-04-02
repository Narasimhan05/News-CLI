package com.newsarticle.util;

import com.newsarticle.model.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsoleFormatter {

    private static final String SEPARATOR = "-".repeat(60);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public static void displayArticles(List<Article> articles, String header, int totalResults) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  " + header);
        System.out.println(SEPARATOR);

        if (articles.isEmpty()) {
            System.out.println("  No articles found.");
            System.out.println(SEPARATOR);
            return;
        }

        System.out.println("  Showing " + articles.size() + " of " + totalResults + " total results");
        System.out.println();

        for (int i = 0; i < articles.size(); i++) {
            displayArticle(articles.get(i), i + 1);
        }

        System.out.println(SEPARATOR);
    }

    public static void displayArticle(Article article, int index) {
        System.out.println(SEPARATOR);
        System.out.println("  [" + index + "] " + article.getTitle());

        if (article.getSource() != null && !article.getSource().isBlank()) {
            System.out.println("  Source: " + article.getSource());
        }

        if (article.getAuthor() != null && !article.getAuthor().isBlank()) {
            System.out.println("  Author: " + article.getAuthor());
        }

        if (article.getPublishedAt() != null && !article.getPublishedAt().isBlank()) {
            System.out.println("  Date:   " + formatDate(article.getPublishedAt()));
        }

        if (article.getDescription() != null && !article.getDescription().isBlank()) {
            System.out.println();
            String desc = article.getDescription();
            if (desc.length() > 200) {
                desc = desc.substring(0, 197) + "...";
            }
            System.out.println("  " + desc);
        }

        if (article.getUrl() != null && !article.getUrl().isBlank()) {
            System.out.println("  Link: " + article.getUrl());
        }

        System.out.println();
    }

    public static void displaySavedArticles(List<Article> articles) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  Saved Articles");
        System.out.println(SEPARATOR);

        if (articles.isEmpty()) {
            System.out.println("  No saved articles found.");
            System.out.println(SEPARATOR);
            return;
        }

        System.out.println("  Total saved: " + articles.size());
        System.out.println();

        for (int i = 0; i < articles.size(); i++) {
            displayArticle(articles.get(i), i + 1);
        }

        System.out.println(SEPARATOR);
    }

    public static void displayHelp() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  Available Commands");
        System.out.println(SEPARATOR);
        System.out.println();

        printCommand("latest", "Fetch and display the latest headlines");
        printCommand("latest --category <cat>", "Latest headlines by category");
        printCommand("search <keyword>", "Search articles by keyword");
        printCommand("search <keyword> --from <date>", "Search with start date (YYYY-MM-DD)");
        printCommand("search <keyword> --to <date>", "Search with end date (YYYY-MM-DD)");
        printCommand("search <keyword> --from <d1> --to <d2>", "Search with date range");
        printCommand("save <number>", "Save article from last results to database");
        printCommand("saved", "View all saved articles");
        printCommand("delete <number>", "Delete saved article from database");
        printCommand("clear", "Clear the screen");
        printCommand("help", "Show this help menu");
        printCommand("exit", "Quit the application");

        System.out.println();
        System.out.println("  Categories: business, entertainment, general, health,");
        System.out.println("              science, sports, technology");
        System.out.println();
        System.out.println(SEPARATOR);
    }

    public static void displayBanner() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println();
        System.out.println("        NEWS ARTICLE CLI");
        System.out.println();
        System.out.println("    Fetch, Search, Filter & Save News Articles");
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println();
        System.out.println("  Type 'help' to see available commands.");
        System.out.println();
    }

    private static void printCommand(String command, String description) {
        System.out.printf("  %-40s %s%n", command, description);
    }

    private static String formatDate(String isoDate) {
        try {
            if (isoDate.contains("T")) {
                LocalDateTime dateTime = LocalDateTime.parse(
                        isoDate.substring(0, isoDate.length() > 19 ? 19 : isoDate.length()));
                return dateTime.format(DISPLAY_FORMAT);
            }
            return isoDate;
        } catch (Exception e) {
            return isoDate;
        }
    }

    public static void printError(String message) {
        System.out.println("  [ERROR] " + message);
    }

    public static void printSuccess(String message) {
        System.out.println("  [OK] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("  [INFO] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("  [WARNING] " + message);
    }

    public static void printLoading(String message) {
        System.out.print("  " + message + "...");
    }

    public static void printLoadingDone() {
        System.out.println(" Done!");
    }
}
