# 快速参考指南

## ⚠️ MyBatis 动态 SQL（重要）

**所有列表/分页/树结构查询必须使用 MyBatis 动态 SQL 语法！**

### 支持的标签

| 标签 | 用途 | 示例 |
|-----|------|------|
| `<if>` | 条件判断 | `<if test="name != null">and name = #{name}</if>` |
| `<elseif>` | 否则如果 | `<elseif test="type == 1">...</elseif>` |
| `<else>` | 否则 | `<else>...</else>` |
| `<where>` | WHERE 子句 | `<where>and ...</where>` |
| `<foreach>` | 循环遍历 | `<foreach collection="ids" item="id">#{id}</foreach>` |
| `<trim>` | 前后缀处理 | `<trim prefix="(" suffix=")">...</trim>` |
| `<set>` | UPDATE SET | `<set>field = #{value}</set>` |

### 标准查询模板

```javascript
let sql = """
    select * from sys_user
    <where>
        is_del = 0
        <if test="name != null and name != ''">
            and name like concat('%', #{name}, '%')
        </if>
        <if test="status != null">
            and status = #{status}
        </if>
    </where>
    order by create_date desc
"""

return db.page(sql)
```

### ❌ 不支持的标签

`<choose>`、`<when>`、`<otherwise>`、`<bind>`、`<include>`、`<sql>`

**详细语法**：`guides/mybatis-syntax.md`

---

## ID 生成规范

**所有 ID 必须符合规范**：
- 格式：32位字符
- 字符集：0-9, a-z（数字和小写字母）
- 示例：`a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6`
- ❌ 不要使用 UUID（带连字符）、大写字母、特殊字符

**Python 生成示例**：
```python
import random
import string

def generate_id():
    chars = string.digits + string.ascii_lowercase
    return ''.join(random.choice(chars) for _ in range(32))
```

**JavaScript 生成示例**：
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

## path 层级关系

**路径拼接规则**：
```
完整接口地址 = 父分组 path + 当前分组 path + 接口 path
```

**示例**：
```
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
- group.json 的 path 只写当前层级（如 `/user`）
- ❌ 不要写完整路径（不要写 `/system/user`）
- magic-api 会根据 parentId 自动拼接

## 权限码推断规则

| 接口名称 | 权限后缀 | 示例 |
|---------|---------|------|
| 列表、list、查询、query | view | `user:view` |
| 保存、save、新增、添加、add、修改、更新、update | save | `user:save` |
| 删除、delete | delete | `user:delete` |
| 导入、import | import | `user:import` |
| 导出、export | export | `user:export` |

## API 参数类型

### Query 参数（parameters）
```json
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
```
**使用**：直接使用参数名 `#{status}`

### 路径参数（paths）
```json
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
```
**配置**：`"path": "/delete/{id}"`
**使用**：直接使用参数名 `id`

### 请求体（requestBody）
**定义**：
```json
"requestBody": "{\n  \"username\": \"admin\",\n  \"password\": \"123456\"\n}"
```
**使用**：`body.username`、`body.password`

### 参数使用规则

| 参数类型 | 定义位置 | 脚本访问 | 适用场景 |
|---------|---------|---------|---------|
| Query 参数 | `parameters` | 直接使用参数名 | GET 过滤条件 |
| 路径参数 | `paths` | 直接使用参数名 | RESTful /{id} |
| 请求体 | `requestBody` | `body.字段名` | POST JSON 数据 |

## HTTP 方法映射

| 接口类型 | HTTP 方法 |
|---------|----------|
| 列表、查询 | GET |
| 保存、新增、修改 | POST |
| 删除 | DELETE |
| 导入 | POST |
| 导出 | GET |

## ⚠️ 禁止使用的参数名（内置变量）

**magic-api 内置变量**（禁止作为参数名）：

| 内置变量 | 用途 | 替代方案 |
|---------|------|---------|
| `path` | 路径参数对象 | `filePath`, `urlPath` |
| `request` | HTTP 请求对象 | `req`, `requestData` |
| `response` | HTTP 响应对象 | `resp`, `responseData` |
| `body` | 请求体对象 | `reqBody`, `data` |
| `db` | 数据库操作模块 | `database`, `dbName` |
| `log` | 日志模块 | `logMsg`, `logContent` |
| `file` | 文件模块 | `fileName`, `fileItem` |
| `exit` | 退出函数 | `exitCode`, `exitFlag` |
| `import` | 导入函数 | `importData` |
| `env` | 环境变量 | `envName`, `envKey` |
| `cache` | 缓存模块 | `cacheKey`, `cacheName` |

