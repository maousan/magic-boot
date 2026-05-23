# 巷道灯ALL端口控制指令优化

## 变更内容

- `控制巷道灯设备开关.ms` 中 `port=ALL` 不再展开为 `RED/YELLOW/GREEN` 三次下发。
- `port=ALL` 改为单次查询 `t_led_color` 中 `color=ALL` 的控制口指令映射。
- server mode 和 client mode 共用该端口命令解析逻辑。

## 影响范围

- `port=ALL` 的 `executedCount` 将从 3 变为 1。
- `ALL/RED/YELLOW/GREEN` 均按 `t_led_color` 表配置查询指令。
