# EPC 管理页面布局变更日志

## 变更内容

- 将 EPC 管理页面调整为统一的表格页面布局。
- 搜索条件从卡片右上角移入页面内容工具栏。
- 表格改为容器自适应高度，表格内容区滚动。
- 分页从表格内置分页调整为底部固定分页栏。

## 影响范围

- `dongxinheping-admin/src/views/BatchEpc.vue`

## 验证

- `npm run build` 通过。
