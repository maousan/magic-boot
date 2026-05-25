# MagicJobController 手动触发任务接口变更日志

日期: 2026-05-25

## 变更内容
- 在 `MagicJobController` 新增 `POST /job/trigger` 接口。
- 接口入参为 `id`，与现有 `POST /job/execute` 保持一致。
- 接口先校验任务资源存在，再通过 `JobMagicDynamicRegistryForQuartz.triggerJob` 触发 Quartz 任务。

## 影响范围
- 影响模块: `magic-api-plugin-job`
- 影响文件: `MagicJobController.java`
- 不影响已有 `/job/execute` 和 `/magic/job/{jobId}/trigger` 接口。

## 验证
- 已完成静态检查。
- 未主动执行编译，遵循项目规则由用户自行编译测试。
