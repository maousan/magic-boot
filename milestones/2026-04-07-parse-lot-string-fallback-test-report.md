# 2026-04-07 测试报告：parseLot 普通库位码兜底

## 测试范围
- `data/dongxinheping/api/东信和平/库位/创建库位码和巷道灯的绑定关系.ms`
- `data/dongxinheping/api/东信和平/库位/绑定库位码和标签码.ms`
- `http/test-dongxinheping-location-led-mapping.http`
- `http/test-dongxinheping-location-label-mapping.http`

## 已执行
1. 编译验证
- 命令：`mvn -pl magic-boot-master -am -DskipTests compile`
- 结果：通过（BUILD SUCCESS）

## 用例更新
1. `test-dongxinheping-location-led-mapping.http`
- 新增“二维码格式输入（双引号开头，取 Lot 字段）”场景

2. `test-dongxinheping-location-label-mapping.http`
- 新增“二维码格式 locationCode（双引号开头，取 Lot 字段）”场景

## 待联调验证（运行态）
- 需启动服务后执行上述 .http 用例，确认：
  - 入参为二维码格式时，实际使用 `Lot` 字段参与绑定
  - `location_code` 入库为纯字符串，不再出现对象序列化字节报错
