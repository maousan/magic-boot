# 里程碑：获取波次用户数量工具函数

## 目标
- 提供统一函数读取波次单操作用户数，减少重复 SQL。

## 完成项
- [x] 新增 `获取波次用户数量.ms`
- [x] 实现 `waveNo` 参数校验
- [x] 实现 `t_picking_upload_user.user_count` 查询与 0 值兜底
- [x] 补齐计划与变更日志文档

## 验收建议
- 在 magic-api 中调用 `/getWaveUserCount`：
  - 传存在的 `waveNo` 返回对应数量
  - 传不存在的 `waveNo` 返回 `0`
  - 不传 `waveNo` 返回 `400`
