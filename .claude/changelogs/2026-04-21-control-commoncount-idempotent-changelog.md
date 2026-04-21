# 2026-04-21 control计数幂等修复变更日志

## 背景
`/api/light/control` 在 mode=1/2/3/4 调用时通过增减内存计数，重复调用同一业务请求会导致 `CommonCountMap.totalCount` 持续累加。

## 变更内容
- 文件：`data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`
- 删除：基于当前用户未拣库位的 `CommonCountMap.increment/decrement` 逻辑。
- 新增：`queryLocationUserCountMap`，按 `waveNo + locationCodes` 从 `t_picking_upload_detail` 统计 `count(distinct bill_id)`。
- 新增：`reconcile_start/reconcile_set/reconcile_remove/reconcile_done` 日志，按数据库实值对 `CommonCountMap` 执行 `set/remove`。
- 行为：重复请求将得到稳定计数，不再出现累计膨胀。

## 预期效果
- 同一波次同一库位重复调用 `/control`，`commonCountSize/commonCountTotal` 保持稳定。
- 多用户库位判断（`turnOnMulti`）仅由数据库真实人数决定。
