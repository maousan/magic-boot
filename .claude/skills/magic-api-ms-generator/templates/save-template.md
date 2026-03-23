# 保存/更新接口模板

## JSON 元数据

```json
{
  "properties": {},
  "id": "自动生成UUID",
  "script": null,
  "groupId": "从 group.json 读取",
  "name": "保存",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/save",
  "method": "POST",
  "parameters": [],
  "options": [{
    "name": "permission",
    "value": "{module}:save",
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
var data = body

let id = data.id

if(id){
    // 更新
    anyline.update("{table_name}", data)
} else {
    // 新增
    anyline.insert("{table_name}", data)
}

return true
```

## 使用说明

1. 替换 `{table_name}` 为实际表名
2. 替换 `{module}` 为模块名（如 `order` → `order:save`）
3. 使用 anyline 框架操作数据库
4. 根据 id 自动判断新增或更新
