# 里程碑：动态存储平台更新前先删除旧平台

日期：2026-03-26

## 背景

参考 x-file-storage 动态增减存储平台实践，动态注册同 key 的存储平台时，应先移除旧平台，避免旧客户端与连接泄漏。

## 变更内容

- 文件：`magic-boot-plugins/magic-api-plugin-file/src/main/java/org/ssssssss/magicapi/file/service/MagicDynamicFileClient.java`
- 在 `put(id, key, name, client, isDefault)` 中新增替换逻辑：
  - 若同 key 已存在旧客户端，先从 `clients` 中移除
  - 同步移除旧 key 的 `defaultFlags`
  - 调用 `client.destroy()` 销毁旧平台资源后，再注册新客户端
- 在 `delete(key)` 中补充资源释放：
  - 删除映射后调用 `client.destroy()`，确保连接与底层资源释放
- 新增私有方法 `destroyClientQuietly(key, client)` 统一做销毁与异常兜底日志。

## 预期效果

- 动态更新存储平台配置时，不会残留旧存储平台客户端。
- 避免重复注册同 key 导致的资源泄漏与行为不一致。
- 删除平台时资源释放更完整。

## 兼容性

- 对外 API 不变。
- 仅加强动态注册/删除时的生命周期管理逻辑。
