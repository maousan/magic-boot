# 变更日志：新增库位码与电子标签条码绑定页面

- 日期：2026-04-07
- 页面文件：magic-boot-master/src/main/resources/static/pda/location-label-mapping.html
- 测试文件：http/test-dongxinheping-location-label-mapping.http

## 变更内容
- 新增 PDA 页面：库位码与电子标签条码绑定。
- 页面支持两类操作：
  - 创建绑定（locationCode + labelCode）
  - 删除绑定（locationCode）
- Tabs 采用经典样式（非按钮风格）。
- 交互保持扫码输入友好：回车提交、清空按钮、状态区提示。

## 前端接口约定
- 创建：POST `/api/location/label-mapping`
- 删除：DELETE `/api/location/label-mapping?locationCode=...`
