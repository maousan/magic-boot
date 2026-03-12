# Backend Real-Time Log WebSocket API

## TL;DR

> **Quick Summary**: Implement a Spring Boot WebSocket endpoint for real-time log streaming to complement the frontend real-time log viewer. Use Apache Commons IO for efficient file tailing, Sa-Token for authentication, and native WebSocket protocol.
>
> **Deliverables**:
> - WebSocket configuration (`WebSocketConfiguration.java`)
> - Token authentication interceptor (`TokenHandshakeInterceptor.java`)
> - Log streaming handler (`LogWebSocketHandler.java`)
> - Log tailing utilities (`LogTailer.java`, `MultiLogTailerManager.java`)
> - Log filters (`LogLevelFilter.java`, `KeywordFilter.java`)
> - Session manager (`SessionManager.java`)
> - Maven dependencies (`spring-boot-starter-websocket`, `commons-io`)
>
> **Estimated Effort**: Medium
> **Parallel Execution**: YES - 3 waves
> **Critical Path**: Dependencies → Configuration → Interceptor → Handler → Tailers → Filters → SessionManager → QA

---

## Context

### Original Request
Implement a backend WebSocket endpoint for real-time log streaming. Complement the frontend real-time log viewer plan at `.sisyphus/plans/realtime-log-viewer.md`. Support 2 log types (application, error), initial 100-line snapshot, log level filtering, keyword filtering, and 30-second heartbeat.

### Interview Summary
**Key Discussions**:
- **Log Types**: 2 types only - application logs (`./logs/all.log`) and error logs (`./logs/error.log`)
- **Initial Lines**: 100 lines on connect (matches frontend)
- **Concurrent Users**: Medium scale (< 50)
- **Log Format**: Use existing Logback format (no JSON conversion)
- **Token Validation**: Validate only on connection (not per message)
- **Permission Scope**: All authenticated users can view both logs
- **File Missing Behavior**: Wait for file to be created (don't close connection)
- **Filter Logic**: AND logic (both level AND keyword must match)
- **Keyword Matching**: Case-insensitive
- **Test Strategy**: Manual QA only (no automated tests)

**Research Findings**:
- **Project Structure**: `org.ssssssss.magicboot` package, Spring Boot 3.1.2, Java 17
- **Auth**: Sa-Token 1.35.0.RC via `StpUtil.isLogin()` and `StpUtil.getLoginId()`
- **Logging**: Logback with `./logs/all.log` (all levels) and `./logs/error.log` (WARN+)
- **Rotation**: Daily + size-based (100MB max, 14 days history)
- **No WebSocket Config**: Must create from scratch
- **Dependencies**: Need to add `spring-boot-starter-websocket` and `commons-io`

### Metis Review
**Identified Gaps** (addressed):
- **Token parameter format**: URL params from handshake (wscat will show in history)
- **File permissions**: Handle gracefully - log error, close connection
- **Encoding issues**: Log error, skip line, don't crash
- **Log level extraction**: Parse from pipe-delimited format: `%d|%-5level|...`
- **Filter default**: Send all logs if filter not specified
- **Heartbeat direction**: Server sends PingMessage every 30s
- **Polling interval**: 100ms with configuration option
- **Buffer sizes**: 100 lines initial, 8KB buffer for streaming
- **Message size limit**: Split lines > 64KB into multiple messages
- **Multi-level handling**: ERROR filter includes only ERROR (not WARN)
- **Rate limiting**: No rate limiting (medium scale)
- **Connection timeout**: No hard timeout (use heartbeat)

---

## Work Objectives

### Core Objective
Create a complete backend WebSocket endpoint that streams log files in real-time to connected clients, with authentication, filtering, and session management.

### Concrete Deliverables
- `magic-boot-master/pom.xml` - Add WebSocket and Commons IO dependencies
- `src/main/java/org/ssssssss/magicboot/configuration/WebSocketConfiguration.java` - WebSocket config
- `src/main/java/org/ssssssss/magicboot/websocket/interceptor/TokenHandshakeInterceptor.java` - Auth interceptor
- `src/main/java/org/ssssssss/magicboot/websocket/handler/LogWebSocketHandler.java` - Main handler
- `src/main/java/org/ssssssss/magicboot/websocket/tailer/LogTailer.java` - Single log tailer
- `src/main/java/org/ssssssss/magicboot/websocket/tailer/MultiLogTailerManager.java` - Multi-log manager
- `src/main/java/org/ssssssss/magicboot/websocket/filter/LogLevelFilter.java` - Level filter
- `src/main/java/org/ssssssss/magicboot/websocket/filter/KeywordFilter.java` - Keyword filter
- `src/main/java/org/ssssssss/magicboot/websocket/session/SessionManager.java` - Session manager

### Definition of Done
- [ ] WebSocket endpoint `/ws/logs` is accessible and accepts connections
- [ ] Sa-Token authentication validates tokens on handshake
- [ ] Initial 100 lines are sent immediately after connection
- [ ] Real-time log streaming works (new logs appear in < 200ms)
- [ ] Log level filtering works (DEBUG, INFO, WARN, ERROR)
- [ ] Keyword filtering works (case-insensitive, AND logic with level)
- [ ] Both log types work (all.log and error.log)
- [ ] Heartbeat/ping-pong maintains connections (30s interval)
- [ ] Log rotation is handled (connections continue after rotation)
- [ ] Session cleanup works on disconnect
- [ ] Error cases handled gracefully (file missing, permissions, encoding)
- [ ] Manual QA passes all scenarios

### Must Have
- WebSocket endpoint at `/ws/logs` with native WebSocket protocol
- Sa-Token authentication via URL parameter `token`
- Apache Commons IO Tailer for log watching (100ms polling)
- ReversedLinesFileReader for initial 100-line snapshot
- Server-side filtering (log levels + keywords, case-insensitive, AND logic)
- 2 log types: application (all.log) and error (error.log)
- 30-second heartbeat with PingMessage/PongMessage
- ConcurrentHashMap thread-safe session management
- Log rotation handling (file size tracking)
- Wait for file creation if file doesn't exist
- All authenticated users can view both logs
- Error handling for file permissions, encoding issues

### Must NOT Have (Guardrails)
- ❌ Do NOT modify any log files (read-only access)
- ❌ Do NOT support arbitrary file paths (security risk - only all.log and error.log)
- ❌ Do NOT allow more than 100 initial lines
- ❌ Do NOT use STOMP protocol (native WebSocket only)
- ❌ Do NOT create Magic-API plugin (use standard Spring WebSocket)
- ❌ Do NOT change Logback configuration or log format
- ❌ Do NOT implement automated tests (manual QA only)
- ❌ Do NOT add authentication beyond Sa-Token
- ❌ Do NOT use file watching libraries other than Apache Commons IO

---

## Verification Strategy (MANDATORY)

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.
> Acceptance criteria requiring "user manually tests/confirms" are FORBIDDEN.

### Test Decision
- **Infrastructure exists**: NO (Spring Boot Test in pom.xml but no test files)
- **Automated tests**: None
- **Framework**: None (manual QA only)
- **Agent-Executed QA**: ALWAYS (mandatory for all tasks)

### QA Policy
Every task MUST include agent-executed QA scenarios.
Evidence saved to `.sisyphus/evidence/task-{N}-{scenario-slug}.{ext}`.

- **WebSocket**: Use Bash (curl/wscat) — Test connection, authentication, message exchange
- **Java/Backend**: Use Bash (mvn clean package, mvn spring-boot:run) — Build and run application
- **File Operations**: Use Bash (cat, tail, touch) — Test log file creation, modification, rotation
- **Process Management**: Use Bash (ps, kill, pkill) — Test application shutdown, restart

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Start Immediately — foundation + dependencies):
├── Task 1: Add Maven dependencies [quick]
├── Task 2: Create WebSocket configuration [quick]
└── Task 3: Create token handshake interceptor [quick]

