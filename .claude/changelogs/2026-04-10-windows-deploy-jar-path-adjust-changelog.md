# 变更日志：Windows 部署脚本 Jar 路径调整

日期：2026-04-10

## 变更内容

- 调整 `deploy-win/start.bat` 的 Jar 发现逻辑：
  - 由 `magic-boot-master/target/*.jar` 改为 `deploy-win/*.jar`（脚本所在目录）。
- 调整 `deploy-win/install-service.ps1` 的服务安装 Jar 来源：
  - 由项目 `target` 目录改为 `deploy-win` 目录。
- 更新 `deploy-win/README.md`：
  - 增加“构建后复制 Jar 到 `deploy-win`”说明。

## 影响范围

- 仅影响 Windows 非 Docker 部署脚本行为。
- 业务代码与运行时功能不受影响。
