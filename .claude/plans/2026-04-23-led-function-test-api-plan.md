# 2026-04-23 LED 函数测试接口计划

## 目标
- 新增一个测试接口，用于调用函数 `/led/turnOnAisleLedByLotNo`，快速验证按库位码亮灯链路。

## 变更范围
- 新增 API 脚本：`data/dongxinheping/api/东信和平/库位/测试根据库位码亮巷道灯函数.ms`
- 更新 HTTP 测试文件：`http/test-dongxinheping-location-led-mapping.http`

## 验证方式
- 通过 HTTP 用例 `POST /api/location/led/light-on-function-test` 传入 `lotNo` 进行联调验证。
