# 变更日志：新增获取波次用户数量工具函数

## 变更日期
- 2026-04-20

## 变更类型
- feat

## 变更内容
- 新增工具函数 `获取波次用户数量`：
  - 路径：`/getWaveUserCount`
  - 目录：`data/dongxinheping/function/工具函数/`
- 按 `waveNo` 查询 `t_picking_upload_user.user_count`，无记录返回 `0`。

## 兼容性说明
- 仅新增函数，不影响既有接口与函数行为。
