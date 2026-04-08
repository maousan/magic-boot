# 2026-03-31 plugin.properties 扩展信息落库变更日志

## 变更内容

1. 在 `PluginManagerService` 新增插件元数据增强逻辑：从插件 JAR 读取 `plugin.properties`。
2. 新增扩展字段解析：`plugin.name`、`plugin.description`、`plugin.provider`、`plugin.class`、`plugin.version`。
3. 调整落库逻辑：`savePluginToDatabase` 与 `buildPluginInfoFromRuntime` 统一接入增强逻辑，优先使用 `plugin.properties`，缺失时回退 `PluginDescriptor`。
4. 新增与更新单元测试，覆盖“有扩展字段落库”和“缺失字段回退”场景。

## 影响评估

- 数据库结构无变更，复用 `magic_plugin` 既有字段。
- 安装流程（ZIP/JAR）与运行时同步流程保持兼容。
- 当插件包不含 `plugin.properties` 或格式异常时，系统自动回退到原有 descriptor 数据，避免安装失败。

## 范围收敛（最新）

- `PluginMetadata` 暂时只扩展 `name` 与 `author`。
- 其他字段（`description`、`provider`、`pluginClass`、`version`）统一使用 PF4J descriptor 原值。
