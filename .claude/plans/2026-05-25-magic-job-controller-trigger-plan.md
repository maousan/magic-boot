# MagicJobController 手动触发任务接口计划

日期: 2026-05-25

## 目标
在 `MagicJobController` 中新增 `trigger` 接口，用于通过 magic-api job 控制器手动触发已注册的定时任务。

## 范围
- 新增 `POST /job/trigger`。
- 入参沿用现有 `/job/execute` 的 `id`。
- 先通过资源服务校验任务存在。
- 复用 `JobMagicDynamicRegistryForQuartz.triggerJob` 触发 Quartz 任务。

## 不做
- 不修改已有 `/job/execute` 的调试执行逻辑。
- 不修改已有 `/magic/job/{jobId}/trigger` 扩展接口。
- 不调整 Quartz 注册、日志记录或任务执行实现。

## 验证
1. 静态检查新增 import、方法签名和调用路径。
2. 确认 `/job/trigger` 与 `/job/execute` 参数风格一致。
