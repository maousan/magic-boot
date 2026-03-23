# 列表查询接口模板

## ⚠️ 核心规范

**所有列表/分页/树结构查询接口必须采用 MyBatis 动态 SQL 语法**

MagicScript 支持的 MyBatis 标签：`<if>`、`<elseif>`、`<else>`、`<where>`、`<foreach>`、`<trim>`、`<set>`

详见：`guides/mybatis-syntax.md`

---

## JSON 元数据

```json
{
  "properties": {},
  "id": "自动生成32位ID",
  "script": null,
  "groupId": "从 group.json 读取",
  "name": "列表",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/list",
  "method": "GET",
  "parameters": [],
  "options": [{
    "name": "permission",
    "value": "{module}:view",
    "description": "允许拥有该权限的访问",
    "required": false,
    "dataType": "String",
    "type": null,
    "defaultValue": null,
    "validateType": null,
    "error": null,
    "expression": null,
    "children": null
  }],
  "requestBody": "{}",
  "headers": [],
  "paths": [],
  "responseBody": null,
  "description": "",
  "requestBodyDefinition": null,
  "responseBodyDefinition": null
}
```

---

## 脚本代码（标准模板）

```javascript
let sql = """
    select
        id,
        name,
        code,
        status,
        create_date
    from {table_name}
    <where>
        is_del = 0
        <if test="name != null and name != ''">
            and name like concat('%', #{name}, '%')
        </if>
        <if test="status != null">
            and status = #{status}
        </if>
        <if test="code != null and code != ''">
            and code = #{code}
        </if>
        <if test="createDateStart != null and createDateStart != ''">
            and create_date >= #{createDateStart}
        </if>
        <if test="createDateEnd != null and createDateEnd != ''">
            and create_date &lt;= #{createDateEnd}
        </if>
    </where>
    order by sort, create_date desc
"""

return db.page(sql)
```

---

## 树结构查询模板

```javascript
let sql = """
    select
        id,
        parent_id,
        name,
        code,
        sort,
        status
    from {table_name}
    <where>
        is_del = 0
        <if test="parentId != null and parentId != ''">
            and parent_id = #{parentId}
        </if>
        <if test="name != null and name != ''">
            and name like concat('%', #{name}, '%')
        </if>
        <if test="status != null">
            and status = #{status}
        </if>
    </where>
    order by sort, create_date
"""

return db.select(sql)
```

---

## IN 查询模板（批量查询）

```javascript
let sql = """
    select * from {table_name}
    <where>
        is_del = 0
        <if test="idList != null and idList.size() > 0">
            and id in
            <foreach collection="idList" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
        </if>
    </where>
"""

return db.select(sql)
```

---

## 多条件分支模板

```javascript
let sql = """
    select * from {table_name}
    <where>
        is_del = 0
        <if test="type == 'pending'">
            and status = 0
        </if>
        <elseif test="type == 'completed'">
            and status = 1
        </elseif>
        <elseif test="type == 'cancelled'">
            and status = 2
        </elseif>
        <else>
            and status in (0, 1)
        </else>
    </where>
    order by create_date desc
"""

return db.page(sql)
```

---

## 使用说明

1. **替换占位符**：
   - `{table_name}` → 实际表名
   - `{module}` → 模块名（如 `product` → `product:view`）

2. **必须遵循的规范**：
   - ✅ 使用 `<where>` 标签包裹条件
   - ✅ 每个条件前加 `and`
   - ✅ 字符串判断：`name != null and name != ''`
   - ✅ 列表判断：`list != null and list.size() > 0`
   - ✅ XML 转义：`<=` 写成 `&lt;=`，`>=` 写成 `&gt;=`
   - ✅ 分页用 `db.page(sql)`，非分页用 `db.select(sql)`

3. **禁止的做法**：
   - ❌ 不使用 MyBatis 语法，直接拼接 SQL
   - ❌ 使用不支持的标签（`<choose>`、`<bind>` 等）
   - ❌ 忘记 `is_del = 0` 软删除过滤
