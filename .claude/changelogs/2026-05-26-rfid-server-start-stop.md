# RFID Server 启停功能变更日志

## 变更时间

2026-05-26

## 变更范围

- `magic-plugin-zintis-rfid`
- `dongxinheping-admin`

## 变更内容

1. RFID 插件新增 `POST /rfid/start` 接口，用于启动 TCP 服务。
2. RFID 插件新增 `POST /rfid/stop` 接口，用于停止 TCP 服务。
3. 前端 `rfid-server.ts` 增加启动和停止 API 方法。
4. RFID Server 页面顶部状态区增加启动和停止按钮。

## 接口路径

插件运行态完整路径：

```text
POST /plugin/zintis-rfid-plugin/api/rfid/start
POST /plugin/zintis-rfid-plugin/api/rfid/stop
```

## 验收点

1. 服务运行中时启动按钮禁用，停止按钮可用。
2. 服务停止后状态显示未启动，活跃连接为 0。
3. 服务启动后状态显示运行中，并返回监听端口。
4. 前端构建通过。