Wave 2 (After Wave 1 — core components):
├── Task 4: Create SessionManager [quick]
├── Task 5: Create LogTailer [quick]
├── Task 6: Create LogLevelFilter [quick]
├── Task 7: Create KeywordFilter [quick]
└── Task 8: Create MultiLogTailerManager [quick]

- [x] 1. Add Maven dependencies

  **What to do**:
  - Add `spring-boot-starter-websocket` dependency to `magic-boot-master/pom.xml`
  - Add `commons-io` version 2.21.0 dependency to `magic-boot-master/pom.xml`
  - Verify no version conflicts with existing dependencies

  **Must NOT do**:
  - Do NOT add other WebSocket libraries (STOMP, Socket.io, etc.)
  - Do NOT add testing libraries (manual QA only)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple dependency addition, clear file location
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3)
  - **Blocks**: Tasks 2, 3 (config and interceptor need WebSocket dependency)
  - **Blocked By**: None (can start immediately)

  **References**:

  **Pattern References** (existing code to follow):
  - `magic-boot-master/pom.xml:29-32` - Dependency structure pattern

  **API/Type References** (contracts to implement against):
  - Spring Boot WebSocket Docs: https://docs.spring.io/spring-boot/reference/web/websocket.html
  - Apache Commons IO Docs: https://commons.apache.org/proper/commons-io/

  **WHY Each Reference Matters**:
  - `pom.xml`: Follows Maven dependency structure in this project
  - Spring Boot Docs: Ensures correct dependency version and configuration
  - Commons IO Docs: Ensures correct artifact ID and version

  **Acceptance Criteria**:

  **QA Scenarios (MANDATORY):**

  ```
  Scenario: Maven dependencies added successfully
    Tool: Bash (mvn dependency:tree)
    Preconditions:
      1. pom.xml has been updated
    Steps:
      1. Run: mvn dependency:tree
      2. Check output for: spring-boot-starter-websocket
      3. Check output for: commons-io:commons-io:jar:2.21.0
    Expected Result: Both dependencies appear in tree without conflicts
    Failure Indicators: Dependency not found, version conflicts, build errors
    Evidence: .sisyphus/evidence/task-1-dependencies.txt

  Scenario: Project builds successfully
    Tool: Bash (mvn clean package)
    Preconditions:
      1. Dependencies added to pom.xml
    Steps:
      1. Run: cd magic-boot-master && mvn clean package -DskipTests
      2. Check build output for: BUILD SUCCESS
    Expected Result: Project builds without errors
    Failure Indicators: BUILD FAILURE, dependency resolution errors
    Evidence: .sisyphus/evidence/task-1-build.txt
  ```

  **Evidence to Capture**:
  - [ ] Dependency tree output
  - [ ] Build output (success/failure)

  **Commit**: YES
  - Message: `feat(websocket): add Maven dependencies for WebSocket and Commons IO`
  - Files: `magic-boot-master/pom.xml`
  - Pre-commit: `mvn clean package -DskipTests`

  **Commit**: YES
  - Message: `feat(websocket): add Maven dependencies for WebSocket and Commons IO`
  - Files: `magic-boot-master/pom.xml`
  - Pre-commit: `mvn clean package -DskipTests`

