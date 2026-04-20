# 里程碑：Windows 非 Docker 部署能力落地

日期：2026-04-10

## 已完成

- 完成 Windows 非 Docker 部署脚本集交付：
  - `deploy-win/set-env.bat`
  - `deploy-win/start.bat`
  - `deploy-win/stop.bat`
  - `deploy-win/install-service.ps1`
  - `deploy-win/README.md`
- 完成部署计划与变更日志沉淀：
  - `.claude/plans/2026-04-10-windows-non-docker-deploy-plan.md`
  - `.claude/changelogs/2026-04-10-windows-non-docker-deploy-changelog.md`

## 验收标准对应

1. 非 Docker 方式可直接启动/停止应用：已提供脚本与 PID 管理机制。
2. Windows 服务化能力：已提供 NSSM 安装脚本。
3. 配置可扩展：已提供 `set-env.local.bat` 覆盖机制。
4. 可运维性：已提供日志路径与排障说明。

## 后续建议

1. 在目标服务器执行一次全流程演练（打包 -> 启动 -> 健康检查 -> 停止）。
2. 若进入生产，建议固定 `set-env.local.bat` 并加密管理数据库/Redis 密码。
3. 若需要公网访问，建议配套 Nginx/HTTPS 与防火墙白名单策略。
