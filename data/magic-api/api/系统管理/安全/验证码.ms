{
  "properties" : { },
  "id" : "be89865140ab409085db6d0cc6d82452",
  "script" : null,
  "groupId" : "1952f25c81084e24b55b11385767dc38",
  "name" : "验证码",
  "createTime" : null,
  "updateTime" : 1772782746134,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/verification/code",
  "method" : "GET",
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"img\": \"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\",\n        \"uuid\": \"c30d19f02767418e94c9d15aca5ef6e0\"\n    },\n    \"timestamp\": 1772782744474,\n    \"executeTime\": 251\n}",
  "description" : "",
  "requestBodyDefinition" : null,
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
      "value" : "success",
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
        "name" : "img",
        "value" : "data:image/png;base64,[B@65617f6d",
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
        "name" : "uuid",
        "value" : "9a080e570e0346bf8b80c035c977c888",
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
    }, {
      "name" : "timestamp",
      "value" : "1647333558240",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "15",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
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
import cn.hutool.captcha.LineCaptcha
import cn.hutool.captcha.CaptchaUtil
import log
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import org.ssssssss.magicboot.model.CodeCacheMap

LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 48);
var uuid = UUID.randomUUID().toString().replace('-', '')
CodeCacheMap.put(uuid, lineCaptcha.getCode())
OutputStream bOut = new ByteArrayOutputStream();
lineCaptcha.write(bOut)
var bytes = bOut.toByteArray()
return {
    img: bytes,
    uuid: uuid
}