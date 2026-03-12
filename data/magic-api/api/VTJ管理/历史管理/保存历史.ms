{
  "properties" : { },
  "id" : "ca87fc36eea546f78aa06a0e39eb580e",
  "script" : null,
  "groupId" : "48261ceac8854872a3b2e3ff932b34d3",
  "name" : "保存历史",
  "createTime" : 1773294540398,
  "updateTime" : 1773321037371,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import org.ssssssss.magicapi.utils.JsonUtils
import log

const { id, items } = body

let row = anyline.queryMap("sys_vtj_history", `++id='${id}'`)

if (!row) {
    row = {
        id,
        createTime: now(),
        content: JsonUtils.toJsonString(body)
    }
    anyline.insert("sys_vtj_history", row)
} else {
    row = {
        id,
        name,
        updateTime: now(),
        content: JsonUtils.toJsonString(body)
    }
    anyline.update("sys_vtj_history", row)
}

return true
