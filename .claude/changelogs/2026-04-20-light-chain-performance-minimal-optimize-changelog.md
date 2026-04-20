# 2026-04-20 亮灯链路性能最小改动优化变更日志

## 变更背景
针对亮灯链路的 3 个核心接口进行最小改动性能优化，目标是在不改变业务语义的前提下减少 SQL 往返次数、降低日志 I/O 压力，并支持按受影响库位收敛计算范围。

## 变更范围
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 主要变更
1. 拣货数据上传
- 将逐条 `insert` 明细改为单条多值批量 `insert`（删后批量写入）。
- 将按库位循环 `count(distinct bill_id)` 改为按受影响库位单次 `group by` 聚合回填 `CommonCountMap`。
- 将大集合日志改为 `size + sample` 形式，减少日志体积。

2. 拣货完成
- 合并“旧未拣数量”统计为单次 `group by` 查询。
- 合并“波次未拣数量/主单未拣数量”统计为两条批量聚合查询，替代按库位循环查询。
- 将 `CommonCountMap` 回填改为单次聚合查询后映射更新。
- 将大集合日志改为 `size + sample` 形式。

3. 亮灯灭灯控制
- 新增受影响库位解析：优先使用 `affectedLocationCodes`，兼容 `locationCodes`。
- 将 `allLocationCodes` 与 `unpickedLocationCodes` 双查询合并为单次汇总查询（`group by` + `sum(case...)`）。
- 在存在受影响库位时，仅对受影响集合计算亮灭灯决策。
- 新增库位汇总日志（`size + sample`）。

## 兼容性说明
- 保持原有接口入参与业务流程不变。
- 若未传 `affectedLocationCodes/locationCodes`，控制接口仍按原逻辑对波次范围计算。

## 验证建议
1. 使用单用户、双用户同时操作同库位场景验证亮灯颜色分支。
2. 使用应用重启后首次请求场景验证 `CommonCountMap` 自愈回填。
3. 对比优化前后同批次明细量下的 SQL 次数与响应时间。
