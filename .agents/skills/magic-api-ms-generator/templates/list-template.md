# 列表查询接口模板

## JSON 元数据

```json
{
  "properties": {},
  "id": "自动生成UUID",
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
  "requestBodyDefinition": {
    "name": "",
    "value": "",
    "description": "",
    "required": false,
    "dataType": "Object",
    "type": null,
    "defaultValue": null,
    "validateType": "",
    "error": "",
    "expression": "",
    "children": []
  },
  "responseBodyDefinition": null
}
```

## 脚本代码

```javascript
let sql = """
    select * from {table_name}
    <where>
        and is_del = 0
        <if test="field1 != null and field1 != ''">
            and field1 like concat('%', #{field1}, '%')
        </if>
        <if test="field2 != null">
            and field2 = #{field2}
        </if>
    </where>
    order by create_date desc
"""

const page = db.page(sql)

return page
```

## 使用说明

1. 替换 `{table_name}` 为实际表名
2. 替换 `{module}` 为模块名（如 `product` → `product:view`）
3. 根据需求修改查询条件
4. 使用 `db.page(sql)` 实现自动分页
