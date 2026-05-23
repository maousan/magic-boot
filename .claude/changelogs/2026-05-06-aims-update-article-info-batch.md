# 2026-05-06 变更日志：更新 article 信息分批提交

## 背景
- `更新article信息.ms` 原先会将传入的 `dataList` 一次性提交到 AIMS `/articles`。
- 当 article 数量较大时，需要限制每次最多提交 100 行。

## 变更内容
1. `更新article信息.ms`
- 新增 `batchSize = 100`。
- 使用 Guava `Lists.partition(dataList, batchSize)` 将 `dataList` 拆成多个批次。
- 新增 `callUpdateArticleBatch` 逐批调用 AIMS `/articles`。
- 返回总数、批次数、成功数、失败数和失败批次明细。

## 影响范围
- 仅影响 AIMS article 更新函数的远端提交方式。
- 入参校验规则保持不变。
