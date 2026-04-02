# 📰 NewsArticle CLI

A Java Core command-line application that fetches news articles from [NewsAPI](https://newsapi.org/), enables keyword search and category/date filtering, and stores articles in a local SQLite database with automatic deduplication.

## Features

- **Fetch Latest Headlines** — Get top headlines by country, optionally filtered by category
- **Search by Keyword** — Full-text search across all news articles with date range filtering
- **Save to Database** — Persist interesting articles to a local SQLite database
- **Automatic Deduplication** — Articles are uniquely identified by URL; duplicates are silently ignored
- **Internal Pagination** — Automatically fetches multiple pages from the API
- **Rich CLI Output** — Colorized, formatted output with ANSI escape codes

## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **NewsAPI Key** — Get one free at [newsapi.org/register](https://newsapi.org/register)

## Setup

1. **Clone/download the project**

2. **Configure your API key**
   
   Edit `src/main/resources/config.properties`:
   ```properties
   newsapi.key=YOUR_API_KEY_HERE
   ```

3. **Build the project**
   ```bash
   mvn clean package
   ```

## Running

### Option 1: Maven exec plugin
```bash
mvn compile exec:java
```

### Option 2: Run the fat JAR
```bash
java -jar target/news-article-cli-1.0-SNAPSHOT.jar
```

## Commands

| Command | Description |
|---------|-------------|
| `latest` | Fetch and display the latest headlines |
| `latest --category <cat>` | Latest headlines filtered by category |
| `search <keyword>` | Search articles by keyword |
| `search <keyword> --from <date>` | Search with start date (YYYY-MM-DD) |
| `search <keyword> --to <date>` | Search with end date (YYYY-MM-DD) |
| `search <keyword> --from <d1> --to <d2>` | Search within a date range |
| `save <number>` | Save article # from last results to database |
| `saved` | View all saved articles |
| `delete <number>` | Delete saved article by number |
| `clear` | Clear the screen |
| `help` | Show the help menu |
| `exit` | Quit the application |

### Categories
`business` · `entertainment` · `general` · `health` · `science` · `sports` · `technology`

## Examples

```
news> latest
news> latest --category technology
news> search artificial intelligence
news> search climate --from 2026-03-01 --to 2026-03-31
news> save 3
news> saved
news> delete 1
```

## Project Structure

```
src/main/java/com/newsarticle/
├── App.java                    # Entry point + REPL loop
├── api/
│   ├── NewsApiClient.java      # HTTP client with pagination
│   └── ApiResponse.java        # API response wrapper
├── cli/
│   ├── CommandParser.java      # Input parsing (commands + flags)
│   └── CommandHandler.java     # Command execution logic
├── db/
│   ├── DatabaseManager.java    # SQLite connection + schema
│   └── ArticleRepository.java  # CRUD operations + dedup
├── model/
│   └── Article.java            # Article data model
└── util/
    ├── Config.java             # Configuration loader
    └── ConsoleFormatter.java   # ANSI-colored output formatting
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Build Tool | Maven |
| Database | SQLite (via sqlite-jdbc) |
| HTTP Client | java.net.http.HttpClient |
| JSON Parser | Gson |
| API | NewsAPI v2 |
