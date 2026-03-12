{
  "properties" : { },
  "id" : "bb3be8e65b164b4684a4d31f56b18495",
  "script" : null,
  "groupId" : "a4e944cd911f4d39a3b881de0c262a2e",
  "name" : "初始化",
  "createTime" : 1773280072176,
  "updateTime" : 1773309181817,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/init",
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
    project
} = data

let row = anyline.query("sys_vtj_project", `++id='${project.id}'`)

if (!row) {
    row = {
        id: project.id,
        name: project.name,
        createTime: now(),
        project: JsonUtils.toJsonString(project)
    }
    anyline.insert("sys_vtj_project", row)
    return data
}

return JsonUtils.readValue(row.getString("project"), Map.class)
