{
  "properties" : { },
  "id" : "7e2d5a5d9c5d46cba7b0f42bae9fdfa8",
  "script" : null,
  "groupId" : "75093e5139944174bc1737b139a5d225",
  "name" : "获取文件",
  "createTime" : 1773284431594,
  "updateTime" : 1773298489853,
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

const row = anyline.query("sys_vtj_file", `++id='${id}'`)

if (row) {
    return JsonUtils.readValue(row.getString("content"),Map.class)
}


