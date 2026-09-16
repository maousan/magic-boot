{
  "properties" : { },
  "id" : "rfidcontrolapi202605060000000001",
  "script" : null,
  "groupId" : "2ud9c5419ag5484mbxxsafin0b0myji0",
  "name" : "RFID控制",
  "createTime" : 1778052000000,
  "updateTime" : 1784705535708,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/rfid",
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
  "requestBody" : "{\n  \"deviceId\": \"192.168.2.134\",\n  \"mode\": \"off\",\n  \"data\": {\n    \"locationId\": \"B2-11-02-01-02\",\n    \"sku\": \"2.01.26.01.20306\",\n    \"lotNo\":\"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002\"\n  }\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": \"操作成功\",\n    \"timestamp\": 1784705530466,\n    \"executeTime\": 92\n}",
  "description" : "RFID控制：on=寻卡，off=停止寻卡。",
  "requestBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "RFID控制请求",
    "required" : true,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "deviceId",
      "value" : "192.168.2.134",
      "description" : "设备唯一标识（设备序列号、MAC或自定义ID），不传时尝试用客户端IP兜底",
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
      "value" : "on",
      "description" : "寻卡/停止寻卡：on=寻卡；off=停止寻卡",
      "required" : true,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "",
      "description" : "当mode为on的时候必填",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ {
        "name" : "locationId",
        "value" : "A1-11-01-01-02",
        "description" : "库位号",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "sku",
        "value" : "2.01.26.01.20306",
        "description" : "物料号",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "lotNo",
        "value" : "",
        "description" : "",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      } ]
    } ]
  },
  "responseBodyDefinition" : null
}
================================
import cn.hutool.json.JSONUtil
import request
import log
import java.net.InetAddress
import com.dtflys.forest.Forest

log.info("rfid body: {}", JSONUtil.toJsonStr(body))

let apiResponse = (code, message) => {
    return {
        code: code,
        message: message,
        data: null
    }
}

let normalizeText = (value) => {
    if(value == null){
        return ''
    }
    return (value + '').trim()
}

let isLoopbackIp = (ip) => {
    let value = normalizeText(ip).toLowerCase()
    return value == '127.0.0.1' || value == 'localhost' || value == '::1' || value == '0:0:0:0:0:0:0:1'
}

let getLocalHostIp = () => {
    try {
        let address = InetAddress.getLocalHost()
        if(address == null){
            return ''
        }
        return normalizeText(address.getHostAddress())
    } catch (e) {
        log.warn('Failed to resolve local host IP, message={}', e)
        return ''
    }
}

let resolveDeviceId = () => {
    let clientIp = normalizeText(request.getClientIP())
    if(clientIp != '' && !isLoopbackIp(clientIp)){
        return clientIp
    }

    let configuredDeviceId = normalizeText(Forest.config().getVariableValue('zintisRfidDefaultDeviceId'))
    if(configuredDeviceId != ''){
        return configuredDeviceId
    }

    let localHostIp = getLocalHostIp()
    if(localHostIp != '' && !isLoopbackIp(localHostIp)){
        return localHostIp
    }

    return clientIp
}

if(body == null){
    exit 400, '请求体不能为空'
}

let mode = normalizeText(body.mode).toLowerCase()
if(mode == ''){
    exit 400, 'mode不能为空'
}
if(mode != 'on' && mode != 'off'){
    exit 400, 'mode仅支持on/off'
}

let deviceId = body.deviceId || resolveDeviceId()
if(deviceId == ''){
    exit 400, '无法获取deviceId'
}

let requestData = body.data
if(mode == 'on'){
    if(requestData == null){
        exit 400, 'mode为on时data不能为空'
    }
    let locationId = normalizeText(requestData.locationId)
    let sku = normalizeText(requestData.sku)
    let lotNo = normalizeText(requestData.lotNo)
    if(locationId == ''){
        exit 400, 'mode为on时data.locationId不能为空'
    }
    if(sku == ''){
        exit 400, 'mode为on时data.sku不能为空'
    }
    // if(lotNo == ''){
    //     exit 400, 'mode为on时data.lotNo不能为空'
    // }
    requestData.locationId = locationId
    requestData.sku = sku
    requestData.lotNo = lotNo

    let batchId = locationId + '_' + sku + '_' + lotNo
    let epcRecord = db.selectOne("select epc from t_batch_epc where batch_id = #{batchId} and status = 1", {batchId})
    if(epcRecord != null){
        requestData.epc = epcRecord.epc
        log.info('resolved epc for batchId=' + batchId + ', epc=' + epcRecord.epc)
    } else {
        log.warn('no active epc binding found for batchId=' + batchId)
    }
}
if(requestData == null){
    requestData = {}
}

let pluginUrl = Forest.config().getVariableValue('zintisRfidCommandUrl')
if(pluginUrl == null || pluginUrl == ''){
    pluginUrl = 'http://127.0.0.1:8090/plugin/zintis-rfid-plugin/api/rfid/command'
}

let pluginRequest = {
    deviceId: deviceId,
    command: mode,
    params: requestData
}

try {
    let response = Forest.post(pluginUrl)
        .contentType('application/json;charset=UTF-8')
        .addHeader('Accept', 'application/json;charset=UTF-8')
        .addBody(pluginRequest)
        .executeAsResponse()

    let statusCode = response.statusCode()
    let responseText = response.readAsString()
    let success = statusCode >= 200 && statusCode < 300
    if(!success){
        log.warn('RFID control failed, deviceId=' + deviceId + ', mode=' + mode + ', status=' + statusCode + ', response=' + responseText)
        exit 500, 'RFID控制失败'
    }

    log.info('RFID control success, deviceId=' + deviceId + ', mode=' + mode + ', status=' + statusCode)
    return '操作成功'
} catch (e) {
    exit 500, 'RFID控制失败: ' + e.message
}
