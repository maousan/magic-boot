# ExtendedMagicJobController 迁移变更日志

## 变更日期
2026-03-27

## 变更类型
功能迁移 / 接口重构（Java Controller -> magic-api .ms）

## 变更内容
- 新增 `暂停任务.ms`，迁移 `pauseJob`。
- 新增 `恢复任务.ms`，迁移 `resumeJob`。
- 新增 `立即执行任务.ms`，迁移 `triggerJobNow`。
- 新增 `查询任务历史.ms`，迁移 `getJobHistory`。
- 新增 `查询任务状态.ms`，迁移 `getJobState`。
- 新增 `http/test-magic-job-ms.http`，覆盖 5 个迁移接口。

## 兼容性说明
- 接口归属 `系统管理/任务管理` 分组，分组路径为 `/system/job`。
- 新接口完整路径：
  - `POST /system/job/{jobId}/pause`
  - `POST /system/job/{jobId}/resume`
  - `POST /system/job/{jobId}/trigger`
  - `GET /system/job/{jobId}/history`
  - `GET /system/job/{jobId}/state`

## 风险说明
- 若运行环境未注册 `JobMagicDynamicRegistryForQuartz` 或 `Scheduler` Bean，接口将返回异常信息字符串。
