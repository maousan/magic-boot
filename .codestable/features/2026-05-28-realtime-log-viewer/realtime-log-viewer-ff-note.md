---
doc_type: feature-ff-note
feature: realtime-log-viewer
date: 2026-05-28
tags: [dongxinheping, websocket, log, admin, monitoring]
---

## 做什么

为 dongxinheping-admin 新增"实时日志"页面，通过 WebSocket 连接后端 `/ws/logs` 端点，实时展示 Java 应用运行日志（替代 SSH tail -f）。后端基础设施已完整实现，本 feature 只做前端。

## 后端（已有，无需改动）

WebSocket 端点 `ws://{host}/ws/logs`，由 `LogWebSocketHandler` 处理：

**连接参数**（URL query params）：
- `token` — SaToken 认证 token（必需，`TokenHandshakeInterceptor` 校验）
- `tail` — 初始历史行数，默认 100
- `since` — 起始时间，ISO8601 格式
- `level` — 日志级别过滤，逗号分隔（如 `INFO,ERROR`）
- `keyword` — 关键字过滤

**消息协议**：
- 服务端推送：纯文本日志行 + JSON 控制消息
- `{"type":"history_end","count":N}` — 历史日志结束标记
- 客户端发送 `PING` → 服务端回复 `PONG`
- 客户端发送 `FILTER:level=INFO,ERROR&keyword=timeout` — 动态更新过滤条件
- 服务端每 30s 发 WebSocket Ping 帧

**支撑组件**：
- `LogRingBuffer` — 环形缓冲区存储历史日志
- `GlobalLogAppender` — Logback appender，实时推送新日志到订阅 session
- `SessionManager` — 管理活跃 WebSocket session
- 大消息自动分片（64KB 上限）

**认证注意**：`TokenHandshakeInterceptor` 要求 SaToken 登录态。dongxinheping-admin 的简单 auth 不产生 SaToken cookie，需要从后端获取 token。方案：连接时调后端接口取当前 SaToken 值，拼到 WebSocket URL。

## 前端方案

1. 新建 `src/views/RealtimeLog.vue` — 日志查看器页面
2. `src/router/index.ts` — 注册路由 `logs`
3. `src/layouts/AdminLayout.vue` — 系统管理菜单组下添加"实时日志"

不需要新建 API 层（WebSocket 不走 HTTP API）。

### 页面布局

整体为终端风格全屏页面：

- **顶部工具栏**（固定）：
  - 连接状态指示灯（绿=已连接 / 红=断开 / 黄=连接中）
  - 日志级别过滤下拉（多选：ALL / DEBUG / INFO / WARN / ERROR），选择后发送 `FILTER:` 消息
  - 关键字搜索输入框，回车后发送 `FILTER:` 消息
  - "暂停自动滚动"按钮（暂停后新日志仍接收但不滚动到底部）
  - "清屏"按钮（清空当前显示的日志）
  - 已接收行数统计

- **日志显示区域**（主体，深色背景 monospace）：
  - 每条日志一行，按级别着色：ERROR 红、WARN 黄、INFO 默认白、DEBUG 灰
  - 新日志追加到底部，自动滚动（暂停时不滚）
  - 最大保留 5000 行，超出后丢弃顶部旧日志（内存保护）
  - 用户手动向上滚动时自动暂停自动滚动

- **底部状态栏**（固定）：
  - 连接时长 | 已接收 N 条 | 过滤级别

### 技术选型

- **WebSocket 管理**：原生 `new WebSocket(url)` + `onmessage` / `onopen` / `onclose` / `onerror`
- **虚拟滚动**：日志量大时 DOM 节点过多会卡顿，使用 `naive-ui` 的虚拟列表或手动实现窗口渲染
- **日志着色**：CSS class 按级别着色，正则解析日志行首的级别标记
- **Token 获取**：连接前调 `GET /plugin/dongxinheping-plugin/api/system/token` 获取当前 SaToken（需后端新增一个简单接口）

### 后端需新增

一个 magic-api 接口返回当前用户的 SaToken 值，供前端拼 WebSocket URL：

`data/dongxinheping/api/东信和平/系统管理/group.json` 下新增 `获取Token.ms`：
```
GET /system/ws-token → { token: StpUtil.getTokenValue() }
```

## 验收标准

- 页面打开后自动连接 WebSocket，显示连接状态指示灯变绿
- 历史日志自动加载并显示（默认 100 行）
- 新日志实时追加，自动滚动到底部
- 手动向上滚动时自动暂停滚动，点击"恢复"或滚到底部时恢复
- 日志按级别着色（ERROR 红、WARN 黄）
- 级别过滤下拉生效，仅显示对应级别的日志
- 关键字搜索生效
- 连接断开后显示红色状态
- 保留日志不超过 5000 行（内存稳定）
- 页面关闭/切走时正确关闭 WebSocket 连接
