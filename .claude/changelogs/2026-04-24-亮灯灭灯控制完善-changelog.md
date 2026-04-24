# 2026-04-24 亮灯灭灯控制接口完善变更日志

## 变更文件
- data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms

## 主要变更
1. 巷道灯聚合控制接入
- 新增 `resolveAisleLedTurnOnCandidates(turnOnLocationCodes)`：
  - 批量将 `t_location_led.status` 置 1
  - 回查 `location_code -> led_id`
  - 生成 `representativeByLed`（每个 LED 一个代表库位）
- 新增 `resolveAisleLedTurnOffCandidates(turnOffLocationCodes)`：
  - 批量将 `t_location_led.status` 置 0
  - 按 `led_id` 聚合判断是否仍有 `status=1`
  - 仅返回允许关灯的代表库位

2. 灯控派发去重与防误关
- 多用户色开灯：按 `led_id` 去重派发，避免同 LED 重复开灯。
- 单用户色开灯：按 `led_id` 去重并使用 `representativeByLed` 作为派发库位。
- 关灯：仅对 `resolveAisleLedTurnOffCandidates` 返回的候选执行，避免同 LED 仍有激活库位时误关。

3. 灯控结果判定增强
- `assertLightControlSuccess` 改为：
  - `result == null` 直接失败
  - `result.success == false` 失败并透传 `message/msg`

4. 单色分组遍历稳定性
- 从直接 `for(colorKey in map)` 调用，改为先构建 `singleColorKeys` 再遍历，降低运行时遍历语义差异风险。

## 兼容性
- 对外接口 `POST /api/light/picking/control` 请求/响应结构不变。
- 未引入字符集转换 SQL（按当前数据库表已统一前提）。
