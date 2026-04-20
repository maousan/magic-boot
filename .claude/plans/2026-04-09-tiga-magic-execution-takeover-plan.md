# 2026-04-09 Tiga Engine 接管 MagicScript 执行链路计划

## 目标

在不修改 `magic-api-parent` 的前提下，仅通过 `magic-boot-master` 将 magic-api 的脚本执行入口切换到 `EngineManager.execute("magic", ...)`，并保持现有 HTTP 接口行为不变。

## 实施项

1. 新增 `TigaMagicAPIService`，实现 `MagicAPIService`，替换核心执行入口。
2. 新增 `TigaMagicApiConfiguration`，通过条件装配与 `@Primary` 覆盖默认 `MagicAPIService` Bean。
3. 新增模块桥接初始化，在应用启动后把 `MagicResourceLoader` 已注册模块同步到 Tiga 全局模块。
4. 新增配置项 `tiga.magic.timeout-ms`，控制统一脚本超时。
5. 保持调试链路不变，不改 `magic-api-parent`、`magic-script`、`tiga-engine`。

## 验证

- 编译验证：`mvn -pl magic-boot-master -am -DskipTests compile`
- 运行验证由业务方按现有流程执行接口回归与超时场景校验。

