{
  "properties" : { },
  "id" : "368c6debc18342c6abb46e795336d573",
  "script" : null,
  "groupId" : "0295f2b4af9145f5a0ea29fa4b797214",
  "name" : "删除",
  "createTime" : null,
  "updateTime" : 1772968394030,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/delete/{id}",
  "method" : "DELETE",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "configure:delete",
    "description" : "允许拥有该权限的访问",
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
    "description" : null,
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
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
return anyline.deletes("sys_configure", 'id', id)