**❌ 错误**：使用 `"name": "path"` 作为参数名
**✅ 正确**：使用 `"name": "filePath"` 替代

## anyline 常用操作

```javascript
// 查询单条
anyline.select("table_name", "id", id)

// 查询列表
anyline.selects("table_name", "status", 1)

// 插入
anyline.insert("table_name", {field1: value1, field2: value2})

// 更新
anyline.update("table_name", data)

// 删除
anyline.deletes("table_name", "id", id)

// 条件删除
anyline.deletes("table_name", "field", value)
```

## db 模块常用操作

```javascript
// 分页查询（自动读取 pageNum、pageSize 参数）
const page = db.page(sql)

// 单值查询
const value = db.selectValue(sql)

// 列表查询
const list = db.select(sql)

// 更新操作
const rows = db.update(sql)
```

**⚠️ 注意**：所有 SQL 必须使用 MyBatis 动态 SQL 语法，不支持字符串拼接！

## 分组配置（group.json）

```json
{
  "properties": {},
  "id": "分组UUID",
  "name": "模块名称",
  "type": "api",  // 或 "task"、"job"
  "parentId": "0",  // 顶级为 "0"，子模块为父分组 ID
  "path": "/module",
  "paths": [],
  "options": []
}
```

## 文件存放位置

| 类型 | 路径 |
|-----|------|
| RESTful API | `data/magic-api/api/{模块名称}/{接口名称}.ms` |
| 定时任务 | `data/magic-api/job/{任务名称}.ms` |

## MagicScript 数组创建语法

**⚠️ 重要：MagicScript 不支持 Java 风格的数组创建语法**

| 用途 | ❌ 错误写法 | ✅ 正确写法 |
|-----|-----------|-----------|
| 创建空 byte 数组 | `new byte[0]` | `new_byte_array(0)` |
| 创建指定大小 byte 数组 | `new byte[10]` | `new_byte_array(10)` |
| 创建 int 数组 | `new int[5]` | `new_int_array(5)` |
| 创建 long 数组 | `new long[5]` | `new_long_array(5)` |
| 创建 double 数组 | `new double[5]` | `new_double_array(5)` |
| 创建 boolean 数组 | `new boolean[5]` | `new_boolean_array(5)` |
| 创建对象数组 | `new String[5]` | `new_array('java.lang.String', 5)` |

**通用语法**：
```javascript
// 基本类型数组
new_byte_array(size)      // byte[]
new_int_array(size)       // int[]
new_long_array(size)      // long[]
new_double_array(size)    // double[]
new_boolean_array(size)   // boolean[]

// 对象数组（需要完整类名）
new_array('java.lang.String', 5)
new_array('java.lang.Object', 10)
```

**常见使用场景**：
```javascript
// 创建空字节流（用于文件标记）
import java.io.ByteArrayInputStream
var emptyStream = new ByteArrayInputStream(new_byte_array(0))

// 创建缓冲区
var buffer = new_byte_array(1024)
```

**❌ 常见错误**：
```javascript
// ❌ 错误：target is null
var arr = new_array(byte.class, 0)  // byte.class 在 MagicScript 中返回 null

// ❌ 错误：语法错误
var arr = new byte[0]  // MagicScript 不支持此语法

// ✅ 正确
var arr = new_byte_array(0)
```

## MagicScript 集合长度获取

**⚠️ 不同类型使用不同的长度获取方式**

| 类型 | 正确方式 | 说明 |
|-----|---------|------|
| 数组 | `arr.length` | 属性访问，不带括号 |
| String | `str.length()` | 方法调用 |
| List/ArrayList | `list.size()` | 方法调用 |
| Map | `map.size()` | 方法调用 |

**示例**：
```javascript
var arr = new_int_array(5)
var str = "hello"
var list = [1, 2, 3]

arr.length      // ✅ 5（数组用属性）
str.length()    // ✅ 5（字符串用方法）
list.size()     // ✅ 3（List 用方法）
list.length()   // ❌ 错误！ArrayList 没有 length() 方法
```

**❌ 常见错误**：
```javascript
var result = []
result.push(1)
result.push(2)

// ❌ 错误：在java.util.ArrayList中找不到方法length()
return { total: result.length() }

// ✅ 正确
return { total: result.size() }
```

## 常见错误处理

```javascript
// 参数验证
if(!param){
    exit 0, '参数不能为空'
}

// 错误处理
try {
    // 业务逻辑
} catch(e) {
    log.error("操作失败: " + e.message)
    return 'error'
}
```
