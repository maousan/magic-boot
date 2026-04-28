# 2026-04-24 巷道灯设备控制页面与接口变更日志

## 新增内容
1. 新增 API：`POST /api/location/led-device-control`
- 文件：`data/dongxinheping/api/东信和平/库位/控制巷道灯设备开关.ms`
- 能力：按设备 + 指令 + 端口执行巷道灯开关。
- 支持参数：
  - `ledId`（设备 MAC）
  - `command`：`ON` / `OFF`
  - `port`：`ALL` / `RED` / `YELLOW` / `GREEN`
  - `timeoutMs`（可选，默认 3000）
- 行为：
  - 设备从 `t_led_device` 查询
  - 端口命令从 `t_led_color` 查询并解析
  - 调用插件 on/off 接口
  - 成功后同步更新 `t_location_led.status`

2. 新增页面：`/pda/aisle-led-control.html`
- 文件：`magic-boot-master/src/main/resources/static/pda/aisle-led-control.html`
- 交互：
  - 选择设备（从 `/api/location/led-devices` 加载）
  - 选择指令（开灯/关灯）
  - 选择控制端口（全部、红、黄、绿）
  - 点击执行调用 `/api/location/led-device-control`

3. 导航入口新增
- 文件：`magic-boot-master/src/main/resources/static/pda/mapping-entry.html`
- 新增“巷道灯设备控制”卡片跳转至新页面。

## 兼容性
- 未修改现有拣货上传、拣货完成、亮灯控制接口协议。
- 新能力为增量新增，不影响既有页面链路。
