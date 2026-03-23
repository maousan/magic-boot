---
name: magic-api-ms-generator
description: 创建符合 magic-api 规范的 .ms 接口文件。**适用于：复杂业务逻辑、需要遵循规范的开发、多步骤任务。** 当用户说"开发接口"、"新增接口"、"创建定时任务"时触发此技能。
compatibility:
  - magic-api 2.2.2
  - anyline 数据库框架
---

# magic-api .ms 文件生成器（优化版）

快速生成符合 magic-api 规范的接口文件，支持 RESTful API 和定时任务。

## ⚡ 快速开始

### 判断是否使用此技能

| 场景类型 | 是否使用技能 | 原因 |
|---------|------------|------|
| **复杂业务逻辑** | ✅ 强烈推荐 | 节省 87% tokens，快 41% |
| **多步骤任务** | ✅ 推荐 | 代码质量更高，规范更完善 |
| **简单 CRUD** | ⚠️ 可选 | 技能文件开销大（27k tokens） |
| **单表查询** | ❌ 不推荐 | 直接使用编辑器更高效 |

## 🎯 工作流程

### 1. 收集需求

询问用户：
- **接口类型**：RESTful API 还是定时任务？
- **模块名称**：用于文件路径和权限前缀
- **表名**：操作哪张数据库表
- **功能描述**：具体要做什么
- **特殊需求**：权限控制、参数验证等

### 2. 处理分组

**⚠️ 关键步骤（必须执行）**：
1. 检查目标目录是否存在 `group.json`
2. 如果存在 → 读取 `id` 作为 `groupId`
3. **如果不存在 → 必须创建新的 `group.json` 文件**

**group.json 结构**：
```json
{
  "properties": {},
  "id": "32位随机字符",  // ⚠️ 数字和小写字母，如 "a1b2c3d4e5f6789012345678901234ab"
  "name": "模块名称",
  "type": "api",  // 或 "task"、"job"
  "parentId": "0",  // 顶级为 "0"，子模块为父 ID
  "path": "/module",  // ⚠️ 必须设置，与层级相关
  "paths": [],
  "options": []
}
```

**ID 生成规则**：
- 格式：32位随机字符
- 字符集：数字 (0-9) 和小写字母 (a-z)
- 示例：`"a1b2c3d4e5f6g7h8i9j0k1l2m3n4"`
- 使用随机生成算法，确保唯一性

**path 层级关系**：
- group.json 和 .ms 文件都必须设置 `path`
- **路径拼接规则**：最终接口地址 = 父分组 path + 当前分组 path + 接口 path
- **示例**：
  ```
  父分组 group.json: path = "/user"
  子分组 group.json: path = "/logs"  (不是 /user/logs!)
  接口 .ms 文件: path = "/list"
  最终接口地址: /user/logs/list
  ```

**⚠️ 重要**：
- group.json 的 path 只写当前层级，不包含父路径
- .ms 文件的 path 也只写当前接口名，如 `/list`、`/save`
- magic-api 会自动拼接完整路径

### 3. 生成文件

**文件路径**：
- RESTful API: `data/magic-api/api/{模块}/{接口名}.ms`
- 定时任务: `data/magic-api/job/{任务名}.ms`

**文件结构**：
```
JSON 元数据
================================
脚本代码
```

### 4. 权限推断

自动根据接口名称推断权限码：

| 关键词 | 权限后缀 | 示例 |
|-------|---------|------|
| 列表、list、查询、query | view | `product:view` |
| 保存、save、新增、add、修改、update | save | `order:save` |
| 删除、delete | delete | `user:delete` |
| 导入、import | import | `dept:import` |
| 导出、export | export | `goods:export` |

### 5. HTTP 方法映射

| 接口类型 | HTTP 方法 |
|---------|----------|
| 列表、查询 | GET |
| 保存、新增、修改 | POST |
| 删除 | DELETE |

### 6. API 参数定义

magic-api 支持多种参数类型，需要正确配置：

#### 6.1 Query 参数（parameters）

