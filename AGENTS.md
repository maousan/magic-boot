# MAGIC-BOOT KNOWLEDGE BASE

**Generated:** 2026-03-03
**Commit:** 8bcd2b9
**Branch:** master

## OVERVIEW

Spring Boot backend + magic-api rapid development platform. Dynamic APIs defined via magic-api UI, minimal Java code. Frontend in separate repo (magic-boot-naive).

**Stack:** Java 17, Spring Boot 3.1.2, magic-api 2.2.2, MySQL, Druid, Sa-Token, Hutool

## STRUCTURE

```
magic-boot/
├── src/main/java/          # Java config (minimal, ~16 files)
├── src/main/resources/     # Spring config, application.yml
├── data/magic-api/         # ⚡ CORE: Dynamic API definitions (see AGENTS.md)
│   ├── api/                # API endpoints (.ms files)
│   ├── datasource/         # DB connection configs
│   └── function/           # Reusable functions
├── db/                     # SQL scripts, migrations
└── pom.xml                 # Maven build
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Add/modify API | `data/magic-api/api/` | Via magic-api UI at `/magic/web` |
| DB connections | `data/magic-api/datasource/` | JSON configs |
| Auth/interceptors | `src/main/java/.../interceptor/` | PermissionInterceptor |
| Custom functions | `src/main/java/.../extension/` | ResponseFunctionExtension |
| Spring config | `src/main/resources/application*.yml` | Multi-profile (dev/online/demo) |
| Constants | `src/main/java/.../model/MagicBootConstants.java` | App constants |

## CODE MAP

| Symbol | Type | Location | Role |
|--------|------|----------|------|
| MagicBootApplication | Class | `.../MagicBootApplication.java` | Spring entry point |
| MagicBootConfiguration | Class | `.../configuration/` | magic-api integration |
| PermissionInterceptor | Class | `.../interceptor/` | Auth checking |
| WebConfiguration | Class | `.../configuration/` | CORS, static resources |
| Global | Class | `.../model/` | Global state holder |
| StatusCode | Class | `.../model/` | Response codes |

## CONVENTIONS

- **API Logic**: Write in magic-api UI, not Java. Most business logic lives in `data/magic-api/api/`
- **Response Format**: `{ code: 200, message: "...", data: {...} }` (see StatusCode)
- **Auth**: Sa-Token via `token` header; permission checked in PermissionInterceptor
- **Naming**: Chinese directory names in `data/magic-api/` (系统管理, 数据管理, etc.)
- **Profiles**: `dev` (8089), `demo` (readonly), `online` (production)

## ANTI-PATTERNS

- **NEVER** commit `target/` - add to .gitignore
- **NEVER** hardcode DB credentials - use environment variables
- **NEVER** write business logic in Java unless extending magic-api
- **NEVER** skip PermissionInterceptor for protected endpoints

## COMMANDS

```bash
# Build
mvn clean package -DskipTests

# Run (dev profile)
mvn spring-boot:run

# Run JAR
java -jar target/magic-boot.jar --spring.profiles.active=dev

# Magic-API Editor
http://localhost:8089/magic/web
# Credentials: admin / admin123456
```

## NOTES

- Druid Monitor: `/druid/*` (admin/123456)
- File upload limit: 200MB
- Token timeout: 30 days
- Logical delete column: `is_del`
- magic-api cache: 1 hour TTL
