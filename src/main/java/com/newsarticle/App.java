package com.newsarticle;

import com.newsarticle.db.DatabaseManager;
import com.newsarticle.service.NewsService;
import com.newsarticle.util.Config;
import com.newsarticle.util.ConsoleFormatter;

import java.util.Scanner;

public class App {

    private static final String PROMPT = "  news> ";
    private static final NewsService service = new NewsService();

    public static void main(String[] args) {
        ConsoleFormatter.displayBanner();
        
        if (!Config.isLoaded() || !Config.isApiKeyConfigured()) {
            ConsoleFormatter.printError("Config or API key is missing. Check config.properties.");
            return;
        }

        DatabaseManager.initialize();
        System.out.println();

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

                String[] parts = input.split(" ", 2);
                String command = parts[0].toLowerCase();
                String argument = (parts.length > 1) ? parts[1].trim() : "";

                switch (command) {
                    case "latest":
                        service.fetchLatest(argument);
                        break;
                    case "search":
                        service.searchNews(argument);
                        break;
                    case "save":
                        service.saveArticle(argument);
                        break;
                    case "saved":
                        service.viewSavedArticles();
                        break;
                    case "delete":
                        service.deleteArticle(argument);
                        break;
                    case "help":
                        ConsoleFormatter.displayHelp();
                        break;
                    case "exit":
                    case "quit":
                        System.out.println("Goodbye!");
                        DatabaseManager.close();
                        System.exit(0);
                        break;
                    default:
                        ConsoleFormatter.printError("Unknown command. Type 'help' to see options.");
                }
                System.out.println();
            }
        } finally {
            DatabaseManager.close();
        }
    }
}