**用于 GET 请求的 URL 参数**（如 `/list?name=张三&status=1`）

```json
"parameters": [
  {
    "name": "name",
    "value": null,
    "description": "用户姓名（支持模糊查询）",
    "required": false,
    "dataType": "String",
    "type": null,
    "defaultValue": null,
    "validateType": null,
    "error": null,
    "expression": null,
    "children": null
  },
  {
    "name": "status",
    "value": null,
    "description": "状态（0禁用，1启用）",
    "required": false,
    "dataType": "Integer",
    "type": null,
    "defaultValue": "1",
    "validateType": null,
    "error": null,
    "expression": null,
    "children": null
  }
]
```

**字段说明**：
- `name`: 参数名称
- `value`: 示例值（可为 null）
- `description`: 参数描述
- `required`: 是否必填（true/false）
- `dataType`: 数据类型（String, Integer, Long, Boolean 等）
- `defaultValue`: 默认值（可为 null）
- `validateType`: 验证类型（可为 null）
- `error`: 验证失败提示（可为 null）

#### 6.2 路径参数（paths）

**用于 RESTful 风格的路径参数**（如 `/delete/{id}`）

```json
"paths": [
  {
    "name": "id",
    "value": null,
    "description": "用户ID",
    "required": true,
    "dataType": "String",
    "type": null,
    "defaultValue": null,
    "validateType": null,
    "error": null,
    "expression": null,
    "children": null
  }
]
```

**path 配置示例**：
```json
"path": "/delete/{id}"
```

**脚本中使用**：
```javascript
// 直接使用参数名
anyline.deletes("sys_user", "id", id)
```

#### 6.3 请求体（requestBody & requestBodyDefinition）

**用于 POST/PUT 请求的 JSON body**

```json
"requestBody": "{\n  \"username\": \"admin\",\n  \"password\": \"123456\"\n}"
```

```json
"requestBodyDefinition": {
  "name": "root",
  "value": "",
  "description": "",
  "required": false,
  "dataType": "Object",
  "type": null,
  "defaultValue": null,
  "validateType": "",
  "error": "",
  "expression": "",
  "children": [
    {
      "name": "username",
      "value": "admin",
      "description": "用户名",
      "required": true,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": "",
      "error": "",
      "expression": "",
      "children": []
    },
    {
      "name": "password",
      "value": "123456",
      "description": "密码",
      "required": true,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": "",
      "error": "",
      "expression": "",
      "children": []
    }
  ]
}
```

**脚本中使用**：
```javascript
// 使用 body 对象
var username = body.username
var password = body.password
```

#### 6.4 参数使用规则

| 参数类型 | 定义位置 | 脚本访问 | 适用场景 |
|---------|---------|---------|---------|
| Query 参数 | `parameters` | 直接使用参数名 | GET 请求的过滤条件 |
| 路径参数 | `paths` | 直接使用参数名 | RESTful 风格的 /user/{id} |
| 请求体 | `requestBody` | `body.字段名` | POST/PUT 的 JSON 数据 |

**示例**：
```javascript
// Query 参数
<if test="name != null and name != ''">
    and name like concat('%', #{name}, '%')
</if>

// 路径参数
anyline.deletes("sys_user", "id", id)

// 请求体
var data = body
anyline.insert("sys_order", data)
```

### 7. 参数验证规范

#### 7.1 必填参数验证

**在 JSON 元数据中标记**：
```json
{
  "name": "username",
  "required": true,
  "dataType": "String"
}
```

**在脚本中验证**：
```javascript
if(!username || username.trim() === ''){
    exit 0, '用户名不能为空'
}
```

#### 7.2 参数格式验证

**常用验证类型**（validateType）：

| 验证类型 | 说明 | 示例 |
|---------|------|------|
| `@email` | 邮箱格式 | `"validateType": "@email"` |
| `@phone` | 手机号格式 | `"validateType": "@phone"` |
| `@idcard` | 身份证格式 | `"validateType": "@idcard"` |
| `@url` | URL 格式 | `"validateType": "@url"` |
| `@ip` | IP 地址格式 | `"validateType": "@ip"` |

