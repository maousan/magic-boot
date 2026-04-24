# 2026-04-24 亮灯对接 Q4 SQL 性能优化变更日志

## 背景
- 亮灯对接接口在计算“单人未完成库位映射”时，原 SQL 使用派生表聚合后再过滤，存在中间结果开销。
- 实测显示该查询在大库位集合下是亮灯链路的重要耗时点之一。

## 变更内容
- 调整接口：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- 调整接口：`data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- 将 `querySingleUnpickedUserMap` 中 SQL 由“子查询聚合 + 外层过滤”改为：
  1. `DISTINCT` 直接去重 `(location_code, user_id)`
  2. 使用 `STRAIGHT_JOIN` 固定连接顺序
  3. 在 `WHERE` 中提前过滤 `ifnull(d.status, 0) <> 1`

## SQL 形态变化
- 旧：
  - `group by d.location_code, m.user_id`
  - 外层 `where x.unpicked_cnt > 0`
- 新：
  - `select distinct d.location_code, m.user_id`
  - `where ... and ifnull(d.status, 0) <> 1`

## 验证结果（2026-04-24）
- 1000 库位样本，20 轮均值：
  - 旧 SQL：`5.719ms`
  - 新 SQL（`distinct + straight_join`）：`2.548ms`
  - 平均耗时下降约 `55.4%`

## 影响说明
- 仅调整查询形态，不改返回结构与业务语义。
- 对灯控分流结果无行为变更预期，仅降低查询开销。
