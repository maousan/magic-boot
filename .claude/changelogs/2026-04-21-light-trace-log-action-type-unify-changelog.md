# 2026-04-21 亮灯日志口径统一修复变更日志

## 变更背景
针对 code review 反馈，修复日志落库口径不一致问题；按用户要求保留同步落库，不调整为异步。

## 变更范围
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 主要修复
1. 统一 `action_type` 枚举口径（error 场景）
- 上传接口异常落库：`action_type` 由 `turn_on` 改为 `error`。
- 完成接口异常落库：`action_type` 由 `mix` 改为 `error`。
- 控制接口异常落库：`action_type` 由模式值衍生改为 `error`。

2. 控制接口 `none` 场景降体积
- 当无亮灭动作时，`location_codes` 不再写入 `allLocationCodes`，改为空数组。
- 新增 `extra_json.allCount` 记录全量库位数量，保留观测能力。

## 未调整项
- 按要求保留同步摘要落库模式（未改为异步队列写入）。

## 验证建议
1. 异常路径检查 `t_light_trace_log.action_type` 是否统一为 `error`。
2. 控制接口 `none` 场景检查 `location_codes=[]` 且 `extra_json.allCount` 存在。
