# 计划：新增AIMS更新article信息函数

- 日期：2026-04-07
- 目标文件：data/dongxinheping/function/aims/数据/更新article信息.ms

## 目标
- 基于“Update article infomation”接口定义，新增可复用的ms函数。
- 封装AIMS `POST /articles` 调用。
- 在函数内做最小必要参数校验。

## 实施步骤
1. 在 `function/aims/数据` 分组新增函数文件。
2. 按 OpenAPI 定义校验 `dataList`、`id`、`data` 必填。
3. 使用 Forest 发起 `POST http://119.29.52.245:9002/articles`。
4. 返回标准结果对象（success/status/response）。
5. 新增 `.http` 测试样例与变更文档。
