# RFID Server 启停接口与页面控制计划

## 背景

RFID Server 页面已调整为参考 `ZintisNetty.vue` 的监控布局，但当前仅支持查看状态和刷新，缺少服务启动/停止控制。

## 目标

1. 后端插件新增 RFID TCP 服务启动接口。
2. 后端插件新增 RFID TCP 服务停止接口。
3. 前端 RFID Server 页面增加启动/停止按钮。
4. 保持现有接口路径、`deviceId` 命令发送能力和页面布局风格不变。

## 实施步骤

1. 在 `RfidApiController` 增加 `POST /rfid/start` 和 `POST /rfid/stop`。
   - verify: 插件 API 路径下可访问 `/plugin/zintis-rfid-plugin/api/rfid/start` 和 `/stop`。
2. 在 `rfid-server.ts` 增加 `startRfidServer`、`stopRfidServer`。
   - verify: TypeScript 构建通过。
3. 在 `RfidServer.vue` 顶部操作区增加启动/停止按钮。
   - verify: 启动按钮在运行中禁用，停止按钮在未运行时禁用。
4. 重新构建前端静态资源。
   - verify: `npm run build` 成功。
