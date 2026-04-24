# 2026-04-24 亮灯灭灯控制 mode=2 单色分组键丢失修复

## 证据
- 日志显示：
  - `decision_done` 中 `turnOnSingle=size=1`
  - 但后续没有 `dispatch_turn_on_single_start/result`
  - 且 `aisleTurnOn=0`

## 根因
- `singleColorKeys` 通过遍历 `singleColorLocationMap` 反推，运行时 map 遍历语义导致 key 丢失，进而未执行单色亮灯派发。

## 修复
- 在构建 `singleColorLocationMap` 时同步维护 `singleColorKeys/singleColorKeySet`。
- 删除从 map 反推 key 的逻辑。

## 影响
- `turnOnSingle` 候选将稳定进入主灯与巷道灯亮灯调用。
