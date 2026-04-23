{
  "properties" : { },
  "id" : "f4b618bc3fbc460caf8ef94f4b86ec03",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "测试根据库位码亮巷道灯函数",
  "createTime" : 1777000000000,
  "updateTime" : 1777000000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/led/light-on-function-test",
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
  "requestBody" : "{\n  \"lotNo\": \"A1-11-01-01-01\",\n  \"timeoutMs\": 3000\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"lotNo\": \"A1-11-01-01-01\",\n        \"ledId\": \"3A:69:7A:08:D0:A5\",\n        \"color\": \"GREEN\",\n        \"deviceIp\": \"192.168.2.102\",\n        \"devicePort\": 9527,\n        \"hostAddress\": 102,\n        \"dataCommand\": 51\n    },\n    \"timestamp\": 1777000000000,\n    \"executeTime\": 101\n}",
  "description" : "测试接口：调用函数 /led/turnOnAisleLedByLotNo，按库位码触发巷道灯亮灯。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import '@/led/turnOnAisleLedByLotNo' as turnOnAisleLedByLotNo

if (body == null) {
    exit 400, '请求体不能为空'
}

let lotNo = body.lotNo
if (lotNo == null || lotNo == '') {
    exit 400, 'lotNo不能为空'
}

const result = turnOnAisleLedByLotNo({
    lotNo: lotNo,
    timeoutMs: body.timeoutMs
})

return result
