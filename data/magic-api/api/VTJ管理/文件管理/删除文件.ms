{
  "properties" : { },
  "id" : "3573e6d57cae4773b46fa50ce1b83d3b",
  "script" : null,
  "groupId" : "75093e5139944174bc1737b139a5d225",
  "name" : "删除文件",
  "createTime" : 1773294276834,
  "updateTime" : 1773294317483,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/{id}",
  "method" : "DELETE",
  "parameters" : [ ],
  "options" : [ ],
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

return anyline.deletes("sys_vtj_file", 'id', id) > 0

