# 测试报告：AIMS绑定article和label函数

- 日期：2026-04-07
- 函数文件：data/dongxinheping/function/aims/标签/绑定article和label.ms
- 测试文件：http/test-dongxinheping-aims-link-article-label.http

## 用例
1. 成功样例：合法 `stationCode + labelCode + articleIdList`，预期 200。
2. 参数错误：`articleIdList` 为空，预期 4xx。

## 结果
- 静态检查通过：函数结构、参数校验、Forest调用均已就位。
- 运行态测试未在当前会话执行外网绑定操作，请在本地按 `.http` 用例验证实际返回。
