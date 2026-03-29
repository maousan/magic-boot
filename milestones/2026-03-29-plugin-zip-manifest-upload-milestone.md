# 2026-03-29 插件 ZIP + Manifest 上传治理里程碑

## 里程碑目标

完成 PF4J 插件上传入口的 ZIP 强约束改造，并落地 Manifest 治理校验链路。

## 完成项

1. 上传约束完成
- 上传入口仅允许 `.zip`
- 非 ZIP 上传直接拒绝

2. 包结构与内容校验完成
- 必须包含 `Manifest.json`
- `entryJar` 必须存在且为 `.jar`
- 防止 ZIP 路径穿越（Zip Slip）
- `checksumSha256` 校验通过后才允许安装

3. 元数据一致性校验完成
- Manifest 中 `pluginId/version` 与 PF4J `plugin.properties` 一致才允许安装

4. 版本兼容校验完成
- 支持通过 `plugin.runtimeVersion` 对 `requiresMagicBoot` 做兼容性判断

5. 测试与示例完成
- 新增 `ZipPluginInstallerTest`（5 个用例）
- 路由测试上传样例改为 ZIP
- `http/test-plugin-api.http` 上传示例改为 ZIP

## 测试结果

执行命令：

`mvn -pl magic-plugin "-Dtest=ZipPluginInstallerTest,PluginAdminControllerRouteTest,PluginManagerServiceTest" test`

结果：

- 测试总数：19
- 通过：19
- 失败：0
- 错误：0
- 状态：BUILD SUCCESS

## 后续建议

1. 在数据库层补充 Manifest 相关字段，支撑治理信息可视化
2. 后续版本将 `signature` 从可选升级为强制校验
3. 增加管理员审核流（上传后审核通过再安装）