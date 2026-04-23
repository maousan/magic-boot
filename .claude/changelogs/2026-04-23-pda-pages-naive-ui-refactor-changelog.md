# 2026-04-23 PDA页面 Naive UI 重构变更日志

## 变更类型
- 页面重构（UI框架升级）

## 变更内容
- 重构页面：
  - `magic-boot-master/src/main/resources/static/pda/mapping-entry.html`
  - `magic-boot-master/src/main/resources/static/pda/led-mapping.html`
  - `magic-boot-master/src/main/resources/static/pda/location-label-mapping.html`
- 引入方式：Vue3 + Naive UI CDN，替换原生 HTML/CSS 控件。
- 保留原有接口行为：
  - 巷道灯绑定：`POST/DELETE /api/location/led-mapping`
  - 标签绑定：`POST/DELETE /api/location/label-mapping`
  - 设备查询：`GET /api/location/led-devices`
- 巷道灯设备下拉行为：显示 `remark(ip)`，提交值使用 `macAddress`。

## 兼容性说明
- 接口路径与请求字段保持兼容。
- 页面仍支持移动端访问与扫码输入后回车提交。

## 验证
- 执行 `mvn -pl magic-boot-master -am -DskipTests package`。
