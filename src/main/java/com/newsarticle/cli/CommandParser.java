package com.newsarticle.cli;

import java.util.*;

/**
 * Parses raw user input into structured commands.
 * Supports commands with arguments and named flags (--flag value).
 */
public class CommandParser {

    private String command;
    private List<String> args;
    private Map<String, String> flags;

    /**
     * Parse a raw input string into command, arguments, and flags.
     *
     * @param input Raw user input string
     * @return A parsed CommandParser instance
     */
    public static CommandParser parse(String input) {
        CommandParser parser = new CommandParser();
        parser.args = new ArrayList<>();
        parser.flags = new HashMap<>();

        if (input == null || input.isBlank()) {
            parser.command = "";
            return parser;
        }

        String[] tokens = input.trim().split("\\s+");
        parser.command = tokens[0].toLowerCase();

        // Parse remaining tokens into args and flags
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("--") && i + 1 < tokens.length) {
                // Named flag: --key value
                String flagName = tokens[i].substring(2).toLowerCase();
                i++;
                parser.flags.put(flagName, tokens[i]);
            } else if (!tokens[i].startsWith("--")) {
                // Positional argument
                parser.args.add(tokens[i]);
            }
        }

        return parser;
    }

    /**
     * Get the main command name (lowercase).
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get positional arguments (excludes flags).
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Get the first positional argument, or null if none.
     */
    public String getFirstArg() {
        return args.isEmpty() ? null : args.get(0);
    }

    /**
     * Get all positional arguments joined as a single string.
     */
    public String getArgsAsString() {
        return String.join(" ", args);
    }

    /**
     * Get a flag value by name, or null if not present.
     */
    public String getFlag(String name) {
        return flags.get(name.toLowerCase());
    }

    /**
     * Check if a flag is present.
     */
    public boolean hasFlag(String name) {
        return flags.containsKey(name.toLowerCase());
    }

    /**
     * Get a flag value as an integer, or default if not present/invalid.
     */
    public int getFlagAsInt(String name, int defaultValue) {
        String value = getFlag(name);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Check if the input was empty/blank.
     */
    public boolean isEmpty() {
        return command.isBlank();
    }

    @Override
    public String toString() {
        return "Command{" + command + ", args=" + args + ", flags=" + flags + '}';
    }
}
