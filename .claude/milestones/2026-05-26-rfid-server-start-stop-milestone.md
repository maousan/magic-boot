# RFID Server 启停控制里程碑

## 目标

RFID TCP 服务支持从管理页面手动启动和停止。

## 已完成

1. 后端插件提供启动接口。
2. 后端插件提供停止接口。
3. 前端 API 封装启停请求。
4. RFID Server 页面状态横幅补齐启动/停止按钮。

## 验收方式

1. 调用插件健康接口确认插件运行态为 `STARTED`。
2. 调用 `/rfid/stop` 后确认 `/rfid/status` 返回 `running=false`。
3. 调用 `/rfid/start` 后确认 `/rfid/status` 返回 `running=true`。
4. 前端执行 `npm run build`。
