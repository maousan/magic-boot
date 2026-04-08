# 2026-04-01 magic-api-ms-generator 严格合规改造计划

## 目标
- 让技能目录与 SKILL.md 严格对齐 skill-creator 规范。
- 保持原能力不变，仅做结构与文档治理。

## 实施项
- SKILL.md frontmatter 仅保留 `name` 与 `description`。
- 正文收敛为 5 个核心区块：输入收集、分组处理、文件生成、校验清单、输出说明。
- 规则统一：ID 仅 32 位小写字母数字随机串；path 仅写当前层级。
- 参数与模板细节迁移至 `references/`。
- 补齐 `agents/openai.yaml`（display_name、short_description、default_prompt）。
- 清理冗余的 guides/templates 旧文档。

## 验收
- SKILL.md frontmatter 无额外字段。
- 技能目录包含 SKILL.md、references、agents/openai.yaml。
- 全文无 UUID 规则冲突与路径拼接冲突。
