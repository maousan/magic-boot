# 2026-04-07 变更日志：parseLot 普通库位码兜底

## 背景
- 线上报错：`Incorrect string value: '\xAC\xED\x00\x05sr...' for column 'location_code'`
- 根因：`parseLot` 在二维码格式输入时返回对象，调用方直接将对象写入 `location_code`。

## 变更内容
1. `创建库位码和巷道灯的绑定关系.ms`
- 解析后统一提取 `Lot` 字段：`parsedLot.Lot`，否则使用原解析值。
- 新增参数校验：`lotNo`、`ledId` 不能为空。

2. `绑定库位码和标签码.ms`
- 解析 `locationCode` 后统一提取 `Lot` 字段，避免把对象传给下游 AIMS。

3. HTTP 用例补充
- `test-dongxinheping-location-led-mapping.http` 新增“二维码格式输入”场景。
- `test-dongxinheping-location-label-mapping.http` 新增“二维码格式 locationCode”场景。

## 影响范围
- 仅影响东信和平库位绑定相关接口入参与解析逻辑。
- 不影响 `parse-qrcode` 返回结构（仍可返回完整解析对象）。
