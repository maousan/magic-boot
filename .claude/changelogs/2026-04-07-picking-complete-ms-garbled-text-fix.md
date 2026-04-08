# 变更日志：拣货完成.ms 乱码修复

- 日期：2026-04-07
- 文件：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`

## 问题描述
- `.ms` 文件头部 JSON 与脚本提示文案存在乱码，且部分字符串闭合异常，导致解析报错：
  - `JsonParseException: Illegal unquoted character ((CTRL-CHAR, code 13))`

## 修复内容
- 重写文件为合法 UTF-8 无 BOM、CRLF。
- 修复头部 JSON 的中文字段，保证 JSON 可解析。
- 修复脚本中的中文提示文案，保证接口返回信息可读。
- 保留并确认“按 location_code 全部拣完后调用灭灯接口”逻辑不变。

## 校验结果
- 头部 JSON 解析通过。
- 关键逻辑关键字仍存在：`locationCodeMap`、`turnOffLedByArticleId`。