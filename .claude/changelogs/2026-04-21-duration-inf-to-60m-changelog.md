# 2026-04-21 inf映射60m变更日志

## 变更背景
业务要求：`duration=inf`不直接下发，统一转为`60m`。

## 变更内容
- [根据articleId开关灯.ms](/D:/IdeaProjects/magic-boot/data/dongxinheping/function/aims/控制灯/根据articleId开关灯.ms)
  - 新增映射：`if(itemDuration == 'inf'){ itemDuration = '60m' }`
- [根据labelCode开关灯.ms](/D:/IdeaProjects/magic-boot/data/dongxinheping/function/aims/控制灯/根据labelCode开关灯.ms)
  - 映射从 `inf -> 0` 调整为 `inf -> 60m`

## 影响评估
- 仅影响`inf`入参，其他时长值保持原样。
- 关灯固定时长逻辑（`duration=0`）不受影响。