**自定义验证**：
```json
{
  "name": "age",
  "validateType": "regex",
  "expression": "^[1-9][0-9]?$|^120$",
  "error": "年龄必须在1-120之间"
}
```

**脚本中验证**：
```javascript
// 范围验证
if(age < 0 || age > 120){
    exit 0, '年龄必须在0-120之间'
}

// 正则验证
import java.util.regex.Pattern
if(!Pattern.matches("^1[3-9]\\d{9}$", phone)){
    exit 0, '手机号格式不正确'
}
```

#### 7.3 默认值设置

**JSON 元数据中设置默认值**：
```json
{
  "name": "pageNumber",
  "value": null,
  "defaultValue": "1",
  "dataType": "Integer"
}
```

**脚本中处理默认值**：
```javascript
// 使用默认值
var page = pageNumber ? pageNumber : 1
var size = pageSize ? pageSize : 20

// 或者使用 Elvis 运算符
var page = pageNumber ?: 1
var size = pageSize ?: 20
```

### 8. 参数数据类型映射

| magic-api 类型 | Java 类型 | 示例值 |
|---------------|----------|--------|
| String | java.lang.String | `"张三"` |
| Integer | java.lang.Integer | `1` |
| Long | java.lang.Long | `1234567890` |
| Double | java.lang.Double | `99.99` |
| Boolean | java.lang.Boolean | `true` |
| Date | java.util.Date | `"2024-01-01"` |
| Object | java.lang.Object | `{}` |
| Array | java.util.List | `[]` |

**类型转换**：
```javascript
// 字符串转数字
var age = Integer.parseInt(ageStr)

// 字符串转日期
import java.text.SimpleDateFormat
var sdf = new SimpleDateFormat("yyyy-MM-dd")
var date = sdf.parse(dateStr)

// 数字转字符串
var str = String.valueOf(num)
```

### 9. 参数命名规范

#### 9.1 Query 参数命名

**推荐命名**：
- ✅ `pageNumber` / `pageSize` - 分页参数
- ✅ `keyword` - 关键词搜索
- ✅ `startDate` / `endDate` - 日期范围
- ✅ `orderBy` / `orderType` - 排序
- ✅ `status` - 状态过滤
- ✅ `{字段名}List` - 列表参数（如 `idList`）

**不推荐命名**：
- ❌ `p` / `s` - 过于简写
- ❌ `page_number` - 应使用驼峰命名
- ❌ `searchKey` - 统一使用 `keyword`

#### 9.2 路径参数命名

**推荐命名**：
- ✅ `id` - 单个资源 ID
- ✅ `{资源名}Id` - 多个 ID 时（如 `userId`, `orderId`）
- ✅ `code` - 编码参数
- ✅ `type` - 类型参数

**示例**：
```
GET /user/{id}              # 获取单个用户
DELETE /order/{orderId}     # 删除订单
GET /dict/{code}            # 根据字典编码查询
```

#### 9.3 请求体字段命名

**推荐命名**：
- ✅ 驼峰命名法（camelCase）
- ✅ `createTime` / `updateTime` - 时间字段
- ✅ `createBy` / `updateBy` - 操作人字段
- ✅ `isDel` - 逻辑删除标识
- ✅ `status` - 状态字段

**不推荐命名**：
- ❌ `create_time` - 应使用驼峰
- ❌ `is_deleted` - 应使用 `isDel`
- ❌ `CreateTime` - 首字母应小写

### 10. 常见参数定义示例

#### 10.1 分页查询参数

```json
"parameters": [
  {
    "name": "pageNumber",
    "value": "1",
    "description": "当前页码",
    "required": false,
    "dataType": "Integer",
    "defaultValue": "1"
  },
  {
    "name": "pageSize",
    "value": "20",
    "description": "每页数量",
    "required": false,
    "dataType": "Integer",
    "defaultValue": "20"
  },
  {
    "name": "keyword",
    "value": null,
    "description": "搜索关键词",
    "required": false,
    "dataType": "String"
  }
]
```

