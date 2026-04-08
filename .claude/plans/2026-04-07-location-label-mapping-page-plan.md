# 计划：新增库位码与电子标签条码绑定页面

- 日期：2026-04-07
- 目标页面：magic-boot-master/src/main/resources/static/pda/location-label-mapping.html

## 目标
- 新增一个 PDA 页面用于绑定和删除“库位码 + 电子标签条码”关系。
- 页面风格与现有 `led-mapping.html` 保持一致（经典 tabs，不使用按钮风格 tabs）。

## 实施步骤
1. 新建页面文件，复用现有移动端布局与交互结构。
2. 新增“创建绑定/删除绑定”双 tab。
3. 创建绑定表单字段：
   - locationCode（库位码）
   - labelCode（电子标签条码）
4. 删除绑定表单字段：
   - locationCode（库位码）
5. 前端接口调用约定：
   - POST `/api/location/label-mapping`
   - DELETE `/api/location/label-mapping?locationCode=...`
6. 补充 `.http` 测试文件与文档。
