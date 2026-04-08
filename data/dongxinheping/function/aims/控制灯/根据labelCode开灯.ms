{
  "properties" : { },
  "id" : "aa5f7cfec04d4109bd1b63158bd0690c",
  "script" : null,
  "groupId" : "d7de530ea18542f2827146962f10bdb4",
  "name" : "根据labelCode开灯",
  "createTime" : 1775233783765,
  "updateTime" : 1775235262437,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/turnOnLedByLabelCode",
  "description" : "",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "data",
    "value" : null,
    "description" : null,
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import '@/aims/led/controlByLabelCode' as controlByLabelCode
import log

const { color, labelCodes } = data

const items = []

for (labelCode in labelCodes) {
    items.push({
        color,
        duration: "inf",
        labelCode,
        patternId: 0
    })
}

controlByLabelCode(items)