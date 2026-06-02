{
  "properties" : { },
  "id" : "lbllighton202605280001abcdefghijklmnop",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "标签亮灯",
  "createTime" : null,
  "updateTime" : 1780047813541,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/label/light-on",
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
  "requestBody" : "{\n  \"labelCode\": \"LB001\",\n  \"color\": \"GREEN\",\n  \"duration\": \"10s\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": { \"success\": true }\n}",
  "description" : "通过labelCode控制AIMS标签灯亮灯，支持选择颜色和时长",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import '@/aims/led/controlByLabelCode' as controlByLabelCode

let labelCode = body.labelCode
let color = body.color
let duration = body.duration

if(labelCode == null || (labelCode + '').trim() == ''){
    exit 400, 'labelCode不能为空'
}
if(color == null || (color + '').trim() == ''){
    exit 400, 'color不能为空'
}
if(duration == null || (duration + '').trim() == ''){
    duration = 'inf'
}

labelCode = (labelCode + '').trim()
color = (color + '').trim()
duration = (duration + '').trim()

let items = []
items.push({
    color: color,
    duration: duration,
    labelCode: labelCode,
    patternId: 1
})

return controlByLabelCode(items)
