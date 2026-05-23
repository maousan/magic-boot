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
    "name" : "id",
    "value" : "",
    "description" : "绑定记录ID",
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

if(aimsHost == null || aimsHost == ''){
    exit 500, 'aimsHost未配置'
}

let buildAimsBaseUrl = (hostValue) => {
    let host = (hostValue + '').trim()
    if(!host.startsWith('http://') && !host.startsWith('https://')){
        host = 'http://' + host
    }
    if(!host.matches('^https?://[^/]+:[0-9]+$')){
        host = host + ':9002'
    }
    return host
}

if(id == null || id == ''){
    exit 400, 'id不能为空'
}

let record = db.selectOne("""
    select label_code from t_location_label where id = #{id}
""", { id: id })

if(record == null){
    exit 404, '绑定记录不存在'
}

let labelCode = record.label_code

let aimsBaseUrl = buildAimsBaseUrl(aimsHost)
let unlinkUrl = `${aimsBaseUrl}/labels/unlink?labelCode=${labelCode}`
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

db.table("t_location_label").where().eq("id", id).delete()

return 1