---

- [x] 2. Create WebSocket configuration

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/configuration/WebSocketConfiguration.java`
  - Annotate class with `@Configuration` and `@EnableWebSocket`
  - Implement `WebSocketConfigurer` interface
  - Override `registerWebSocketHandlers(WebSocketHandlerRegistry registry)` method
  - Register WebSocket handler for path `/ws/logs`
  - Add `TokenHandshakeInterceptor` for authentication
  - Set allowed origins (configure for frontend access)
  - Register handler as bean (for dependency injection)

  **Must NOT do**:
  - Do NOT enable STOMP or other protocols (native WebSocket only)
  - Do NOT register other WebSocket paths

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Standard Spring WebSocket configuration pattern
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3)
  - **Blocks**: Task 10 (handler registration in config)
  - **Blocked By**: Task 1 (needs WebSocket dependency)

  **References**:
  - `src/main/java/org/ssssssss/magicboot/configuration/WebConfiguration.java` - Spring config pattern
  - Spring WebSocketConfigurer: https://docs.spring.io/spring-framework/reference/web/websocket.html#server-config


  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: WebSocket configuration bean registered
    Tool: Bash (mvn spring-boot:run + grep)
    Preconditions: 1. Application started successfully
    Steps:
      1. Run: mvn spring-boot:run > /dev/null 2>&1 &
      2. Wait 10 seconds for startup
      3. Run: grep -i "WebSocketConfiguration" logs/*.log
    Expected Result: WebSocketConfiguration initialized without errors
    Failure Indicators: Configuration not loaded, bean creation errors
    Evidence: .sisyphus/evidence/task-2-config-init.txt

  Scenario: WebSocket endpoint registered
    Tool: Bash (curl/wscat - connection test)
    Preconditions: 1. Application is running
    Steps:
      1. Run: curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Key: test" -H "Sec-WebSocket-Version: 13" http://localhost:8081/ws/logs
      2. Check response for: HTTP/1.1 101 Switching Protocols
    Expected Result: WebSocket handshake succeeds (101 response) or 403/401 (if auth fails)
    Failure Indicators: 404 Not Found (endpoint not registered), 500 Server Error
    Evidence: .sisyphus/evidence/task-2-endpoint-registered.txt
  ```

  **Commit**: YES
  - Message: `feat(websocket): add WebSocket configuration`
  - Files: `src/main/java/org/ssssssss/magicboot/configuration/WebSocketConfiguration.java`

---

- [x] 3. Create token handshake interceptor

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/websocket/interceptor/TokenHandshakeInterceptor.java`
  - Implement `HandshakeInterceptor` interface
  - Extract `token` parameter from WebSocket handshake URL query string
  - Validate token using `StpUtil.isLogin()` (Sa-Token API)
  - On validation failure: return `false` to reject connection
  - On validation success: store user info (loginId) in session attributes
  - Handle URL parsing errors gracefully

  **Must NOT do**:
  - Do NOT validate token on every message (only on handshake per user requirement)
  - Do NOT use HTTP headers (WebSocket handshake doesn't have them reliably)
  - Do NOT implement custom authentication (use Sa-Token only)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Standard Spring WebSocket interceptor pattern
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2)
  - **Blocks**: Task 9 (handler uses authenticated user info)
  - **Blocked By**: Task 1 (needs Sa-Token)

  **References**:
  - `src/main/java/org/ssssssss/magicboot/interceptor/PermissionInterceptor.java` - Sa-Token usage pattern
  - `src/main/java/org/ssssssss/magicboot/model/MagicBootConstants.java` - Constants pattern


  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Valid token allows connection
    Tool: Bash (wscat)
    Preconditions: 1. Application is running 2. Valid Sa-Token available
    Steps:
      1. Get valid token from application (login endpoint or database)
      2. Run: wscat -c "ws://localhost:8081/ws/logs?token={VALID_TOKEN}"
      3. Wait 5 seconds
      4. Check connection status
    Expected Result: Connection established successfully (connected state)
    Failure Indicators: Connection rejected immediately, 403/401 error
    Evidence: .sisyphus/evidence/task-3-valid-token.txt


  Scenario: Invalid token rejected
    Tool: Bash (wscat)
    Preconditions: 1. Application is running
    Steps:
      1. Run: wscat -c "ws://localhost:8081/ws/logs?token=INVALID_TOKEN"
      2. Wait 5 seconds
      3. Check connection status
    Expected Result: Connection rejected immediately (disconnected state or error)
    Failure Indicators: Connection succeeds with invalid token
    Evidence: .sisyphus/evidence/task-3-invalid-token.txt

  Scenario: Missing token rejected
    Tool: Bash (wscat)
    Preconditions: 1. Application is running
    Steps:
      1. Run: wscat -c "ws://localhost:8081/ws/logs"
      2. Wait 5 seconds
      3. Check connection status
    Expected Result: Connection rejected immediately
    Failure Indicators: Connection succeeds without token
    Evidence: .sisyphus/evidence/task-3-missing-token.txt
  ```

  **Commit**: YES
  - Message: `feat(websocket): add token handshake interceptor`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/interceptor/TokenHandshakeInterceptor.java`

  **Commit**: YES
  - Message: `feat(websocket): add token handshake interceptor`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/interceptor/TokenHandshakeInterceptor.java`

