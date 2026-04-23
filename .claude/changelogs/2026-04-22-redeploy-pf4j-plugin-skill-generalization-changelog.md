# 2026-04-22 redeploy-pf4j-plugin-skill-generalization changelog

## 变更内容
- 新增通用 Skill：`.codex/skills/redeploy-pf4j-plugin-windows/SKILL.md`
- 适用范围从 `magic-plugin-zintis-led` 扩展到项目所有 PF4J 插件。
- 旧 Skill `redeploy-zintis-led-plugin` 改为兼容别名并指向通用 Skill。

## 价值
- 统一插件重部署流程，减少人为差异和重复排障。
- 保持历史入口兼容，避免已有使用方式失效。
