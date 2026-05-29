{
  "properties" : { },
  "id" : "forestlogupd01a2b3c4d5e6f7a8b9c0d1e",
  "script" : null,
  "groupId" : "forestlog001a2b3c4d5e6f7a8b9c0d1e2f",
  "name" : "更新日志配置",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/update",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\n  \"logEnabled\": true,\n  \"logRequest\": true,\n  \"logResponseStatus\": true,\n  \"logResponseContent\": true\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "\"配置已更新\"",
  "description" : "运行时开关 Forest HTTP 客户端日志（不持久化，重启后恢复）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import com.dtflys.forest.Forest

const cfg = Forest.config()

if(body.logEnabled != null){
    cfg.setLogEnabled(body.logEnabled)
}
if(body.logRequest != null){
    cfg.setLogRequest(body.logRequest)
}
if(body.logResponseStatus != null){
    cfg.setLogResponseStatus(body.logResponseStatus)
}
if(body.logResponseContent != null){
    cfg.setLogResponseContent(body.logResponseContent)
}
if(body.logResponseHeaders != null){
    cfg.setLogResponseHeaders(body.logResponseHeaders)
}

return '配置已更新（运行时生效，重启后恢复）'