---

- [ ] 4. Create SessionManager

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/websocket/session/SessionManager.java`
  - Use `ConcurrentHashMap<String, WebSocketSession>` for thread-safe session storage
  - Implement methods: `addSession(session, userId)`, `removeSession(sessionId)`, `getSession(sessionId)`, `getAllSessions()`, `getSessionsByUser(userId)`
  - Implement `closeAll()` to cleanup all sessions on application shutdown
  - Use `ConcurrentWebSocketSessionDecorator` for thread-safe message sending
  - Handle null sessions gracefully

  **Must NOT do**:
  - Do NOT use regular `HashMap` (not thread-safe)
  - Do NOT persist sessions to database (in-memory only)
  - Do NOT use synchronization (use concurrent collections)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Thread-safe session management, standard concurrent pattern
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 5, 6, 7, 8)
  - **Blocks**: Tasks 8, 9 (MultiLogTailerManager and handler use SessionManager)
  - **Blocked By**: Task 1 (needs WebSocket dependency)

  **References**:
  - `src/main/java/org/ssssssss/magicboot/configuration/WebConfiguration.java` - Thread-safe bean pattern

  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Session storage thread-safe
    Tool: Bash (wscat + concurrent connections)
    Preconditions: 1. Application is running
    Steps:
      1. Start 5 concurrent wscat connections: for i in {1..5}; do wscat -c "ws://localhost:8081/ws/logs?token=VALID" & done
      2. Wait 10 seconds
      3. Check all connections are tracked
    Expected Result: All 5 sessions stored without errors or race conditions
    Failure Indicators: ConcurrentModificationException, lost sessions
    Evidence: .sisyphus/evidence/task-4-concurrent-sessions.txt

  Scenario: Session cleanup on disconnect
    Tool: Bash (wscat + grep)
    Preconditions: 1. 3 wscat connections are active
    Steps:
      1. Kill one wscat process
      2. Wait 5 seconds
      3. Check SessionManager size decreased by 1
    Expected Result: Session removed from storage
    Failure Indicators: Session not removed, memory leak
    Evidence: .sisyphus/evidence/task-4-session-cleanup.txt
  ```

  **Commit**: YES
  - Message: `feat(websocket): add session manager`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/session/SessionManager.java`

---

- [ ] 5. Create LogTailer

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/websocket/tailer/LogTailer.java`
  - Wrap Apache Commons IO `Tailer` with custom listener interface
  - Implement `startTailing(consumer)` method with 100ms polling interval
  - Implement `stopTailing()` method to cleanup resources
  - Implement `getLastNLines(n)` method using `ReversedLinesFileReader`
  - Handle log file rotation by tracking file position
  - Detect file truncation (rotation) and reset position
  - Handle file not found by waiting for file creation

  **Must NOT do**:
  - Do NOT use other file watching libraries (WatchService, etc.)
  - Do NOT modify log files (read-only)
  - Do NOT block waiting for file creation (use non-blocking tail)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Apache Commons IO Tailer wrapping, straightforward
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 4, 6, 7, 8)
  - **Blocks**: Task 8 (MultiLogTailerManager uses LogTailer)
  - **Blocked By**: Task 1 (needs commons-io dependency)

  **References**:
  - Apache Commons IO Tailer: https://commons.apache.org/proper/commons-io/javadocs/api-2.21.0/org/apache/commons/io/input/Tailer.html
  - Apache Commons IO ReversedLinesFileReader: https://commons.apache.org/proper/commons-io/javadocs/api-2.21.0/org/apache/commons/io/input/ReversedLinesFileReader.html

  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Read last 100 lines efficiently
    Tool: Bash (mvn spring-boot:run + echo + cat)
    Preconditions:
      1. Log file exists with > 100 lines
    Steps:
      1. Write 200 lines to logs/all.log: for i in {1..200}; do echo "$i: $(date)" >> logs/all.log; done
      2. Trigger LogTailer.getLastNLines(100)
      3. Check output for 100 lines
    Expected Result: Returns last 100 lines from file
    Failure Indicators: Returns wrong count, reads entire file inefficiently
    Evidence: .sisyphus/evidence/task-5-last-100-lines.txt

  Scenario: Detect log rotation
    Tool: Bash (mvn spring-boot:run + log rotation simulation)
    Preconditions:
      1. LogTailer is tailing a file
    Steps:
      1. While tailing, delete log file and recreate it: rm logs/all.log && touch logs/all.log
      2. Write new lines to new file
      3. Check if new lines appear in tailer output
    Expected Result: Tailer detects rotation, starts reading new file
    Failure Indicators: Tailer stuck on old file, new lines not appearing
    Evidence: .sisyphus/evidence/task-5-log-rotation.txt

  ```

  **Commit**: YES
  - Message: `feat(websocket): add log tailer wrapper`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/tailer/LogTailer.java`

