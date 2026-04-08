# 2026-04-03 东信和平亮灯控制接口落地计划

## 目标
- 按 OpenAPI 落地 `POST /api/light/control`。
- 当前阶段仅落库 `t_light_control_log`，不调用三方API。

## 实施范围
- 新增亮灯控制分组与 `亮灯灭灯控制.ms`。
- 完成参数校验、日志入库、事务控制与响应定义。
- 补充 `.http` 测试用例及测试报告。

## 实施项
1. 新增分组 `data/dongxinheping/api/东信和平/亮灯控制`（路径 `/light`）。
2. 新增脚本 `亮灯灭灯控制.ms`（路径 `/control`）。
3. 校验 `waveNo/userId/mode` 必填、`mode/color/duration` 枚举。
4. 写入 `t_light_control_log`，记录请求原文、动作类型、路径等字段。
5. 预留三方调用 TODO 注释，后续补充。
6. 新增 HTTP 用例及测试报告。

## 验收标准
- `/api/light/control` 成功返回 200 + `指令发送成功`。
- 参数错误返回 400。
- 数据库成功插入日志记录。