# 2026-04-24 东信和平 DZJH004 HTTP 调用函数变更日志

## 变更背景
- 需要按接口文档 5.1.4 新增 magic-api 函数调用 HTTP 接口。
- 签名逻辑需要独立沉淀为工具函数，避免散落在业务调用函数中。

## 变更内容
- 新增函数：`data/dongxinheping/function/工具函数/按库位查询库存返回.ms`
- 新增工具函数：`data/dongxinheping/function/工具函数/生成WMS接口签名.ms`

## 逻辑说明
- 主函数业务入参：
  - `EDIGROUPID`
  - `warehouseId`
  - `locationId`
- 配置项：
  - `dzjh004Url`
  - `dzjh004ClientCustomerId`
  - `dzjh004ClientDb`
  - `dzjh004AppToken`
  - `dzjh004AppKey`
  - `dzjh004AppSecret`
- 签名工具函数入参：
  - `appSecret`
  - `dataText`

## 影响范围
- 仅新增独立函数文件，不修改现有 API 路由与亮灯链路。
