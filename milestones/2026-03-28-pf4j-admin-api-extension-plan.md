# PF4J 插件管理面接口扩展计划（P0+P1）

## 日期
- 2026-03-28

## 目标
- 补齐插件管理后端的运行态观测、同步治理、启用禁用、审计与健康检查能力。
- 保持现有接口兼容，统一返回结构 `{code,message,data}`。

## 本轮范围
- P0：runtime summary/detail、scan-new、sync、install-by-path、reconcile
- P1：enable/disable、audit list、health
- P2：预校验与批量操作仅文档占位，不在本轮实现

## 关键实现
1. Service 新增：
- `getRuntimeSummary`
- `getRuntimePluginInfo`
- `reconcilePlugins(dryRun)`
- `enablePlugin`/`disablePlugin`
- `getPluginHealth`
- `listAuditLogs`

2. Controller 新增：
- `GET /plugin/admin/runtime/summary`
- `GET /plugin/admin/runtime/{pluginId}`
- `POST /plugin/admin/scan-new`
- `POST /plugin/admin/sync`
- `POST /plugin/admin/install/by-path`
- `POST /plugin/admin/reconcile`
- `POST /plugin/admin/enable/{pluginId}`
- `POST /plugin/admin/disable/{pluginId}`
- `GET /plugin/admin/health/{pluginId}`
- `GET /plugin/admin/audit/list`

3. 测试与脚本：
- 补充 Service/Controller 单测
- 扩展 `http/test-plugin-api.http`

## 验收标准
- 新增接口可调用，旧接口行为不变
- `reconcile dryRun=true` 不产生落库写入
- 同步类接口重复执行保持幂等
- 单测通过
