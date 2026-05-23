# MagicScript MyBatis 动态 SQL 语法

MagicScript 支持 MyBatis 风格的动态 SQL，但**仅支持以下标签**：

| 标签 | 用途 | 示例 |
|-----|------|------|
| `<if>` | 条件判断 | `<if test="name != null">...</if>` |
| `<elseif>` | 否则如果 | `<elseif test="type == 1">...</elseif>` |
| `<else>` | 否则 | `<else>...</else>` |
| `<where>` | WHERE 子句（自动处理 AND） | `<where>and ...</where>` |
| `<foreach>` | 循环遍历 | `<foreach collection="ids" item="id">...</foreach>` |
| `<trim>` | 前后缀处理 | `<trim prefix="(" suffix=")">...</trim>` |
| `<set>` | UPDATE SET 子句 | `<set>field = #{value}</set>` |

## ⚠️ 不支持的标签

以下 MyBatis 标签 **不被支持**：
- ❌ `<choose>` / `<when>` - 使用 `<if>` + `<elseif>` + `<else>` 替代
- ❌ `<bind>` - 在脚本中预定义变量替代
- ❌ `<include>` / `<sql>` - 直接内联 SQL
- ❌ `<selectKey>` - 使用其他方式获取主键

---

## 1. `<if>` 条件判断

**语法**：
```xml
<if test="条件表达式">
    SQL 片段
</if>
```

**常用条件表达式**：
```javascript
// 字符串非空判断
<if test="name != null and name != ''">
    and name like concat('%', #{name}, '%')
</if>

// 数字判断
<if test="status != null">
    and status = #{status}
</if>

// 列表非空判断
<if test="idList != null and idList.size() > 0">
    and id in
    <foreach collection="idList" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
</if>

// 多条件组合
<if test="startDate != null and startDate != '' and endDate != null and endDate != ''">
    and create_date between #{startDate} and #{endDate}
</if>
```

---

## 2. `<elseif>` + `<else>` 多分支

**语法**：
```xml
<if test="条件1">
    SQL 片段1
</if>
<elseif test="条件2">
    SQL 片段2
</elseif>
<else>
    SQL 片段3
</else>
```

**示例**：
```javascript
let sql = """
    select * from sys_order
    <where>
        is_del = 0
        <if test="type == 'pending'">
            and status = 0
        </if>
        <elseif test="type == 'completed'">
            and status = 1
        </elseif>
        <else>
            and status in (0, 1, 2)
        </else>
    </where>
"""
```

---

## 3. `<where>` 自动处理 AND/OR

**作用**：自动去除开头的 `AND` 或 `OR`

**语法**：
```xml
<where>
    and condition1
    and condition2
</where>
```

**示例**：
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

// 如果 name 和 status 都为空，生成：select * from sys_user where is_del = 0 order by create_date desc
// 如果只有 name 有值，生成：select * from sys_user where is_del = 0 and name like '%xxx%' order by create_date desc
```

**⚠️ 注意**：
- `<where>` 内部的条件前要加 `and` 或 `or`
- 只有子元素返回内容时才插入 `WHERE`
- 会自动去除开头多余的 `AND` 或 `OR`

---

## 4. `<foreach>` 循环遍历

**语法**：
```xml
<foreach collection="集合名" item="元素变量" open="前缀" separator="分隔符" close="后缀">
    #{元素变量}
