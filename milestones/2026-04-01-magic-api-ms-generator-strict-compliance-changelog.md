# 2026-04-01 magic-api-ms-generator 严格合规改造变更日志

## 变更概览
- 将技能文档改为严格合规结构，降低上下文开销并消除规则冲突。

## 具体变更
- 重写 `SKILL.md`：
  - frontmatter 仅保留 `name`、`description`。
  - 主体精简为 5 个执行区块。
  - 删除重复说明、性能宣传与示例对话。
- 新增 `references/ms-parameter-cheatsheet.md`。
- 新增 `references/ms-templates.md`。
- 新增 `agents/openai.yaml`。
- 删除旧 `guides/quick-reference.md`。
- 删除旧 `templates/list-template.md`、`templates/save-template.md`、`templates/task-template.md`。

## 一致性修复
- 删除 “UUID.randomUUID()” 口径，统一为 32 位小写字母数字随机串。
- 明确 path 规则：每层仅写本层 path，最终地址由层级拼接。
