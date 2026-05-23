{
  "properties" : { },
  "id" : "a4f6c9d72e8b41f0a5c3d97b6e120f84",
  "script" : null,
  "groupId" : "qou8in0pu4dvva7g20trefax4lo26ye8",
  "name" : "测试向指定客户端发送消息函数",
  "createTime" : null,
  "updateTime" : 1779359181731,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/netty-send-function-test",
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
  "requestBody" : "{\n  \"remoteAddress\": \"192.168.2.102:51853\",\n  \"payload\": [102, 53, 186, 60, 7],\n  \"payloadFormat\": \"json\",\n  \"waitResponse\": false\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"status\": 200,\n  \"responseText\": \"{...}\"\n}",
  "description" : "测试接口：调用函数 /led/sendToClient，向指定LED Netty客户端发送ASCII、HEX或JSON消息。",
  "requestBodyDefinition" : {
    "name" : "root",
    "value" : null,
    "description" : null,
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : [ {
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
      "children" : [ ]
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
      "children" : [ ]
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
      "children" : [ ]
    }, {
      "name" : "payloadFormat",
      "value" : "json",
      "description" : "负载格式，ascii、hex或json",
      "required" : false,
      "dataType" : "String",
      "type" : "java.lang.String",
      "defaultValue" : "ascii",
      "validateType" : null,
      "error" : null,
      "expression" : null,
      "children" : [ ]
    }, {
      "name" : "waitResponse",
      "value" : "false",
      "description" : "是否等待客户端回包，默认false",
      "required" : false,
      "dataType" : "Boolean",
      "type" : "java.lang.Boolean",
      "defaultValue" : "false",
      "validateType" : null,
      "error" : null,
      "expression" : null,
      "children" : [ ]
    } ]
  },
  "responseBodyDefinition" : null
}
================================
import '@/led/sendToClient' as sendToClient

if (body == null) {
    exit 400, '请求体不能为空'
}

let remoteAddress = body.remoteAddress
if (remoteAddress == null || remoteAddress == '') {
    exit 400, 'remoteAddress不能为空'
}

const result = sendToClient(remoteAddress, body.content, body.payload, body.payloadArray, body.payloadFormat, body.waitResponse)

return result
