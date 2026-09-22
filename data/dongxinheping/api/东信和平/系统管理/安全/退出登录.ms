{
  "properties": {},
  "id": "5526d5ad0a174bafa7d814e08943b7a3",
  "script": null,
  "groupId": "1713a9b35b73415da26919dd54682bdc",
  "name": "退出登录",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/logout",
  "method": "GET",
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
  "description": ""
}
================================
import cn.dev33.satoken.stp.StpUtil
StpUtil.logout()
exit 200
