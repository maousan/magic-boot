# magic-api-plugin-excel 插件使用说明

## 简介

`magic-api-plugin-excel` 是一个基于 EasyExcel 的 magic-api 插件，用于在 magic-api 脚本中方便地将数据导出为 Excel 文件。

## 功能特性

- 支持 `List<Map>`、`List<Entity>`、单个对象导出
- 支持三种表头配置方式：
  - Map 映射：`{"username": "用户名"}`
  - 二维数组：`[["username", "用户名"]]`
  - 列顺序 +Map：支持自定义列输出顺序
- 支持动态文件名和中文文件名编码处理
- 支持模板填充和动态生成
- 支持自动列宽和自定义样式
- 支持响应式下载（自动设置响应头）

## 快速开始

### 1. 基础导出（最简单）

```javascript
// 查询数据
var data = db.select("select id, username, email from sys_user where is_del = 0")

// 直接导出下载
excel.download(data, "用户导出")
```

### 2. 自定义表头

```javascript
var data = db.select("select id, username, email from sys_user")

// 方式 1：Map 映射表头
var headers = {
    "id": "ID",
    "username": "用户名",
    "email": "邮箱"
}
excel.download(data, "用户导出", headers)

// 方式 2：二维数组表头
var headers = [
    ["id", "ID"],
    ["username", "用户名"],
    ["email", "邮箱"]
]
var excelBytes = excel.write(data, headers)

// 方式 3：列顺序 +Map 表头
var columnOrder = ["username", "email", "id"]  // 控制输出顺序
var headers = {
    "username": "用户名",
    "email": "邮箱",
    "id": "ID"
}
var excelBytes = excel.write(data, columnOrder, headers)
```

### 3. 导出对象列表

```javascript
// 查询实体对象
var users = db.selectEntities("select * from sys_user where is_del = 0", SysUser.class)

// 导出
excel.downloadEntities(users, "用户导出")
```

### 4. 单个对象导出

```javascript
// 查询单个对象
var user = db.selectOne("select * from sys_user where id = 1")

// 导出为单行 Excel
var excelBytes = excel.writeObject(user, {"username": "用户名", "email": "邮箱"})
```

### 5. 模板填充导出

```javascript
// 列表数据填充
var data = db.select("select username, email from sys_user")
excel.downloadWithTemplate("user_template.xlsx", data, "用户导出")

// 单个对象填充
var user = db.selectOne("select * from sys_user where id = 1")
excel.fillTemplate("user_detail_template.xlsx", user)
```

### 6. 完整配置示例

```javascript
var data = db.select("select * from sys_user")

// 创建配置
var config = excel.createConfig("用户导出_" + excel.generateFileName(""))

// 设置表头
config.setHeaderMap({
    "username": "用户名",
    "email": "邮箱",
    "create_time": "创建时间"
})

// 设置列顺序
config.setColumnOrder(["username", "email", "create_time"])

// 设置 Sheet 名称
config.setSheetName("用户列表")

// 导出
excel.download(data, config)
```

## API 参考

### excel.write(data, headers)
导出 `List<Map>` 为 Excel 字节数组

**参数：**
- `data`: `List<Map<String, Object>>` - 数据列表
- `headers`: `Map<String, String>` - 表头映射（可选）

**返回：** `byte[]` - Excel 文件字节

---

### excel.write(data, columnOrder, headers)
按指定列顺序导出

**参数：**
- `data`: `List<Map<String, Object>>` - 数据列表
- `columnOrder`: `List<String>` - 列顺序
- `headers`: `Map<String, String>` - 表头映射

**返回：** `byte[]`

---

### excel.write(data, headersArray)
使用二维数组表头导出

**参数：**
- `data`: `List<Map<String, Object>>` - 数据列表
- `headersArray`: `List<List<String>>` - 二维数组表头 `[[字段，列名], ...]`

**返回：** `byte[]`

---

### excel.writeEntities(data, headers)
导出对象列表

**参数：**
- `data`: `List<?>` - 实体对象列表
- `headers`: `Map<String, String>` - 表头映射（可选）

**返回：** `byte[]`

---

### excel.writeObject(obj, headers)
导出单个对象

**参数：**
- `obj`: `Object` - 单个对象
- `headers`: `Map<String, String>` - 表头映射（可选）

**返回：** `byte[]`

---

### excel.download(data, fileName, headers)
导出并自动设置响应头下载

**参数：**
- `data`: `List<Map<String, Object>>` - 数据列表
- `fileName`: `String` - 文件名（不含扩展名）
- `headers`: `Map<String, String>` - 表头映射（可选）

---

### excel.downloadEntities(data, fileName, headers)
导出对象列表并下载

---

### excel.writeWithTemplate(templateName, data)
使用模板填充导出列表数据

**参数：**
- `templateName`: `String` - 模板文件名
- `data`: `List<Map<String, Object>>` - 数据列表

**返回：** `byte[]`

---

### excel.fillTemplate(templateName, data)
使用模板填充单个对象

**参数：**
- `templateName`: `String` - 模板文件名
- `data`: `Object` - 填充数据（Map 或对象）

**返回：** `byte[]`

---

### excel.downloadWithTemplate(templateName, data, fileName)
使用模板填充并下载

---

### excel.generateFileName(prefix)
生成带时间戳的文件名

**参数：**
- `prefix`: `String` - 文件名前缀

**返回：** `String` - 类似 `prefix_20240101_120000`

---

### excel.createConfig(fileName)
创建导出配置对象

**参数：**
- `fileName`: `String` - 文件名

**返回：** `ExcelExportConfig`

## ExcelExportConfig 配置项

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| fileName | String | 文件名 | - |
| headerMap | Map<String, String> | 表头映射 | - |
| columnOrder | List<String> | 列顺序 | - |
| columnWidths | Map<String, Integer> | 列宽配置 | - |
| autoWidth | boolean | 自动列宽 | true |
| defaultStyle | boolean | 默认样式 | true |
| templatePath | String | 模板路径 | - |
| useTemplate | boolean | 使用模板 | false |
| sheetName | String | Sheet 名称 | Sheet1 |
| maxRowsPerSheet | int | 每 Sheet 最大行数 | 10000 |

## 模板语法

### 列表数据模板

在模板中使用 `{.fieldName}` 语法：

| 用户名 | 邮箱 | 创建时间 |
|--------|------|----------|
| {.username} | {.email} | {.create_time} |

### 单个对象模板

使用 `{data.fieldName}` 语法：

- 用户名：{data.username}
- 邮箱：{data.email}

## 配置

在 `application.yml` 中配置：

```yaml
magic-api:
  plugin:
    excel:
      template-base-path: excel-templates/  # 模板基础路径
```

## 注意事项

1. 模板文件需放在 `src/main/resources/excel-templates/` 目录下
2. `download` 系列方法只能在 Web 请求环境中使用
3. 中文文件名已自动处理编码
4. 大数据量时建议设置 `maxRowsPerSheet` 进行分 Sheet 导出
