# 变更日志：CommonCountMap 重启后自愈回填

## 变更日期
- 2026-04-20

## 变更类型
- fix

## 变更范围
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`

## 问题说明
- 在同波次同用户重复上传/完成场景下，若应用重启导致 `CommonCountMap` 为空，差量计数可能为 0，缓存无法自动恢复，出现空Map日志。

## 修复内容
- 在上传与完成接口中，对受影响 `locationCode` 增加数据库实值回填逻辑：
  - 实时查询该库位“未拣状态”的 distinct 用户数；
  - 数量 > 0 则 `CommonCountMap.set(locationCode, count)`；
  - 数量 <= 0 则 `CommonCountMap.remove(locationCode)`。

## 结果
- 计数逻辑由“仅差量更新”升级为“差量识别 + 实值回填”，可在重启后自愈，避免缓存漂移。
