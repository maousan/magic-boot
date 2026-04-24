# 拣货数据上传全量重写执行计划（2026-04-23）

## 目标
- 仅重写 `拣货数据上传.ms`，不复用旧上传封装。
- 固定链路：拣货数据落库（事务1）-> 全局锁 -> 更新 `t_location_status`（事务2）-> 同步亮/关灯。
- 灯控失败返回 500，且不回滚已提交状态。

## 实施步骤
1. 重写上传接口脚本：
   - 入参与明细字段校验。
   - 事务1写入 `t_picking_upload` + 批量写入 `t_picking_upload_detail`。
2. 引入 MySQL 全局锁：
   - 锁键 `light:picking:upload:global`。
   - `GET_LOCK` 获取，失败返回 503。
   - `RELEASE_LOCK` 在 finally 释放。
3. 锁内事务2重算状态：
   - 按 affected 库位重算 `status0_user_count` / `status1_user_count`。
   - UPSERT `t_location_status` 并更新版本与时间。
4. 锁内同步灯控：
   - `status0 > 1` -> `BLUE`
   - `status0 = 1` -> 用户色（缺省 `GREEN`）
   - `status0 = 0` -> 关灯
   - 灯控返回失败时抛错，接口返回 500。
5. 用例补充：
   - 在 `http/test-dongxinheping-picking-upload.http` 增加并发锁占用与灯控失败不回滚用例说明。

## 验证方式
- 静态检查：
  - 上传脚本不再引用旧上传公共函数。
  - 存在 `GET_LOCK/RELEASE_LOCK`、两段事务与 503/500 分支。
- 动态验证（由联调/测试执行）：
  - 单人/多人/最后一人完成的亮灭灯正确性。
  - 并发上传的锁行为。
  - 灯控失败时状态已提交且接口返回 500。
