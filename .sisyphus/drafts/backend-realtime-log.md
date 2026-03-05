# Draft: Backend Real-time Log WebSocket API

## Requirements (confirmed)
- Create backend WebSocket endpoint for real-time log streaming
- Complement frontend plan at `.sisyphus/plans/realtime-log-viewer.md`
- Frontend expects: `ws://host/ws/logs?token=xxx`

## Technical Decisions
- **WebSocket Protocol**: Native WebSocket (not STOMP) - matches frontend
- **Auth Mechanism**: Sa-Token via URL parameter `token`
- **Log Tailing**: Apache Commons IO `Tailer` (production-ready, simple)
- **Log Reading**: `ReversedLinesFileReader` for initial 100 lines
- **Session Management**: `ConcurrentHashMap` thread-safe
- **Heartbeat**: 30-second ping-pong interval
- **Rotation Handling**: Track file size, detect truncation
- **Log Filtering**: Server-side filter chains (log levels, keywords)
- **Multi-log Support**: One handler per log type, managed by MultiLogTailerManager

## Research Findings

### 1. Project Structure
- **Package**: `org.ssssssss.magicboot`
- **Main App**: `magic-boot-master/src/main/java/org/ssssssss/magicboot/MagicBootApplication.java`
- **Config Package**: `configuration/` (no existing WebSocket config)
- **Auth**: Sa-Token via `StpUtil.isLogin()` and `StpUtil.getLoginId()`
- **Logging Framework**: Logback (`logback-spring.xml`)
- **Log Files**: 
  - `./logs/all.log` - All logs (async, queueSize=1024)
  - `./logs/error.log` - WARN and above
- **No WebSocket dependency**: Must add `spring-boot-starter-websocket`

### 2. Logback Configuration Details
- Rotation: Daily with SizeAndTimeBasedRollingPolicy
- Max file size: 100MB
- Max history: 14 days
- Total cap: 2GB
- Pattern: ReqId, TraceId/SpanId, timestamp, level, logger, message

### 3. Recommended Implementation Pattern
```
org.ssssssss.magicboot/
├── configuration/
│   └── WebSocketConfiguration.java    [NEW]
├── websocket/
│   ├── handler/
│   │   └── LogWebSocketHandler.java    [NEW]
│   ├── interceptor/
│   │   └── TokenHandshakeInterceptor.java [NEW]
│   ├── tailer/
│   │   ├── LogTailer.java             [NEW]
│   │   └── MultiLogTailerManager.java   [NEW]
│   ├── filter/
│   │   ├── LogLevelFilter.java         [NEW]
│   │   └── KeywordFilter.java          [NEW]
│   └── session/
│       └── SessionManager.java          [NEW]
└── utils/
    └── ... existing utilities
```

### 4. Dependencies to Add
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.21.0</version>
</dependency>
```

### 5. Key Implementation Patterns

**WebSocket Auth**: Extract token from URL query param, validate via `StpUtil.isLogin()`

**Log Tailing**: Apache Commons IO `Tailer.builder()` with 100ms delay

**Initial Snapshot**: `ReversedLinesFileReader` to read last 100 lines efficiently

**Rotation**: Track file position, detect when file shrinks (rotated)

**Filtering**: Chain of predicates (log level + keyword)

**Heartbeat**: `PingMessage` every 30s, expect `PongMessage`

**Session Management**: `ConcurrentHashMap<String, WebSocketSession>`

## Open Questions
- Test strategy for backend implementation

## Test Infrastructure
- **Testing framework**: Spring Boot Test included in pom.xml (no test files exist yet)
- **Current state**: No test files in project
- **Test patterns**: Not established (no existing tests to follow)

## Log Types (User Confirmed)
- **Application logs**: `./logs/all.log` - All log levels
- **Error logs**: `./logs/error.log` - WARN and above only
- **Access logs**: Not supported (excluded per user)
## Scope Boundaries
- INCLUDE:
  - WebSocket endpoint `/ws/logs`
  - Sa-Token authentication via URL parameter
  - Apache Commons IO Tailer for log watching
  - Initial 100-line snapshot on connect
  - Log level filtering (DEBUG, INFO, WARN, ERROR)
  - Keyword filtering
  - Multi-log-type support (application, error, access)
  - Heartbeat/ping-pong (30s interval)
  - Log rotation handling
  - Session management and cleanup
  - Error handling for disconnections

- EXCLUDE:
  - Frontend implementation (separate plan exists)
  - STOMP protocol (use native WebSocket)
  - Magic-API plugin (use standard Spring WebSocket)
  - Log storage/archival (read-only access)
  - Log modification/rewriting (read-only streaming)
