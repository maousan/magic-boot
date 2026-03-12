{
  "properties" : { },
  "id" : "fd1a16c61fd54ac8bb10cccf8a6c8a24",
  "script" : null,
  "groupId" : "51e64213e7fe4441b0713dbf0794ee14",
  "name" : "保存数据库",
  "createTime" : 1773053388148,
  "updateTime" : 1773054621813,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "该接口需要登录才允许访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================

if (body.id !== null and body.id !== '') {
    return anyline.update("sys_database", body)
} else {
    return anyline.insert("sys_database", body)
}