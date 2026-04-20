# 2026-04-21 亮灯链路日志落库计划

## 目标
实现亮灯链路日志落库，支持按 traceId/waveNo/userId 快速检索请求影响的库位与亮灭灯决策，同时控制性能开销，不影响业务主数据落库。

## 范围
- `magic-boot-master/src/main/resources/db/migration/*.sql`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 设计
1. 新增日志表 `t_light_trace_log`
- 存储摘要事件，不逐条落每个库位动作。
- 关键字段：`trace_id/api_name/stage/action_type/wave_no/user_id/location_codes/location_count/extra_json/create_time`。
- 索引：`trace_id`、`wave_no+create_time`、`user_id+create_time`。

2. 接口侧落库策略
- 每个接口在主业务处理完成后落 1 条 `done` 摘要日志。
- 接口异常时追加 1 条 `error` 摘要日志。
- 落库采用 `try/catch` 最佳努力，不影响主业务返回。

3. 性能与稳定性约束
- 不改动业务主事务边界。
- 不新增大批量同步写入，仅单条摘要记录。
- 落库失败只打印错误日志，不中断业务流程。

## 验证
1. 发起上传→完成→控制链路请求，确认每步均有 `t_light_trace_log` 记录。
2. 按同一 `trace_id` 检索，能还原影响库位与决策。
3. 模拟日志落库失败，确认业务接口仍返回成功（fail-open）。
