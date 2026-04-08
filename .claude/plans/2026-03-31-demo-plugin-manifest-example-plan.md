# 2026-03-31 Demo 插件 Manifest 示例文件计划

## 目标

为 magic-plugin-demo 新增标准 Manifest.json 示例模板文件 Manifest.json.example，并补齐本次功能调整的变更日志与里程碑文档。

## 标准字段来源

来源于 magic-plugin/src/main/java/org/ssssssss/magicboot/pf4j/service/ZipPluginInstaller.java 的 alidateManifest 与解析逻辑：

- 必填：pluginId、ersion、displayName、equiresMagicBoot、permissions、checksumSha256
- 建议保留：manifestVersion（默认可回落 1.0）、entryJar（默认可回落 plugin.jar）
- 条件必填：signature（当签名强校验开启时）
- 允许扩展字段：后端通过 @JsonIgnoreProperties(ignoreUnknown = true) 忽略未知字段

## 实施步骤

1. 在 magic-plugin-demo/ 根目录新增 Manifest.json.example。
2. 模板值对齐 Demo 插件现状：
   - pluginId: demo-plugin
   - ersion: 1.0.0
   - displayName: Demo Plugin
   - entryJar: plugin.jar
   - equiresMagicBoot: >=1.0.0
   - permissions: ["plugin:view"]
3. checksumSha256、signature 使用占位值。
4. 在里程碑文档中说明如何替换为真实哈希与签名。
5. 新增变更日志与里程碑文档，记录交付与使用说明。

## 验收标准

1. Manifest.json.example 为合法 JSON（无注释、无尾逗号）。
2. 示例字段满足上传校验必填约束。
3. pluginId/version 与 magic-plugin-demo/src/main/resources/plugin.properties 一致。
4. 文档中明确真实 checksumSha256/signature 替换方式。
5. 无对外 API、数据库、协议变更。