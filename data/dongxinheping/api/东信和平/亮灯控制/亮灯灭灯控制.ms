{
  "properties" : { },
  "id" : "9dvgr4la975s18sogzyialv59fawnb10",
  "script" : null,
  "groupId" : "qou8in0pu4dvva7g20trefax4lo26ye8",
  "name" : "亮灯灭灯控制",
  "createTime" : null,
  "updateTime" : 1776138310377,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/control",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "该接口需要登录才允许访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\n  \"waveNo\": \"WV202603250001\",\n  \"userId\": \"USER001\",\n  \"mode\": 3,\n  \"color\": \"GREEN\",\n  \"duration\": \"inf\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"指令发送成功\",\n    \"data\": null,\n    \"timestamp\": 1775325451620,\n    \"executeTime\": 175\n}",
  "description" : "亮灯/灭灯控制接口，当前仅记录日志，三方对接逻辑预留。",
  "requestBodyDefinition" : {
    "name" : "root",
    "value" : "",
    "description" : "亮灯/灭灯请求",
    "required" : true,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "waveNo",
      "value" : "WV202603250001",
      "description" : "波次号",
      "required" : true,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "userId",
      "value" : "USER001",
      "description" : "用户ID",
      "required" : true,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "mode",
      "value" : "1",
      "description" : "控制模式(1自动亮灯/2手动亮灯/3自动灭灯/4手动灭灯)",
      "required" : true,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "color",
      "value" : "GREEN",
      "description" : "灯光颜色，默认GREEN",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : "GREEN",
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "duration",
      "value" : "inf",
      "description" : "亮灯时长，默认inf",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : "inf",
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  },
  "responseBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "code",
      "value" : "200",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "指令发送成功",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "null",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
import log
import java.util.UUID
import redis
import org.ssssssss.magicapi.utils.JsonUtils
import '@/aims/led/turnOffLedByArticleId' as turnOffLedByArticleId
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId

if (body == null) {
    exit 400, '请求体不能为空'
}

let payload = body

if (!payload.waveNo) {
    exit 400, 'waveNo不能为空'
}
if (!payload.userId) {
    exit 400, 'userId不能为空'
}
if (payload.mode == null) {
    exit 400, 'mode不能为空'
}

let modeVal = payload.mode
if (modeVal != 1 && modeVal != 2 && modeVal != 3 && modeVal != 4) {
    exit 400, 'mode仅支持1/2/3/4'
}

let colorVal = payload.color
if (colorVal == null || colorVal == '') {
    colorVal = 'GREEN'
}
let durationVal = payload.duration
if (durationVal == null || durationVal == '') {
    durationVal = 'inf'
}

if (colorVal != 'RED' && colorVal != 'GREEN' && colorVal != 'BLUE' && colorVal != 'YELLOW' && colorVal != 'CYAN') {
    exit 400, 'color仅支持RED/GREEN/BLUE/YELLOW/CYAN'
}

if (durationVal != '10s' && durationVal != '30s' && durationVal != '1m' && durationVal != '2m' && durationVal != '5m' && durationVal != '10m' && durationVal != '15m' && durationVal != '20m' && durationVal != '30m' && durationVal != '60m' && durationVal != 'inf') {
    exit 400, 'duration仅支持10s/30s/1m/2m/5m/10m/15m/20m/30m/60m/inf'
}

let actionType = 'OFF'
if (modeVal == 1 || modeVal == 2) {
    actionType = 'ON'
}

let requestJson = JsonUtils.toJsonString(payload)
let transState = null

try {
    transState = anyline.beginTrans()

    anyline.insert('t_light_control_log', {
        id: UUID.randomUUID().toString().replace('-', ''),
        wave_no: payload.waveNo,
        user_id: payload.userId,
        mode: modeVal,
        color: colorVal,
        duration: durationVal,
        action_type: actionType,
        request_path: '/api/light/control',
        request_json: requestJson,
        create_time: new Date()
    })

    // TODO 预留：后续在此补充三方亮灯系统API调用逻辑
    const redisKey = "picking:"+payload.waveNo
    if (modeVal == 1 || modelValue == 2) {
        redis.default.hashPut(redisKey, payload.userId, 1)
    } else {
        redis.default.hashDelete(redisKey, [payload.userId])
    }

    const userCount = redis.default.hashSize(redisKey)
    if (userCount === 0) {
        //灭灯
    } else if (userCount === 1) {
        //亮绿灯
    } else {
        //亮其他灯
    }

    anyline.commitTrans(transState)
    exit 200, '指令发送成功'
} catch (e) {
    if (transState != null) {
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {}
    }
    log.error('亮灯控制接口异常: ' + e.message)
    exit 500, '指令发送失败: ' + e.message
}