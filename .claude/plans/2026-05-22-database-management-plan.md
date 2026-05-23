# 数据库管理页面开发计划

日期: 2026-05-22

## 目标
在 dongxinheping-admin 管理后台新增数据库管理页面，提供 SQL 控制台功能，支持对数据库表进行增删改查操作。

## 功能范围
- Schema 浏览：左侧可折叠树形面板，展示所有表名、行数、字段名、类型、PK/NN 标记
- SQL 编辑：文本编辑区域，支持输入任意 SQL
- 查询执行：SELECT 查询返回结果表格，显示行数和执行时间
- 写操作执行：INSERT/UPDATE/DELETE 需两步确认（预览→确认执行）

不含数据库备份恢复功能。

## 后端 API (magic-api)

### 新建分组
- 路径: `data/dongxinheping/api/东信和平/数据库管理/`
- group.json path: `/database`

### 端点
1. `GET /api/database/tables` — 查询 information_schema 获取全部表及字段元数据
2. `POST /api/database/query` — 接收 `{ sql }` 执行 SELECT，返回行数据+耗时
3. `POST /api/database/execute` — 接收 `{ sql, confirmed }` 执行写操作，两步确认

## 前端 (dongxinheping-admin)

### 新增文件
1. `src/api/database.ts` — API 调用封装
2. `src/views/DatabaseManagement.vue` — 主页面组件

### 修改文件
1. `src/router/index.ts` — 新增 /database 路由
2. `src/layouts/AdminLayout.vue` — 新增「数据库管理」菜单项

### 布局
```
┌─────────────────────────────────────────┐
│  数据库管理            [12张表]  [侧边栏] │
├──────────┬──────────────────────────────┤
│  Schema  │  SQL 输入区                   │
│  树      │  [执行]                       │
│          ├──────────────────────────────┤
│          │  查询结果表格                  │
│          │  12行 · 45ms                  │
└──────────┴──────────────────────────────┘
```

## 实施步骤
1. 创建后端 magic-api 分组和 3 个 API 文件
2. 创建前端 API 服务 database.ts
3. 创建前端页面 DatabaseManagement.vue
4. 更新路由和菜单
