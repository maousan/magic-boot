{
  "properties" : { },
  "id" : "8c8045196d3848beab1e1b6cf2e578ee",
  "script" : null,
  "groupId" : "d7de530ea18542f2827146962f10bdb4",
  "name" : "根据articleId关灯",
  "createTime" : 1775235122037,
  "updateTime" : 1779932448796,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/turnOffLedByArticleId",
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
import '@/aims/led/controlByArticleId' as controlByArticleId

const { stationCode, articleIds } = data

const items = []

for (articleId in articleIds) {
    items.push({
        stationCode,
        color: "RED",
        duration: "0s",
        articleId,
        patternId: 0
    })
}

return controlByArticleId(stationCode, items)
