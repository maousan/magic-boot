{
  "properties" : { },
  "id" : "forestlogget01a2b3c4d5e6f7a8b9c0d1e",
  "script" : null,
  "groupId" : "forestlog001a2b3c4d5e6f7a8b9c0d1e2f",
  "name" : "获取日志配置",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/get",
  "method" : "GET",
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"logEnabled\": true,\n  \"logRequest\": true,\n  \"logResponseStatus\": true,\n  \"logResponseContent\": true\n}",
  "description" : "获取 Forest HTTP 客户端日志开关状态",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import com.dtflys.forest.Forest

const cfg = Forest.config()

return {
    logEnabled: cfg.isLogEnabled(),
    logRequest: cfg.isLogRequest(),
    logResponseStatus: cfg.isLogResponseStatus(),
    logResponseContent: cfg.isLogResponseContent(),
    logResponseHeaders: cfg.isLogResponseHeaders()
}
