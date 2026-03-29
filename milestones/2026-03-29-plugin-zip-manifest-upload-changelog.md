# 2026-03-29 插件上传 ZIP 强约束变更日志

## 变更类型

功能调整 / 安全治理增强

## 实际落地内容

- `/plugin/admin/upload` 从“接受 JAR”调整为“仅接受 ZIP”
- 新增 `ZipPluginInstaller`，实现 ZIP 包处理与治理校验
- 强制校验 ZIP 包内 `Manifest.json`
- 强制校验 `entryJar` 存在且为 `.jar`
- 强制校验 `checksumSha256`
- 增加 `Manifest.pluginId/version` 与 `plugin.properties` 一致性校验
- 支持 `requiresMagicBoot` 与运行时版本比较（通过 `plugin.runtimeVersion` 配置）
- 上传示例与路由测试改为 ZIP 输入

## 影响文件

- `magic-plugin/src/main/java/org/ssssssss/magicboot/pf4j/service/PluginManagerService.java`
- `magic-plugin/src/main/java/org/ssssssss/magicboot/pf4j/service/ZipPluginInstaller.java`
- `magic-plugin/src/main/java/org/ssssssss/magicboot/pf4j/configuration/PluginProperties.java`
- `magic-plugin/src/test/java/org/ssssssss/magicboot/pf4j/service/ZipPluginInstallerTest.java`
- `magic-plugin/src/test/java/org/ssssssss/magicboot/pf4j/service/PluginManagerServiceTest.java`
- `magic-plugin/src/test/java/org/ssssssss/magicboot/pf4j/controller/PluginAdminControllerRouteTest.java`
- `http/test-plugin-api.http`

## 兼容性说明

- 新上传入口不再接受 `.jar`
- 历史已安装插件的运行与管理能力不受本次上传入口调整影响

## 风险与缓解

- 风险：发布侧仍按旧 JAR 直传会失败
- 缓解：返回明确错误信息，HTTP 示例已更新为 ZIP 上传