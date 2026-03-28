# PF4J 安装入口统一改造计划（install）

## 日期
- 2026-03-28

## 目标
- 将 `/plugin/admin/install/by-path` 收敛到统一入口 `/plugin/admin/install`。
- 通过 `source` 区分 `LOCAL_PATH` 与 `REMOTE_URL`。
- 保留 `/plugin/admin/upload` 作为文件上传安装入口。

## 实施项
1. Controller：移除 `install/by-path` 路由，新增/启用 `POST /plugin/admin/install` JSON 入参。
2. Service：新增 `installPluginBySource` 与 `installFromRemoteUrl`，复用 `loadAndInstallPlugin`。
3. 参数校验：source 必填且仅支持 `LOCAL_PATH`、`REMOTE_URL`。
4. 远程安装：仅支持 http/https 基础下载，下载临时文件后安装，失败清理。
5. 测试与脚本：补齐 Controller/Service 单测，更新 `http/test-plugin-api.http`。

## 验收标准
- `/plugin/admin/install` 支持本地与远程两种来源。
- `/plugin/admin/install/by-path` 返回 404。
- `/plugin/admin/upload` 仍可用。
- `mvn -pl magic-plugin test` 通过。
