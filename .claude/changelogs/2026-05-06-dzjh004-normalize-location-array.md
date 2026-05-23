# 2026-05-06 变更日志：DZJH004 库位数组入参兼容

## 背景
- `按库位查询库存返回.ms` 的 `normalizeLocationIds` 原先会把 `locationId` 统一转成字符串再按逗号拆分。
- 当调用方传入字符串数组时，数组会被整体字符串化，无法逐项生成 `locationdetail`。

## 变更内容
1. `按库位查询库存返回.ms`
- 新增 `addLocationId` 内部 helper，统一处理单个库位值的中文逗号替换、逗号拆分、去空和去重。
- `normalizeLocationIds` 兼容 `java.util.List` 与 Java 数组，数组入参会逐项归一化。
- 保留原字符串入参行为，仍支持 `A01,A02` 和 `A01，A02`。

## 影响范围
- 仅影响 DZJH004 按库位查询库存函数的 `locationId` 入参归一化。
- 不调整远端调用地址、请求方法和响应解析逻辑。
