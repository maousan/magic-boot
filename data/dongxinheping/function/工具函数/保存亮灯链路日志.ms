{
  "properties" : { },
  "id" : "ab8e6b6e7f4f4a53b2a5f4b8d79b9c11",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "保存亮灯链路日志",
  "createTime" : 1776700800000,
  "updateTime" : 1776700800000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/saveLightTraceLog",
  "description" : "将亮灯链路摘要日志落库到t_light_trace_log，失败不抛异常",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "traceId",
    "value" : null,
    "description" : "链路追踪ID",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "apiName",
    "value" : null,
    "description" : "接口标识",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "stage",
    "value" : null,
    "description" : "阶段(done/error)",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "actionType",
    "value" : null,
    "description" : "动作类型",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "waveNo",
    "value" : null,
    "description" : "波次号",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "userId",
    "value" : null,
    "description" : "用户ID",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "locationCodes",
    "value" : null,
    "description" : "库位集合",
    "required" : false,
    "dataType" : "Array",
    "type" : "java.util.List",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "extraData",
    "value" : null,
    "description" : "扩展信息",
    "required" : false,
    "dataType" : "Object",
    "type" : "java.util.Map",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import log
import java.util.UUID
import org.ssssssss.magicapi.utils.JsonUtils

if(traceId == null || traceId == ''){
    return false
}
if(apiName == null || apiName == ''){
    return false
}
if(stage == null || stage == ''){
    stage = 'done'
}

let codes = locationCodes
if(codes == null){
    codes = []
}

try {
    anyline.insert('t_light_trace_log', {
        id: UUID.randomUUID().toString().replace('-', ''),
        trace_id: traceId,
        api_name: apiName,
        stage: stage,
        action_type: actionType,
        wave_no: waveNo,
        user_id: userId,
        location_codes: JsonUtils.toJsonString(codes),
        location_count: codes.size(),
        extra_json: extraData == null ? null : JsonUtils.toJsonString(extraData),
        create_time: new Date()
    })
    return true
} catch (e) {
    log.error('saveLightTraceLog fail, traceId=' + traceId + ', api=' + apiName + ', stage=' + stage + ', message=' + e.message)
    return false
}
