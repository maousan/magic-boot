# 变更日志：Windows 启动脚本健康检查重试

日期：2026-04-10

## 变更内容

- 为 `deploy-win/start.bat` 增加启动后健康检查重试机制：
  - 健康检查 URL：`http://127.0.0.1:%APP_PORT%%HEALTH_CHECK_PATH%`
  - 失败自动重试，超过阈值后自动终止刚启动的进程并返回非 0。
- 为 `deploy-win/set-env.bat` 增加可配置参数：
  - `HEALTH_CHECK_PATH`
  - `HEALTH_CHECK_RETRIES`
  - `HEALTH_CHECK_INTERVAL_SEC`
  - `HEALTH_CHECK_TIMEOUT_SEC`
- 更新 `deploy-win/README.md`，补充健康检查参数说明。

## 影响范围

- 仅影响 Windows 非 Docker 启动脚本行为。
- 业务代码与接口逻辑不受影响。
