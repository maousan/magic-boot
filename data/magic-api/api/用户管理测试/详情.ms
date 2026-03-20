{
  "properties": {},
  "id": "vgsaaefz0w97lno9m62ju82shofxyk0q",
  "script": null,
  "groupId": "zqov8sdr55f5wmdwfqkwfvmjkd77w9bf",
  "name": "详情",
  "createTime": null,
  "updateTime": null,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/detail/{id}",
  "method": "GET",
  "parameters": [],
  "options": [
    {
      "name": "permission",
      "value": "user-test:view",
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
  "requestBody": "{}",
  "headers": [],
  "paths": [
    {
      "name": "id",
      "value": "1",
      "description": "用户ID（必填）",
      "required": true,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": "用户ID不能为空",
      "expression": null,
      "children": null
    }
  ],
  "responseBody": null,
  "description": "用户详情查询接口 - 测试路径参数 /detail/{id}",
  "requestBodyDefinition": null,
  "responseBodyDefinition": {
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
    "children": [
      {
        "name": "id",
        "value": "1",
        "description": "用户ID",
        "required": false,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "",
        "error": "",
        "expression": "",
        "children": []
      },
      {
        "name": "username",
        "value": "admin",
        "description": "用户名",
        "required": false,
        "dataType": "String",
        "type": null,
        "defaultValue": null,
        "validateType": "",
        "error": "",
        "expression": "",
        "children": []
      }
    ]
  }
}
================================
import log

log.info("查询用户详情，ID: " + id)

// 参数验证
if(!id || id.trim() === ''){
    exit 0, '用户ID不能为空'
}

// 查询用户详情
const user = anyline.select("sys_user", "id", id)

if(!user){
    exit 0, '用户不存在'
}

log.info("查询成功：" + user.username)

return user
