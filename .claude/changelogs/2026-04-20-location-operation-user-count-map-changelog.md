# 变更日志：新增locationCode操作用户数全局线程安全Map

## 变更日期
- 2026-04-20

## 变更类型
- feat

## 变更内容
- 新增 `LocationOperationUserCountMap` 全局类：
  - 路径：`magic-boot-master/src/main/java/org/ssssssss/magicboot/model/LocationOperationUserCountMap.java`
  - 用途：记录每个 `locationCode` 的操作用户数。
- 使用 `ConcurrentHashMap + AtomicInteger` 保证并发安全。
- 增加原子方法：`increment/decrement/set/get/remove/clear/snapshot`。
- 加入统一参数校验，阻止空 `locationCode` 写入。

## 兼容性说明
- 仅新增类，不影响现有逻辑。
