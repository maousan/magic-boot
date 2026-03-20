{
  "properties" : { },
  "id" : "ebe40a0cf4b84192b6bbe923a0ecbd9c",
  "script" : null,
  "groupId" : "4f0230049d7e4f39b1e0897cc0f46f9a",
  "name" : "个人信息修改",
  "createTime" : 1646490239466,
  "updateTime" : 1773997316437,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/center/update",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\r\n    \"name\":\"\",\r\n    \"id\":\"\",\r\n    \"introduction\":\"\"\t\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.secure.SaSecureUtil;

const data = body

return db.table('sys_user').primary('id').update(data, true)
