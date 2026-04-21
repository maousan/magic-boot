# 2026-04-21 control下发稳定性修复计划

## 目标
解决 `/api/light/control` 出现仅打印 dispatch 而无下游回执日志的问题，恢复链路可观测性与可执行性。

## 方案
- 控制接口新增可配置下发模式：
  - 默认同步下发（`controlDispatchAsync=false`）
  - 可切换异步下发（`controlDispatchAsync=true`）
- 同步下发时在主线程打印 `dispatch_result`（含 action 与 success）。
- 保留原有 `dispatch_error` 异常日志。
- 修复一个脚本兼容性隐患：将 `!==` 改为 `!=`。

## 影响范围
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`
- `magic-boot-master/src/main/resources/application-dongxinheping.yml`

## 验证
- 调用 `/api/light/control` 后应出现 `dispatch sync ...` 与 `stage=dispatch_result`。
- 仍可在下游函数中看到 AIMS 调用成功/失败日志。
- 若改 `controlDispatchAsync=true`，行为退回异步分发。
