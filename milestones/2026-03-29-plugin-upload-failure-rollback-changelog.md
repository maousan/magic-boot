# 2026-03-29 插件安装回滚与日志修复变更日志

## 变更背景
- 代码审查指出两个问题：
  - 安装失败回滚可能删除已存在插件包。
  - 插件启停成功日志文案与真实操作相反。

## 变更内容
- 调整 `PluginManagerService` 的上传目标路径策略：
  - 新增同名冲突规避逻辑，上传时为新文件分配不冲突文件名，避免覆盖已有插件包。
  - 保留失败回滚删除逻辑，但删除对象仅限本次新建的安装文件，不再误伤历史文件。
- 调整 `ZipPluginInstaller` 的 ZIP 安装落盘策略：
  - 原先 `REPLACE_EXISTING` 覆盖写入改为不冲突文件名写入。
- 修正 `PluginManagerService` 启停成功日志文案：
  - `startPlugin` 输出 `Plugin started successfully`。
  - `stopPlugin` 输出 `Plugin stopped successfully`。
- 新增回归测试：
  - `PluginManagerServiceTest.installPlugin_whenLegacyJarInstallFails_shouldNotOverwriteExistingJar`。

## 影响评估
- 安全性/稳定性提升：避免安装失败时误删线上已有插件包。
- 可观测性提升：启停日志与真实动作一致，便于排障与审计。
- 兼容性：对外接口与返回结构无破坏性变更。

## 测试结果
- `mvn -pl magic-plugin -Dtest=PluginManagerServiceTest#installPlugin_whenLegacyJarInstallFails_shouldNotOverwriteExistingJar test -q`：通过。
- `mvn -pl magic-plugin -Dtest=ZipPluginInstallerTest test -q`：通过。
- `mvn -pl magic-plugin -Dtest=PluginManagerServiceTest test -q`：通过。
