# 2026-04-23 PDA 页面 Ant Design Mobile 迁移变更日志

## 变更摘要
- 将 `mapping-entry.html` 从 Vue + Naive UI 重写为 React + Ant Design Mobile。
- 将 `led-mapping.html` 从 Vue + Naive UI 重写为 React + Ant Design Mobile。
- 将 `location-label-mapping.html` 从 Vue + Naive UI 重写为 React + Ant Design Mobile。

## 关键行为保持
- 接口路径保持不变：
  - `GET /api/location/led-devices`
  - `POST /api/location/led-mapping`
  - `DELETE /api/location/led-mapping?lotNo=...`
  - `POST /api/location/label-mapping`
  - `DELETE /api/location/label-mapping?labelCode=...`
- 巷道灯设备下拉继续“显示 IP，提交 MAC”。
- 颜色下拉继续带“第 1/2/3 层”序号说明。

## 交互调整
- 统一采用 Ant Design Mobile 的 `NavBar/Tabs/Card/Button/Input`。
- 状态提示采用页面内状态卡片 + Toast 即时反馈。
- 保留扫码后回车触发提交的行为。

## 风险提示
- 页面依赖公网 CDN，离线或网络受限环境需补充本地静态资源方案。
