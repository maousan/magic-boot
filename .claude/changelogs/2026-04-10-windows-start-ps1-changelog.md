# 变更日志：新增 PowerShell 启动脚本与环境加载方式

日期：2026-04-10

## 变更内容

- 新增 `deploy-win/start.ps1`，支持在 PowerShell 下直接启动应用。
- 新增 `deploy-win/set-env.ps1`，支持将变量注入当前 PowerShell 会话。
- 更新 `deploy-win/README.md`，补充 PowerShell 点源用法与示例。

## 问题修复

- 解决“在 PowerShell 执行 set-env 后变量看起来不生效”的使用问题（子进程作用域）。
