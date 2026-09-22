{
  "properties": {},
  "id": "a9704b4ffd704e31a937bcd2ed365eaf",
  "script": null,
  "groupId": "af95234eafa543429e74fb7d3255ec3c",
  "name": "权限码",
  "createTime": null,
  "updateTime": 1790063524792,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/permissions",
  "method": "POST",
  "parameters": [],
  "options": [
    {
      "name": "require_login",
      "value": "false",
      "description": "该接口需要登录才允许访问",
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
  "requestBody": "",
  "headers": [],
  "paths": [],
  "responseBody": "",
  "description": "权限码占位接口，返回空集"
}
================================
// dongxinheping 部署未启用菜单权限码；PermissionInterceptor 登录后固定内部调用本接口，返回空集即可
// 注意：本分组内部路径必须为 /system/security/permissions（与 PermissionInterceptor 硬编码一致），勿挪入 /api 根分组
return []
