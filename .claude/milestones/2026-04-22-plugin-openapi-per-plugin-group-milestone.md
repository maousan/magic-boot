# 里程碑：插件 OpenAPI 按插件分组完成（2026-04-22）

## 里程碑结果
- 已支持 PF4J 插件 OpenAPI “一插件一分组”。
- `magic-plugin-zintis-led` 可在独立分组中查看接口文档。

## 验证结果
- 构建成功：`mvn -pl magic-boot-master -am -DskipTests package`

## 后续建议
- 如需在插件 STOPPED 时即时移除分组，可补充分组注销机制。
