# 变更日志：新增 stop.ps1

日期：2026-04-10

## 变更内容

- 新增 `deploy-win/stop.ps1`，与 `stop.bat` 行为对齐：
  - 按 `run/app.pid` 精确停进程
  - PID 缺失/无效时自动清理并给出提示
- 更新 `deploy-win/README.md`，加入 `stop.ps1` 用法。
