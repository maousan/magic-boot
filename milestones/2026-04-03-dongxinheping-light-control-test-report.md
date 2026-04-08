# 2026-04-03 东信和平亮灯控制接口测试报告

## 测试对象
- 接口：`POST /api/light/control`
- 脚本：`data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms`
- 用例：`http/test-dongxinheping-light-control.http`

## 接口实测结果
1. `1-auto-on`：200，`指令发送成功`
2. `2-manual-off`：200，`指令发送成功`
3. `3-invalid-mode`：400，`mode仅支持1/2/3/4`
4. `4-invalid-color`：400，`color仅支持RED/GREEN/BLUE/YELLOW/CYAN`
5. `5-invalid-duration`：400，`duration仅支持10s/30s/1m/2m/5m/10m/15m/20m/30m/60m/inf`
6. `6-missing-waveNo`：400，`body[waveNo]为必填项`

## 数据库核验结果
`SELECT wave_no,user_id,mode,action_type,request_path FROM t_light_control_log ...` 返回：
- `WV-LIGHT-0001 | USER-LIGHT-01 | 1 | ON  | /api/light/control`
- `WV-LIGHT-0002 | USER-LIGHT-02 | 4 | OFF | /api/light/control`

## 结论
- 接口已按 OpenAPI 定义生效。
- 当前版本仅日志入库，不调用三方接口（符合当前阶段要求）。
- 参数校验与动作类型推断（ON/OFF）正常。