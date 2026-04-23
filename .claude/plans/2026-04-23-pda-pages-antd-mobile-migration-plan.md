# 2026-04-23 PDA 页面 Ant Design Mobile 迁移计划

## 目标
- 将 `static/pda` 下三页从 `Vue3 + Naive UI` 迁移为 `React18 + Ant Design Mobile`。
- 保持现有后端接口路径与行为不变。
- 保留 PDA 场景核心交互（扫码输入、创建/删除操作、状态反馈）。

## 范围
- `magic-boot-master/src/main/resources/static/pda/mapping-entry.html`
- `magic-boot-master/src/main/resources/static/pda/led-mapping.html`
- `magic-boot-master/src/main/resources/static/pda/location-label-mapping.html`

## 方案
1. 使用 CDN 方式引入 `react/react-dom/antd-mobile`，避免新增构建链路。
2. 入口页重建为 React 卡片导航。
3. 两个绑定页重建为：
   - 顶部 `NavBar` 返回入口
   - 中部业务操作 `Tabs`
   - 底部状态卡片
4. 继续复用原有接口：
   - `/api/location/led-devices`
   - `/api/location/led-mapping`
   - `/api/location/label-mapping`
5. LED 设备下拉保持“显示 IP，提交 MAC”。

## 风险与回退
- 风险：CDN 依赖不可用会导致页面空白。
- 回退：可直接回滚三页 HTML 到上一个 commit（Naive UI 版本）。

## 验证
- 编译命令：`mvn -pl magic-boot-master -am -DskipTests package`
- 手工验证：
  - 页面可打开且可跳转
  - 创建/删除请求路径不变
  - 状态文案可见
