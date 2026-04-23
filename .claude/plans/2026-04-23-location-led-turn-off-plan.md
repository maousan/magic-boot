# 2026-04-23 库位码关灯函数计划

## 目标
- 新增按库位码关巷道灯函数 `turnOffAisleLedByLotNo`。
- 新增测试接口 `POST /api/location/led/light-off-function-test` 便于联调。

## 变更范围
- `data/dongxinheping/function/led/根据库位码关巷道灯.ms`
- `data/dongxinheping/api/东信和平/库位/测试根据库位码关巷道灯函数.ms`
- `http/test-dongxinheping-location-led-mapping.http`

## 验证
- 调用测试接口，检查插件 `/control/off` 返回是否成功。
