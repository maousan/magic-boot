# Excel 模板文件说明

## 模板文件位置

将 Excel 模板文件放在此目录下：`src/main/resources/excel-templates/`

## 模板语法

EasyExcel 支持两种模板填充方式：

### 1. 列表数据填充（推荐用于导出行数据）

在模板中使用 `{.fieldName}` 语法：

| 用户名 | 邮箱 | 创建时间 |
|--------|------|----------|
| {.username} | {.email} | {.create_time} |

填充时会自动扩展行。

### 2. 单个对象填充

在模板中使用 `{data.fieldName}` 语法：

- 用户名：{data.username}
- 邮箱：{data.email}
- 创建时间：{data.create_time}

## 示例模板制作步骤

1. 打开 Excel，创建表头行
2. 在第二行使用 `{.字段名}` 语法填写占位符
3. 保存文件为 `.xlsx` 格式
4. 将文件放到 `excel-templates` 目录

## 使用示例

```javascript
// 模板填充列表数据
var data = db.select("select username, email from sys_user")
excel.downloadWithTemplate("user_template.xlsx", data, "用户导出")

// 模板填充单个对象
var user = db.selectOne("select * from sys_user where id = 1")
excel.fillTemplate("user_detail_template.xlsx", user)
```
