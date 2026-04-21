# 2026-04-21 亮灯链路业务测试计划（简单到复杂）

## 1. 目标与范围
本计划覆盖以下 3 个接口的业务场景验证：
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- `data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`

验证目标：
1. 核心业务流程正确（上传 -> 完成 -> 控制）。
2. 库位亮灭灯决策正确（单用户/多用户/混合）。
3. `CommonCountMap` 与数据库状态一致（含重启后自愈场景）。
4. `t_light_trace_log` 日志落库语义正确（`action_type`、`location_codes`）。

## 2. 前置条件
1. 已完成数据库迁移，存在 `t_light_trace_log` 表。
2. 系统已重启并可正常访问 3 个接口。
3. 已准备至少 1 个测试波次号（建议：`WV202603250001`）。
4. 已清理或标记历史干扰数据（按 `waveNo` 过滤）。

## 3. 测试分层策略（由简单到复杂）
- L1 基础可用：单接口最小成功/失败。
- L2 核心流程：上传与完成联动，验证状态流转。
- L3 业务规则：单用户/多用户、亮灭灯决策。
- L4 复杂组合：同请求 mixed 动作、多接口串联。
- L5 稳定性与边界：异常、幂等、重启恢复。

## 4. 详细测试用例

### L1 基础可用

#### TC-01 上传接口最小成功
- 接口：`拣货数据上传`
- 输入：合法 `waveNo/userId/updateTime/details`（至少1条）
- 步骤：调用上传接口
- 预期：
  1. HTTP 200，业务码成功
  2. `t_picking_upload`/`t_picking_upload_detail` 写入成功
  3. 日志出现 `stage=receive`、`stage=done`

#### TC-02 上传接口参数校验失败
- 接口：`拣货数据上传`
- 输入：缺少 `waveNo` 或 `details=[]`
- 步骤：调用上传接口
- 预期：
  1. HTTP 400
  2. 提示参数缺失
  3. 主表/明细表不新增记录

#### TC-03 完成接口最小成功
- 接口：`拣货完成`
- 输入：存在的 `waveNo+userId`，明细合法
- 步骤：先上传，再调用完成
- 预期：
  1. HTTP 200
  2. 对应明细 `status` 正确更新
  3. 日志出现 `stage=light_decision` 与 `stage=done`

#### TC-04 控制接口最小成功
- 接口：`亮灯灭灯控制`
- 输入：合法 `waveNo/userId/mode`
- 步骤：调用控制接口
- 预期：
  1. HTTP 200
  2. `t_light_control_log` 新增一条
  3. `t_light_trace_log` 新增 `api_name=control` 的 `done`

### L2 核心流程联动

#### TC-05 上传后立即完成（同用户）
- 场景：用户上传待拣数据后，完成其中一部分
- 步骤：上传 -> 完成
- 预期：
  1. 完成后仅未拣库位保持亮灯
  2. `CommonCountMap` 数量变化与明细状态一致
  3. `t_light_trace_log` 形成同 `traceId`（或同波次）的连续记录

#### TC-06 上传全拣完数据
- 场景：上传请求中所有明细 `actualQuantity == planQuantity`
- 预期：
  1. 不应产生新的待拣亮灯
  2. `action_type` 应为 `turn_off` 或 `mix`（视是否同时存在亮灭）
  3. `location_codes` 与真实动作集合一致

### L3 业务规则验证

#### TC-07 单用户库位亮灯
- 场景：某库位仅1个用户未拣
- 预期：
  1. 该库位归入单用户亮灯集合
  2. `t_light_trace_log.extra_json.turnOnSingle` 包含该库位

#### TC-08 多用户库位亮灯
- 场景：同库位存在2个及以上用户未拣
- 预期：
  1. 该库位归入多用户集合
  2. `CommonCountMap` 对应库位计数 > 1

#### TC-09 complete 混合动作（有亮有灭）
- 场景：同次完成请求造成部分库位需亮、部分需灭
- 预期：
  1. `t_light_trace_log.action_type = mix`
  2. `location_codes` 为亮灭并集
  3. `extra_json.turnOn*` 与 `extra_json.turnOff` 同时有值

#### TC-10 control 无动作场景
- 场景：无库位需要亮/灭
- 预期：
  1. `action_type = none`
  2. `location_codes = []`
  3. `extra_json.allCount` 存在且 > 0（若有库位）

### L4 复杂串联场景

#### TC-11 三接口完整串联（简单到复杂主链路）
- 步骤：
  1. 上传（待拣）
  2. 完成（部分完成）
  3. 控制（手动/自动）
- 预期：
  1. 三接口都成功
  2. 亮灭灯结果符合当下未拣状态
  3. `t_light_trace_log` 可按 `waveNo+userId`完整还原链路

#### TC-12 多用户交错操作
- 场景：USER001 与 USER002 对同波次不同/相同库位交错上传与完成
- 预期：
  1. 多用户计数增减准确
  2. 多用户颜色逻辑正确
  3. 不出现“全部完成但仍常亮”或“未完成却灭灯”

### L5 稳定性与边界

#### TC-13 重复上传幂等性
- 场景：相同上传请求重复调用
- 预期：
  1. 明细不重复膨胀
  2. `CommonCountMap` 不异常累加

#### TC-14 控制日志落库失败容错（fail-open）
- 场景：人为制造 `t_light_trace_log` 写入失败（例如临时改权限/字段）
- 预期：
  1. 接口主流程仍返回成功（可观测失败不影响业务）
  2. 日志出现 `trace_persist_error`

#### TC-15 应用重启恢复
- 场景：重启后直接调用完成或上传
- 预期：
  1. `CommonCountMap` 可按数据库值回填
  2. 首次请求后亮灭灯决策正确

## 5. 执行顺序建议
1. 先跑 L1/L2（验证可用与主流程）
2. 再跑 L3（规则准确性）
3. 最后跑 L4/L5（复杂与稳定性）

## 6. 关键检查SQL（示例）
```sql
-- 最新链路日志
select id, trace_id, api_name, stage, action_type, location_count, create_time
from t_light_trace_log
order by create_time desc
limit 100;

-- 按波次查看日志
select trace_id, api_name, stage, action_type, location_codes, extra_json, create_time
from t_light_trace_log
where wave_no = 'WV202603250001'
order by create_time asc;

-- 查看控制审计
select id, wave_no, user_id, mode, color, duration, action_type, create_time
from t_light_control_log
where wave_no = 'WV202603250001'
order by create_time desc;
```

## 7. 通过标准
1. P0：无业务错误（状态错、亮灭决策错、计数错）。
2. P1：日志落库语义正确（`action_type/location_codes/extra_json` 一致）。
3. P2：异常场景满足 fail-open，不影响主业务成功。
