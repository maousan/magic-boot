# 2026-04-23 PDA页面 Naive UI 重构计划

## 目标
- 将 `static/pda` 下的绑定相关页面统一重构为 Naive UI 风格。
- 保持现有接口路径与业务行为不变。
- 保持移动端扫码输入可用性。

## 范围
- `mapping-entry.html`
- `led-mapping.html`
- `location-label-mapping.html`

## 方案
1. 使用 Vue3 + Naive UI CDN（免构建）进行页面重构。
2. 统一视觉结构：顶部导航、标题区、主表单卡片、状态卡片。
3. 将原生输入与按钮替换为 `n-input`/`n-select`/`n-button`/`n-tabs` 等组件。
4. 保留原接口调用逻辑和状态判断。
5. 对巷道灯设备下拉保留“显示IP、提交MAC”的行为。

## 验证
- 编译命令：`mvn -pl magic-boot-master -am -DskipTests package`
- 手工校验：页面能打开、创建/删除操作请求路径不变、状态提示正常。
