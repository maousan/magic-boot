---
name: magic-api 分页查询陷阱
description: magic-api .ms 文件中分页查询的禁止模式和正确做法，避免 Druid Wall Filter 拦截和 db.page() 方法解析失败
type: feedback
---

## 规则

**1. 禁止 `WHERE 1=1` 永真条件**
Druid 连接池的 Wall Filter 会拦截 `WHERE 1=1`，报错 `sql injection violation: select alway true condition not allow`。必须用 `<where>` + `<if>` 标签代替。

**2. 禁止 `db.page(sql, params, page, pageSize)` 四参数签名**
MagicScript 的方法解析器无法将 `LinkedHashMap` 匹配到 `Map` 参数类型，报错 `找不到方法page(String,LinkedHashMap,Integer,Integer)`。

**3. 正确的分页查询模式**

```javascript
// ✅ 方式 1：自动分页（推荐）
let sql = """
    select * from t_inventory
    <where>
        <if test="sku != null and sku != ''">
            and sku like concat('%', #{sku}, '%')
        </if>
    </where>
    order by update_time desc
"""
return db.page(sql)

// ✅ 方式 2：手动指定 limit/offset
return db.page(sql, pageSize, (page - 1) * pageSize)
```

**Why:** Druid Wall Filter 默认开启永真条件拦截；MagicScript 方法签名解析比 Java 更严格，只匹配具体类型不匹配接口。

**How to apply:** 生成 .ms 查询接口时，始终使用 `<where>` + `<if>` MyBatis 动态 SQL 标签，`#{param}` 自动绑定脚本变量，无需手动构建 params Map。
