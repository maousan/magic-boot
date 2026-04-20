# 变更日志：CommonCountMap 通用计数类完善

## 变更日期
- 2026-04-20

## 变更类型
- refactor

## 变更范围
- `magic-boot-master/src/main/java/org/ssssssss/magicboot/model/CommonCountMap.java`

## 变更内容
- 泛化命名：将 `locationCode`/`userCount` 语义收敛为通用 `key`/`count`。
- 新增通用增量方法 `add(key, delta)`，并保持 `increment/decrement` 兼容。
- 零值自动清理：计数归零时移除键，避免长期堆积无效条目。
- 新增查询能力：
  - `containsKey(key)`
  - `size()`
  - `totalCount()`
- 入参校验统一为 `validateKey`，异常信息改为通用 `key不能为空`。

## 兼容性说明
- 现有 `increment/decrement/set/get/remove/clear/snapshot` 方法仍可继续使用。
