# Nix Shell Setup for ServiceManager

## TL;DR

> **Quick Summary**: Create a Nix flake to provide JDK 25 and Maven for reproducible builds
> 
> **Deliverables**: 
> - flake.nix file for dependencies
> - Test shell works with `nix develop`
> - Verify build with `mvn compile`
> 
> **Estimated Effort**: Quick
> **Parallel Execution**: NO - sequential (small task)
> **Critical Path**: Create flake.nix → Test shell → Verify build

---

## Context

### Original Request
User wants a Nix shell for dependency management for their ServiceManager Java project.

### Project Analysis
- **Build Tool**: Maven (pom.xml present)
- **Java Version**: 17 in pom.xml, but user requested JDK 25
- **Dependencies**: 
  - SLF4J 2.0.9 (logging)
  - Logback 1.4.11 (logging impl)
  - Gson 2.10.1 (JSON)
  - JUnit 5.10.0 (testing via Maven)

### Technical Decisions
- **Nix Style**: Flakes (modern, reproducible)
- **Java**: JDK 25 (as requested)
- **Testing**: Via Maven (junit-jupiter already in pom.xml)

---

## Work Objectives

### Core Objective
Create a Nix flake that provides JDK 25 and Maven, enabling `nix develop` to enter a dev shell with all build dependencies.

### Concrete Deliverables
- `flake.nix` - Nix flake with JDK 25 + Maven
- Shell tested with `nix develop`
- Project compiles with `mvn compile`

### Definition of Done
- [ ] `flake.nix` file created
- [ ] `nix develop` enters shell successfully
- [ ] `mvn compile` produces .class files in target/

### Must Have
- JDK 25
- Maven (via nixpkgs)

### Must NOT Have
- Multiple nix files (keep it simple - single flake.nix)

---

## Execution Strategy

### Sequential (Single Task)
This is a small task - no parallelism needed.

---

## TODOs

- [ ] 1. Create flake.nix with JDK 25 and Maven

  **What to do**:
  - Create flake.nix file in project root
  - Use nixpksgs#jdk25 (or latest available)
  - Include nixpkgs#maven
  - Add overlay for easy updates

  **References**:
  - Nix Flakes documentation for structure
  - nixpksgs.jdk25 or nixpkgs.latest for Java

  **QA Scenarios**:

  ```
  Scenario: flake.nix is valid Nix flake
    Tool: Bash
    Preconditions: flake.nix exists
    Steps:
      1. Run: nix flake metadata
      2. Check output shows valid flake
    Expected Result: Valid flake metadata displayed
    Evidence: Terminal output showing flake is valid

  ```
  
  ```
  Scenario: nix develop enters shell
    Tool: interactive_bash (tmux)
    Preconditions: flake.nix is valid
    Steps:
      1. Run: nix develop
      2. Verify java -version shows JDK 25
      3. Verify mvn -version shows Maven
    Expected Result: Shell enters, java and mvn available
    Evidence: java -version and mvn -version output
  ```

  ```
  
  ```
  Scenario: Project compiles
    Tool: interactive_bash (tmux)
    Preconditions: Shell entered
    Steps:
      1. Run: mvn compile
      2. Check target/classes has .class files
    Expected Result: Compilation succeeds
    Evidence: target/classes/com/servicemanager/*.class exists
  ```

- [ ] 2. Document usage in README (optional)

  **What to do**:
  - Add Nix setup instructions to README.md
  - Document `nix develop` command

---

## Final Verification Wave

- [ ] F1. **Full Stack Test** — `quick`
  - Enter shell with `nix develop`
  - Run `mvn compile`
  - Run `mvn test` to verify JUnit works
  Output: Build [PASS/FAIL] | Tests [N pass/N fail] | VERDICT: APPROVE/REJECT

---

## Commit Strategy

- Message: `chore: add nix flake for development`
- Files: `flake.nix`
- Pre-commit: `nix build` to validate

---

## Success Criteria

### Verification Commands
```bash
nix develop  # Enters shell
java -version  # Shows JDK 25
mvn -version  # Shows Maven
mvn compile   # SUCCESS
mvn test     # All tests pass
```