# Service Manager GUI — Developer Reference

> For: Raheem (UI + Testing)  
> From: Semion (Logic Layer)

## How to Build & Run

```bash
cd gui
mvn compile           # build
mvn test              # run all tests
mvn clean javafx:run  # launch the GUI
```

## Project Structure (under `gui/`)

```
src/main/java/com/servicemanager/gui/
├── App.java                      JavaFX Application entry point
├── Launcher.java                 main() workaround for module-path
├── controller/
│   └── MainController.java       JavaFX Controller
├── model/
│   ├── Service.java              service data (name, command, workingDir, state)
│   ├── Execution.java            DB execution record (start/finish times)
│   └── Output.java               DB output line record
├── service/
│   ├── ServiceManager.java       main hub — call this from your controller
│   ├── ServicesLoader.java       merges JSON + DB services on startup
│   ├── ServiceProcessor.java     interface for process lifecycle
│   ├── JavaProcessor.java        async process launcher (background thread)
│   └── ServiceObserver.java      Observer interface for event notifications
├── dao/
│   ├── DAOFactory.java           Abstract Factory interface
│   ├── SQLiteDAOFactory.java     Factory implementation (SQLite)
│   ├── ServiceDAO.java           interface
│   ├── ExecutionDAO.java         interface
│   ├── OutputDAO.java            interface
│   ├── SQLiteServiceDAO.java     implementation (JDBC)
│   ├── SQLiteExecutionDAO.java   implementation
│   └── SQLiteOutputDAO.java      implementation
├── db/
│   └── DatabaseManager.java      Singleton — auto-creates tables on first use
├── config/
│   └── ConfigLoader.java         reads services.json (File I/O requirement)
└── exception/
    ├── ServiceException.java     abstract base
    ├── ConfigLoadException.java
    ├── ServiceNotFoundException.java
    ├── ServiceAlreadyRunningException.java
    ├── ServiceNotRunningException.java
    ├── InvalidCommandException.java
    └── DuplicateServiceException.java

src/main/resources/
├── services.json                 predefined seed services
└── com/servicemanager/gui/
    └── main-view.fxml            FXML layout (placeholder)
```

## `ServiceManager` API — What Your Controller Calls

### Service List & Status

```java
List<Service> getServices()           // returns all services with live status
```

`Service` model fields:

| Field | Type | Description |
|-------|------|-------------|
| `getName()` | `String` | Service name |
| `getCommand()` | `String` | Shell command to execute |
| `getWorkingDir()` | `String` | Working directory |
| `isRunning()` | `boolean` | Currently running? |
| `getStatus()` | `String` | "RUNNING" or "STOPPED" |
| `getLogs()` | `List<String>` | In-memory log lines for this session |

### Actions (all catch `ServiceException`)

| Method | Returns | Throws |
|--------|---------|--------|
| `startService(name)` | `String` | `ServiceNotFoundException`, `ServiceAlreadyRunningException` |
| `stopService(name)` | `String` | `ServiceNotFoundException`, `ServiceNotRunningException` |
| `restartService(name)` | `String` | Same as above |
| `getServiceStatus(name)` | `String` | `ServiceNotFoundException` |
| `getServiceLogs(name, lines)` | `String` | `ServiceNotFoundException` |
| `createCustomService(name, cmd, dir)` | `void` | `DuplicateServiceException`, `IllegalArgumentException` |

### DB Queries

```java
List<Execution> getExecutionHistory(String serviceName)
List<Output> getOutputs(int executionId)
```

## New Feature: Create Custom Service

**Flow:**
1. User fills in name + command + workingDir in a form/dialog
2. Controller calls `serviceManager.createCustomService(name, command, workingDir)`
3. On success, call `getServices()` again to refresh the list
4. On `DuplicateServiceException` or `IllegalArgumentException`, show error to user

**Validation already handled by logic:**
- Name must not be empty → `IllegalArgumentException`
- Command must not be empty → `IllegalArgumentException`
- Duplicate name (checked against JSON + DB) → `DuplicateServiceException`

## Database (SQLite — Hands Off)

- Auto-created as `unirep.db` in the project root on first run
- Three tables:

