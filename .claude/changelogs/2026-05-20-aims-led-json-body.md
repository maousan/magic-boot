# 2026-05-20 变更日志：AIMS LED 控制请求体修复

## 背景
- 调用 `controlByArticleId` 时，Forest 在 `.addBody(ledItems)` 处抛出类型转换异常：
  `java.util.HashMap cannot be cast to com.dtflys.forest.utils.RequestNameValue`。
- 原因是 `ledItems` 为 `ArrayList<HashMap>`，直接传给 `.addBody(Collection)` 会被 Forest 按请求键值参数集合处理，而不是按 JSON 数组处理。

## 变更内容
- `根据articleId开关灯.ms`：将 `ledItems` 序列化为 JSON 字符串后转成 UTF-8 byte[] 写入请求体，避免 Forest JSON converter 再次解析顶层数组字符串。
- `根据labelCode开关灯.ms`：同步修复同类 `.addBody(ledItems)` / 字符串 JSON body 用法。

## 影响范围
- 仅影响 AIMS LED 控制函数请求体构建。
- AIMS 接口收到的请求体结构仍为 JSON 数组。
