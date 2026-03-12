{
  "properties" : { },
  "id" : "f9a469103ca74172bdd2e8ab8db3c126",
  "script" : null,
  "groupId" : "48261ceac8854872a3b2e3ff932b34d3",
  "name" : "删除历史",
  "createTime" : 1773294682637,
  "updateTime" : 1773294704902,
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

return anyline.deletes("sys_vtj_history", 'id', id) > 0

