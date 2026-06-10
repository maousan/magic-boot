{
  "properties" : { },
  "id" : "58b2d475a38a4652999967efc4fbfaa5",
  "script" : null,
  "groupId" : "d7de530ea18542f2827146962f10bdb4",
  "name" : "根据articleId开关灯",
  "createTime" : 1775235016864,
  "updateTime" : 1780579969771,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/controlByArticleId",
  "description" : "",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "stationCode",
    "value" : null,
    "description" : null,
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "data",
    "value" : null,
    "description" : null,
    "required" : false,
    "dataType" : "String",
    "type" : "java.util.Collection",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import com.dtflys.forest.Forest
import log
import cn.hutool.http.HttpRequest
import cn.hutool.json.JSONUtil
import java.util.ArrayList
import java.util.HashMap
import java.util.List

const aimsHost = Forest.config().getVariableValue("aimsHost")

if(data == null || !(data instanceof List)){
    return {
        success: false,
        status: 400,
        message: 'data不能为空，且必须为数组类型'
    }
}

for(item in data){
    let duration = item.duration
    if(duration == 'inf') {
        duration = '60m'
    }
    if(duration == '0s') {
        duration = '0'
    }
    item.duration = duration
}

let jsonBody = JSONUtil.toJsonStr(data)
log.info("标签灯请求数据: {}", jsonBody)
let response = HttpRequest.put(`http://${aimsHost}/labels/contents/led/article`)
    .header('Content-Type', 'application/json;charset=UTF-8')
    .body(jsonBody)
    .execute()

let statusCode = response.getStatus()
let responseText = response.body()
let success = statusCode == 202

if(!success){
    log.warn('AIMS亮灯调用失败, status=' + statusCode + ', response=' + responseText)
} else {
    log.info("AIMS亮灯调用成功")
}

return {
    success: success,
    status: statusCode,
    message: success ? '调用成功' : '调用失败',
    responseText: responseText
}
