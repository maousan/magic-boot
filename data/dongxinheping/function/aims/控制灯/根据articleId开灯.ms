{
  "properties" : { },
  "id" : "d2a712a230544435b03e8a2df5900b22",
  "script" : null,
  "groupId" : "d7de530ea18542f2827146962f10bdb4",
  "name" : "根据articleId开灯",
  "createTime" : 1775235163576,
  "updateTime" : 1775533563803,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/turnOnLedByArticleId",
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

const { stationCode, color, articleIds } = data

const items = []

for (articleId in articleIds) {
    items.push({
        color,
        duration: "inf",
        articleId,
        patternId: 0
    })
}

controlByArticleId(stationCode, items)