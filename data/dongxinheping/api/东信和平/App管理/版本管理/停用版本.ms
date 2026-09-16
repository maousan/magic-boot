{
  "properties" : { },
  "id" : "disableappver0000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "停用版本",
  "createTime" : null,
  "updateTime" : 1784704489316,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/disable/{id}",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
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
  "paths" : [ {
    "name" : "id",
    "value" : null,
    "description" : "版本记录ID",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "responseBody" : null,
  "description" : "停用版本（熔断，参考litepos）：停用后不再作为最新版本下发，客户端下次检查自动落到次新可用版本；记录保留，可随时启用",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if(!id){
    exit 0, '版本ID不能为空'
}

db.table('t_app_version').primary('id').update({id: id, disabled: 1})

return {id: id, disabled: 1}
