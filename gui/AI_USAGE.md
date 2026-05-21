# AI Usage Log

**Student:** Semion Andreev  
**Student ID:** 24283788  
**Project:** Service Manager — Project 2 (GUI + Database + Testing)

---

## Summary

GenAI tools were used throughout the development process to assist with architecture planning, code generation, debugging, and documentation. The following log documents the main prompts used, categorised by task type. All AI-generated outputs were critically reviewed, tested, and in many cases corrected or refined before inclusion.

---

## 1. Project Analysis & Architecture Planning

**Prompt:**  
"Analyse the existing CLI Service Manager project (PDC-SC-Project-1) and plan an architecture for porting it to a JavaFX GUI application using a three-layer architecture (GUI, business logic, data access). Evaluate database options and design patterns appropriate for the extension."

**AI Role:**  
Assisted in analysing the existing codebase structure and proposing a layered architecture. The AI suggested the initial package layout and design pattern selection (MVC, DAO, Singleton, Strategy, Factory).

**Human Role:**  
Evaluated the proposed architecture against assignment requirements, chose SQLite over Derby after lecturer consultation, decided against Hibernate to avoid unnecessary complexity, and validated the design pattern selections.

---

## 2. Database Schema Design

**Prompt:**  
"Design a SQLite database schema for the Service Manager application that stores: (1) custom user-created services, (2) execution history with start/finish timestamps, and (3) output logs per execution. The schema must work with JDBC and support the DAO pattern."

**AI Role:**  
Generated the initial table definitions (services, executions, outputs) with column types and constraints.

**Human Role:**  
Simplified the schema to match the specific requirements — removed unnecessary columns, adjusted data types, and ensured foreign key relationships were correct.

---

## 3. Implementation — Model & Exception Layer

**Prompt:**  
"Port the Service model class from Project 1 into the new GUI project structure. Create supporting model classes for Execution and Output. Port the exception hierarchy including ServiceException (abstract), ConfigLoadException, ServiceNotFoundException, ServiceNotRunningException, ServiceAlreadyRunningException, and InvalidCommandException."

**AI Role:**  
Generated the initial Java classes for models and exceptions, preserving the inheritance hierarchy and encapsulation patterns from Project 1.

**Human Role:**  
Added the DuplicateServiceException for the new custom service feature, verified all getter/setter methods aligned with the new database schema, and ensured exception recovery hints were meaningful.

---

## 4. Implementation — Database & DAO Layer

**Prompt:**  
"Create a DatabaseManager singleton class for SQLite that auto-creates tables on first connection. Implement DAO interfaces and SQLite implementations for services, executions, and outputs using JDBC."

**AI Role:**  
Generated the DatabaseManager singleton pattern, DAO interfaces, and SQLite JDBC implementations including INSERT, SELECT, UPDATE, and DELETE operations.

**Human Role:**  
Reviewed all SQL statements for correctness, added proper resource management (try-with-resources), fixed the `last_insert_rowid()` handling for execution IDs, and added the `updateFinish` method for tracking process completion.

---

## 5. Implementation — Business Logic Layer

**Prompt:**  
"Port the ServiceManager from Project 1 and extend it with: (a) a createCustomService method that validates inputs and persists to SQLite, (b) an async JavaProcessor that runs processes in a background thread and logs output to the database, and (c) a ServicesLoader that merges services from JSON configuration and the database."

**AI Role:**  
Generated the initial ServiceManager, ServicesLoader, ServiceProcessor interface, and the rewritten async JavaProcessor.

**Human Role:**  
Significantly restructured the JavaProcessor to run process I/O on a daemon thread (preventing GUI freeze), added proper error handling for process lifecycle, validated all input in createCustomService, wrote the duplicate-detection logic in ServicesLoader, and tested all integration points.

---

## 6. Implementation — Test UI

**Prompt:**  
"Create a simple JavaFX test UI with a TableView showing services, action buttons (Start, Stop, Restart, Status, Logs, Refresh), a create-service form, and an output TextArea. Wire it to the ServiceManager API."

**AI Role:**  
Generated the initial FXML layout and JavaFX controller with table bindings and button handlers.

**Human Role:**  
Rewrote the controller to use proper JavaFX patterns (ObservableList, PropertyValueFactory, selection listeners), added error handling for all user-facing operations, and ensured the UI correctly reflects the underlying state after each action.

---

## 7. Testing — JUnit 4 Test Suite

**Prompt:**  
"Create JUnit 4 tests for all layers: model POJOs, DAO CRUD operations against a temporary SQLite database, ServiceManager business logic with mocked dependencies (Mockito), ConfigLoader edge cases, exception classes, and ServicesLoader merge logic."

**AI Role:**  
Generated the initial test class structure and test method signatures for all layers.

**Human Role:**  
Corrected test assertions to match actual behaviour (e.g., `getLogs()` returns a reference, not a copy), fixed static imports for JUnit 4 compatibility, ensured proper test isolation with `@Before`/`@After` cleanup of temp databases, and verified all 46 tests pass reliably.

---

## 8. Debugging & Fixes

**Prompt (various):**  
- "Fix SLF4J: Failed to load class StaticLoggerBinder warning"  
- "Fix SQLite: Unknown module warning and enable-native-access"  
- "Service Manager loaded 0 services — fix ConfigLoader path resolution"  
- "Format output as 'Admin:%s service started' and '===== %s Logs ====='"

**AI Role:**  
Identified the root causes (transitive dependency conflicts, classpath vs module-path issues, file path vs classpath resource loading) and suggested fixes.

**Human Role:**  
Evaluated each proposed fix, implemented the correct solution (excluding conflicting SLF4J from sqlite-jdbc, adding proper JVM args, switching to classpath resource loading), and verified the fixes did not break existing tests.

---

## 10. Observer Pattern & Notification System

**Prompt:**  
"Add an Observer pattern to the Service Manager so the GUI automatically receives events when services start, stop, finish, or are created. The MainController should implement the observer interface and update the UI asynchronously."

**AI Role:**  
Generated the ServiceObserver interface, the observer list management in ServiceManager, the callback wiring in JavaProcessor's async thread, and the MainController implementation with Platform.runLater for thread safety.

**Human Role:**  
Reviewed the thread safety of CopyOnWriteArrayList vs synchronized blocks, ensured the DI constructor also initialises the observer list, verified that existing tests continue to pass without modification, and documented the Observer API in TEAMMATE_REFERENCE.md for the UI teammate.

---

## 11. Documentation

**Prompt:**  
"Create developer documentation for my teammate covering the ServiceManager API, database schema, design patterns, and testing guidance."

**AI Role:**  
Generated the initial TEAMMATE_REFERENCE.md with structured sections.

**Human Role:**  
Rewrote sections to match the actual implementation, added specific method signatures and return types, verified all code examples were correct, and added testing guidance with concrete test case examples.

---

## Appendix: Key Design Decisions

| Decision | AI Suggested | Human Decision | Rationale |
|---|---|---|---|
| Database | Derby | SQLite | Lecturer permitted SQLite as alternative; simpler setup |
| ORM | Hibernate | JDBC | 3 small tables didn't justify Hibernate complexity |
| Process execution | Blocking | Async (daemon thread) | Prevent GUI freeze during long-running services |
| Service loading | JSON only | JSON + DB merge | Preserve Project 1 File I/O requirement + add DB persistence |
| ConfigLoader | File path | Classpath resource | Reliable path resolution across different run environments |
