# 快速参考指南

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
| 导出、export | export | `user:export` |

## HTTP 方法映射

| 接口类型 | HTTP 方法 |
|---------|----------|
| 列表、查询 | GET |
| 保存、新增、修改 | POST |
| 删除 | DELETE |
| 导入 | POST |
| 导出 | GET |

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
// 分页查询
const page = db.page(sql)

// 单值查询
const value = db.selectValue(sql)

// 列表查询
const list = db.select(sql)
```

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