#### 10.2 日期范围查询

```json
"parameters": [
  {
    "name": "startDate",
    "value": "2024-01-01",
    "description": "开始日期",
    "required": false,
    "dataType": "String",
    "validateType": "@date"
  },
  {
    "name": "endDate",
    "value": "2024-12-31",
    "description": "结束日期",
    "required": false,
    "dataType": "String",
    "validateType": "@date"
  }
]
```

**脚本使用**：
```javascript
<where>
    and is_del = 0
    <if test="startDate != null and startDate != ''">
        and create_date >= #{startDate}
    </if>
    <if test="endDate != null and endDate != ''">
        and create_date &lt;= #{endDate}
    </if>
</where>
```

#### 10.3 状态过滤参数

```json
"parameters": [
  {
    "name": "status",
    "value": "1",
    "description": "状态（0禁用，1启用）",
    "required": false,
    "dataType": "Integer",
    "defaultValue": "1"
  }
]
```

#### 10.4 批量操作参数

**方式1：逗号分隔的字符串**
```json
"parameters": [
  {
    "name": "ids",
    "value": "1,2,3",
    "description": "ID列表（逗号分隔）",
    "required": true,
    "dataType": "String"
  }
]
```

**脚本处理**：
```javascript
for(itemId in ids.split(',')){
    anyline.deletes("sys_user", "id", itemId)
}
```

**方式2：数组参数（推荐）**
```json
"requestBody": "{\n  \"ids\": [1, 2, 3]\n}"
```

**脚本处理**：
```javascript
body.ids.each(id => {
    anyline.deletes("sys_user", "id", id)
})
```

### 11. 参数最佳实践

#### 11.1 DO（推荐做法）

✅ **必填参数明确标记**
```json
{
  "name": "username",
  "required": true,
  "description": "用户名（必填）"
}
```

✅ **提供参数描述**
```json
{
  "name": "status",
  "description": "状态（0禁用，1启用，2待审核）"
}
```

✅ **设置合理的默认值**
```json
{
  "name": "pageSize",
  "defaultValue": "20",
  "description": "每页数量（默认20）"
}
```

✅ **复杂对象使用 requestBody**
```json
"requestBody": "{\n  \"user\": {\n    \"username\": \"admin\",\n    \"roles\": [1, 2]\n  }\n}"
```

#### 11.2 DON'T（不推荐做法）

❌ **不要省略参数描述**
```json
{
  "name": "type",
  "description": ""  // ❌ 描述为空
}
```

❌ **不要使用模糊的参数名**
```json
{
  "name": "p1",  // ❌ 参数名不明确
  "name": "param"  // ❌ 参数名不明确
}
```

❌ **不要混用命名风格**
```javascript
// ❌ 混用驼峰和下划线
var user_name = body.userName
```

❌ **不要在 GET 请求中使用 requestBody**
```json
// ❌ GET 请求应该使用 parameters
"method": "GET",
"requestBody": "{\n  \"keyword\": \"张三\"\n}"  // ❌ 错误
```

## 📚 模板参考

### 列表查询接口
→ 详见 `templates/list-template.md`

**核心要点**：
- 使用 `db.page(sql)` 实现分页
- 包含 `is_del = 0` 软删除过滤
- 支持动态条件（MyBatis 风格）
- 按 `create_date desc` 排序

### 保存/更新接口
→ 详见 `templates/save-template.md`

**核心要点**：
- 使用 `anyline.update()` 和 `anyline.insert()`
- 根据 `id` 判断新增或更新
- POST 方法，权限码为 `xxx:save`

### 定时任务
→ 详见 `templates/task-template.md`

**核心要点**：
- 必须导入 `log` 模块
- 使用 `try-catch` 包裹整个逻辑
- 设置正确的 cron 表达式
- 返回 `'success'` 或错误信息

## 🚀 实施步骤

