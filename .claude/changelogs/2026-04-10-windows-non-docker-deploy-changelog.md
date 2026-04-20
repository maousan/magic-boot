# 变更日志：Windows 非 Docker 部署脚本

日期：2026-04-10

## 变更概述

- 新增 Windows 非 Docker 部署目录 `deploy-win`。
- 新增环境变量装载脚本、应用启停脚本、NSSM 服务安装脚本与使用文档。
- 保持业务代码零改动，仅补充部署资产。

## 详细变更

1. 新增 `deploy-win/set-env.bat`
   - 提供 MySQL/Redis/应用/Tiga 的默认环境变量。
   - 增加 `set-env.local.bat` 覆盖机制，便于本地私有配置。
   - 同步设置 `SPRING_DATA_REDIS_*` 与 `SPRING_REDIS_*`，兼容 Redisson 配置读取。

2. 新增 `deploy-win/start.bat`
   - 自动检测 Java 可用性。
   - 自动选择 `magic-boot-master/target` 最新 Jar 启动。
   - 启动后写入 `run/app.pid`，日志输出到 `logs/app.out.log` 与 `logs/app.err.log`。

3. 新增 `deploy-win/stop.bat`
   - 根据 PID 文件精确停止应用，避免误杀其他 Java 进程。
   - 自动清理失效 PID 文件。

4. 新增 `deploy-win/install-service.ps1`
   - 基于 NSSM 创建并启动 `magic-boot-app` 服务。
   - 配置服务环境变量与日志滚动策略。

5. 新增 `deploy-win/README.md`
   - 补充前置条件、启动方式、服务化安装步骤与常见排障说明。

## 影响范围

- 新增文件，不影响现有 Docker 流程和业务功能。
- 仅影响 Windows 非 Docker 部署场景。
