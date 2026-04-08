# 2026-03-31 plugin.properties 扩展信息落库计划

## 目标

为 PF4J 插件安装与运行时同步流程新增 `plugin.properties` 读取增强函数，并将读取到的扩展信息落库到 `magic_plugin`，至少包含 `description`。

## 范围

- 模块：`magic-plugin`
- 重点链路：上传安装（ZIP/JAR）后入库、运行时同步入库
- 数据表：`magic_plugin`（已有 `description` 字段）

## 实施步骤

1. 在 `PluginManagerService` 增加读取函数：从插件 JAR 中定位并读取 `plugin.properties`。
2. 提取扩展字段：`plugin.name`、`plugin.description`、`plugin.provider`、`plugin.class`、`plugin.version`。
3. 合并策略：优先使用 `plugin.properties` 字段，缺失则回退 `PluginDescriptor`。
4. 在 `savePluginToDatabase` 与 `buildPluginInfoFromRuntime` 统一调用增强函数，保证两条落库路径一致。
5. 新增/调整测试，验证：
   - 有 `plugin.description` 时 `PluginInfo.description` 正确入库；
   - 无扩展字段时回退 `PluginDescriptor` 逻辑不变。
6. 输出变更日志与里程碑文档。

## 验收标准

1. 安装插件后，`magic_plugin.description` 不再固定为空，能写入 `plugin.properties` 的值。
2. `plugin.name/provider/class/version` 在有扩展信息时可按增强值写入（缺失时回退）。
3. 现有安装流程（ZIP/JAR）与运行时同步流程不发生行为回退。
4. 单元测试覆盖新增增强逻辑，且本次变更涉及测试可执行通过。