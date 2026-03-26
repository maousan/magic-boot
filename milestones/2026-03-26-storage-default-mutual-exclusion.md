# 里程碑：存储平台默认值强互斥

日期：2026-03-26  
范围：`magic-api-plugin-file` 默认平台选择逻辑

## 变更内容（已完成）
- `MagicDynamicFileClient.put(..., isDefault)` 增加互斥处理：
  - 当某平台设置为默认时，先清空其他平台默认标记，再设置当前平台为默认。
- `MagicDynamicFileClient.delete(...)` 删除平台时同步移除 `storageInfoMap` 中对应项，避免残留状态。
- 新增 `syncStorageDefaultFlags(...)`，确保 `StorageInfo.isDefault` 与运行时默认状态一致。
- `getStorageInfoList()` 按 `isDefault` 排序，`true` 固定排在第一位。
- 为 `put(..., isDefault)` 与 `delete(...)` 增加 `synchronized`，降低并发下默认值竞争风险。

## 测试补充（已完成）
- 在 `MagicDynamicFileClientDefaultKeyTest` 新增用例：
  - 连续将 A、B、C 设为默认后，只保留一个默认标记（C）。

## 结果
- 在同一运行期内，A/B/C 三个平台只能有一个默认平台生效，满足强互斥要求。
