# MAGIC-BOOT PROJECT KNOWLEDGE BASE

**Generated:** 2026-03-04
**Commit:** 5abf9b5
**Branch:** master

## OVERVIEW

基于 magic-api 的快速开发平台。Spring Boot 3.1.2 + Java 17 后端，Vue 3 + naive-ui 前端。支持在浏览器中动态编写 API 和 Vue 组件，实现即改即生效的快速开发。

## STRUCTURE

```
magic-boot/
├── magic-boot-master/      # 主应用模块（Spring Boot 入口）
├── magic-api-plugins/      # magic-api 扩展插件集
│   ├── magic-editor/       # 编辑器前端资源
│   ├── magic-api-plugin-redis/   # Redis 插件
│   ├── magic-api-plugin-mqtt/    # MQTT 插件
│   └── magic-api-plugin-job/     # 定时任务插件
├── data/magic-api/         # 运行时 API 定义（通过 Web UI 编辑）
└── db/                     # 数据库初始化脚本
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| 应用入口 | `magic-boot-master/src/main/java/.../MagicBootApplication.java` |
| 主配置 | `magic-boot-master/src/main/resources/application.yml` |
| API 定义 | `data/magic-api/api/` （通过 `/magic/web` UI 编辑） |
| 数据源配置 | `data/magic-api/datasource/*.json` |
| 可复用函数 | `data/magic-api/function/` |
| 插件扩展 | `magic-api-plugins/magic-api-plugin-*/` |
| Docker 部署 | `Dockerfile`, `docker-compose.yml` |

## CONVENTIONS

### 构建流程
- **Maven 多模块**：根 pom.xml 聚合 `magic-api-plugins` 和 `magic-boot-master`
- **前端构建集成**：插件通过 `exec-maven-plugin` 调用 `npm install && npm run build`
- **Profile 控制**：
  - `dev` profile：`skip.npm=true`，跳过前端构建
  - `prod` profile：`skip.npm=false`，执行完整前端构建

### 代码风格
- 前端：Vue 3 + Vite，无 ESLint/Prettier 强制配置
- 后端：标准 Spring Boot 风格

### 环境变量
- 参考 `.env.example`：`MYSQL_*`、`REDIS_PASSWORD`、`SPRING_PROFILES_ACTIVE`、`JAVA_OPTS`
- 通过 `SPRING_PROFILES_ACTIVE` 切换环境配置

## ANTI-PATTERNS

- **DO NOT** 直接编辑 `data/magic-api/api/*.ms` 文件 — 使用 `/magic/web` Web UI
- **DO NOT** 删除 magic-api 文件 — 使用 magic-api 删除功能（自动备份）
- **DO NOT** 跳过备份 — 备份存储在 `magic_backup_record_v2` 表中

## COMMANDS

```bash
# 开发环境构建（跳过前端）
mvn clean package -Pdev -DskipTests

# 生产环境构建（完整构建）
mvn clean package -Pprod -DskipTests

# Docker 部署
docker-compose up -d

# 仅启动基础服务（MySQL + Redis）
docker-compose up -d mysql redis
```

## NOTES

- **Magic-API Web UI**：`http://localhost:8081/magic/web`
- **API 缓存**：默认 1 小时（`magic-api.cache.ttl: 3600000`）
- **SQL 调试**：`magic-api.show-sql: true`
- **逻辑删除列**：`is_del`，值为 `1` 表示已删除
- **Token 配置**：Sa-Token，30 天有效期，UUID 风格
- **文件上传限制**：200MB
- **数据库监控**：`/druid/*`（admin/123456）
