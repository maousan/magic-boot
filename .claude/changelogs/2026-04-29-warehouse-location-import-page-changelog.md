# 2026-04-29 仓库库位导入静态页面变更日志

## 新增内容
- 新增静态页面：`/pda/warehouse-location-import.html`
- 新增页面模板下载文件：`/pda/templates/warehouse-location.xlsx`
- PDA 首页新增“仓库库位 Excel 导入”入口。

## 功能
- 选择 `.xlsx` / `.xls` 文件。
- 使用 `multipart/form-data` 调用 `POST /api/location/warehouse-location/import`。
- 展示导入总行数、成功数、失败数和失败明细。

## 兼容性
- 未修改已有库位绑定、电子标签绑定和巷道灯控制页面行为。
