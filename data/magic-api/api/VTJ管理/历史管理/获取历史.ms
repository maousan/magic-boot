{
  "properties" : { },
  "id" : "43d69842ccd346b688d0bacd429a029e",
  "script" : null,
  "groupId" : "48261ceac8854872a3b2e3ff932b34d3",
  "name" : "获取历史",
  "createTime" : 1773294758477,
  "updateTime" : 1773379849588,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/{id}",
  "method" : "GET",
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
import org.ssssssss.magicapi.utils.JsonUtils

const row = anyline.queryMap("sys_vtj_history", `++id='${id}'`)

if (!row) {
    return
}

if (row.content) {
    row.content = JsonUtils.readValue(row.content, Map.class)
}

return row.content

