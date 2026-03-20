{
  "properties": {},
  "id": "266zsqx9jg8gu6gj1at9hd5ffzye7hag",
  "script": null,
  "groupId": "tyrjbfgxpxxpwyr5xhtnlqd37vqraxzj",
  "name": "保存",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/save",
  "method": "POST",
  "parameters": [],
  "options": [
    {
      "name": "permission",
      "value": "order:save",
      "description": "允许拥有该权限的访问",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    }
  ],
  "requestBody": "{\n  \"order_no\": \"订单编号\",\n  \"customer_id\": \"客户ID\",\n  \"amount\": \"订单金额\",\n  \"status\": \"订单状态\"\n}",
  "headers": [],
  "paths": [],
  "responseBody": null,
  "description": "订单保存接口，有id时更新，无id时新增",
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
================================
var data = body

let id = data.id

if(id){
    // 更新
    anyline.update("sys_order", data)
} else {
    // 新增
    anyline.insert("sys_order", data)
}

return true
