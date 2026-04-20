# 2026-04-09 Docker 部署 Flyway 就绪调整

## 变更摘要

- 明确生产配置启用 Flyway：
  - `magic-boot-master/src/main/resources/application-prod.yml`
  - `spring.flyway.enabled: true`
- Docker 启动参数增加 Flyway 开关透传：
  - `docker-compose.yml`
  - `SPRING_FLYWAY_ENABLED=${SPRING_FLYWAY_ENABLED:-true}`
- 环境变量示例增加 Flyway 开关：
  - `.env`
  - `SPRING_FLYWAY_ENABLED=true`

## 预期效果

- 容器启动时可通过环境变量显式控制 Flyway 是否执行。
- 在 `prod` profile 下，默认可执行迁移与种子脚本（按项目现有 locations 配置）。

## 注意事项

- 当前 `docker-compose.yml` 仍挂载 `./db` 到 MySQL 初始化目录。
- 若首次建库同时使用 MySQL 初始化 SQL 与 Flyway，请确认两者无重复建表/重复数据冲突。
