# 2026-04-23 库位码触发巷道灯亮灯函数计划

## 目标
- 新增一个按库位码触发巷道灯亮灯的函数。
- 入参仅需 `lotNo`（可选 `timeoutMs`）；
- 根据 `lotNo` -> `t_location_led` -> `t_led_device` 查出设备 `ip`，端口固定默认 `9527`。
- 根据绑定颜色到 `t_led_color` 查出对应 `dataCommand`，并调用 Zintis LED 插件 `control/on`。

## 范围
- 新增 Magic Function：`data/dongxinheping/function/aims/控制灯/根据库位码亮巷道灯.ms`

## 实现要点
1. 解析库位码（兼容二维码格式 `Lot` 字段）；
2. 查询绑定关系（`t_location_led`）获取 `led_id` 与 `color`；
3. 查询设备信息（`t_led_device`）获取 `ip`；
4. 查询颜色配置（`t_led_color`）获取命令码（`code`）并转换为十进制 `dataCommand`；
5. 组装并调用插件接口 `POST /plugin/zintis-led-plugin/api/control/on`；
6. 返回汇总结果（lotNo、color、ip、port、dataCommand、插件响应）。

## 验证
- 编译验证：`mvn -pl magic-boot-master -am -DskipTests package`
