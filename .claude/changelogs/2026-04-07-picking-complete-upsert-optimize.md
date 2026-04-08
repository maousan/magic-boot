# 变更日志：拣货完成明细写入改为 Upsert

- 日期：2026-04-07
- 文件：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`

## 变更内容
- 将明细处理从 `select + delete + insert` 改为单条 `insert ... on duplicate key update`。
- upsert 语句中补充了 `wave_no` 写入，避免新增行时缺少该字段。
- 保留“按库位统计未完成并灭灯”的后续逻辑不变。

## 性能收益
- 减少每条明细的数据库往返次数。
- 减少删除再插入带来的索引维护开销。
- 缩短事务持锁时间。

## 注意事项
- `on duplicate key update` 需要业务唯一键生效：
  - 建议唯一索引：`(bill_id, material_code, batch_no, location_code)`。