{
  "properties" : { },
  "id" : "aimsget001a2b3c4d5e6f7a8b9c0d1e2f",
  "script" : null,
  "groupId" : "aimscfg001a2b3c4d5e6f7a8b9c0d1e2f",
  "name" : "获取配置",
  "createTime" : null,
  "updateTime" : 1779933447781,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
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
  "responseBody" : "{\n  \"aimsHost\": \"119.29.52.245:9002\",\n  \"stationCode\": \"10001\",\n  \"mixColor\": \"CYAN\",\n  \"controlDispatchAsync\": \"false\"\n}",
  "description" : "获取当前 AIMS 服务运行时配置",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import com.dtflys.forest.Forest

const cfg = Forest.config()

return {
    aimsHost: cfg.getVariableValue("aimsHost"),
    stationCode: cfg.getVariableValue("stationCode"),
    mixColor: cfg.getVariableValue("mixColor"),
    controlDispatchAsync: cfg.getVariableValue("controlDispatchAsync"),
    lightOnDuration: cfg.getVariableValue('lightOnDuration')
}
