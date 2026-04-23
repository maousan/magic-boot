# 2026-04-23 LED 函数测试接口变更日志

- 新增测试接口：`POST /api/location/led/light-on-function-test`
- 接口内部调用函数：`/led/turnOnAisleLedByLotNo`
- 新增 HTTP 测试用例：`http/test-dongxinheping-location-led-mapping.http` 第 7 节
