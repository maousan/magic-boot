# 巷道灯设备控制函数计划

## 目标

- 新增 `led` 函数，复用 `控制巷道灯设备开关.ms` 的 server/client 控制能力。
- 调整 `根据库位码亮巷道灯.ms`，由它查询库位绑定关系后调用新函数执行亮灯。

## 验证

- 检查新函数元数据 JSON 可解析。
- 检查 `根据库位码亮巷道灯.ms` 已导入并调用新函数。
- 检查新函数保留 `mode/ledId/command/port/timeoutMs/waitResponse` 入参。
