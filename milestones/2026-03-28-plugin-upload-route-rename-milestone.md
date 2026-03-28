# PF4J 插件上传路由改名里程碑

## 里程碑名称
- 上传接口去歧义：`/install` -> `/upload`

## 完成时间
- 2026-03-28

## 完成项
- `PluginAdminController` 上传接口路由改为 `POST /plugin/admin/upload`。
- 移除旧路由 `POST /plugin/admin/install`。
- 更新 `http/test-plugin-api.http` 调用样例。
- 新增路由测试，验证旧路由 404、新路由可访问。

## 验收结果
- 上传与路径安装语义已分离：
  - 上传并安装：`/plugin/admin/upload`
  - 按路径安装：`/plugin/admin/install/by-path`
