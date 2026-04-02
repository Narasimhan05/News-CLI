package com.newsarticle.util;

import com.newsarticle.model.Article;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for formatting articles for console output.
 * Provides colorized, readable output using ANSI escape codes.
 */
public class ConsoleFormatter {

    // ANSI color codes
    private static final String RESET   = "\u001B[0m";
    private static final String BOLD    = "\u001B[1m";
    private static final String DIM     = "\u001B[2m";
    private static final String CYAN    = "\u001B[36m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String BLUE    = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String RED     = "\u001B[31m";
    private static final String WHITE   = "\u001B[97m";

    private static final String SEPARATOR = DIM + "─".repeat(70) + RESET;
    private static final String DOUBLE_SEP = DIM + "═".repeat(70) + RESET;

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    /**
     * Display a list of articles with pagination info.
     */
    public static void displayArticles(List<Article> articles, String header, int totalResults) {
        System.out.println();
        System.out.println(DOUBLE_SEP);
        System.out.println(BOLD + CYAN + "  📰 " + header + RESET);
        System.out.println(DOUBLE_SEP);

        if (articles.isEmpty()) {
            System.out.println(YELLOW + "  No articles found." + RESET);
            System.out.println(DOUBLE_SEP);
            return;
        }

        System.out.println(DIM + "  Showing " + articles.size()
                + " of " + totalResults + " total results" + RESET);
        System.out.println();

        for (int i = 0; i < articles.size(); i++) {
            displayArticle(articles.get(i), i + 1);
        }

        System.out.println(DOUBLE_SEP);
    }

    /**
     * Display a single article with formatting.
     */
    public static void displayArticle(Article article, int index) {
        System.out.println(SEPARATOR);
        System.out.printf("  %s%s[%d]%s %s%s%s%n",
                BOLD, GREEN, index, RESET,
                BOLD + WHITE, article.getTitle(), RESET);

        if (article.getSource() != null && !article.getSource().isBlank()) {
            System.out.printf("  %s🏢 Source:%s %s%n", BLUE, RESET, article.getSource());
        }

        if (article.getAuthor() != null && !article.getAuthor().isBlank()) {
            System.out.printf("  %s✍️  Author:%s %s%n", MAGENTA, RESET, article.getAuthor());
        }

        if (article.getPublishedAt() != null && !article.getPublishedAt().isBlank()) {
            System.out.printf("  %s📅 Date:%s   %s%n", YELLOW, RESET, formatDate(article.getPublishedAt()));
        }

        if (article.getDescription() != null && !article.getDescription().isBlank()) {
            System.out.println();
            String desc = article.getDescription();
            if (desc.length() > 200) {
                desc = desc.substring(0, 197) + "...";
            }
            System.out.println("  " + DIM + desc + RESET);
        }

        if (article.getUrl() != null && !article.getUrl().isBlank()) {
            System.out.printf("  %s🔗 %s%s%n", CYAN, article.getUrl(), RESET);
        }

        System.out.println();
    }

    /**
     * Display saved articles from the database.
     */
    public static void displaySavedArticles(List<Article> articles) {
        System.out.println();
        System.out.println(DOUBLE_SEP);
        System.out.println(BOLD + GREEN + "  💾 Saved Articles" + RESET);
        System.out.println(DOUBLE_SEP);

        if (articles.isEmpty()) {
            System.out.println(YELLOW + "  No saved articles found." + RESET);
            System.out.println(DOUBLE_SEP);
            return;
        }

        System.out.println(DIM + "  Total saved: " + articles.size() + RESET);
        System.out.println();

        for (int i = 0; i < articles.size(); i++) {
            displayArticle(articles.get(i), i + 1);
        }

        System.out.println(DOUBLE_SEP);
    }

    /**
     * Display the help menu.
     */
    public static void displayHelp() {
        System.out.println();
        System.out.println(DOUBLE_SEP);
        System.out.println(BOLD + CYAN + "  📋 Available Commands" + RESET);
        System.out.println(DOUBLE_SEP);
        System.out.println();

        printCommand("latest", "Fetch and display the latest headlines");
        printCommand("latest --category <cat>", "Latest headlines by category");
        printCommand("search <keyword>", "Search articles by keyword");
        printCommand("search <keyword> --from <date>", "Search with start date (YYYY-MM-DD)");
        printCommand("search <keyword> --to <date>", "Search with end date (YYYY-MM-DD)");
        printCommand("search <keyword> --from <d1> --to <d2>", "Search with date range");
        printCommand("save <number>", "Save article # from last results to database");
        printCommand("saved", "View all saved articles");
        printCommand("delete <number>", "Delete saved article # from database");
        printCommand("clear", "Clear the screen");
        printCommand("help", "Show this help menu");
        printCommand("exit", "Quit the application");

        System.out.println();
        System.out.println(DIM + "  Categories: business, entertainment, general, health," + RESET);
        System.out.println(DIM + "              science, sports, technology" + RESET);
        System.out.println();
        System.out.println(DOUBLE_SEP);
    }

    /**
     * Display the welcome banner.
     */
    public static void displayBanner() {
        System.out.println();
        System.out.println(BOLD + CYAN);
        System.out.println("  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║        📰  N E W S   A R T I C L E   C L I          ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║     Fetch • Search • Filter • Save News Articles     ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        System.out.println(DIM + "  Type 'help' to see available commands." + RESET);
        System.out.println();
    }

    /**
     * Print a formatted command entry for help.
     */
    private static void printCommand(String command, String description) {
        System.out.printf("  %s%-40s%s %s%s%s%n",
                GREEN, command, RESET,
                DIM, description, RESET);
    }

    /**
     * Format an ISO date string for display.
     */
    private static String formatDate(String isoDate) {
        try {
            if (isoDate.contains("T")) {
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(
                        isoDate.substring(0, isoDate.length() > 19 ? 19 : isoDate.length()));
                return dateTime.format(DISPLAY_FORMAT);
            }
            return isoDate;
        } catch (Exception e) {
            return isoDate;
        }
    }

    /**
     * Print an error message.
     */
    public static void printError(String message) {
        System.out.println(RED + "  ❌ " + message + RESET);
    }

    /**
     * Print a success message.
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + "  ✅ " + message + RESET);
    }

    /**
     * Print an info message.
     */
    public static void printInfo(String message) {
        System.out.println(CYAN + "  ℹ️  " + message + RESET);
    }

    /**
     * Print a warning message.
     */
    public static void printWarning(String message) {
        System.out.println(YELLOW + "  ⚠️  " + message + RESET);
    }

    /**
     * Print a loading message.
     */
    public static void printLoading(String message) {
        System.out.print(DIM + "  ⏳ " + message + "..." + RESET);
    }

    /**
     * End a loading message.
     */
    public static void printLoadingDone() {
        System.out.println(GREEN + " Done!" + RESET);
    }
}
