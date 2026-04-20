# 里程碑：locationCode操作用户数全局线程安全Map

## 目标
- 提供进程内全局、线程安全的数据结构，记录每个 `locationCode` 的操作用户数。

## 完成项
- [x] 新增 `LocationOperationUserCountMap` 类
- [x] 提供并发安全的加减与读写接口
- [x] 支持快照导出与全量清理
- [x] 增加空值校验与负值兜底
- [x] 补齐计划与变更日志文档

## 验收建议
- 并发场景下分别调用 `increment/decrement/get` 校验计数准确性。
- 调用 `snapshot` 验证输出不可变且与当前计数一致。
