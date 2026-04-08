# 变更日志：拣货完成库位未完成统计性能优化

- 日期：2026-04-07
- 文件：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`

## 变更背景
- 原逻辑按 `location_code` 循环执行 `count` 查询，存在 N+1 查询问题。

## 变更内容
- 将逐库位查询改为单次聚合查询：
  - SQL：按 `bill_id` + `status!=1` 统计未完成数量，`group by location_code`。
- 将聚合结果组装为 `unpickedLocationMap`。
- 遍历本次请求的 `locationCodeMap` 时，改为内存读取未完成数量。

## 效果
- 数据库查询次数从 `N(库位数)` 降为 `1`。
- 在库位数量较多的场景下降低数据库往返与事务耗时。
- 业务语义保持不变：仅当库位下未完成数为 0 才加入灭灯列表。