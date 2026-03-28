# PF4J 手动初始化同步接口变更日志

## 变更日期
- 2026-03-28

## 变更类型
- 新增功能

## 变更内容
- 新增管理接口：`POST /plugin/admin/init-sync`
- 接口行为：调用 `initMissingPluginsFromRuntime()`，返回补录统计信息。
- 新增控制层单元测试（成功/异常分支）。
- 在 `http/test-plugin-api.http` 增加接口测试脚本。

## 兼容性
- 不影响现有插件管理接口。
- 仅新增端点，默认无破坏性影响。

