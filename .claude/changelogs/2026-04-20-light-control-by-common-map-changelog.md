# 变更日志：亮灯灭灯控制改为按库位 CommonCountMap 决策

## 变更日期
- 2026-04-20

## 变更类型
- refactor

## 变更范围
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 变更内容
- 移除 Redis 波次用户计数逻辑，统一使用 `CommonCountMap`。
- 新增“当前用户在当前波次的未拣库位”查询，并按 mode 对对应库位做 `increment/decrement`。
- 亮灭灯决策改为库位维度：
  - `count <= 0` 或库位已无未拣 -> 灭灯
  - `count == 1` -> 单用户色（点亮模式用请求色，灭灯后补亮用 GREEN）
  - `count > 1` -> 多用户色（`mixColor` 或回退 `CYAN`）
- `stationCode` 增加空值兜底（默认 `10001`）。
- 增加 `CommonCountMap` 快照日志，便于调试排查。

## 兼容性说明
- 接口入参/出参不变。
- 灯控策略从“波次用户数”切换为“库位用户数”，符合当前业务口径。
