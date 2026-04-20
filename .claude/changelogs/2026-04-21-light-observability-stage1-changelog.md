# 2026-04-21 亮灯链路可观测性阶段1变更日志

## 变更目标
在不影响业务主链路落库性能的前提下，增强亮灯链路日志可追踪能力，支持按 `traceId` 快速串联上传、完成、控制三个接口。

## 变更范围
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 变更内容
1. 增加 `traceId` 生成与透传
- 若入参无 `traceId`，接口内自动生成（UUID 去短横线）。
- 将 `traceId` 回写到 `payload`，用于后续链路传递。

2. 统一关键节点结构化日志
- 新增统一前缀字段：`traceId/api/waveNo/userId/stage`。
- 关键阶段日志：`receive`、`light_decision`、`reconcile_*`、`dispatch_error`、`done`、`error`。

3. 异步下游调用上下文透传
- 调用亮灯/灭灯接口时附带 `traceId`、`waveNo`、`userId`。

## 性能影响说明
- 未新增数据库写入。
- 未引入额外同步阻塞流程。
- 库位集合仍保持 `size+sample` 输出，避免日志膨胀。

## 验证建议
1. 同一次业务请求检查日志中 `traceId` 一致性。
2. 根据 `traceId` 可追踪到对应亮灯决策及下发行为。
3. 对比改造前后接口耗时，确认无明显性能回退。
