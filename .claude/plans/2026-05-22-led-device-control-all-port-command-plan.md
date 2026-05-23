# 巷道灯ALL端口控制指令优化计划

## 目标

- `port=ALL` 时只下发一次控制口指令，不再展开遍历 `RED/YELLOW/GREEN`。
- 控制口指令从 `t_led_color` 表的 `color=ALL` 映射读取，保持配置来源一致。

## 验证

- 检查 `控制巷道灯设备开关.ms` 中 `ALL` 仅加入一次 `ports`。
- 检查 `ALL/RED/YELLOW/GREEN` 均走同一段 `t_led_color` 查询和 code 解析逻辑。
