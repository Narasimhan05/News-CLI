package com.newsarticle.db;

import com.newsarticle.util.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static Connection connection;

    public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String baseUrl = "jdbc:mysql://" + Config.getDbHost() + ":" + Config.getDbPort()
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            try (Connection tempConn = DriverManager.getConnection(
                    baseUrl, Config.getDbUsername(), Config.getDbPassword())) {
                try (Statement stmt = tempConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + Config.getDbName());
                }
            }

            connection = DriverManager.getConnection(
                    Config.getDbUrl(), Config.getDbUsername(), Config.getDbPassword());

            createTables();

            System.out.println("  [OK] MySQL Database initialized: " + Config.getDbName()
                    + " @ " + Config.getDbHost() + ":" + Config.getDbPort());

        } catch (ClassNotFoundException e) {
            System.err.println("  [ERROR] MySQL JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("  [ERROR] Database initialization failed: " + e.getMessage());
            System.err.println("  [INFO] Make sure MySQL is running and credentials are correct in config.properties");
        }
    }

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

            try {
                stmt.execute(createDateIndex);
            } catch (SQLException e) {
                // Index already exists
            }
        }
    }

    public static Connection getConnection() {
        return connection;
    }

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
