# 2026-04-24 亮灯灭灯控制 mode=2 无效果定位修复

## 变更文件
- data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms

## 修复内容
1. 波次库位查询增强：
- `queryWaveLocationCodes` 从仅 `t_picking_upload_detail.wave_no` 命中
- 调整为 `detail.wave_no OR main.wave_no` 双通道命中：
  - `d.wave_no = waveNo` 或 `m.wave_no = waveNo`（`m.id = d.bill_id`）

2. mode=2 空库位兜底：
- 当 `mode=2` 且未查询到可控库位时，返回 `404` 与明确提示：
  - `未找到可亮灯库位，请先上传拣货数据`
- 避免返回 200 但实际无动作。

## 影响
- 不改变其它模式的成功路径行为。
- 提升手动亮灯模式可诊断性，减少“请求成功但无效果”的假成功。