---

- [ ] 6. Create LogLevelFilter

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/websocket/filter/LogLevelFilter.java`
  - Implement functional interface `BiConsumer<String, String>` (log line, enabled levels string)
  - Parse log level from pipe-delimited format: `%d|%-5level|...`
  - Extract level string from position 2 (0-indexed) after first pipe
  - Compare against enabled levels (comma-separated: DEBUG,INFO,WARN,ERROR)
  - Pass line to next consumer if level matches, otherwise skip
  - Handle malformed log lines gracefully (skip line, log error)
  - Handle missing level parameter (send all levels)

  **Must NOT do**:
  - Do NOT parse log level from regex (use string split)
  - Do NOT use enum for levels (string comparison is simpler)
  - Do NOT implement WARN->ERROR hierarchy (exact match only)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple string parsing, filtering logic
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 4, 5, 7, 8)
  - **Blocks**: Task 8 (MultiLogTailerManager uses LogLevelFilter)
  - **Blocked By**: Task 1 (needs WebSocket dependency)

  **References**:
  - `src/main/resources/logback-spring.xml` - Log format pattern reference

  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Filter by log level (ERROR only)
    Tool: Bash (echo test)
    Preconditions:
      1. LogLevelFilter created
    Steps:
      1. Input log lines: "2025-03-05 10:00:00|INFO|test", "2025-03-05 10:00:01|ERROR|test", "2025-03-05 10:00:02|WARN|test"
      2. Apply filter with levels="ERROR"
      3. Check output
    Expected Result: Only ERROR line passes through
    Failure Indicators: WARN or INFO lines pass through
    Evidence: .sisyphus/evidence/task-6-filter-level.txt

  Scenario: No level filter sends all
    Tool: Bash (echo test)
    Preconditions:
      1. LogLevelFilter created
    Steps:
      1. Input log lines with all levels
      2. Apply filter with levels="" or null
      3. Check output
    Expected Result: All lines pass through
    Failure Indicators: Lines filtered out incorrectly
    Evidence: .sisyphus/evidence/task-6-no-filter.txt
  ```

  **Commit**: YES
  - Message: `feat(websocket): add log level filter`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/filter/LogLevelFilter.java`

---

- [ ] 7. Create KeywordFilter

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/websocket/filter/KeywordFilter.java`
  - Implement functional interface for filtering with keyword string
  - Check if log line contains keyword (case-insensitive)
  - Pass line to next consumer if keyword found or keyword is empty
  - Skip line if keyword not found
  - Treat keyword as literal string (not regex)
  - Handle null/empty keyword (send all lines)

  **Must NOT do**:
  - Do NOT use regex for matching (use `String.contains()`)
  - Do NOT implement case-sensitive matching (case-insensitive per user requirement)
  - Do NOT support regex metacharacters as special

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple string contains, straightforward
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 4, 5, 6, 8)
  - **Blocks**: Task 8 (MultiLogTailerManager uses KeywordFilter)
  - **Blocked By**: Task 1 (needs WebSocket dependency)

  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Filter by keyword (case-insensitive)
    Tool: Bash (echo test)
    Preconditions:
      1. KeywordFilter created
    Steps:
      1. Input log lines: "database connection failed", "Database timeout", "system error"
      2. Apply filter with keyword="database"
      3. Check output
    Expected Result: Both "database" and "Database" lines pass through (case-insensitive)
    Failure Indicators: Only lowercase passes through, only exact match passes
    Evidence: .sisyphus/evidence/task-7-filter-keyword.txt

  Scenario: Empty keyword sends all
    Tool: Bash (echo test)
    Preconditions:
      1. KeywordFilter created
    Steps:
      1. Input log lines
      2. Apply filter with keyword="" or null
      3. Check output
    Expected Result: All lines pass through
    Failure Indicators: Lines filtered out incorrectly
    Evidence: .sisyphus/evidence/task-7-empty-keyword.txt
  ```

  **Commit**: YES
  - Message: `feat(websocket): add keyword filter`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/filter/KeywordFilter.java`

---

