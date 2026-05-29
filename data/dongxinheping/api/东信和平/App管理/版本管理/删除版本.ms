{
  "properties" : { },
  "id" : "delappver000000000000000000000001",
  "script" : null,
  "groupId" : "appversion000000000000000000001",
  "name" : "删除版本",
  "createTime" : null,
  "updateTime" : 1779947889800,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/delete/{id}",
  "method" : "DELETE",
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
  "description" : "软删除版本记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if(!id){
    exit 0, '版本ID不能为空'
}

db.table('app_version').primary('id').update({id: id, is_del: 1})