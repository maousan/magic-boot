# locationCode操作用户数全局Map计划

## 目标
- 新增一个全局线程安全Map类，用于记录每个 `locationCode` 的操作用户数。

## 变更范围
- 新增 Java 类：
  - `magic-boot-master/src/main/java/org/ssssssss/magicboot/model/LocationOperationUserCountMap.java`

## 设计要点
- 基于 `ConcurrentHashMap<String, AtomicInteger>` 实现并发安全。
- 提供原子操作方法：
  - `increment`、`decrement`、`set`、`get`、`remove`、`clear`、`snapshot`
- 计数下限为 `0`，禁止出现负值。
- `locationCode` 为空时抛 `IllegalArgumentException`。

## 风险与应对
- 风险：调用方传空 `locationCode` 导致脏数据。
- 应对：统一入参校验，直接抛出明确异常信息。