- [ ] 8. Create MultiLogTailerManager

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/websocket/tailer/MultiLogTailerManager.java`
  - Use `ConcurrentHashMap<String, LogTailer>` for tailers by log type
  - Implement `startTailing(logType, filePath, consumer)` method
  - Implement `stopTailing(logType)` method
  - Implement `stopAll()` method to cleanup on application shutdown
  - Support 2 log types: "application" (all.log) and "error" (error.log)
  - Manage LogTailer lifecycle (create, start, stop, cleanup)
  - Handle duplicate tail requests (reuse existing tailer)

  **Must NOT do**:
  - Do NOT support arbitrary file paths (security - only predefined types)
  - Do NOT allow more than 100 initial lines per type
  - Do NOT implement log file watching (use LogTailer wrapper)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Manager pattern, coordinates multiple tailers
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (with Tasks 4, 5, 6, 7)
  - **Blocks**: Task 9 (LogWebSocketHandler uses MultiLogTailerManager)
  - **Blocked By**: Tasks 4, 5, 6, 7 (uses SessionManager, LogTailer, filters)

  **References**:
  - `src/main/java/org/ssssssss/magicboot/websocket/tailer/LogTailer.java` - Tailer API

  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Tail multiple log types simultaneously
    Tool: Bash (mvn spring-boot:run + log generation)
    Preconditions:
      1. Application is running
    Steps:
      1. Start tailing for both "application" and "error" types
      2. Generate logs to both all.log and error.log
      3. Check if new lines from both files appear
    Expected Result: Both log types are tailed simultaneously
    Failure Indicators: Only one log type works, tailers interfere
    Evidence: .sisyphus/evidence/task-8-multi-tail.txt

  Scenario: Stop tailing for specific type
    Tool: Bash (mvn spring-boot:run)
    Preconditions:
      1. 2 tailers are active
    Steps:
      1. Call stopTailing("application")
      2. Write to logs/all.log
      3. Check if new lines appear
      4. Write to logs/error.log
      5. Check if new lines appear
    Expected Result: all.log lines stopped, error.log lines continue
    Failure Indicators: Both stopped, neither stopped
    Evidence: .sisyphus/evidence/task-8-stop-tail.txt
  ```

  **Commit**: YES
  - Message: `feat(websocket): add multi-log tailer manager`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/tailer/MultiLogTailerManager.java`

  **Commit**: YES
  - Message: `feat(websocket): add multi-log tailer manager`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/tailer/MultiLogTailerManager.java`

---

