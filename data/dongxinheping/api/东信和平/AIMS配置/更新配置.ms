{
  "properties" : { },
  "id" : "aimsupdate01a2b3c4d5e6f7a8b9c0d1e2",
  "script" : null,
  "groupId" : "aimscfg001a2b3c4d5e6f7a8b9c0d1e2f",
  "name" : "更新配置",
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
  "requestBody" : "{\n  \"aimsHost\": \"119.29.52.245:9002\",\n  \"stationCode\": \"10001\",\n  \"mixColor\": \"CYAN\",\n  \"controlDispatchAsync\": false\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"配置已更新\",\n  \"data\": null\n}",
  "description" : "更新 AIMS 服务运行时配置（不持久化，重启后恢复）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import com.dtflys.forest.Forest

const cfg = Forest.config()

if(body.aimsHost != null){
    cfg.setVariable("aimsHost", body.aimsHost)
}
if(body.stationCode != null){
    cfg.setVariable("stationCode", body.stationCode)
}
if(body.mixColor != null){
    cfg.setVariable("mixColor", body.mixColor)
}
if(body.controlDispatchAsync != null){
    cfg.setVariable("controlDispatchAsync", body.controlDispatchAsync)
}

return '配置已更新（运行时生效，重启后恢复）'
