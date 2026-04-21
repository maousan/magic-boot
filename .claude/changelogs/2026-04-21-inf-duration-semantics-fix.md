# 2026-04-21 inf 时长语义修正变更日志

## 背景
- 业务约定：`inf` 表示常亮。
- 现状问题：在控制灯通用函数中，`inf` 被转换为 `0`，导致下游收到的时长不符合协议语义。

## 变更内容
- 文件：`data/dongxinheping/function/aims/控制灯/根据articleId开关灯.ms`
- 调整：移除 `if(itemDuration == 'inf'){ itemDuration = '0' }` 转换逻辑。
- 结果：请求中的 `duration='inf'` 将原样透传到下游 AIMS 接口。

## 影响说明
- 开灯链路：`duration='inf'` 不再被改写。
- 关灯链路：`根据articleId关灯.ms` 仍按既有逻辑发送 `duration='0'`，不受本次修改影响。

## 验证建议
- 发送亮灯请求（mode=1/2，duration=inf），确认下游请求体 `duration` 为 `inf`。
- 发送关灯请求（mode=3/4），确认下游请求体 `duration` 仍为 `0`。
