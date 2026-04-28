# 2026-04-24 label-mapping 友好错误信息优化

## 变更文件
- data/dongxinheping/api/东信和平/库位/绑定库位码和标签码.ms

## 变更内容
- 新增 AIMS 错误提取与友好文案映射：
  - 从响应体/异常信息中提取 `returnMsg`
  - 对 `Checksum of label code is not matched` 映射为中文：
    - `标签码校验位不匹配，请确认标签码是否正确`
- 新增请求异常 catch：
  - `executeAsResponse()` 抛出异常时，仍返回业务友好错误，不再透出底层网络异常原文。

## 结果
- `/api/location/label-mapping` 在 AIMS 报错时，返回信息更友好、可读。
