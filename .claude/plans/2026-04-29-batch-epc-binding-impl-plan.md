# 批次EPC绑定关系管理 - 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增批次ID与RFID EPC一对一绑定关系的CRUD API和PDA管理页面

**Architecture:** 在东信和平模块下新增"批次EPC绑定"分组，5个 magic-api .ms 接口文件 + 1个PDA HTML页面。数据库表 t_batch_epc 由绑定接口自动创建。前端使用 React.createElement + antd-mobile，绿色主题风格与现有页面一致。

**Tech Stack:** MagicScript (.ms), MySQL, React (createElement), antd-mobile

---

## File Structure

| 操作 | 文件 | 职责 |
|------|------|------|
| Create | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/group.json` | API分组定义 |
| Create | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/绑定.ms` | POST 绑定接口 |
| Create | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/解绑.ms` | POST 解绑接口 |
| Create | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/按批次查询.ms` | GET 按批次查询 |
| Create | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/按EPC查询.ms` | GET 按EPC查询 |
| Create | `data/dongxinheping/api/东信和平/库位/批次EPC绑定/列表查询.ms` | GET 分页列表 |
| Create | `magic-boot-master/src/main/resources/static/pda/batch-epc.html` | PDA管理页面 |
| Modify | `magic-boot-master/src/main/resources/static/pda/index.html:75-99` | 添加导航入口 |
| Create | `http/test-batch-epc.http` | HTTP接口测试 |

---

### Task 1: 创建 API 分组

**Files:**
- Create: `data/dongxinheping/api/东信和平/库位/批次EPC绑定/group.json`

- [ ] **Step 1: 创建分组目录和 group.json**

父分组: `库位` (id: `6ce8544b21954e189dc7aeb484aeddfc`, path: `/location`)

```json
{
  "properties" : { },
  "id" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "批次EPC绑定",
  "type" : "api",
  "parentId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "path" : "/batch-epc",
  "createTime" : null,
  "updateTime" : null,
  "createBy" : null,
  "updateBy" : null,
  "paths" : [ ],
  "options" : [ ]
}
```

---

### Task 2: 创建绑定接口

**Files:**
- Create: `data/dongxinheping/api/东信和平/库位/批次EPC绑定/绑定.ms`

- [ ] **Step 1: 创建绑定接口 .ms 文件**

groupId 指向 Task 1 创建的分组 ID `a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6`。

```json
{
  "properties" : { },
  "id" : "b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "绑定",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/bind",
  "method" : "POST",
  "parameters" : [ {
    "name" : "batchId",
    "value" : "",
    "description" : "业务批次ID",
    "required" : true,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "epc",
    "value" : "",
    "description" : "RFID标签EPC",
    "required" : true,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"data\": {\n    \"id\": \"xxx\",\n    \"batchId\": \"B001\",\n    \"epc\": \"EPC001\",\n    \"bindTime\": \"2026-04-29\"\n  }\n}",
  "description" : "绑定业务批次ID与RFID EPC的一对一关系。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import cn.hutool.core.util.IdUtil

let batchId = request.getParameter('batchId')
let epc = request.getParameter('epc')

if(batchId == null || (batchId + '').trim() == ''){
    exit 400, '批次ID不能为空'
}
if(epc == null || (epc + '').trim() == ''){
    exit 400, 'EPC不能为空'
}
batchId = (batchId + '').trim()
epc = (epc + '').trim()

db.update("""
    create table if not exists t_batch_epc (
        id          varchar(32)  not null primary key comment '主键',
        batch_id    varchar(128) not null comment '业务批次ID',
        epc         varchar(128) not null comment 'RFID标签EPC',
        status      tinyint      not null default 1 comment '状态: 1=绑定中, 0=已解绑',
        bind_time   datetime     not null default current_timestamp comment '绑定时间',
        unbind_time datetime     null comment '解绑时间',
        create_time datetime     not null default current_timestamp comment '创建时间',
        update_time datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
        index idx_batch_id (batch_id),
        index idx_epc (epc),
        index idx_status (status)
    ) comment='批次EPC绑定关系表'
""")

let existing = db.select("""
    select batch_id, epc from t_batch_epc where status = 1 and (batch_id = #{batchId} or epc = #{epc})
""", {batchId: batchId, epc: epc})

if(existing != null && existing.size() > 0){
    let conflict = existing.get(0)
    if(conflict.batch_id == batchId){
        exit 400, '批次ID已绑定EPC: ' + conflict.epc
    }
    exit 400, 'EPC已绑定批次: ' + conflict.batch_id
}

let id = IdUtil.fastSimpleUUID()
db.update("""
    insert into t_batch_epc (id, batch_id, epc, status, bind_time)
    values (#{id}, #{batchId}, #{epc}, 1, now())
""", {id: id, batchId: batchId, epc: epc})

log.info('batch-epc bind: batchId=' + batchId + ', epc=' + epc)

return {
    success: true,
    data: {
        id: id,
        batchId: batchId,
        epc: epc,
        bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: id})
    }
}
```

- [ ] **Step 2: 在 magic-api 编辑器中重载脚本**

在 magic-api 编辑器中刷新分组列表，确认"批次EPC绑定"分组和"绑定"接口出现。

- [ ] **Step 3: 在 http/test-batch-epc.http 中写入绑定测试并执行**

```http
### 批次EPC绑定 - 绑定
POST {{baseUrl}}/api/location/batch-epc/bind
Content-Type: application/x-www-form-urlencoded

batchId=TEST-BATCH-001 & epc=EPC-TEST-001
```

预期返回 `{"success":true,"data":{"id":"...","batchId":"TEST-BATCH-001","epc":"EPC-TEST-001","bindTime":"..."}}`

---

### Task 3: 创建解绑接口

**Files:**
- Create: `data/dongxinheping/api/东信和平/库位/批次EPC绑定/解绑.ms`

- [ ] **Step 1: 创建解绑接口 .ms 文件**

```json
{
  "properties" : { },
  "id" : "c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "解绑",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/unbind",
  "method" : "POST",
  "parameters" : [ {
    "name" : "batchId",
    "value" : "",
    "description" : "业务批次ID（与epc至少传一个）",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "epc",
    "value" : "",
    "description" : "RFID标签EPC（与batchId至少传一个）",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"message\": \"解绑成功\"\n}",
  "description" : "解除批次与EPC的绑定关系。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log

let batchId = request.getParameter('batchId')
let epc = request.getParameter('epc')

batchId = batchId != null ? (batchId + '').trim() : ''
epc = epc != null ? (epc + '').trim() : ''

if(batchId == '' && epc == ''){
    exit 400, 'batchId和epc至少传一个'
}

var sql = "select id, batch_id, epc from t_batch_epc where status = 1"
var params = {}

if(batchId != ''){
    sql = sql + " and batch_id = #{batchId}"
    params.batchId = batchId
}
if(epc != ''){
    sql = sql + " and epc = #{epc}"
    params.epc = epc
}

let record = db.selectOne(sql, params)

if(record == null){
    exit 404, '未找到活跃绑定记录'
}

db.update("""
    update t_batch_epc set status = 0, unbind_time = now() where id = #{id}
""", {id: record.id})

log.info('batch-epc unbind: batchId=' + record.batch_id + ', epc=' + record.epc)

return {
    success: true,
    message: '解绑成功'
}
```

- [ ] **Step 2: 重载脚本后测试解绑**

```http
### 批次EPC绑定 - 解绑
POST {{baseUrl}}/api/location/batch-epc/unbind
Content-Type: application/x-www-form-urlencoded

batchId=TEST-BATCH-001
```

预期返回 `{"success":true,"message":"解绑成功"}`

---

### Task 4: 创建查询接口

**Files:**
- Create: `data/dongxinheping/api/东信和平/库位/批次EPC绑定/按批次查询.ms`
- Create: `data/dongxinheping/api/东信和平/库位/批次EPC绑定/按EPC查询.ms`

- [ ] **Step 1: 创建按批次查询 .ms 文件**

```json
{
  "properties" : { },
  "id" : "d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "按批次查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/query-by-batch",
  "method" : "GET",
  "parameters" : [ {
    "name" : "batchId",
    "value" : "",
    "description" : "业务批次ID",
    "required" : true,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"data\": null\n}",
  "description" : "根据批次ID查询当前绑定的EPC。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let batchId = request.getParameter('batchId')

if(batchId == null || (batchId + '').trim() == ''){
    exit 400, '批次ID不能为空'
}
batchId = (batchId + '').trim()

let record = db.selectOne("""
    select id, batch_id, epc, bind_time from t_batch_epc
    where status = 1 and batch_id = #{batchId}
""", {batchId: batchId})

if(record == null){
    return {success: true, data: null}
}

return {
    success: true,
    data: {
        id: record.id,
        batchId: record.batch_id,
        epc: record.epc,
        bindTime: record.bind_time
    }
}
```

- [ ] **Step 2: 创建按EPC查询 .ms 文件**

```json
{
  "properties" : { },
  "id" : "e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "按EPC查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/query-by-epc",
  "method" : "GET",
  "parameters" : [ {
    "name" : "epc",
    "value" : "",
    "description" : "RFID标签EPC",
    "required" : true,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"data\": null\n}",
  "description" : "根据EPC查询当前绑定的批次ID。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let epc = request.getParameter('epc')

if(epc == null || (epc + '').trim() == ''){
    exit 400, 'EPC不能为空'
}
epc = (epc + '').trim()

let record = db.selectOne("""
    select id, batch_id, epc, bind_time from t_batch_epc
    where status = 1 and epc = #{epc}
""", {epc: epc})

if(record == null){
    return {success: true, data: null}
}

return {
    success: true,
    data: {
        id: record.id,
        batchId: record.batch_id,
        epc: record.epc,
        bindTime: record.bind_time
    }
}
```

- [ ] **Step 3: 重载脚本后测试查询**

先重新绑定一条数据，然后测试查询：

```http
### 先绑定
POST {{baseUrl}}/api/location/batch-epc/bind
Content-Type: application/x-www-form-urlencoded

batchId=TEST-BATCH-001 & epc=EPC-TEST-001

### 按批次查询
GET {{baseUrl}}/api/location/batch-epc/query-by-batch?batchId=TEST-BATCH-001

### 按EPC查询
GET {{baseUrl}}/api/location/batch-epc/query-by-epc?epc=EPC-TEST-001
```

预期两个查询都返回 `{success: true, data: {id:"...", batchId:"TEST-BATCH-001", epc:"EPC-TEST-001", bindTime:"..."}}`

---

### Task 5: 创建列表查询接口

**Files:**
- Create: `data/dongxinheping/api/东信和平/库位/批次EPC绑定/列表查询.ms`

- [ ] **Step 1: 创建列表查询 .ms 文件**

```json
{
  "properties" : { },
  "id" : "f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "列表查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "status",
    "value" : "",
    "description" : "状态过滤: 1=绑定中, 0=已解绑, 不传则全部",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "keyword",
    "value" : "",
    "description" : "关键词搜索(匹配batchId或epc)",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "page",
    "value" : "1",
    "description" : "页码",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "pageSize",
    "value" : "20",
    "description" : "每页条数",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "string",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"data\": {\n    \"list\": [],\n    \"total\": 0\n  }\n}",
  "description" : "分页查询批次EPC绑定记录。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let status = request.getParameter('status')
let keyword = request.getParameter('keyword')
let page = request.getParameter('page')
let pageSize = request.getParameter('pageSize')

page = page != null ? Integer.valueOf(page + '') : 1
pageSize = pageSize != null ? Integer.valueOf(pageSize + '') : 20
if(page < 1){ page = 1 }
if(pageSize < 1 || pageSize > 100){ pageSize = 20 }

var sql = "select id, batch_id, epc, status, bind_time, unbind_time from t_batch_epc where 1=1"
var params = {}

if(status != null && (status + '').trim() != ''){
    sql = sql + " and status = #{status}"
    params.status = Integer.valueOf((status + '').trim())
}
if(keyword != null && (keyword + '').trim() != ''){
    sql = sql + " and (batch_id like concat('%', #{keyword}, '%') or epc like concat('%', #{keyword}, '%'))"
    params.keyword = (keyword + '').trim()
}

sql = sql + " order by create_time desc"

var list = db.page(sql, params, page, pageSize)

var items = []
if(list != null && list.content != null){
    for(item in list.content){
        items.add({
            id: item.id,
            batchId: item.batch_id,
            epc: item.epc,
            status: item.status,
            bindTime: item.bind_time,
            unbindTime: item.unbind_time
        })
    }
}

return {
    success: true,
    data: {
        list: items,
        total: list != null ? list.total : 0
    }
}
```

- [ ] **Step 2: 重载脚本后测试列表查询**

```http
### 列表查询 - 全部
GET {{baseUrl}}/api/location/batch-epc/list

### 列表查询 - 仅绑定中
GET {{baseUrl}}/api/location/batch-epc/list?status=1

### 列表查询 - 关键词搜索
GET {{baseUrl}}/api/location/batch-epc/list?keyword=TEST
```

---

### Task 6: 创建 PDA 管理页面

**Files:**
- Create: `magic-boot-master/src/main/resources/static/pda/batch-epc.html`

- [ ] **Step 1: 创建 batch-epc.html**

页面风格与 `warehouse-location-import.html` 一致：绿色主题、antd-mobile、React.createElement。
包含：绑定操作区（批次ID+EPC输入框+绑定按钮）、查询操作区（输入框+查询按钮）、结果展示卡片（含解绑按钮）。

```html
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
  <title>批次EPC绑定管理</title>
  <link rel="stylesheet" href="../vendor/antd-mobile/global.css" />
  <style>
    * { box-sizing: border-box; }
    html, body, #app {
      margin: 0;
      padding: 0;
      min-height: 100%;
      background: #ecfdf5;
      font-family: "Microsoft YaHei", "PingFang SC", "Noto Sans SC", Arial, sans-serif;
      color: #064e3b;
    }
    .page { max-width: 680px; margin: 0 auto; padding: 0 12px 14px; }
    .hero {
      margin: 10px 0 12px;
      border-radius: 12px;
      background: #059669;
      color: #fff;
      padding: 16px;
    }
    .hero h1 { margin: 0; font-size: 22px; line-height: 1.2; }
    .hero p { margin: 8px 0 0; color: #d1fae5; font-size: 14px; line-height: 1.45; }
    .panel {
      margin-bottom: 12px;
      border: 1px solid #a7f3d0;
      border-radius: 10px;
      background: #fff;
      padding: 12px;
    }
    .panel-title { margin: 0 0 10px; color: #064e3b; font-size: 16px; font-weight: 700; }
    .input-row { margin-bottom: 8px; }
    .input-label { display: block; margin-bottom: 4px; color: #065f46; font-size: 13px; font-weight: 700; }
    .input-field {
      width: 100%;
      height: 42px;
      border: 1px solid #a7f3d0;
      border-radius: 8px;
      background: #fff;
      padding: 0 10px;
      color: #064e3b;
      font-size: 14px;
      outline: none;
    }
    .input-field:focus { border-color: #10b981; }
    .result-card {
      border: 1px solid #d1fae5;
      border-radius: 8px;
      background: #f0fdf4;
      padding: 12px;
    }
    .result-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 6px 0;
      font-size: 14px;
    }
    .result-row + .result-row { border-top: 1px solid #d1fae5; }
    .result-label { color: #047857; }
    .result-value { color: #064e3b; font-weight: 700; }
    .btn-row { display: flex; gap: 8px; margin-top: 10px; }
    .status-badge {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 700;
    }
    .status-active { background: #d1fae5; color: #047857; }
    .status-inactive { background: #fef3c7; color: #92400e; }
    .empty-text { color: #6b7280; font-size: 14px; text-align: center; padding: 20px 0; }
  </style>
</head>
<body>
<div id="app"></div>
<script src="../vendor/react/react.production.min.js"></script>
<script src="../vendor/react-dom/react-dom.production.min.js"></script>
<script src="../vendor/antd-mobile/antd-mobile.min.js"></script>
<script>
(function () {
  var e = React.createElement;
  var useState = React.useState;

  var antd = window.antdMobile || window.antd;
  if (!antd) { throw new Error("Ant Design Mobile 加载失败"); }

  var NavBar = antd.NavBar;
  var Button = antd.Button;
  var Toast = antd.Toast;
  var Dialog = antd.Dialog;

  function apiPost(path, params) {
    return new Promise(function (resolve, reject) {
      var parts = [];
      for (var key in params) {
        if (params[key] != null && (params[key] + '').trim() !== '') {
          parts.push(encodeURIComponent(key) + '=' + encodeURIComponent(params[key]));
        }
      }
      var xhr = new XMLHttpRequest();
      xhr.open("POST", "/api/location/batch-epc" + path, true);
      xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
      xhr.timeout = 15000;
      xhr.onreadystatechange = function () {
        if (xhr.readyState !== 4) return;
        var json = null;
        try { json = JSON.parse(xhr.responseText); } catch (ignore) {}
        resolve({ status: xhr.status, json: json });
      };
      xhr.onerror = function () { reject(new Error("网络异常")); };
      xhr.ontimeout = function () { reject(new Error("请求超时")); };
      xhr.send(parts.join('&'));
    });
  }

  function apiGet(path) {
    return new Promise(function (resolve, reject) {
      var xhr = new XMLHttpRequest();
      xhr.open("GET", "/api/location/batch-epc" + path, true);
      xhr.timeout = 15000;
      xhr.onreadystatechange = function () {
        if (xhr.readyState !== 4) return;
        var json = null;
        try { json = JSON.parse(xhr.responseText); } catch (ignore) {}
        resolve({ status: xhr.status, json: json });
      };
      xhr.onerror = function () { reject(new Error("网络异常")); };
      xhr.ontimeout = function () { reject(new Error("请求超时")); };
      xhr.send();
    });
  }

  function App() {
    var sBindBatch = useState("");
    var bindBatch = sBindBatch[0]; var setBindBatch = sBindBatch[1];
    var sBindEpc = useState("");
    var bindEpc = sBindEpc[0]; var setBindEpc = sBindEpc[1];
    var sQueryKeyword = useState("");
    var queryKeyword = sQueryKeyword[0]; var setQueryKeyword = sQueryKeyword[1];
    var sResult = useState(null);
    var result = sResult[0]; var setResult = sResult[1];
    var sLoading = useState(false);
    var loading = sLoading[0]; var setLoading = sLoading[1];

    function handleBind() {
      if (!bindBatch.trim() || !bindEpc.trim()) {
        Toast.show({ icon: "fail", content: "请输入批次ID和EPC" });
        return;
      }
      setLoading(true);
      apiPost("/bind", { batchId: bindBatch, epc: bindEpc }).then(function (res) {
        var j = res.json || {};
        if (j.success) {
          Toast.show({ icon: "success", content: "绑定成功" });
          setResult(j.data);
          setQueryKeyword(bindBatch);
        } else {
          Toast.show({ icon: "fail", content: j.message || "绑定失败" });
        }
      }).catch(function (err) {
        Toast.show({ icon: "fail", content: err.message });
      }).finally(function () { setLoading(false); });
    }

    function handleQuery() {
      if (!queryKeyword.trim()) {
        Toast.show({ icon: "fail", content: "请输入批次ID或EPC" });
        return;
      }
      setLoading(true);
      apiGet("/query-by-batch?batchId=" + encodeURIComponent(queryKeyword)).then(function (res) {
        var j = res.json || {};
        if (j.success && j.data) {
          setResult(j.data);
        } else {
          return apiGet("/query-by-epc?epc=" + encodeURIComponent(queryKeyword));
        }
      }).then(function (res2) {
        if (res2) {
          var j2 = res2.json || {};
          setResult(j2.success ? j2.data : null);
        }
      }).catch(function (err) {
        Toast.show({ icon: "fail", content: err.message });
      }).finally(function () { setLoading(false); });
    }

    function handleUnbind() {
      if (!result) return;
      Dialog.confirm({
        content: "确认解绑？",
        onConfirm: function () {
          setLoading(true);
          apiPost("/unbind", { batchId: result.batchId }).then(function (res) {
            var j = res.json || {};
            if (j.success) {
              Toast.show({ icon: "success", content: "解绑成功" });
              setResult(null);
            } else {
              Toast.show({ icon: "fail", content: j.message || "解绑失败" });
            }
          }).catch(function (err) {
            Toast.show({ icon: "fail", content: err.message });
          }).finally(function () { setLoading(false); });
        }
      });
    }

    return e("div", { className: "page" }, [
      e(NavBar, {
        key: "nav",
        back: "返回入口导航",
        onBack: function () { window.location.href = "./index.html"; }
      }, "批次EPC绑定管理"),
      e("div", { className: "hero", key: "hero" }, [
        e("h1", { key: "title" }, "批次 EPC 绑定管理"),
        e("p", { key: "sub" }, "维护业务批次ID与RFID标签EPC的绑定关系，用于跟踪批次寻物。")
      ]),
      e("div", { className: "panel", key: "bind" }, [
        e("h2", { className: "panel-title", key: "title" }, "绑定"),
        e("div", { className: "input-row", key: "batch" }, [
          e("label", { className: "input-label", key: "label" }, "批次ID"),
          e("input", {
            className: "input-field", key: "input",
            placeholder: "输入业务批次ID",
            value: bindBatch,
            onChange: function (ev) { setBindBatch(ev.target.value); }
          })
        ]),
        e("div", { className: "input-row", key: "epc" }, [
          e("label", { className: "input-label", key: "label" }, "EPC"),
          e("input", {
            className: "input-field", key: "input",
            placeholder: "输入RFID标签EPC",
            value: bindEpc,
            onChange: function (ev) { setBindEpc(ev.target.value); }
          })
        ]),
        e(Button, {
          key: "btn", block: true, color: "primary",
          disabled: loading, onClick: handleBind
        }, "绑定")
      ]),
      e("div", { className: "panel", key: "query" }, [
        e("h2", { className: "panel-title", key: "title" }, "查询"),
        e("div", { className: "input-row", key: "search" }, [
          e("input", {
            className: "input-field", key: "input",
            placeholder: "输入批次ID或EPC查询",
            value: queryKeyword,
            onChange: function (ev) { setQueryKeyword(ev.target.value); }
          })
        ]),
        e(Button, {
          key: "btn", block: true, color: "primary", fill: "outline",
          disabled: loading, onClick: handleQuery
        }, "查询"),
        result ? e("div", { className: "result-card", key: "result", style: { marginTop: 10 } }, [
          e("div", { className: "result-row", key: "batch" }, [
            e("span", { className: "result-label", key: "l" }, "批次ID"),
            e("span", { className: "result-value", key: "v" }, result.batchId)
          ]),
          e("div", { className: "result-row", key: "epc" }, [
            e("span", { className: "result-label", key: "l" }, "EPC"),
            e("span", { className: "result-value", key: "v" }, result.epc)
          ]),
          e("div", { className: "result-row", key: "time" }, [
            e("span", { className: "result-label", key: "l" }, "绑定时间"),
            e("span", { className: "result-value", key: "v" }, result.bindTime || "-")
          ]),
          e("div", { className: "btn-row", key: "actions" }, [
            e(Button, {
              key: "unbind", color: "danger", fill: "outline",
              size: "small", onClick: handleUnbind
            }, "解绑")
          ])
        ]) : e("div", { className: "empty-text", key: "empty" }, "暂无查询结果")
      ])
    ]);
  }

  var mountNode = document.getElementById("app");
  if (ReactDOM.createRoot) {
    ReactDOM.createRoot(mountNode).render(e(App));
  } else {
    ReactDOM.render(e(App), mountNode);
  }
})();
</script>
</body>
</html>
```

---

### Task 7: 添加导航入口

**Files:**
- Modify: `magic-boot-master/src/main/resources/static/pda/index.html:75-99`

- [ ] **Step 1: 在 navItems 数组末尾添加新入口**

在 `index.html` 的 `navItems` 数组最后一个元素（仓库库位 Excel 导入）之后，添加：

```javascript
        {
          title: "批次EPC绑定管理",
          desc: "维护业务批次ID与RFID标签EPC的绑定关系。",
          href: "./batch-epc.html",
          btn: "进入批次EPC绑定"
        }
```

---

### Task 8: 编写 HTTP 测试用例

**Files:**
- Create: `http/test-batch-epc.http`

- [ ] **Step 1: 创建完整的 HTTP 测试文件**

```http
### ============================================
### 批次EPC绑定管理 - 接口测试
### ============================================

@baseUrl = http://localhost:9999

### 1. 绑定 - 正常绑定
POST {{baseUrl}}/api/location/batch-epc/bind
Content-Type: application/x-www-form-urlencoded

batchId = BATCH-001 &
epc = EPC-2000ABCD

### 2. 绑定 - 重复绑定（应失败）
POST {{baseUrl}}/api/location/batch-epc/bind
Content-Type: application/x-www-form-urlencoded

batchId = BATCH-001 &
epc = EPC-OTHER

### 3. 按批次查询
GET {{baseUrl}}/api/location/batch-epc/query-by-batch?batchId=BATCH-001

### 4. 按EPC查询
GET {{baseUrl}}/api/location/batch-epc/query-by-epc?epc=EPC-2000ABCD

### 5. 列表查询 - 全部
GET {{baseUrl}}/api/location/batch-epc/list

### 6. 列表查询 - 仅绑定中
GET {{baseUrl}}/api/location/batch-epc/list?status=1

### 7. 解绑
POST {{baseUrl}}/api/location/batch-epc/unbind
Content-Type: application/x-www-form-urlencoded

batchId = BATCH-001

### 8. 解绑后查询（应返回 null）
GET {{baseUrl}}/api/location/batch-epc/query-by-batch?batchId=BATCH-001

### 9. 重新绑定（解绑后应能重新绑定）
POST {{baseUrl}}/api/location/batch-epc/bind
Content-Type: application/x-www-form-urlencoded

batchId = BATCH-001 &
epc = EPC-NEW-TAG

### 10. 列表查询 - 包含历史
GET {{baseUrl}}/api/location/batch-epc/list
```

---

### Task 9: 编译打包

- [ ] **Step 1: Maven 编译打包**

```bash
mvn package -DskipTests -pl magic-boot-master -am
```

预期：BUILD SUCCESS

- [ ] **Step 2: 重启服务并执行 HTTP 测试**

重启服务后，依次执行 `http/test-batch-epc.http` 中的测试用例，验证：
- 绑定成功
- 重复绑定被拒绝
- 双向查询正确
- 解绑成功
- 解绑后查询返回 null
- 重新绑定成功
- PDA 页面可正常访问和操作

---

## Self-Review Checklist

- [x] Spec coverage: 所有5个API + PDA页面 + 导航入口 + HTTP测试均已覆盖
- [x] Placeholder scan: 无 TBD/TODO/占位符，每个步骤包含完整代码
- [x] Type consistency: API参数名 (batchId/epc) 在所有接口和页面中一致
- [x] groupId 一致: 所有 .ms 文件使用同一个 groupId `a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6`
- [x] 表结构一致性: 所有接口操作同一张表 t_batch_epc，字段名一致 (batch_id, epc, status, bind_time, unbind_time)
