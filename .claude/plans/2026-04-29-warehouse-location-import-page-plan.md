# 2026-04-29 仓库库位导入静态页面计划

## 目标
- 新增一个 PDA 静态页面，用于下载导入模板、选择 Excel 文件并调用仓库库位导入接口。

## 变更范围
- 新增页面：`magic-boot-master/src/main/resources/static/pda/warehouse-location-import.html`
- 新增静态模板：`magic-boot-master/src/main/resources/static/pda/templates/warehouse-location.xlsx`
- 更新导航页：`magic-boot-master/src/main/resources/static/pda/index.html`

## 页面行为
- 下载模板：`./templates/warehouse-location.xlsx`
- 上传接口：`POST /api/location/warehouse-location/import`
- 上传字段：`file`
- 支持展示导入总行数、成功数、失败数和最多 20 条失败明细。

## 验证方式
- 静态检查 HTML 引用本地 vendor 和正确接口路径。
- 检查模板文件存在且导航入口存在。
- 按项目要求，修改 HTML 后执行一次编译命令。
