package com.newsarticle.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration loader.
 * Reads settings from config.properties on the classpath.
 */
public class Config {

    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    static {
        load();
    }

    private static void load() {
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("[Config] config.properties not found on classpath.");
                return;
            }
            properties.load(input);
            loaded = true;
        } catch (IOException e) {
            System.err.println("[Config] Error loading config.properties: " + e.getMessage());
        }
    }

    public static String getApiKey() {
        return get("newsapi.key", "");
    }

    public static String getBaseUrl() {
        return get("newsapi.base.url", "https://newsapi.org/v2");
    }

    public static String getDbHost() {
        return get("db.host", "localhost");
    }

    public static int getDbPort() {
        return getInt("db.port", 3306);
    }

    public static String getDbName() {
        return get("db.name", "news_articles");
    }

    public static String getDbUsername() {
        return get("db.username", "root");
    }

    public static String getDbPassword() {
        return get("db.password", "");
    }

    public static String getDbUrl() {
        return "jdbc:mysql://" + getDbHost() + ":" + getDbPort() + "/" + getDbName()
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static int getApiPageSize() {
        return getInt("api.page.size", 20);
    }

    public static int getDisplayPageSize() {
        return getInt("display.page.size", 5);
    }

    public static String getDefaultCountry() {
        return get("default.country", "us");
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean isApiKeyConfigured() {
        String key = getApiKey();
        return key != null && !key.isBlank() && !key.equals("YOUR_API_KEY_HERE");
    }
}
