package com.newsarticle.cli;

import java.util.*;

public class CommandParser {

    private String command;
    private List<String> args;
    private Map<String, String> flags;

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

        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("--") && i + 1 < tokens.length) {
                String flagName = tokens[i].substring(2).toLowerCase();
                i++;
                parser.flags.put(flagName, tokens[i]);
            } else if (!tokens[i].startsWith("--")) {
                parser.args.add(tokens[i]);
            }
        }

        return parser;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getFirstArg() {
        return args.isEmpty() ? null : args.get(0);
    }

    public String getArgsAsString() {
        return String.join(" ", args);
    }

    public String getFlag(String name) {
        return flags.get(name.toLowerCase());
    }

    public boolean hasFlag(String name) {
        return flags.containsKey(name.toLowerCase());
    }

    public int getFlagAsInt(String name, int defaultValue) {
        String value = getFlag(name);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean isEmpty() {
        return command.isBlank();
    }

    @Override
    public String toString() {
        return "Command{" + command + ", args=" + args + ", flags=" + flags + '}';
    }
}
