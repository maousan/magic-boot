{
  "properties" : { },
  "id" : "enableappver00000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "启用版本",
  "createTime" : null,
  "updateTime" : 1784704489316,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/enable/{id}",
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
  "description" : "启用已停用的版本，恢复作为最新版本候选",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if(!id){
    exit 0, '版本ID不能为空'
}

db.table('t_app_version').primary('id').update({id: id, disabled: 0})

return {id: id, disabled: 0}
