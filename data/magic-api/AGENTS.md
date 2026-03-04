# MAGIC-API DEFINITIONS

**Purpose:** Dynamic API definitions, datasources, and reusable functions for magic-api runtime.

## OVERVIEW

This directory contains all magic-api artifacts loaded at runtime. APIs are created/edited via the magic-api web UI (`/magic/web`), not by editing files directly.

## STRUCTURE

```
magic-api/
├── api/                    # API endpoint definitions (.ms files)
│   ├── 系统管理/            # System management APIs
│   │   ├── 用户管理/        # User management
│   │   ├── 角色管理/        # Role management
│   │   ├── 菜单管理/        # Menu management
│   │   ├── 组织机构/        # Organization
│   │   ├── 数据字典/        # Data dictionary
│   │   ├── 配置中心/        # Config center
│   │   ├── 文件管理/        # File management
│   │   ├── 富文本/          # Rich text
│   │   ├── 日志管理/        # Logs
│   │   └── 在线用户/        # Online users
│   ├── 数据管理/            # Data management APIs
│   └── OSS/                 # Object storage APIs
├── datasource/              # Database connection configs (.json)
│   └── *.json               # Named datasource definitions
└── function/                # Reusable function definitions
    ├── 配置中心/            # Config functions
    └── 权限/                # Permission functions
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| User CRUD APIs | `api/系统管理/用户管理/` |
| Role/Permission APIs | `api/系统管理/角色管理/` |
| Menu APIs | `api/系统管理/菜单管理/` |
| Organization APIs | `api/系统管理/组织机构/` |
| Dictionary APIs | `api/系统管理/数据字典/` |
| File upload APIs | `api/系统管理/文件管理/` |
| DB connections | `datasource/*.json` |

## CONVENTIONS

- **Editing**: Use magic-api web UI (`/magic/web`), not direct file edits
- **File format**: `.ms` files are magic-api script format (JSON-like)
- **Naming**: Chinese directory names match business domains
- **Backup**: magic-api auto-backups to `magic_backup_record_v2` table
- **Cache**: APIs cached for 1 hour (configurable in application.yml)

## ANTI-PATTERNS

- **DO NOT** edit `.ms` files manually - use the web UI
- **DO NOT** delete files directly - use magic-api delete function
- **DO NOT** skip backup - backups are versioned in DB

## NOTES

- Resource location configured via `magic-api.resource.location` in application.yml
- Backups stored in database table `magic_backup_record_v2`
- Show SQL: `magic-api.show-sql: true` for debugging
- SQL column case: camelCase (`magic-api.sql-column-case: camel`)
