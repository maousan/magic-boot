# 2026-04-23 库位状态真值表改造变更日志

## 变更摘要
- 新增 `t_location_status` 作为库位亮灭灯状态真值表。
- 上传、完成、控制三条链路全部切换到状态表驱动。
- 移除脚本层对 `CommonCountMap` 的依赖。

## 数据库变更
- 新增迁移：`V20260423.001__create_t_location_status_table.sql`
- 新增迁移：`V20260423.002__init_t_location_status_from_picking_detail.sql`

## 代码变更
- 新增工具函数：`库位状态工具.ms`（状态刷新、快照查询、分流决策）。
- 重构上传公共逻辑：reconcile 从内存计数改为状态表刷新。
- 重构拣货完成：提交后按状态表判定单用户/多用户/关灯。
- 重构亮灯灭灯控制：控制决策使用状态表快照。
- 删除 Java 侧 `CommonCountMap` 与恢复器实现，彻底移除内存计数方案。

## 兼容性说明
- 对外接口路径、请求体与响应结构保持不变。
- trace 扩展字段移除 `commonCountSize/commonCountTotal`，保持基础字段结构。

## 补充调整（同日）
- 根据实现评审意见，已移除本次新增的细粒度可观测性日志与 trace 扩展噪音字段，保留必要日志。
