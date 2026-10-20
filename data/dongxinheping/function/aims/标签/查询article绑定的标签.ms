{
  "properties" : { },
  "id" : "1e4199911d2649a18209cf8e32fe2741",
  "script" : null,
  "groupId" : "a18fa4b15beb4aedbac4201627d9175f",
  "name" : "查询article绑定的标签",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/getLabelsByArticle",
  "description" : "调用AIMS GET /labels/article：按articleId（=库位locationId）查询该article当前绑定的电子标签列表（含labelCode、在线状态、电量等）",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "articleId",
    "value" : null,
    "description" : "articleId（本系统即库位locationId）",
    "required" : true,
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
import log
import java.util.ArrayList
import com.dtflys.forest.Forest
import com.alibaba.fastjson.JSONArray

const aimsHost = Forest.config().getVariableValue("aimsHost")
const stationCode = Forest.config().getVariableValue("stationCode")
// 2026-10-20 兜底：未配置 aimsHost 时用 127.0.0.1，避免拼出 http://null/
let host = (aimsHost == null || (aimsHost + '').trim() == '') ? '127.0.0.1' : aimsHost

if(articleId == null || (articleId + '').trim() == ''){
    return {
        success: false,
        status: 400,
        message: 'articleId不能为空',
        labels: new ArrayList()
    }
}
articleId = (articleId + '').trim()

let url = `http://${host}/labels/article`
let response = Forest.get(url)
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addQuery('articleId', articleId)
    .addQuery('stationCode', stationCode)
    .addQuery('contents', false)
    .connectTimeout(10000)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()
let success = statusCode >= 200 && statusCode < 300

if(!success){
    log.warn('AIMS get labels by article failed, articleId=' + articleId + ', status=' + statusCode + ', response=' + responseText)
    return {
        success: false,
        status: statusCode,
        message: responseText,
        labels: new ArrayList()
    }
}

log.info('AIMS get labels by article ok, articleId=' + articleId + ', labels=' + (responseText == null ? 0 : responseText.length()))

return {
    success: true,
    status: statusCode,
    // 2026-10-20 用 fastjson 解析：hutool 会把 null 包成 JSONNull，经 Jackson 序列化接口直接 500；fastjson 的 null 是普通 null 无此问题
    labels: JSONArray.parseArray(responseText)
}