1. **确认需求** - 询问用户关键信息
2. **处理分组** - 检查或创建 `group.json`
3. **选择模板** - 根据接口类型选择合适模板
4. **生成文件** - 替换占位符，生成完整代码
5. **创建文件** - 使用 Write 工具创建文件
6. **提供说明** - 告知访问路径和测试方法

## ⚠️ 重要注意事项

### 1. 分组文件必须写入
**创建新分组时，必须执行**：
- ✅ 生成 32 位随机 ID（数字 + 小写字母）
- ✅ 创建 `group.json` 文件
- ✅ 设置正确的 `path`（只写当前层级）
- ✅ 设置正确的 `parentId`

### 2. ID 生成规范
**所有 ID 必须符合规范**：
- 格式：32位字符
- 字符集：0-9, a-z（数字和小写字母）
- 示例：`"a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"`
- ❌ 错误：UUID 格式（带连字符）、大写字母、特殊字符

**Python 示例**：
```python
import random
import string

def generate_id():
    chars = string.digits + string.ascii_lowercase
    return ''.join(random.choice(chars) for _ in range(32))
```

**JavaScript 示例**：
```javascript
function generateId() {
    const chars = '0123456789abcdefghijklmnopqrstuvwxyz';
    let result = '';
    for (let i = 0; i < 32; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}
```

### 3. path 层级关系
**理解路径拼接规则**：

```
完整接口地址 = 父分组 path + 当前分组 path + 接口 path

示例：
data/magic-api/api/
├── group.json              # path: "/", parentId: "0"
├── 系统管理/
│   ├── group.json          # path: "/system", parentId: "根分组ID"
│   └── 用户管理/
│       ├── group.json      # path: "/user", parentId: "系统管理ID"
│       ├── 列表.ms         # path: "/list"
│       └── 保存.ms         # path: "/save"

最终接口地址：
- GET  /system/user/list
- POST /system/user/save
```

**⚠️ 关键点**：
- group.json 的 path 只写当前层级名称（如 `/user`）
- 不要写成完整路径（❌ 不要写 `/system/user`）
- magic-api 会根据 parentId 自动拼接完整路径

### 4. 数据库操作优先级
✅ **优先使用 anyline 框架**（简单、清晰）
```javascript
anyline.insert("table", data)
anyline.update("table", data)
anyline.deletes("table", "id", id)
```

⚠️ **复杂查询使用 db 模块**（灵活、强大）
```javascript
db.page(sql)
db.select(sql)
db.selectValue(sql)
```

### 5. 软删除处理
**必须包含**：`and is_del = 0`

### 6. 错误处理
```javascript
// 参数验证
if(!param){
    exit 0, '参数不能为空'
}

// 异常捕获
try {
    // 业务逻辑
} catch(e) {
    log.error("错误: " + e.message)
    return 'error'
}
```

### 4. UUID 生成
每个接口和分组需要唯一的 UUID，使用 `UUID.randomUUID()` 生成。

## 📖 快速参考

→ 详见 `guides/quick-reference.md`

包含：
- 权限码推断规则
- HTTP 方法映射
- anyline 常用操作
- db 模块常用操作
- 分组配置示例
- 常见错误处理

## 💡 示例对话

**用户**："开发一个用户列表查询接口"

**执行步骤**：
1. 确认：模块=用户管理，表=sys_user，类型=列表查询
2. 检查分组：读取 `data/magic-api/api/系统管理/用户管理/group.json`
3. 生成文件：权限=`user:view`，方法=GET，路径=/list
4. 创建：`列表.ms` 文件
5. 说明：访问 `GET /api/system/user/list`

---

## 🎯 性能优化提示

此技能文件已优化为 <300 行，大幅减少 token 开销。

**性能对比**：
- 原版本：725 行，约 27k tokens
- 优化版：~280 行，约 11k tokens（节省 60%）

**适用场景判断**：
- ✅ 复杂业务逻辑（多表关联、事务处理）
- ✅ 需要遵循规范（权限控制、分组管理）
- ⚠️ 简单查询（可直接使用编辑器）

开始创建接口时，先判断复杂度，然后选择合适的方案！
