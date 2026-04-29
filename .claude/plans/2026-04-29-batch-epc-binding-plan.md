# 批次EPC绑定关系管理 - 设计文档

**日期**: 2026-04-29
**模块**: 东信和平 / 库位 / 批次EPC绑定
**状态**: 待实施

---

## 1. 需求概述

新增一个业务批次ID与RFID EPC的一对一绑定关系维护表，用于跟踪批次寻物体。EPC是RFID标签的唯一标识。

### 核心需求

- **绑定关系**: 一对一，一个批次ID绑定一个EPC
- **批次ID**: 通用字符串字段，由调用方自行传入
- **操作**: 绑定/解绑接口、双向查询（批次↔EPC）、PDA管理页面
- **历史**: 解绑不删除记录，保留绑定历史

---

## 2. 数据库设计

```sql
CREATE TABLE t_batch_epc (
    id          VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '主键',
    batch_id    VARCHAR(128) NOT NULL COMMENT '业务批次ID',
    epc         VARCHAR(128) NOT NULL COMMENT 'RFID标签EPC',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1=绑定中, 0=已解绑',
    bind_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    unbind_time DATETIME     NULL COMMENT '解绑时间',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_batch_id (batch_id),
    INDEX idx_epc (epc),
    INDEX idx_status (status)
) COMMENT='批次EPC绑定关系表'
```

**设计决策**:

- 唯一性由应用层保证（绑定前查询 status=1 是否已存在），不设 UNIQUE KEY，与项目现有风格一致
- 解绑时设 status=0 + unbind_time，保留历史记录
- 同一EPC解绑后可重新绑定新批次（旧记录 status=0，新记录插入）

---

## 3. API 接口设计

**路径前缀**: `/api/location/batch-epc`
**归属**: 东信和平 > 库位 > 批次EPC绑定（分组）

### 3.1 绑定

- **接口**: `POST /api/location/batch-epc/bind`
- **参数**: `batchId` (string, 必填), `epc` (string, 必填)
- **逻辑**:
  1. 校验 batchId 和 epc 非空
  2. 查询 `SELECT * FROM t_batch_epc WHERE status=1 AND (batch_id=? OR epc=?)`
  3. 若已存在活跃绑定 → 返回冲突提示（告知哪个已绑定）
  4. 插入新记录，status=1
- **返回**: `{success: true, data: {id, batchId, epc, bindTime}}`

### 3.2 解绑

- **接口**: `POST /api/location/batch-epc/unbind`
- **参数**: `batchId` (string) 或 `epc` (string)，至少传一个
- **逻辑**:
  1. 查找 status=1 的记录（按 batchId 或 epc）
  2. 不存在 → 返回未找到
  3. 更新 status=0, unbind_time=now()
- **返回**: `{success: true, message: "解绑成功"}`

### 3.3 按批次查询

- **接口**: `GET /api/location/batch-epc/query-by-batch`
- **参数**: `batchId` (string, 必填)
- **逻辑**: 查询 status=1 的活跃绑定
- **返回**: `{success: true, data: {id, batchId, epc, bindTime}}` 或 null

### 3.4 按EPC查询

- **接口**: `GET /api/location/batch-epc/query-by-epc`
- **参数**: `epc` (string, 必填)
- **逻辑**: 查询 status=1 的活跃绑定
- **返回**: `{success: true, data: {id, batchId, epc, bindTime}}` 或 null

### 3.5 列表查询

- **接口**: `GET /api/location/batch-epc/list`
- **参数**: `status` (int, 可选, 默认不过滤), `keyword` (string, 可选, 模糊匹配 batchId 或 epc), `page` (int), `pageSize` (int)
- **逻辑**: 分页查询，支持状态过滤和关键词搜索
- **返回**: `{success: true, data: {list: [...], total: N}}`

---

## 4. PDA 管理页面

**文件**: `magic-boot-master/src/main/resources/static/pda/batch-epc.html`
**入口**: 在 `pda/index.html` 导航中增加"批次EPC绑定"入口

### 页面布局

1. **NavBar** — "批次EPC绑定管理"，返回入口导航
2. **绑定操作区** — 批次ID输入框 + EPC输入框 + "绑定"按钮
3. **查询操作区** — 一个输入框 + 查询按钮，支持按批次ID或EPC查询
4. **结果展示区** — 绑定状态卡片（批次ID / EPC / 绑定时间 / 状态）
5. **操作按钮** — 查到活跃绑定后可"解绑"

### 交互流程

- 输入批次ID和EPC → 点绑定 → Toast提示成功/失败
- 输入批次ID或EPC任意一个 → 点查询 → 显示当前绑定信息
- 查到活跃绑定 → 点解绑 → 确认后解绑 → 刷新结果

### 风格

与 `warehouse-location-import.html` 保持一致：绿色主题、antd-mobile 组件、React.createElement 语法。

---

## 5. 文件清单

| 类型 | 文件 | 说明 |
|------|------|------|
| API分组 | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/group.json` | 新分组 |
| API | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/绑定.ms` | 绑定接口 |
| API | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/解绑.ms` | 解绑接口 |
| API | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/按批次查询.ms` | 按批次查询 |
| API | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/按EPC查询.ms` | 按EPC查询 |
| API | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/列表查询.ms` | 分页列表 |
| 页面 | `magic-boot-master/src/main/resources/static/pda/batch-epc.html` | PDA管理页面 |
| 入口 | `magic-boot-master/src/main/resources/static/pda/index.html` | 增加导航入口 |
| HTTP测试 | `http/test-batch-epc.http` | 接口测试用例 |
