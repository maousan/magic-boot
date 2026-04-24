# 2026-04-24 亮灯灭灯控制 mode=2 强制亮灯修复

## 变更文件
- data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms

## 修复内容
- 调整 `mode=2`（手动亮灯）决策逻辑：
  - 从“按 `t_location_status.status0_user_count` 自动判定”
  - 改为“按当前波次库位强制进入亮灯列表（单色分组）”。

## 影响
- `mode=2` 请求会对该波次库位执行亮灯派发（使用请求中的 `color` 与 `duration`）。
- 其他模式（1/3/4）逻辑保持不变。
