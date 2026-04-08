# 2026-04-03 拣货上传用户数统计变更日志

## 背景
- 拣货上传接口需要额外维护按波次统计的操作用户数量。
- 新增统计表：`t_picking_upload_user(wave_no,user_count,create_time)`。

## 变更内容
- 调整接口：`data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`。
- 在上传事务内新增用户数统计逻辑：
  1. 执行 `count(distinct user_id)` 统计当前 `wave_no` 的操作用户数。
  2. 按 `wave_no` 对 `t_picking_upload_user` 执行更新或插入。

## 影响说明
- 同一波次下，不同用户上传后 `user_count` 自动递增（按去重用户统计）。
- 同一用户重复上传不会重复计数。
- 统计写入与主从上传数据同事务提交，保证一致性。