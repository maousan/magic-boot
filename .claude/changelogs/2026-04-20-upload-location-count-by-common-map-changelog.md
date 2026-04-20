# 变更日志：拣货数据上传按库位用户数计数并接入重启恢复

## 变更日期
- 2026-04-20

## 变更类型
- refactor

## 变更范围
- `data/dongxinheping/api/东信和平/亮灯对接/拣货数据上传.ms`
- `magic-boot-master/src/main/java/org/ssssssss/magicboot/service/CommonCountMapRecoveryInitializer.java`

## 变更内容
- 计数维度修正：
  - 从“波次维度 userCount”改为“库位维度 locationCode userCount”。
  - 通过旧/新未拣库位集合做差量更新，调用 `CommonCountMap.increment/decrement` 维护计数。
- 亮灯策略修正：
  - 按 `CommonCountMap.get(locationCode)` 判断单用户/多用户库位。
  - 单用户库位使用用户映射色（无映射默认 GREEN），多用户库位使用 BLUE。
- 启动恢复机制：
  - 新增 `CommonCountMapRecoveryInitializer`，应用启动后从数据库聚合未拣数据，自动重建 `CommonCountMap`。
  - 恢复异常仅记录告警，不阻断应用启动。
