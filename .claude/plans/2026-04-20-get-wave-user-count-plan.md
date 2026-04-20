# 获取波次用户数量工具函数计划

## 目标
- 新增工具函数，用于按波次号读取 `t_picking_upload_user.user_count`。

## 变更范围
- `data/dongxinheping/function/工具函数/获取波次用户数量.ms`

## 设计
- 输入参数：`waveNo`（必填）。
- 逻辑：
  - 参数为空返回 400。
  - SQL 查询 `t_picking_upload_user` 的 `user_count`。
  - 无记录或异常值时返回 `0`。
- 输出：`Integer`。

## 风险与应对
- 风险：调用方传空波次号导致误判。
- 应对：函数内进行必填校验并直接 `400` 失败。
