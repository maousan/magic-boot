# 2026-04-21 control开灯去除inf写死变更日志

## 变更背景
亮灯链路中存在`duration`默认或下发值写死`inf`的问题，不满足当前业务口径。

## 变更内容
- [亮灯灭灯控制.ms](/D:/IdeaProjects/magic-boot/data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms)
  - 开灯调用 `turnOnLedByArticleId` 时显式透传 `duration`
  - `duration` 默认值由 `inf` 调整为 `60m`
  - 接口示例与字段说明默认值同步为 `60m`
- [根据articleId开灯.ms](/D:/IdeaProjects/magic-boot/data/dongxinheping/function/aims/控制灯/根据articleId开灯.ms)
  - 由写死 `duration: "inf"` 改为使用入参 `duration`
  - 当未传入时默认 `60m`

## 影响说明
- 开灯下发时长可由接口入参控制，不再固定`inf`。
- 关灯链路不受影响。
