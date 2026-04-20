# 里程碑：Windows 发布与回滚流程闭环

日期：2026-04-10

## 已完成

- 发布脚本：`deploy-win/release.bat`
- 回滚脚本：`deploy-win/rollback.bat`
- 启动脚本升级：`deploy-win/start.bat` 优先读取 `current/app.jar`
- 服务安装脚本升级：`deploy-win/install-service.ps1` 对齐 `current/app.jar`
- 文档更新：`deploy-win/README.md`

## 结果

- Windows 非 Docker 部署从“手动替换 jar”升级为“版本归档 + current 切换 + 失败自动回滚”。
- 具备基础生产可操作性与可追溯版本目录结构。

## 建议后续

1. 增加发布前校验（磁盘空间、端口占用、数据库连通性）。
2. 增加回滚后二次验证与告警通知（邮件/IM）。
3. 将数据库迁移纳入发布编排（按环境开关）。
