# 2026-04-21 inf映射60m调整计划

## 目标
将灯控链路中`duration=inf`统一映射为`60m`后再下发AIMS。

## 范围
- `data/dongxinheping/function/aims/控制灯/根据articleId开关灯.ms`
- `data/dongxinheping/function/aims/控制灯/根据labelCode开关灯.ms`

## 风险
- 若AIMS侧对`inf`和`60m`业务语义不等价，可能带来亮灯时长差异。

## 验证
- 构造`duration=inf`请求，检查下游请求体时长为`60m`。
- 构造`duration=1m`请求，检查下游请求体保持`1m`。
