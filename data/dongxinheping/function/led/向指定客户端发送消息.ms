{
  "properties" : { },
  "id" : "d6f9a2c81b7345e0a4c7d9b61e328f05",
  "script" : null,
  "groupId" : "628f77553ba14e47ae84391269b008bb",
  "name" : "向指定客户端发送消息",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/sendToClient",
  "description" : "调用LED插件LedDeviceController.sendToClient接口向指定Netty客户端发送消息",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "remoteAddress",
    "value" : null,
    "description" : "客户端远端地址，格式IP:Port",
    "required" : true,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "content",
    "value" : null,
    "description" : "兼容旧参数，要发送的负载文本",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "payload",
    "value" : null,
    "description" : "负载内容，payloadFormat=ascii/hex时作为文本，payloadFormat=json时会先反序列化",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "payloadArray",
    "value" : null,
    "description" : "数值数组负载，优先级高于payload",
    "required" : false,
    "dataType" : "Array",
    "type" : "java.util.List",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "payloadFormat",
    "value" : null,
    "description" : "负载格式，ascii、hex或json",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : "ascii",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "waitResponse",
    "value" : null,
    "description" : "是否等待客户端回包，默认false",
    "required" : false,
    "dataType" : "Boolean",
    "type" : "java.lang.Boolean",
    "defaultValue" : "false",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import log
import com.dtflys.forest.Forest
import org.ssssssss.magicapi.utils.JsonUtils
import java.util.List
import java.util.Map

if(remoteAddress == null || remoteAddress == ''){
    return { code: 400, message: 'remoteAddress不能为空' }
}
remoteAddress = (remoteAddress + '').trim()

let sendPayload = payload
if(sendPayload == null || sendPayload == ''){
    sendPayload = content
}

let sendPayloadArray = payloadArray
if(sendPayloadArray == null){
    sendPayloadArray = []
}

if((sendPayload == null || sendPayload == '') && sendPayloadArray.size() == 0){
    return { code: 400, message: 'payload或payloadArray不能为空' }
}

let sendPayloadFormat = payloadFormat
if(sendPayloadFormat == null || sendPayloadFormat == ''){
    sendPayloadFormat = 'ascii'
}
sendPayloadFormat = (sendPayloadFormat + '').trim().toLowerCase()
if(sendPayloadFormat != 'ascii' && sendPayloadFormat != 'hex' && sendPayloadFormat != 'json'){
    return { code: 400, message: 'payloadFormat仅支持ascii/hex/json' }
}

if(sendPayloadFormat == 'json'){
    if(sendPayloadArray.size() == 0){
        let parsedPayload = null
        try {
            if(sendPayload instanceof List || sendPayload instanceof Map){
                parsedPayload = sendPayload
            } else {
                parsedPayload = JsonUtils.readValue(sendPayload + '', Object.class)
            }
        } catch(e) {
            return { code: 400, message: 'payload不是合法JSON: ' + e.message }
        }

        if(parsedPayload instanceof List){
            sendPayloadArray = parsedPayload
            sendPayload = null
        } else {
            sendPayload = JsonUtils.toJsonString(parsedPayload)
        }
    }
    sendPayloadFormat = 'ascii'
}

let pluginUrl = Forest.config().getVariableValue('zintisLedNettySendUrl')
if(pluginUrl == null || pluginUrl == ''){
    pluginUrl = 'http://127.0.0.1:8090/plugin/zintis-led-plugin/api/netty/server/send'
}

let requestBody = {
    remoteAddress: remoteAddress,
    payload: sendPayload,
    payloadArray: sendPayloadArray,
    payloadFormat: sendPayloadFormat,
    waitResponse: waitResponse == true
}

let response = Forest.post(pluginUrl)
    .contentType('application/json;charset=UTF-8')
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addBody(requestBody)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()
if(statusCode < 200 || statusCode >= 300){
    log.warn('send led netty client failed, remoteAddress=' + remoteAddress + ', status=' + statusCode + ', response=' + responseText)
    return { code: 502, message: '调用LED插件发送接口失败', status: statusCode, responseText: responseText }
}

return {
    code: 200,
    message: 'success',
    status: statusCode,
    responseText: responseText
}
