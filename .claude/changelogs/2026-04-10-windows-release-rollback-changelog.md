# 变更日志：Windows 发布与回滚流程完善

日期：2026-04-10

## 变更概述

- 新增发布脚本与回滚脚本，形成可执行闭环。
- 启动脚本支持优先读取 `current/app.jar`。
- 服务安装脚本改为固定绑定 `current/app.jar`，与发布流程统一。
- 重写 `deploy-win/README.md`，补充发布、回滚与服务化操作。

## 详细变更

1. 新增 `deploy-win/release.bat`
   - 发布版本归档到 `deploy-win/releases/<version>/app.jar`
   - 切换 `deploy-win/current/app.jar`
   - 启动并健康检查
   - 启动失败自动回滚到上一版本（若存在）

2. 新增 `deploy-win/rollback.bat`
   - 支持指定版本回滚
   - 支持读取 `previous.version` 快速回滚
   - 回滚后自动启动并执行健康检查

3. 更新 `deploy-win/start.bat`
   - 优先使用 `deploy-win/current/app.jar`
   - 无 `current` 时保留原有目录扫描兜底

4. 更新 `deploy-win/install-service.ps1`
   - 服务运行 Jar 改为 `deploy-win/current/app.jar`

5. 更新 `deploy-win/README.md`
   - 增加发布/回滚命令与目录约定说明

## 影响范围

- 仅影响 Windows 非 Docker 部署脚本。
- 业务代码和接口行为不受影响。
