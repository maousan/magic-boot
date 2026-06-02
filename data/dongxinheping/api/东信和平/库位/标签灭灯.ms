{
  "properties" : { },
  "id" : "lbllightoff202605280001abcdefghijk",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "标签灭灯",
  "createTime" : null,
  "updateTime" : 1780047816032,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/label/light-off",
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
  "requestBody" : "{\n  \"labelCode\": \"LB001\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": { \"success\": true }\n}",
  "description" : "通过labelCode控制AIMS标签灯灭灯",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import '@/aims/led/controlByLabelCode' as controlByLabelCode

let labelCode = body.labelCode

if(labelCode == null || (labelCode + '').trim() == ''){
    exit 400, 'labelCode不能为空'
}

labelCode = (labelCode + '').trim()

let items = []
items.push({
    color: "RED",
    duration: "0",
    labelCode: labelCode,
    patternId: 1
})

return controlByLabelCode(items)
