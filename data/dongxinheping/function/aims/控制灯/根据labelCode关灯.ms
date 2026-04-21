{
  "properties" : { },
  "id" : "b945c35d91404981bb6f2c5320664ada",
  "script" : null,
  "groupId" : "d7de530ea18542f2827146962f10bdb4",
  "name" : "根据labelCode关灯",
  "createTime" : 1775233521155,
  "updateTime" : 1775234503542,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/turnOffLedByLabelCode",
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

const { labelCodes } = data

const items = []

for (labelCode in labelCodes) {
    items.push({
        color: "RED",
        duration: "0",
        labelCode,
        patternId: 0
    })
}

controlByLabelCode(items)
