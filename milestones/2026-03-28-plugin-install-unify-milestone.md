# PF4J 安装入口统一里程碑

## 里程碑名称
- 统一安装入口（install）

## 完成时间
- 2026-03-28

## 完成项
- 收敛安装路由到 `POST /plugin/admin/install`。
- 支持 `source=LOCAL_PATH | REMOTE_URL` 两类安装来源。
- 移除旧路由 `POST /plugin/admin/install/by-path`。
- 保留 `POST /plugin/admin/upload` 作为上传安装入口。
- 补齐 Controller/Service 测试与 HTTP 脚本。

## 验收结果
- install 统一入口可用。
- by-path 接口不可用（404）。
- upload 行为保持可用。
