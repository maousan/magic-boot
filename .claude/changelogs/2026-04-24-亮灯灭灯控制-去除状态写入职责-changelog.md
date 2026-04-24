# 2026-04-24 亮灯灭灯控制去除状态写入职责

## 变更文件
- data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms

## 变更内容
- 将 `recalcLocationStatusBatch` 调整为 `queryLocationStatusBatch`，仅查询状态，不再写入 `t_location_status`。
- 删除控制接口中的事务写入逻辑（`beginTrans/commitTrans/rollbackTrans`）。
- 控制接口职责收敛为：根据状态执行库位灯和巷道灯开关，不再承担状态落库维护。

## 说明
- 对外接口结构不变。
- `mode=4` 手动灭灯强制关灯逻辑保持不变。
