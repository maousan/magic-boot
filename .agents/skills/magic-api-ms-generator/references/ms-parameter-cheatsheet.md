# ms 参数速查

## 参数位置规则
- Query 参数：`parameters`（GET 过滤条件）
- 路径参数：`paths`（如 `/delete/{id}`）
- 请求体：`requestBody` + `requestBodyDefinition`（POST/PUT JSON）

## 最小示例

### Query 参数（parameters）
```json
"parameters": [
  {
    "name": "keyword",
    "required": false,
    "dataType": "String",
    "defaultValue": null
  },
  {
    "name": "pageNumber",
    "required": false,
    "dataType": "Integer",
    "defaultValue": "1"
  }
]
```

### 路径参数（paths）
```json
"path": "/delete/{id}",
"paths": [
  {
    "name": "id",
    "required": true,
    "dataType": "String"
  }
]
```

### 请求体（requestBody）
```json
"requestBody": "{\n  \"username\": \"admin\",\n  \"status\": 1\n}",
"requestBodyDefinition": {
  "name": "root",
  "dataType": "Object",
  "children": [
    { "name": "username", "required": true, "dataType": "String" },
    { "name": "status", "required": false, "dataType": "Integer" }
  ]
}
```

## 参数校验建议
- 必填项：在元数据 `required=true`，脚本中二次校验。
- 默认值：优先在元数据声明，脚本中兜底。
- 命名风格：统一 camelCase。
- 不要在 GET 中使用 `requestBody`。
