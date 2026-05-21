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
├── App.java                      ← JavaFX Application entry point
├── Launcher.java                 ← main() workaround for module-path
├── controller/
│   └── MainController.java       ← YOUR DOMAIN — JavaFX Controller
├── model/
│   ├── Service.java              ← service data (name, command, workingDir, state)
│   ├── Execution.java            ← DB execution record (start/finish times)
│   └── Output.java               ← DB output line record
├── service/
│   ├── ServiceManager.java       ← MAIN HUB — call this from your controller
│   ├── ServicesLoader.java       ← merges JSON + DB services on startup
│   ├── ServiceProcessor.java     ← interface for process lifecycle
│   ├── JavaProcessor.java        ← async process launcher (background thread)
│   └── ServiceObserver.java      ← Observer interface for event notifications
├── dao/
│   ├── DAOFactory.java           ← Abstract Factory interface
│   ├── SQLiteDAOFactory.java     ← Factory implementation (SQLite)
│   ├── ServiceDAO.java           ← interface
│   ├── ExecutionDAO.java         ← interface
│   ├── OutputDAO.java            ← interface
│   ├── SQLiteServiceDAO.java     ← implementation (JDBC)
│   ├── SQLiteExecutionDAO.java   ← implementation
│   └── SQLiteOutputDAO.java      ← implementation
├── db/
│   └── DatabaseManager.java      ← Singleton — auto-creates tables on first use
├── config/
│   └── ConfigLoader.java         ← reads services.json (File I/O requirement)
└── exception/
    ├── ServiceException.java     ← abstract base
    ├── ConfigLoadException.java
    ├── ServiceNotFoundException.java
    ├── ServiceAlreadyRunningException.java
    ├── ServiceNotRunningException.java
    ├── InvalidCommandException.java
    └── DuplicateServiceException.java

src/main/resources/
├── services.json                 ← predefined seed services
└── com/servicemanager/gui/
    └── main-view.fxml            ← YOUR DOMAIN — FXML layout (placeholder)
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

## UI Problem: Real-Time Logs & Output

The current `Logs` button and output area are **too basic**. Here's what needs fixing:

### Problem
1. **Logs button** calls `getServiceLogs(name, 50)` which returns in-memory log lines — but only the last 50, and they're already visible in the output area
2. **Output area** is a plain TextArea that just appends result strings — it doesn't show live streaming output while a service runs
3. **No execution context** — you can't see each run's output separately or browse historical output from the database

### Task Requirements
Your job is to design and implement a **better output/logs experience** in the UI. Some ideas:

1. **Live output viewer**: When you click "Start" (or "Logs"), open a new tab/window that shows the service's in-memory log lines, refreshing every 500ms via `Timeline` or `AnimationTimer`
2. **Execution history panel**: Add a list/table showing all past executions for the selected service (use `getExecutionHistory(name)`), with start time, finish time, and exit code
3. **Execution detail view**: When an execution is selected, show its output lines (use `getOutputs(executionId)`) in a scrollable text area
4. **"Follow" mode**: When a service is running, auto-scroll to the latest output

### Useful API
```java
// Returns List<Execution> with start/finish times, status, exit code
serviceManager.getExecutionHistory("service-name")

// Returns List<Output> with timestamped lines
serviceManager.getOutputs(executionId)

// In-memory logs from current session
service.getLogs(maxLines)
```

### What to Improve
- Replace the plain TextArea with a TabPane or SplitPane separating "Output" and "Execution History"
- Show live output for the currently selected running service
- Let users click an old execution to see its DB-stored output
- Make the UI feel responsive and informative when a service runs

This is your main design challenge. The logic layer already stores everything in the DB — you just need to surface it well.

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
