# Windows 发布与回滚流程完善计划

日期：2026-04-10

## 目标

- 为 Windows 非 Docker 部署提供可执行的发布与回滚闭环。
- 发布具备基本安全性：停止旧进程、切换版本、健康检查失败自动回滚。
- 回滚支持指定版本与上一版本快速回退。

## 设计

1. 目录约定（`deploy-win` 下）
   - `releases/<version>/app.jar`
   - `current/app.jar`
   - `current.version`
   - `previous.version`

2. 脚本职责
   - `release.bat`
     - 接收新 Jar 路径与可选版本号
     - 归档到 `releases`
     - 更新 `current/app.jar`
     - 启动并做健康检查（复用 `start.bat`）
     - 启动失败自动回滚
   - `rollback.bat`
     - 回滚到指定版本，或默认 `previous.version`
     - 更新 `current/app.jar` 后重启
   - `start.bat`
     - 优先使用 `current/app.jar`，否则回退旧扫描逻辑

3. 服务化一致性
   - `install-service.ps1` 改为固定加载 `current/app.jar`，与发布流程对齐。

## 验证

- 静态验证：脚本内容、路径、版本文件写入逻辑正确。
- 命令验证：`release.bat`/`rollback.bat` 的帮助输出与参数校验正常。
