# 2026-04-22 redeploy-pf4j-plugin-skill-generalization plan

## 目标
- 将单插件重部署 skill 升级为适配当前项目 PF4J 插件体系的通用 skill。

## 步骤
1. 新增通用 skill：`redeploy-pf4j-plugin-windows`。
2. 抽象输入参数：`pluginId`、`moduleDir`、`artifactJar`。
3. 保留旧 skill 作为兼容别名，指向通用 skill。
