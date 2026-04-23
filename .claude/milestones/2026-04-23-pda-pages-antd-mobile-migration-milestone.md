# 2026-04-23 PDA 页面 Ant Design Mobile 迁移里程碑

## 里程碑名称
PDA 映射页面 UI 框架迁移（Naive UI -> Ant Design Mobile）

## 完成标准
- 三个页面完成 React + Ant Design Mobile 改造：
  - `mapping-entry.html`
  - `led-mapping.html`
  - `location-label-mapping.html`
- 后端接口调用路径保持兼容。
- 基础编译通过。

## 验收结果
- 代码已完成迁移。
- 已执行编译命令（见本次任务输出）。
- 待业务侧在 PDA 实机做扫码链路回归验证。

## 后续建议
- 如需弱网/离线保障，增加本地静态资源托管，替换 CDN 依赖。
