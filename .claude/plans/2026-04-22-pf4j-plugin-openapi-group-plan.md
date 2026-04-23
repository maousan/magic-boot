# 2026-04-22 pf4j-plugin-openapi-group plan

## 目标
- 让 PF4J 动态插件接口在 springdoc 中可见，并与 magic-api 脚本文档分离展示。

## 方案
1. 在 `magic-boot-master` 新增 `GroupedOpenApi` 配置。
2. 默认按 `plugin.apiPrefixTemplate` 生成路径匹配（如 `/plugin/*/api/**`）。
3. 增加独立 group 名称 `pf4j-plugin-api`。
4. 编译验证 `magic-boot-master` 模块。
