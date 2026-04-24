# 拣货完成接口重构计划（2026-04-23）

## 目标
- 重构 `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`，与上传接口保持同一并发与状态口径。
- 统一链路：完成明细落库（事务1）-> 获取全局锁 -> 批量更新 `t_location_status`（事务2）-> 锁内同步亮/关灯。

## 约束
- 对外接口路径和成功/失败文案保持兼容。
- 不再依赖 `locationStatusUtils`，直接在脚本内完成状态重算与灯控分流。
- 灯控失败返回 500，不回滚已提交状态事务。

## 实施步骤
1. 保留并加强入参校验（含 `details[].id`）。
2. 事务1：
   - 校验主单存在。
   - 批量 `upsert` 完成明细到 `t_picking_upload_detail`。
   - 更新主单 `update_time`。
3. 锁控制：
   - 使用 `GET_LOCK('light:picking:upload:global', timeout)`。
   - 失败返回 503，finally 释放锁。
4. 事务2：
   - 对本次 `affectedLocationCodes` 批量聚合重算状态。
   - 批量 `upsert` `t_location_status`。
5. 灯控：
   - `status0 > 1` -> `BLUE`
   - `status0 = 1` -> 用户映射色（缺省 `GREEN`）
   - `status0 = 0` -> 关灯
   - 同步调用并检查返回，失败抛错。
6. 回归脚本：
   - 更新 `http/test-dongxinheping-picking-complete.http` 使其匹配新校验字段。

## 验证
- 静态检查：移除 `locationStatusUtils` 和 `async` 调度路径。
- 关键分支检查：503（锁失败）/500（灯控或异常）/200（成功）均存在。
