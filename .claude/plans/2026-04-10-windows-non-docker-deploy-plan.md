# Windows 非 Docker 部署方案计划（magic-boot）

## 1. 目标

- 在 Windows 环境提供一套可执行的非 Docker 部署脚本。
- 覆盖本地进程启动、停止、环境变量加载、服务化安装（NSSM）。
- 保持与现有项目结构兼容，不改业务代码。

## 2. 边界

- 仅新增部署脚本与部署文档，不修改 `magic-boot-master` 业务实现。
- 不引入新依赖，不变更数据库结构。
- 服务化采用 NSSM，若未安装 NSSM 提供明确提示。

## 3. 交付物

- `deploy-win/set-env.bat`：统一环境变量入口。
- `deploy-win/start.bat`：后台启动 Jar，记录 PID。
- `deploy-win/stop.bat`：按 PID 停止应用。
- `deploy-win/install-service.ps1`：将应用注册为 Windows 服务（NSSM）。
- `deploy-win/README.md`：操作说明与排障指引。
- 配套变更日志与里程碑文档。

## 4. 实施步骤

1. 新建 `deploy-win` 目录与脚本骨架。
2. 实现环境变量装载与默认值策略。
3. 实现启动/停止脚本并加入 PID 文件机制。
4. 实现 NSSM 服务安装脚本。
5. 编写 README 使用说明。
6. 补充变更日志与里程碑文档。
7. 执行静态检查（路径、变量、命令可用性）。

## 5. 验证方式

- 手动验证：
  - 执行 `deploy-win\start.bat` 后，`run\app.pid` 生成。
  - 访问 `http://127.0.0.1:8089/actuator/health` 返回 `{"status":"UP"}`（或 200）。
  - 执行 `deploy-win\stop.bat` 后进程停止，`run\app.pid` 删除。
- 服务化验证：
  - 执行 `install-service.ps1` 成功后可见 `magic-boot-app` 服务。
  - 服务启动后接口可访问。

## 6. 风险与回退

- 风险：目标机未安装 JDK17 / NSSM 导致脚本失败。
- 风险：端口冲突导致启动失败。
- 回退：删除 `deploy-win` 目录脚本，不影响业务代码；服务化可通过 NSSM 删除服务。
