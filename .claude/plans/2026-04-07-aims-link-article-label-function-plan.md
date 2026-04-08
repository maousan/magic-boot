# 计划：新增AIMS绑定article和label函数

- 日期：2026-04-07
- 目标文件：data/dongxinheping/function/aims/标签/绑定article和label.ms

## 目标
- 基于 “Link Product to label” 接口定义新增ms函数。
- 封装 AIMS `POST /labels/link/{stationCode}`。
- 提供最小必要参数校验和统一返回结构。

## 实施步骤
1. 在 `function/aims/标签` 分组新增函数文件。
2. 校验 `stationCode`、`labelCode`、`articleIdList`。
3. 调用 Forest POST 到 AIMS 标签绑定接口。
4. 返回 `{success,status,response}` 结果。
5. 新增 `.http` 测试样例与变更文档。
