# PF4J 启动时插件元数据初始化计划

## 日期
- 2026-03-27

## 背景
- 当前插件目录 `plugins/` 存在有效插件时，PF4J 可自动加载运行。
- 但数据库 `magic_plugin` 可能为空，导致管理列表与运行态不一致。

## 目标
- 在系统启动后执行一次“增量同步”：
  - 将“运行态已加载但 DB 缺失”的插件补录到 `magic_plugin`。
  - 不覆盖已有 DB 记录，避免破坏人工维护状态。

## 设计方案
1. 新增配置开关：`plugin.init-sync-on-startup`（默认 `true`）。
2. 新增启动初始化器（`ApplicationRunner`）：
   - 在 `plugin.enabled=true` 且 `plugin.init-sync-on-startup=true` 时执行。
3. 在 `PluginManagerService` 增加“运行态补录”方法：
   - 遍历 `pluginManager.getPlugins()`。
   - 比对 DB 中 `pluginId`。
   - 仅对缺失记录执行 `insert`。
   - 状态字段按运行态映射（`STARTED/STOPPED/DISABLED/CREATED`）。
4. 输出启动日志：总运行态插件数、补录数、跳过数。

## 风险与规避
- 风险：启动期并发写入。
  - 规避：初始化只执行一次，且按插件 ID 判断缺失再写入。
- 风险：JAR 路径无法可靠回填。
  - 规避：优先使用 PF4J `pluginPath`，无法获取则留空。

## 验证计划
- 单元测试：
  - 运行态有插件、DB为空 -> 补录成功。
  - 运行态有插件、DB已有同 ID -> 不重复写入。
- 手工验证：
  - 启动后调用 `/plugin/admin/list`，确认插件可见且 `runtimeState` 正确。

