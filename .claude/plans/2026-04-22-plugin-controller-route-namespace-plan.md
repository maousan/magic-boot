# 2026-04-22 plugin-controller-route-namespace-plan

## 目标
- 避免 PF4J 插件 Controller 与主应用或其它插件发生路径冲突。

## 实施方案
1. 动态注册插件 Controller 时统一增加命名空间前缀，默认模板：`/plugin/{pluginId}/api`。
2. 注册映射时保留方法级完整条件（HTTP Method、params、headers、consumes、produces），避免“同路径不同方法”冲突误判。
3. 前缀模板放入 `plugin` 配置项，支持按环境调整。
4. 同步更新 zintis-led 插件 HTTP 测试脚本路径。

## 验证
- 运行 `magic-plugin` 与 `magic-plugin-zintis-led` 定向测试。
- 编译 `magic-plugin-zintis-led` 确认插件包可正常产出。
