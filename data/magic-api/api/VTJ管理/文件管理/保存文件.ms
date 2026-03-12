{
  "properties" : { },
  "id" : "690ba19b6553430aac138724984c6026",
  "script" : null,
  "groupId" : "75093e5139944174bc1737b139a5d225",
  "name" : "保存文件",
  "createTime" : 1773279972081,
  "updateTime" : 1773294968640,
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

const { id,name,type } = body


let row = anyline.query("sys_vtj_file", `++id='${id}'`)

if (!row) {
    row = {
        id,
        name,
        type,
        createTime: now(),
        content: JsonUtils.toJsonString(body)
    }
    anyline.insert("sys_vtj_file", row)
} else {
    row = {
        id,
        name,
        type,
        updateTime: now(),
        content: JsonUtils.toJsonString(body)
    }
    anyline.update("sys_vtj_file", row)
}

return true
