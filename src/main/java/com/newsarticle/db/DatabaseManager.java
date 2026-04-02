package com.newsarticle.db;

import com.newsarticle.util.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the MySQL database connection and schema initialization.
 * Automatically creates the database and tables if they don't exist.
 */
public class DatabaseManager {

    private static Connection connection;

    /**
     * Initialize the database: create the schema and connect.
     */
    public static void initialize() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // First connect without a specific database to create it if needed
            String baseUrl = "jdbc:mysql://" + Config.getDbHost() + ":" + Config.getDbPort()
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            try (Connection tempConn = DriverManager.getConnection(
                    baseUrl, Config.getDbUsername(), Config.getDbPassword())) {
                try (Statement stmt = tempConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + Config.getDbName());
                }
            }

            // Now connect to the actual database
            connection = DriverManager.getConnection(
                    Config.getDbUrl(), Config.getDbUsername(), Config.getDbPassword());

            createTables();

            System.out.println("  ✅ MySQL Database initialized: " + Config.getDbName()
                    + " @ " + Config.getDbHost() + ":" + Config.getDbPort());

        } catch (ClassNotFoundException e) {
            System.err.println("  ❌ MySQL JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("  ❌ Database initialization failed: " + e.getMessage());
            System.err.println("  💡 Make sure MySQL is running and credentials are correct in config.properties");
        }
    }

    /**
     * Create the articles table if it doesn't exist.
     */
    private static void createTables() throws SQLException {
        String createArticlesTable = """
                CREATE TABLE IF NOT EXISTS articles (
                    id           INT AUTO_INCREMENT PRIMARY KEY,
                    source       VARCHAR(255),
                    author       VARCHAR(255),
                    title        VARCHAR(500) NOT NULL,
                    description  TEXT,
                    url          VARCHAR(2048) UNIQUE NOT NULL,
                    url_to_image VARCHAR(2048),
                    published_at VARCHAR(50),
                    content      TEXT,
                    category     VARCHAR(50),
                    saved_at     DATETIME DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;

        String createDateIndex = """
                CREATE INDEX idx_articles_published ON articles(published_at)
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createArticlesTable);

            // Create index only if it doesn't exist (MySQL doesn't support IF NOT EXISTS for indexes)
            try {
                stmt.execute(createDateIndex);
            } catch (SQLException e) {
                // Index already exists — ignore
            }
        }
    }

    /**
     * Get the active database connection.
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Close the database connection gracefully.
     */
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("  Database connection closed.");
            } catch (SQLException e) {
                System.err.println("  Error closing database: " + e.getMessage());
            }
        }
    }
}
