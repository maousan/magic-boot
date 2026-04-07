---
name: magic-api-ms-generator
description: 创建符合 magic-api 规范的 .ms 接口文件（含 RESTful API 与定时任务）。当用户提出“开发接口”“新增接口”“创建定时任务”等需求，且涉及分组管理、权限约束、参数定义或模板化生成时使用此技能。
---

# magic-api .ms 文件生成器

## 1. 输入收集

先收集最小必要信息：
- 接口类型：RESTful API 或定时任务
- 模块名称：用于目录与权限前缀
- 业务对象：表名或任务对象
- 功能描述：查询/保存/删除/导入/导出/调度
- 约束条件：参数必填、校验规则、权限要求

## 2. 分组处理

按目录检查 `group.json`：
1. 存在则读取 `id` 作为 `groupId`。
2. 不存在则创建 `group.json`。

`group.json` 必须满足：
- `id` 使用 **32 位随机字符串**（字符集仅 `0-9`、`a-z`）。
- `path` 仅写当前层级路径（例如 `/system`、`/user`）。
- `parentId` 顶级为 `0`，子分组使用父分组 id。

路径拼接规则：
- 最终接口地址 = 父分组 path + 当前分组 path + 接口 path。
- 分组与接口都只写本层 path，不写完整拼接路径。

## 3. 文件生成

输出路径：
- RESTful API：`data/magic-api/api/{模块}/{接口名}.ms`
- 定时任务：`data/magic-api/job/{任务名}.ms`

文件结构：
1. JSON 元数据
2. 分隔线（`================================`）
3. 脚本代码

权限与方法推断：
- 权限后缀：`view/save/delete/import/export`（按接口语义匹配）
- 方法映射：`GET`（查）、`POST`（增改）、`DELETE`（删）

数据库操作约定：
- 优先使用 `anyline` 进行增删改。
- 复杂查询使用 `db.page/db.select/db.selectValue`。
- 列表查询默认包含软删除过滤：`and is_del = 0`。

## 4. 校验清单

生成前后都检查：
- `SKILL` 规则一致：仅使用 32 位小写字母数字 ID，不使用 UUID 连字符格式。
- `path` 仅为当前层级。
- `groupId`、`method`、`permission`、`path` 填写完整。
- 参数定义位置正确：
  - Query 参数放 `parameters`。
  - 路径参数放 `paths`。
  - 请求体放 `requestBody/requestBodyDefinition`。

详细参数规则与示例见：
- [references/ms-parameter-cheatsheet.md](references/ms-parameter-cheatsheet.md)
- [references/ms-templates.md](references/ms-templates.md)

## 5. 输出说明

完成后向用户明确：
- 生成文件路径
- 接口访问路径（或任务 cron）
- 关键参数与权限码
- 建议测试入口（`http` 目录新增 `.http` 用例）
