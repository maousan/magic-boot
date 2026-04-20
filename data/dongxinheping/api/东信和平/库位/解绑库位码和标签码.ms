{
  "properties" : { },
  "id" : "8c4a1e7d2b5f4a9c8d6e1f3b2a7c5e4d",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "解绑库位码和标签码",
  "createTime" : 1775542800000,
  "updateTime" : 1776665196457,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/label-mapping",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "labelCode",
    "value" : "03E2D88CE1DC",
    "description" : "label code",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "api access requires login",
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
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": 1,\n  \"timestamp\": 1775542800000,\n  \"executeTime\": 20\n}",
  "description" : "call AIMS unlink by labelCode only, no local db operation",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest

const aimsHost = Forest.config().getVariableValue("aimsHost")
const stationCode = Forest.config().getVariableValue("stationCode")

if(labelCode == null || labelCode == ''){
    exit 400, 'labelCode不能为空'
}

let unlinkUrl = `http://${aimsHost}:9002/labels/unlink?labelCode=${labelCode}`
let unlinkResponse = Forest.post(unlinkUrl)
    .contentType('application/json;charset=UTF-8')
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .executeAsResponse()

let unlinkStatus = unlinkResponse.statusCode()
let unlinkBody = unlinkResponse.readAsString()

if(unlinkStatus < 200 || unlinkStatus >= 300){
    log.warn('AIMS unlink failed, labelCode=' + labelCode + ', status=' + unlinkStatus + ', response=' + unlinkBody)
    exit 502, 'AIMS解绑失败: ' + unlinkBody
}

log.info('unlink location-label mapping success, labelCode=' + labelCode + ', status=' + unlinkStatus)
return 1
