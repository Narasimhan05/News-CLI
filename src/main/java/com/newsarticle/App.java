package com.newsarticle;

import com.newsarticle.cli.CommandHandler;
import com.newsarticle.cli.CommandParser;
import com.newsarticle.db.DatabaseManager;
import com.newsarticle.util.Config;
import com.newsarticle.util.ConsoleFormatter;

import java.util.Scanner;

/**
 * Main entry point for the NewsArticle CLI application.
 *
 * A command-driven tool to fetch, search, filter, and save news articles
 * from NewsAPI into a local SQLite database.
 *
 * Usage:
 *   mvn compile exec:java
 *   or
 *   java -jar target/news-article-cli-1.0-SNAPSHOT.jar
 */
public class App {

    private static final String PROMPT = "\u001B[36m  news>\u001B[0m ";

    public static void main(String[] args) {

        // Show the welcome banner
        ConsoleFormatter.displayBanner();

        // Check configuration
        if (!Config.isLoaded()) {
            ConsoleFormatter.printError("Failed to load configuration. Check config.properties.");
            return;
        }

        if (!Config.isApiKeyConfigured()) {
            ConsoleFormatter.printWarning("API key not configured yet!");
            ConsoleFormatter.printInfo("Edit src/main/resources/config.properties and set 'newsapi.key'");
            ConsoleFormatter.printInfo("Get a free key at: https://newsapi.org/register");
            System.out.println();
        }

        // Initialize database
        DatabaseManager.initialize();
        System.out.println();

        // Create command handler
        CommandHandler handler = new CommandHandler();

        // Main input loop
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(PROMPT);

                if (!scanner.hasNextLine()) {
                    break;
                }

                String input = scanner.nextLine().trim();

                if (input.isBlank()) {
                    continue;
                }

                CommandParser cmd = CommandParser.parse(input);

                try {
                    handler.handle(cmd);
                } catch (Exception e) {
                    ConsoleFormatter.printError("Unexpected error: " + e.getMessage());
                }

                System.out.println();
            }
        } finally {
            DatabaseManager.close();
        }
    }
}
