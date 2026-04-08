# 2026-04-01 东信和平拣货完成接口变更日志

## 新增
- 新增接口：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`。
- 新增接口路由：`POST /api/light/picking/complete`。
- 新增测试文件：`http/test-dongxinheping-picking-complete.http`。
- 新增测试报告：`milestones/2026-04-01-dongxinheping-picking-complete-test-report.md`。

## 说明
- 接口按 `userId + waveNo` 命中主表。
- 明细按 `bill_id + material_code + batch_no + location_code` 更新，不存在时插入。
- 全流程启用事务，异常回滚。