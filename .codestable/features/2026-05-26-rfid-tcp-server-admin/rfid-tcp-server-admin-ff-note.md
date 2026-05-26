---
doc_type: feature-ff-note
feature: rfid-tcp-server-admin
date: 2026-05-26
tags: [dongxinheping, rfid, tcp-server, admin, plugin]
---

## 做什么
为 dongxinheping-admin 前端新增"RFID TCP Server 管理"页面，对接 magic-plugin-zintis-rfid 插件接口，展示服务状态和设备连接列表，支持向设备发送指令。

## 后端接口（已有，pluginClient 直连）
- GET `/plugin/zintis-rfid-plugin/api/rfid/status` → `{ running, port, activeConnections }`
- GET `/plugin/zintis-rfid-plugin/api/rfid/devices` → `{ running, devices: [{ deviceId, remoteAddress, connectedAt, lastActiveAt }] }`
- POST `/plugin/zintis-rfid-plugin/api/rfid/command` → `{ deviceId, command, params }` → `{ success, msgId, status, message }`

## 前端方案
1. 新建 `src/views/RfidServer.vue` — 页面组件
2. 新建 `src/api/rfid-server.ts` — API 层（pluginClient）
3. `src/router/index.ts` — 注册路由
4. `src/layouts/AdminLayout.vue` — 侧边栏增加菜单

### 页面布局
- 顶部：服务状态卡片（运行状态 Tag、监听端口、活跃连接数、刷新按钮）
- 下方：设备列表表格（deviceId、remoteAddress、connectedAt 格式化、lastActiveAt 格式化、操作列"发送指令"按钮）
- 发送指令：点击后弹出 Modal，输入 command 和可选 params JSON，确认发送

### 技术选型
- Naive UI 组件：NCard、NDataTable、NTag、NButton、NModal、NSpace、NDescriptions
- pluginClient（无 baseURL 前缀，直连插件路径）
- 时间戳格式化为本地时间字符串

## 验收标准
- 页面正常加载，状态卡片显示服务运行状态
- 设备列表表格正确展示连接设备
- 发送指令 Modal 能正常提交并显示结果
