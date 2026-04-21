# 2026-04-21 control计数幂等修复计划

## 目标
修复 `亮灯灭灯控制` 接口重复调用导致 `CommonCountMap.totalCount` 持续累加的问题。

## 方案
- 移除 `mode` 驱动的 `CommonCountMap.increment/decrement`。
- 按库位从数据库实时统计当前未拣用户数（`count(distinct bill_id)`）并回填 `CommonCountMap`。
- 对本次受影响库位执行 `set/remove`，确保重复调用结果一致。

## 影响范围
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

## 验证
- 对同一请求连续调用多次，`commonCountTotal` 不再递增。
- 观察 `reconcile_set/reconcile_remove` 日志与数据库一致。
