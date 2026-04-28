# 2026-04-24 label-mapping aimsHost 端口拼接修复

## 问题
- 报错：`Invalid URL port: "90029002"`。
- 根因：脚本将 `aimsHost` 再次强制拼接 `:9002`，当配置值已带端口时出现重复端口。

## 修复
- 文件：`data/dongxinheping/api/东信和平/库位/绑定库位码和标签码.ms`
- 文件：`data/dongxinheping/api/东信和平/库位/解绑库位码和标签码.ms`
- 新增 `buildAimsBaseUrl`：
  - 无协议时自动补 `http://`
  - 无端口时补默认 `:9002`
  - 已有端口时不重复拼接
- 增加 `aimsHost` 空配置保护。

## 影响
- 兼容 `aimsHost=host` 与 `aimsHost=host:port` 两种配置。
