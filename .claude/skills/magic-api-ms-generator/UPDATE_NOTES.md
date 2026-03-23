# magic-api-ms-generator 技能更新说明

## 更新日期
2026-03-23

## 更新内容

### 1. 新增禁止使用内置变量的规则

#### 在 SKILL.md 中新增的内容

**位置**：第 9.0 节「禁止使用内置变量」

**新增的内置变量列表**：

| 内置变量 | 用途 | 说明 |
|---------|------|------|
| `path` | 路径参数对象 | 存储所有路径参数，如 `path.id` |
| `request` | HTTP 请求对象 | 获取请求头、Cookie 等 |
| `response` | HTTP 响应对象 | 设置响应头、状态码等 |
| `body` | 请求体对象 | POST/PUT 请求的 JSON body |
| `db` | 数据库操作模块 | 执行 SQL 查询 |
| `log` | 日志模块 | 记录日志 |
| `file` | 文件模块 | 文件上传下载 |
| `exit` | 退出函数 | 终止脚本执行 |
| `import` | 导入函数 | 导入 Java 类 |
| `env` | 环境变量 | 获取配置信息 |
| `cache` | 缓存模块 | 缓存操作 |

#### 参数命名替代方案

| 禁止使用 | 替代方案 | 说明 |
|---------|---------|------|
| `path` | `filePath`, `urlPath`, `targetPath` | 文件路径、URL路径 |
| `request` | `req`, `requestData`, `reqBody` | 请求数据 |
| `response` | `resp`, `responseData`, `result` | 响应数据 |
| `body` | `reqBody`, `data`, `payload` | 请求体数据 |
| `db` | `database`, `dbName` | 数据库名称 |
| `log` | `logMsg`, `logContent` | 日志内容 |
| `file` | `fileName`, `fileItem`, `doc` | 文件对象 |
| `exit` | `exitCode`, `exitFlag` | 退出标识 |
| `import` | `importData`, `importFile` | 导入数据 |
| `env` | `envName`, `envKey` | 环境名称 |
| `cache` | `cacheKey`, `cacheName` | 缓存键名 |

### 2. 更新快速参考指南

**文件**：`guides/quick-reference.md`

**新增内容**：
- 新增「禁止使用的参数名（内置变量）」表格
- 修复了 HTTP 方法映射重复的问题

### 3. 更新最佳实践

**文件**：`SKILL.md` 第 11.2 节「DON'T（不推荐做法）」

**新增内容**：
```json
❌ 不要使用内置变量作为参数名（⚠️ 严重错误）
{
  "name": "path",      // ❌ 会覆盖内置的 path 对象
  "name": "request",   // ❌ 会覆盖内置的 request 对象
  "name": "body",      // ❌ 会覆盖内置的 body 对象
}
```

## 问题原因

在 magic-api 脚本中，内置变量（如 `path`、`request`）有特殊含义：

```javascript
// 路径参数访问
var userId = path.id  // path 是内置对象

// 请求体访问
var username = body.username  // body 是内置对象

// 数据库操作
var result = db.select(sql)  // db 是内置模块
```

如果将参数命名为 `path`，会覆盖内置的 `path` 对象，导致：

```javascript
// ❌ 参数名为 "path" 时
var filePath = path  // 这里的 path 是参数值，不是内置对象
var userId = path.id // ❌ 错误！path 已经不是对象了
```

## 测试验证

更新后的技能文件会在生成接口时自动检查参数名是否使用了内置变量，并提供替代方案。

## 文件变更清单

| 文件 | 变更类型 | 说明 |
|-----|---------|------|
| `SKILL.md` | 修改 | 新增第 9.0 节和更新第 11.2 节 |
| `guides/quick-reference.md` | 修改 | 新增内置变量表格，修复重复内容 |
| `UPDATE_NOTES.md` | 新增 | 本文件 |

## 使用示例

### 错误示例（使用内置变量名）

```json
{
  "parameters": [
    {
      "name": "path",  // ❌ 禁止使用
      "description": "文件路径"
    }
  ]
}
```

### 正确示例（使用替代名称）

```json
{
  "parameters": [
    {
      "name": "filePath",  // ✅ 使用替代名称
      "description": "文件路径"
    }
  ]
}
```

## 注意事项

1. **严重程度**：使用内置变量作为参数名是严重错误，会导致脚本无法正常运行
2. **自动检查**：技能会在生成接口时自动检查并提示
3. **已有接口**：对于已存在的接口，需要手动检查并修改参数名
