# PF4J 插件管理接口变更日志（上传路由去歧义）

## 日期
- 2026-03-28

## 变更类型
- 接口调整（破坏性）

## 变更内容
- 上传安装接口由 `POST /plugin/admin/install` 重命名为 `POST /plugin/admin/upload`。
- 旧接口 `POST /plugin/admin/install` 已移除，不提供兼容映射。
- 按路径安装接口 `POST /plugin/admin/install/by-path` 保持不变。
- 上传接口返回结构保持 `{code,message,data}`。

## 影响范围
- 需要调用方将上传接口地址切换为 `/plugin/admin/upload`。
- 若仍调用旧地址，将收到 404。
