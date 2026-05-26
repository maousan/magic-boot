# 巷道灯绑定设备选择变更日志

## 变更内容

- 巷道灯绑定新增弹窗中，“设备ID”由手输 `NInput` 调整为可搜索 `NSelect`。
- 设备选项复用现有 LED 设备分页接口加载，展示 MAC 地址、IP 和备注。
- 新增绑定时仍提交选中设备的 MAC 地址作为 `ledId`，保持后端接口不变。

## 影响范围

- `dongxinheping-admin/src/views/LedMapping.vue`

## 验证

- `npm run build` 通过。
