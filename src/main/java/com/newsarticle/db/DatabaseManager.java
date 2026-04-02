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
        try (Statement stmt = connection.createStatement()) {

            // 1. CATEGORY table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS category (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    name        VARCHAR(100) NOT NULL UNIQUE,
                    description TEXT
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // 2. SOURCE table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS source (
                    id            INT AUTO_INCREMENT PRIMARY KEY,
                    api_source_id VARCHAR(255),
                    name          VARCHAR(255) NOT NULL,
                    language      VARCHAR(10),
                    country       VARCHAR(10)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // 3. FETCH_LOG table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fetch_log (
                    id            INT AUTO_INCREMENT PRIMARY KEY,
                    page_number   INT,
                    page_size     INT,
                    total_results INT,
                    status        VARCHAR(50),
                    fetched_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
                    category_id   INT,
                    FOREIGN KEY (category_id) REFERENCES category(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // 4. ARTICLE table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS articles (
                    id           INT AUTO_INCREMENT PRIMARY KEY,
                    title        VARCHAR(500) NOT NULL,
                    description  TEXT,
                    content      TEXT,
                    url          VARCHAR(2048) UNIQUE NOT NULL,
                    author       VARCHAR(255),
                    published_at VARCHAR(50),
                    fetched_at   INT,
                    source_id    INT,
                    category     VARCHAR(50),
                    url_to_image VARCHAR(2048),
                    saved_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (fetched_at) REFERENCES fetch_log(id),
                    FOREIGN KEY (source_id) REFERENCES source(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // 5. HISTORY table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS history (
                    id           INT AUTO_INCREMENT PRIMARY KEY,
                    keyword      VARCHAR(255),
                    from_date    VARCHAR(20),
                    to_date      VARCHAR(20),
                    result_count INT DEFAULT 0,
                    searched_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
                    category_id  INT,
                    FOREIGN KEY (category_id) REFERENCES category(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // Seed default categories
            stmt.execute("""
                INSERT IGNORE INTO category (name, description) VALUES
                ('business', 'Business news'),
                ('entertainment', 'Entertainment news'),
                ('general', 'General news'),
                ('health', 'Health news'),
                ('science', 'Science news'),
                ('sports', 'Sports news'),
                ('technology', 'Technology news')
            """);

            // Create index on articles published_at
            try {
                stmt.execute("CREATE INDEX idx_articles_published ON articles(published_at)");
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
