# RFID Server 页面布局调整变更日志

## 变更时间

2026-05-26

## 变更范围

`dongxinheping-admin/src/views/RfidServer.vue`

## 变更内容

1. RFID Server 页面布局对齐 `ZintisNetty.vue`。
2. 增加顶部服务状态横幅、自动刷新开关和指标卡。
3. 设备列表与指令控制台改为左右分栏。
4. 发送指令由弹窗改为右侧固定控制台，减少操作跳转。
5. 保持现有 RFID API、路由和 `deviceId` 字段不变。

## 验收点

1. 页面能正常展示服务状态、端口、活跃连接和在线终端数。
2. 设备列表可选择目标终端。
3. 指令控制台可按 `deviceId` 发送命令。
4. 页面构建通过。
