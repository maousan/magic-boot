{
  "properties" : { },
  "id" : "2c761001c58d4833b23dfc0fc00fc582",
  "script" : null,
  "groupId" : "51e64213e7fe4441b0713dbf0794ee14",
  "name" : "删除数据库",
  "createTime" : 1773055447434,
  "updateTime" : 1773055473612,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/delete/{id}",
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

return anyline.deletes("sys_database", "id", id)