{
  "properties" : { },
  "id" : "f57c812210b045319fc49d6283d95ea9",
  "script" : null,
  "groupId" : "75093e5139944174bc1737b139a5d225",
  "name" : "发布文件",
  "createTime" : 1773285285999,
  "updateTime" : 1773294242943,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/publish",
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

const { project, file } = body

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
