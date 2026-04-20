# 变更日志：拣货完成按库位用户数接入 CommonCountMap

## 变更日期
- 2026-04-20

## 变更类型
- refactor

## 变更范围
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`

## 变更内容
- 计数维度从波次维度切换为库位维度：
  - 以“当前用户在该库位未拣状态”的前后变化做差量更新。
  - 新增未拣库位执行 `CommonCountMap.increment(locationCode)`。
  - 完成清零的库位执行 `CommonCountMap.decrement(locationCode)`。
- 亮灯策略改为按库位计数分色：
  - `CommonCountMap.get(locationCode) > 1` 使用多用户色 `BLUE`。
  - 否则使用单用户色（用户映射色，默认 `GREEN`）。
- 保留灭灯逻辑：
  - 库位无未拣数据时进入灭灯列表并异步下发。
- 增加 `stationCode` 配置兜底（默认 `10001`）。
