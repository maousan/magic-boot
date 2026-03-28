# 插件上传接口去歧义改名计划

## 日期
- 2026-03-28

## 背景
- 当前 `POST /plugin/admin/install` 与 `POST /plugin/admin/install/by-path` 命名语义冲突，调用方难以区分“上传并安装”与“按路径安装”。

## 目标
- 将上传接口重命名为 `POST /plugin/admin/upload`。
- 立即移除旧路由，不提供兼容层。
- 保持服务层行为、权限策略、响应结构不变。

## 实施项
1. 控制器路由改名：`/install` -> `/upload`。
2. 文案统一：明确为“上传并安装”。
3. 测试更新：新增/更新路由测试，验证旧路由 404。
4. HTTP 脚本更新：`http/test-plugin-api.http` 改为新路由，并保留 `/install/by-path`。
5. 文档更新：记录破坏性改动。

## 验收标准
1. `POST /plugin/admin/upload` 可用。
2. `POST /plugin/admin/install/by-path` 行为不变。
3. `POST /plugin/admin/install` 返回 404。
4. 模块测试通过且无编译错误。
