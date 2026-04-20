# 2026-04-21 亮灯链路日志落库变更日志

## 变更目标
实现亮灯链路摘要日志落库，支持按 `traceId` 检索上传、完成、控制三个接口的关键决策与库位影响范围，并保持业务主链路 fail-open。

## 变更范围
- `magic-boot-master/src/main/resources/db/migration/V20260421.001__create_t_light_trace_log_table.sql`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 主要变更
1. 新增日志表 `t_light_trace_log`
- 存储接口摘要事件：`done/error`。
- 支持字段：`trace_id/api_name/stage/action_type/wave_no/user_id/location_codes/location_count/extra_json/create_time`。
- 新增索引：`trace_id`、`wave_no+create_time`、`user_id+create_time`。

2. 三接口新增摘要落库（best-effort）
- 上传接口：落 `done/error` 摘要。
- 完成接口：落 `done/error` 摘要。
- 控制接口：落 `done/error` 摘要。

3. 稳定性策略
- 落库异常不影响业务结果，统一记录 `trace_persist_error`。
- 不调整业务主事务边界。

## 性能说明
- 每个接口仅新增 1 条摘要记录（异常时额外 1 条）。
- 不进行逐库位明细行落库，`location_codes` 以 JSON 数组存储。

## 验证建议
1. 先执行 Flyway 迁移，确认表已创建。
2. 执行上传→完成→控制链路，按同一 `trace_id` 查询表记录。
3. 检查 `api_name` 与 `stage` 是否覆盖完整链路。
4. 对比改造前后接口耗时，确认无明显性能回退。
