# UniRep — Service Manager GUI

A JavaFX desktop application for managing system services with SQLite persistence, async process execution, and a layered architecture built on eight design patterns.

## Prerequisites

- **JDK 25**
- **Apache Maven 3.9+**
- JavaFX 21.0.6 (resolved automatically by Maven)

## Build & Run

```bash
cd gui
mvn clean javafx:run
```

Tests:

```bash
mvn clean test
```

Package as JAR (requires JavaFX SDK on module-path at runtime):

```bash
mvn clean package
```

## Features

- **Start / Stop / Restart** system services asynchronously
- **Log viewer** — fetches the last 50 execution outputs per service
- **Custom service creation** — name, command, optional working directory
- **Auto-refresh** — Observer pattern updates the table and output area on every state change
- **SQLite persistence** — stores services, executions, and outputs across sessions
- **JSON import** — predefined services loaded from `services.json`
- **About Us panel** — team info with profile photos, social links, and contribution descriptions

## Design Patterns

| Pattern | Where |
|---|---|
| **MVC** | FXML (View), Controllers (Control), Model classes (Model) |
| **Singleton** | `DatabaseManager` — single SQLite connection |
| **Abstract Factory** | `DAOFactory` / `SQLiteDAOFactory` — pluggable DB backends |
| **Strategy** | `ServiceProcessor` / `JavaProcessor` — swappable execution strategies |
| **Observer** | `ServiceObserver` / `ServiceManager` — UI auto-update on state changes |
| **DAO** | 3 interfaces + 3 SQLite implementations (Service, Execution, Output) |
| **Simple Factory** | `DAOFactory.createServiceDAO()` etc. returns concrete DAOs |
| **Layered Architecture** | controller → service → dao → db |

## Project Structure

```
gui/
├── pom.xml
├── src/main/java/com/servicemanager/gui/
│   ├── App.java                   # JavaFX entry point
│   ├── Launcher.java              # main() launcher
│   ├── config/
│   │   └── ConfigLoader.java      # JSON services loader
│   ├── controller/
│   │   └── MainController.java    # JavaFX controller + ServiceObserver
│   ├── dao/
│   │   ├── DAOFactory.java        # Abstract factory interface
│   │   ├── SQLiteDAOFactory.java  # Concrete SQLite factory
│   │   ├── ServiceDAO.java        # Service CRUD interface
│   │   ├── SQLiteServiceDAO.java  # Service CRUD implementation
│   │   ├── ExecutionDAO.java      # Execution CRUD interface
│   │   ├── SQLiteExecutionDAO.java
│   │   ├── OutputDAO.java         # Output CRUD interface
│   │   └── SQLiteOutputDAO.java
│   ├── db/
│   │   └── DatabaseManager.java   # Singleton SQLite connection
│   ├── exception/                 # 7 custom exception classes
│   ├── model/
│   │   ├── Service.java
│   │   ├── Execution.java
│   │   └── Output.java
│   └── service/
│       ├── ServiceManager.java    # Orchestration hub
│       ├── ServiceObserver.java   # Observer interface
│       ├── ServiceProcessor.java  # Strategy interface
│       └── JavaProcessor.java     # Async process executor
├── src/main/resources/
│   ├── services.json              # Seed service definitions
│   ├── logback.xml                # Logging configuration
│   ├── pfp/                       # Profile photos (add your own)
│   └── com/servicemanager/gui/
│       ├── main-view.fxml         # Main window layout
│       └── styles.css             # Stylesheet
└── src/test/java/                 # 46 JUnit 4 tests
```

## Usage

1. **Start** the app with `mvn clean javafx:run`
2. Select a service from the table and click **Start** / **Stop** / **Restart** / **Logs**
3. Create a custom service by filling the form at the bottom and clicking **Create Service**
4. Open **Help → Documentation** to view this file
5. Open **Help → About Us** for team information

## Configuration

- **`services.json`** — preloaded service definitions (name, command, working directory)
- **`logback.xml`** — logging level and output format
- **Database** — `unirep.db` created automatically in the project root (gitignored)

## Technologies

- Java 25, JavaFX 21.0.6, JUnit 4, Mockito 2.28.2
- Gson 2.10.1, SQLite 3.45.1.0 (JDBC), Logback 1.4.11
- Maven (build, dependency management, JavaFX plugin)

## Authors

- **Semion Andreev** — logic layer (model, DAO, service, patterns, tests)
- **Raheem Khawaja** — UI layer (FXML layout, UI tests, logging UX)
