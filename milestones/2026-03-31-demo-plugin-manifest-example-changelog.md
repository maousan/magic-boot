# 2026-03-31 Demo 插件 Manifest 示例文件变更日志

## 变更内容

1. 新增 magic-plugin-demo/Manifest.json.example 标准示例模板。
2. 模板字段与 ZipPluginInstaller 当前上传校验逻辑对齐。
3. 示例值与 Demo 插件 plugin.properties 对齐（pluginId=demo-plugin，plugin.version=1.0.0）。
4. 对 checksumSha256 与 signature 提供占位值，避免误用假值进行发布。

## 影响评估

- 仅新增示例与文档，不修改运行时代码。
- 不涉及数据库结构变更。
- 不涉及外部 API 与协议变更。