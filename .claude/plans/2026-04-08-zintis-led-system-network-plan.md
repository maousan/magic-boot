# magic-plugin-zintis-led 获取系统 MAC/IP 功能实施计划

## 目标
- 新增获取设备系统网络信息能力：IP 地址与 MAC 地址。

## 实施内容
- 新增 DTO：`LedSystemNetworkResponse`。
- 新增服务方法：`LedControlService#querySystemNetwork`。
- 新增接口：`POST /led/system/network`。
- 从查询响应 payload 中解析：
  - IPv4 地址（如 `192.168.2.198`）
  - MAC 地址（支持 `AA-BB-CC-DD-EE-FF`、`AA:BB:CC:DD:EE:FF`、`AABBCCDDEEFF`）

## 验证
- 编译：`mvn -pl magic-plugin-zintis-led package -DskipTests`
- 复制 jar 到 `plugins` 并确认时间戳。
