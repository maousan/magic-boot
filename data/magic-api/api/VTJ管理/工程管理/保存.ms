{
  "properties" : { },
  "id" : "96082f16ad844d14903f5ebbfa82951f",
  "script" : null,
  "groupId" : "a4e944cd911f4d39a3b881de0c262a2e",
  "name" : "保存",
  "createTime" : 1773222125488,
  "updateTime" : 1773305612691,
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

const { type, data } = body

const {
    project,
    materials,
    files,
    histories,
    vue
} = data

let row = anyline.query("sys_vtj_project", `++id='${project.id}'`)

if (!row) {
    exit 0, '工程不存在'
} 

row = {
    id: project.id,
    name: project.name,
    project: JsonUtils.toJsonString(project||{}),
    materials: JsonUtils.toJsonString(materials||{}),
    updateTime: now()
}
anyline.update("sys_vtj_project", row)

return true
