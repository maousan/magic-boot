# LED Netty客户端设备保存变更日志

## 变更内容

- LED 插件继承主应用 Spring Context，以便获取主应用的 `JdbcTemplate`。
- 新增 `LedDeviceRegistryService`，负责将 Netty 客户端上报的 MAC/IP 保存到 `t_led_device`。
- `LedNettyServerHandler` 在解析到客户端上报 MAC 后触发设备保存。
- 新 MAC 自动插入；已存在 MAC 只更新 IP，不覆盖 `remark`。

## 说明

- TCP 连接刚建立时没有设备 MAC，因此保存动作发生在客户端首次上报包含 MAC 的数据帧后。
- 如果上报帧未包含 IP，则使用 TCP 远端地址 IP 兜底。
