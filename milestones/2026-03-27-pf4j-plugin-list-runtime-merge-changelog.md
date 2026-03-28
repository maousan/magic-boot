# PF4J 插件列表运行态合并变更日志

## 变更日期
- 2026-03-27

## 变更背景
- 现场现象：`DemoApiInterceptor` 可执行，但插件管理列表可能显示“未加载/无记录”。
- 根因：插件列表接口以数据库记录为主，未覆盖 PF4J 自动加载后的运行态插件。

## 变更内容
- 调整 `PluginManagerService#listAllPlugins`：
  - 以数据库插件为基础集合；
  - 合并 PF4J `pluginManager.getPlugins()` 运行态插件；
  - 同 `pluginId` 时不重复，优先保留数据库主数据，并刷新 `runtimeState`；
  - 对“仅运行态、未入库”的插件，补齐列表展示字段，避免不可见。

## 影响范围
- 插件管理列表接口 `/plugin/admin/list` 的返回结果更准确。
- 不影响插件安装、启动、停止、卸载流程逻辑。

## 回归关注点
- 同插件 ID 的去重行为是否稳定。
- 未入库插件的状态显示是否符合预期（`runtimeState` 与 `statusEnum`）。

## 验证
- 新增单元测试：
  - `listAllPlugins_运行态存在但数据库不存在时_应返回运行态插件`
  - `listAllPlugins_数据库与运行态同ID时_应合并且不重复`

