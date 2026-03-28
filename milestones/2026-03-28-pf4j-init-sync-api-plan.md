# PF4J 手动初始化同步接口计划

## 日期
- 2026-03-28

## 背景
- 已支持启动时自动补录运行态插件到 DB。
- 仍需要在运行中提供“手动触发补录”能力，方便运维即时修复数据一致性。

## 目标
- 新增管理接口：`POST /plugin/admin/init-sync`
- 调用现有服务方法 `initMissingPluginsFromRuntime()` 执行增量补录。
- 返回补录统计（运行态总数、补录数、跳过数）。

## 实施步骤
1. 在 `PluginAdminController` 增加 `init-sync` 端点。
2. 复用 `PluginManagerService#initMissingPluginsFromRuntime`。
3. 新增单元测试验证成功/异常分支。
4. 在 `http/test-plugin-api.http` 增加接口测试脚本。

## 风险与规避
- 风险：重复触发导致重复入库。
  - 规避：服务层已按 `pluginId` 去重，仅补录缺失记录。

