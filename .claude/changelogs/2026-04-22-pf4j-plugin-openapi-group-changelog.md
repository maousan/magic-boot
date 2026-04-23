# 2026-04-22 pf4j-plugin-openapi-group changelog

## 变更内容
- 新增 PF4J 插件接口 OpenAPI 分组配置：`pf4j-plugin-api`。
- 路径匹配基于 `plugin.apiPrefixTemplate`（默认映射到 `/plugin/*/api/**`）。
- 保留现有 magic-api OpenAPI 输出，不影响原 `/v3/api-docs/magic-api/openapi.json`。

## 影响
- Swagger UI 中可以独立查看插件 Controller 接口文档。
- 插件接口与 magic-api 脚本文档不再混淆。
