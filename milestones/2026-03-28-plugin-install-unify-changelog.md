# PF4J 安装接口统一变更日志

## 日期
- 2026-03-28

## 变更类型
- 功能调整（接口收敛）

## 变更内容
- 新增统一安装入口：`POST /plugin/admin/install`。
- 通过 `source` 区分来源：`LOCAL_PATH`（本地路径）和 `REMOTE_URL`（远程下载）。
- 旧接口 `POST /plugin/admin/install/by-path` 已移除，不提供兼容。
- 保留 `POST /plugin/admin/upload` 用于 multipart 文件上传安装。

## 影响说明
- 调用方需从 `/install/by-path` 迁移到 `/install`。
- 远程安装当前仅支持基础 http/https 下载，不支持认证头与重试策略。
