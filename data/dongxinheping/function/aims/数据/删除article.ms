{
  "properties" : { },
  "id" : "d3l7e4t6a5r8t9c2l1e0a3b6f7d8e9",
  "script" : null,
  "groupId" : "03a2d754c617417cb68306951d6f8cf6",
  "name" : "删除article",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/deleteArticle",
  "description" : "调用AIMS DELETE /articles/article接口删除单个article",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "articleId",
    "value" : null,
    "description" : "要删除的articleId",
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
import log
import com.dtflys.forest.Forest

const aimsHost = Forest.config().getVariableValue("aimsHost")
const stationCode = Forest.config().getVariableValue("stationCode")

if(articleId == null || (articleId + '').trim() == ''){
    return {
        success: false,
        status: 400,
        message: 'articleId不能为空'
    }
}
articleId = (articleId + '').trim()

try {
    let response = Forest.delete(`http://${aimsHost}/articles/article`)
        .addQuery("articleId", articleId)
        .addQuery("stationCode", stationCode)
        .executeAsResponse()
    let statusCode = response.statusCode()
    let success = statusCode >= 200 && statusCode < 300
    if(success){
        log.info('AIMS delete article success, articleId=' + articleId + ', status=' + statusCode)
    } else {
        log.warn('AIMS delete article failed, articleId=' + articleId + ', status=' + statusCode + ', response=' + response.readAsString())
    }
    return {
        success: success,
        status: statusCode,
        articleId: articleId
    }
} catch (err) {
    log.warn('AIMS delete article error, articleId=' + articleId + ', message=' + err.message)
    return {
        success: false,
        status: 500,
        articleId: articleId,
        message: err.message
    }
}