</foreach>
```

**属性说明**：
| 属性 | 说明 | 必填 |
|-----|------|------|
| `collection` | 集合参数名 | ✅ |
| `item` | 当前元素变量名 | ✅ |
| `open` | 开始字符 | ❌ |
| `separator` | 元素间分隔符 | ❌ |
| `close` | 结束字符 | ❌ |
| `index` | 索引变量名 | ❌ |

**IN 查询示例**：
```javascript
let sql = """
    select * from sys_user
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
```

**批量插入示例**：
```javascript
let sql = """
    insert into sys_log (id, content, create_time)
    values
    <foreach collection="logList" item="log" separator=",">
        (#{log.id}, #{log.content}, #{log.createTime})
    </foreach>
"""
```

---

## 5. `<trim>` 自定义前后缀

**语法**：
```xml
<trim prefix="前缀" suffix="后缀" prefixOverrides="忽略的前缀" suffixOverrides="忽略的后缀">
    SQL 片段
</trim>
```

**替代 `<where>` 示例**：
```javascript
let sql = """
    select * from sys_user
    <trim prefix="where" prefixOverrides="and|or">
        <if test="name != null and name != ''">
            and name = #{name}
        </if>
        <if test="status != null">
            and status = #{status}
        </if>
    </trim>
"""
```

**替代 `<set>` 示例**：
```javascript
let sql = """
    update sys_user
    <trim prefix="set" suffixOverrides=",">
        <if test="name != null">
            name = #{name},
        </if>
        <if test="phone != null">
            phone = #{phone},
        </if>
    </trim>
    where id = #{id}
"""
```

---

## 6. `<set>` UPDATE 语句专用

**作用**：自动处理 SET 子句，去除多余的逗号

**语法**：
```xml
<set>
    field1 = #{value1},
    field2 = #{value2},
</set>
```

**示例**：
```javascript
let sql = """
    update sys_user
    <set>
        <if test="name != null and name != ''">
            name = #{name},
        </if>
        <if test="phone != null">
            phone = #{phone},
        </if>
        update_time = now()
    </set>
    where id = #{id}
"""
```

**⚠️ 注意**：
- 只有子元素返回内容时才插入 `SET`
- 会自动去除末尾多余的逗号

---

## 7. 完整示例

### 分页列表查询
```javascript
let sql = """
    select
        id,
        name,
        code,
        status,
        create_date
    from sys_role
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

### 树结构查询
```javascript
let sql = """
    select
        id,
        parent_id,
        name,
        code,
        sort
    from sys_dept
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

### 批量删除（IN 查询）
```javascript
let sql = """
    delete from sys_user_role
    where user_id in
    <foreach collection="userIdList" item="userId" open="(" separator="," close=")">
        #{userId}
    </foreach>
"""

db.update(sql)
```

---

## 8. 常见错误

### ❌ 错误：使用了不支持的标签
```javascript
// ❌ 不支持 <choose>
<choose>
    <when test="type == 1">...</when>
    <otherwise>...</otherwise>
</choose>

// ✅ 正确：使用 <if> + <elseif> + <else>
<if test="type == 1">...</if>
<elseif test="type == 2">...</elseif>
<else>...</else>
```

### ❌ 错误：忘记加 AND
```javascript
// ❌ 条件之间没有 AND
<where>
    is_del = 0
    <if test="name != null">
        name = #{name}  // 缺少 AND
    </if>
</where>

// ✅ 正确
<where>
    is_del = 0
    <if test="name != null">
        and name = #{name}
    </if>
</where>
```

### ❌ 错误：使用 WHERE 1=1 永真条件
```javascript
// ❌ Druid Wall Filter 会拦截，报 sql injection violation
var sql = "select * from t_user where 1=1"
if(name != null){ sql = sql + " and name = #{name}" }
db.page(sql, {}, page, pageSize)

// ✅ 正确：使用 <where> + <if> 标签
let sql = """
    select * from t_user
    <where>
        <if test="name != null and name != ''">
            and name = #{name}
        </if>
    </where>
"""
return db.page(sql)
```

### ❌ 错误：手动拼 params Map 传给 db.page()
```javascript
// ❌ 方法解析失败：找不到 page(String, LinkedHashMap, Integer, Integer)
var params = {}
params.name = name
db.page(sql, params, page, pageSize)

// ✅ 正确：MyBatis 动态 SQL + #{} 自动绑定脚本变量
let sql = """
    select * from t_user
    <where>
        <if test="name != null and name != ''">
            and name = #{name}
        </if>
    </where>
"""
return db.page(sql)
// 或手动指定分页：db.page(sql, pageSize, (page - 1) * pageSize)
```

### ❌ 错误：XML 转义字符
```javascript
// ❌ 直接使用 < 符号
<if test="age < 18">

// ✅ 正确：使用 XML 转义
<if test="age &lt; 18">

// 或者使用 >= 反转条件
<if test="age >= 18">
```

**XML 转义对照表**：
| 字符 | 转义 | 说明 |
|-----|------|------|
| `<` | `&lt;` | 小于 |
| `>` | `&gt;` | 大于 |
| `&` | `&amp;` | 和号 |
| `"` | `&quot;` | 双引号 |
| `'` | `&apos;` | 单引号 |
