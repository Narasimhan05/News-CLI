package com.newsarticle;

import com.newsarticle.cli.CommandHandler;
import com.newsarticle.cli.CommandParser;
import com.newsarticle.db.DatabaseManager;
import com.newsarticle.util.Config;
import com.newsarticle.util.ConsoleFormatter;

import java.util.Scanner;

public class App {

    private static final String PROMPT = "  news> ";

    public static void main(String[] args) {

        ConsoleFormatter.displayBanner();

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

        DatabaseManager.initialize();
        System.out.println();

        CommandHandler handler = new CommandHandler();

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