- [ ] 9. Create LogWebSocketHandler

  **What to do**:
  - Create `src/main/java/org/ssssssss/magicboot/websocket/handler/LogWebSocketHandler.java`
  - Extend `TextWebSocketHandler` for text-based WebSocket messages
  - Override `afterConnectionEstablished(WebSocketSession session)`:
    - Get user info from session attributes (set by interceptor)
    - Register session in SessionManager
    - Extract log type from URL query param (default: "application")
    - Extract log level filter from URL query param (optional, comma-separated)
    - Extract keyword filter from URL query param (optional)
    - Send initial 100 lines using LogTailer.getLastNLines()
    - Start tailing using MultiLogTailerManager
    - Schedule heartbeat every 30 seconds (send PingMessage)
  - Override `handleTextMessage(WebSocketSession session, TextMessage message)`:
    - Respond to client PING with PONG
    - Handle filter update messages (change level, change keyword)
  - Override `afterConnectionClosed(WebSocketSession session, CloseStatus status)`:
    - Remove session from SessionManager
    - Stop tailing for this session
    - Cancel heartbeat scheduler
    - Log disconnect event
  - Override `handleTransportError(WebSocketSession session, Throwable exception)`:
    - Remove session from SessionManager
    - Log error
    - Close session
  - Implement filtering chain: LogLevelFilter → KeywordFilter
  - Handle missing log file by waiting (do not close connection)
  - Handle log rotation gracefully (switch to new file)
  - Handle encoding errors (log error, skip line, continue)
  - Split lines > 64KB into multiple messages
  - Use `ConcurrentWebSocketSessionDecorator` for thread-safe sending

  **Must NOT do**:
  - Do NOT implement STOMP (native WebSocket only)
  - Do NOT support arbitrary file paths (only application/error)
  - Do NOT allow more than 100 initial lines
  - Do NOT implement reconnection logic (client handles that)
  - Do NOT store logs in memory (only session metadata)

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Complex integration handler, coordinates many components
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (after Wave 2)
  - **Blocks**: Task 10 (handler must exist to register)
  - **Blocked By**: Tasks 3, 4, 8 (uses interceptor, SessionManager, MultiLogTailerManager)

  **References**:
  - `src/main/java/org/ssssssss/magicboot/websocket/interceptor/TokenHandshakeInterceptor.java` - User info extraction
  - `src/main/java/org/ssssssss/magicboot/websocket/session/SessionManager.java` - Session management
  - `src/main/java/org/ssssssss/magicboot/websocket/tailer/MultiLogTailerManager.java` - Tailing API
  - `src/main/java/org/ssssssss/magicboot/websocket/filter/LogLevelFilter.java` - Level filtering
  - `src/main/java/org/ssssssss/magicboot/websocket/filter/KeywordFilter.java` - Keyword filtering
  - Spring WebSocketHandler: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/socket/handler/TextWebSocketHandler.html

  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Connection established with initial 100 lines
    Tool: Bash (wscat + log check)
    Preconditions:
      1. Application is running
      2. logs/all.log has > 100 lines
    Steps:
      1. Run: wscat -c "ws://localhost:8081/ws/logs?token=VALID&type=application" 2>&1 &
      2. Capture output to file
      3. Count received messages (should be 100)
    Expected Result: 100 messages received immediately after connect
    Failure Indicators: No initial lines sent, wrong count, connection fails
    Evidence: .sisyphus/evidence/task-9-initial-100.txt

  Scenario: Real-time log streaming
    Tool: Bash (wscat + log generation)
    Preconditions:
      1. wscat is connected and receiving logs
    Steps:
      1. Write new line: echo "$(date)|INFO|Test message" >> logs/all.log
      2. Wait 5 seconds
      3. Check wscat output
    Expected Result: New log line appears in wscat output within 5 seconds
    Failure Indicators: New line not appearing, delay > 5 seconds
    Evidence: .sisyphus/evidence/task-9-realtime-stream.txt

  Scenario: Log level filtering
    Tool: Bash (wscat)
    Preconditions:
      1. wscat is connected
    Steps:
      1. Run: wscat -c "ws://localhost:8081/ws/logs?token=VALID&type=application&level=ERROR" &
      2. Generate INFO and ERROR logs
      3. Check if only ERROR logs appear
    Expected Result: Only ERROR level logs received
    Failure Indicators: INFO logs received, ERROR logs missing
    Evidence: .sisyphus/evidence/task-9-level-filter.txt

  Scenario: Keyword filtering (case-insensitive, AND logic)
    Tool: Bash (wscat)
    Preconditions:
      1. wscat is connected
    Steps:
      1. Run: wscat -c "ws://localhost:8081/ws/logs?token=VALID&type=application&level=INFO&keyword=database" &
      2. Generate logs: "database connected", "Database timeout", "system started"
      3. Check which logs appear
    Expected Result: Only "database connected" and "Database timeout" appear (case-insensitive, matches keyword)
    Failure Indicators: "system started" appears, case-sensitive (uppercase not matched)
    Evidence: .sisyphus/evidence/task-9-keyword-filter.txt

  Scenario: Heartbeat maintains connection
    Tool: Bash (wscat + timeout)
    Preconditions:
      1. wscat is connected
    Steps:
      1. Keep wscat connection open for 2 minutes
      2. Monitor connection stability
    Expected Result: Connection stays alive due to periodic PingMessages
    Failure Indicators: Connection drops after idle timeout (usually 60s)
    Evidence: .sisyphus/evidence/task-9-heartbeat.txt

  Scenario: Session cleanup on disconnect
    Tool: Bash (wscat + session check)
    Preconditions:
      1. wscat is connected
    Steps:
      1. Record initial SessionManager size
      2. Kill wscat process
      3. Wait 5 seconds
      4. Check SessionManager size decreased by 1
    Expected Result: Session removed, tailer stopped
    Failure Indicators: Session not removed, memory leak, tailer still running
    Evidence: .sisyphus/evidence/task-9-session-cleanup.txt

  ```

  **Commit**: YES
  - Message: `feat(websocket): add log streaming handler`
  - Files: `src/main/java/org/ssssssss/magicboot/websocket/handler/LogWebSocketHandler.java`

---

- [ ] 10. Register handler in configuration

  **What to do**:
  - Update `src/main/java/org/ssssssss/magicboot/configuration/WebSocketConfiguration.java`
  - Create `@Bean` method `logWebSocketHandler()` that returns `LogWebSocketHandler`
  - Update `registerWebSocketHandlers()` to use the bean
  - Ensure handler is properly autowired with dependencies (SessionManager, MultiLogTailerManager)
  - Verify interceptor is still attached
  - Test that all beans are created correctly

  **Must NOT do**:
  - Do NOT create handler as anonymous inner class (must be bean)
  - Do NOT remove interceptor
  - Do NOT register other handlers

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple bean registration and configuration update
  - **Skills**: []
    - No special skills needed

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Wave 3 (after Wave 2)
  - **Blocks**: None (final task)
  - **Blocked By**: Tasks 2, 9 (config exists, handler exists)

  **References**:
  - `src/main/java/org/ssssssss/magicboot/configuration/WebSocketConfiguration.java` - Existing config
  - `src/main/java/org/ssssssss/magicboot/websocket/handler/LogWebSocketHandler.java` - Handler to register

  **Acceptance Criteria**:
  **QA Scenarios (MANDATORY):**
  ```
  Scenario: Handler bean registered
    Tool: Bash (mvn spring-boot:run + grep)
    Preconditions:
      1. Application is running
    Steps:
      1. Run: mvn spring-boot:run > /dev/null 2>&1 &
      2. Wait 10 seconds
      3. Run: grep -i "LogWebSocketHandler" logs/*.log
    Expected Result: LogWebSocketHandler bean created without errors
    Failure Indicators: Bean creation failed, dependency injection errors
    Evidence: .sisyphus/evidence/task-10-handler-bean.txt

  Scenario: Endpoint accessible
    Tool: Bash (wscat)
    Preconditions:
      1. Application is running
    Steps:
      1. Run: wscat -c "ws://localhost:8081/ws/logs?token=VALID" &
      2. Wait 5 seconds
      3. Check connection status
    Expected Result: Connection established successfully
    Failure Indicators: 404 Not Found (endpoint not registered), 500 Server Error
    Evidence: .sisyphus/evidence/task-10-endpoint-accessible.txt
  ```

  **Commit**: YES
  - Message: `feat(websocket): register log handler in config`
  - Files: `src/main/java/org/ssssssss/magicboot/configuration/WebSocketConfiguration.java`

---

Wave 3 (After Wave 2 — integration):
├── Task 9: Create LogWebSocketHandler [unspecified-high]
└── Task 10: Register handler in configuration [quick]

Wave FINAL (After ALL tasks — verification):
├── Task F1: Integration QA (manual) [unspecified-high]
├── Task F2: Error handling QA (manual) [unspecified-high]
├── Task F3: Performance QA (manual) [unspecified-high]
└── Task F4: Security QA (manual) [unspecified-high]

Critical Path: Task 1 → Task 2 → Task 3 → Task 4 → Task 5 → Task 8 → Task 9 → Task 10 → F1-F4
Parallel Speedup: ~60% faster than sequential
Max Concurrent: 3 (Waves 1 & 2), 1 (Wave 3)
```

### Dependency Matrix

- **1**: — — 2, 3
- **2**: 1 — 10
- **3**: 1 — 9
- **4**: 1 — 8, 9
- **5**: 1 — 8
- **6**: 1 — 8
- **7**: 1 — 8
- **8**: 4, 5, 6, 7 — 9
- **9**: 3, 8 — 10
- **10**: 2, 9 — F1-F4

### Agent Dispatch Summary

- **1**: **3** — T1 → `quick`, T2 → `quick`, T3 → `quick`
- **2**: **6** — T4 → `quick`, T5 → `quick`, T6 → `quick`, T7 → `quick`, T8 → `quick`
- **3**: **2** — T9 → `unspecified-high`, T10 → `quick`
- **FINAL**: **4** — F1, F2, F3, F4 → `unspecified-high`

---

## TODOs

---

## Final Verification Wave (MANDATORY — after ALL implementation tasks)

> 4 review agents run in PARALLEL. ALL must APPROVE. Rejection → fix → re-run.

- [ ] F1. **Integration QA** — `unspecified-high`
  Test end-to-end functionality: connection, authentication, initial snapshot, real-time streaming, filtering for both log types.
  Output: `Scenarios [N/N pass] | Integration [N/N] | VERDICT`

- [ ] F2. **Error Handling QA** — `unspecified-high`
  Test error cases: file not found, file permissions, encoding issues, log rotation, application shutdown, network interruption.
  Output: `Scenarios [N/N pass] | Edge Cases [N tested] | VERDICT`

- [ ] F3. **Performance QA** — `unspecified-high`
  Test performance: 10 concurrent connections, memory usage, high log volume, long log lines, heartbeat timing.
  Output: `Scenarios [N/N pass] | Performance [acceptable/unacceptable] | VERDICT`

- [ ] F4. **Security QA** — `unspecified-high`
  Test security: invalid token, missing token, path traversal, arbitrary file access, SQL injection in keyword.
  Output: `Scenarios [N/N pass] | Security [safe/unsafe] | VERDICT`

---

## Commit Strategy

- **Task 1**: `feat(websocket): add Maven dependencies for WebSocket and Commons IO` — pom.xml
- **Tasks 2-3**: `feat(websocket): add WebSocket config and auth interceptor` — WebSocketConfiguration.java, TokenHandshakeInterceptor.java
- **Tasks 4-8**: `feat(websocket): add session management, tailers, and filters` — 5 files
- **Tasks 9-10**: `feat(websocket): add log streaming handler and register` — LogWebSocketHandler.java, WebSocketConfiguration.java

---

## Success Criteria

### Verification Commands
```bash
# Check all files exist
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/configuration/WebSocketConfiguration.java
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/websocket/interceptor/TokenHandshakeInterceptor.java
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/websocket/handler/LogWebSocketHandler.java
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/websocket/tailer/LogTailer.java
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/websocket/tailer/MultiLogTailerManager.java
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/websocket/filter/LogLevelFilter.java
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/websocket/filter/KeywordFilter.java
ls magic-boot-master/src/main/java/org/ssssssss/magicboot/websocket/session/SessionManager.java

# Build project
cd magic-boot-master
mvn clean package -DskipTests

# Run application
mvn spring-boot:run

# Test WebSocket connection (in separate terminal)
wscat -c "ws://localhost:8081/ws/logs?token=VALID_TOKEN"
```

### Final Checklist
- [ ] All "Must Have" features implemented
- [ ] All "Must NOT Have" exclusions respected
- [ ] All integration QA scenarios pass
- [ ] All error handling scenarios pass
- [ ] All performance scenarios pass
- [ ] All security scenarios pass
- [ ] No scope creep detected
- [ ] All evidence files saved