### `services` — user-created custom services
| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER | PK auto |
| name | TEXT | UNIQUE |
| command | TEXT | |
| working_dir | TEXT | default '' |

### `executions` — start/finish tracking
| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER | PK auto |
| service_name | TEXT | |
| start_time | TEXT | ISO-8601 |
| finish_time | TEXT | nullable |
| status | TEXT | RUNNING/FINISHED/FAILED |
| exit_code | INTEGER | nullable |

### `outputs` — stdout lines per execution
| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER | PK auto |
| execution_id | INTEGER | FK → executions |
| timestamp | TEXT | ISO-8601 |
| line | TEXT | |
| stream | TEXT | STDOUT/STDERR |

## Design Patterns Used

| Pattern | Where | Purpose |
|---------|-------|---------|
| **MVC** | Controller ↔ ServiceManager ↔ View | Overall architecture |
| **DAO** | ServiceDAO / SQLiteServiceDAO, etc. | Data access abstraction |
| **Singleton** | DatabaseManager | Single SQLite connection |
| **Abstract Factory** | DAOFactory / SQLiteDAOFactory | Pluggable DAO creation (swap databases) |
| **Strategy** | ServiceProcessor / JavaProcessor | Pluggable process backend |
| **Observer** | ServiceObserver / MainController | GUI auto-updates on service state changes |

## Exception Handling in Controller

```java
try {
    serviceManager.startService(name);
} catch (ServiceNotFoundException e) {
    // show "Service not found" alert
} catch (ServiceAlreadyRunningException e) {
    // show "Already running" alert  
} catch (ServiceException e) {
    // fallback — show e.getMessage()
}
```

All exceptions extend `ServiceException` and have `getRecoveryHint()` for user-facing suggestions.

## Tests Already Written (46 passing)

| Test Class | Count | Scope |
|-----------|-------|-------|
| `model.ServiceTest` | 7 | Model getters/setters/status/logs |
| `model.ExecutionTest` | 2 | Execution POJO |
| `model.OutputTest` | 2 | Output POJO |
| `config.ConfigLoaderTest` | 3 | JSON load edge cases |
| `dao.SQLiteServiceDAOTest` | 5 | CRUD on temp SQLite DB |
| `dao.SQLiteExecutionDAOTest` | 4 | Insert/query finish update |
| `dao.SQLiteOutputDAOTest` | 2 | Insert/query by execution |
| `service.ServicesLoaderTest` | 3 | JSON+DB merge logic |
| `service.ServiceManagerTest` | 11 | Business logic with mocks |
| `exception.ExceptionTest` | 7 | Message/hint/abstract check |

## Observer Pattern (Already Wired — Just Use It)

The `MainController` already implements `ServiceObserver` and auto-refreshes the table on any service event:

```java
// Add observer to any custom component:
serviceManager.addObserver(myObserver);

// Events fired:
// "started"  — service has been started
// "stopped"  — service has been stopped
// "finished" — service process exited (async)
// "created"  — new custom service created
```

The observer callback runs on the JavaFX thread (`Platform.runLater`), so you can safely update UI controls.

## UI Problem: Logging & Output

The current Logs button and output area feel bare. Think of a better way of logging and outputting service activity — live output while running, execution history, DB-stored logs per run, whatever you think makes the experience informative and polished.

1. **Replace `main-view.fxml`** with actual layout (service table, start/stop buttons, create dialog)
2. **Implement `MainController.java`** — inject `ServiceManager`, wire FXML actions to API
3. **Write UI tests** (Suggested names below):

```java
// Example UI test signatures:
public class MainControllerTest {
    @Test public void startButtonCallsServiceManagerStartService() { ... }
    @Test public void stopButtonCallsServiceManagerStopService() { ... }
    @Test public void createServiceDialogCallsCreateCustomService() { ... }
    @Test public void errorAlertShownWhenServiceNotFound() { ... }
    @Test public void serviceListRefreshesAfterCreate() { ... }
}
```

## Build Notes

- JDK 25 required
- No manual DB setup — SQLite creates `unirep.db` automatically
- No manual JAR setup — Maven handles all dependencies
- Run with: `mvn clean javafx:run`
