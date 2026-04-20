# Get all articles information 适配函数计划

## 目标
- 在 `data/dongxinheping` 目录下新增可复用函数，适配 OpenAPI “Get all articles information”。
- 与现有 AIMS 函数风格保持一致，支持快速联调。

## 变更范围
- 新增函数文件：
  - `data/dongxinheping/function/aims/数据/获取全部article信息.ms`

## 设计要点
- 默认请求路径 `/articles`，允许通过 `endpointPath` 覆盖。
- 默认读取配置 `aimsHost`，端口优先 `aimsPort`，否则回退 `9002`。
- 支持常见查询参数：`articleCode`、`articleName`、`pageNo`、`pageSize`、`stationCode`。
- 支持 `rawQuery` 透传额外 query 参数。
- 支持可选 `Authorization` 头。

## 风险与应对
- 风险：外部 OpenAPI 临时链接不可达，无法自动读取完整字段定义。
- 应对：先提供兼容型函数参数设计，待接口字段确认后可快速扩展映射。
