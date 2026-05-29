{
  "properties" : { },
  "id" : "8a2f7c4d3e6b5a0f1c2d4e7b8a9f0c1d",
  "script" : null,
  "groupId" : "03a2d754c617417cb68306951d6f8cf6",
  "name" : "删除article信息",
  "createTime" : null,
  "updateTime" : 1779966420613,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/deleteArticleInfo",
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
const batchSize = 20
const maxPages = 3
const placeholderData = {}
const dataFields = ['warehouseId', 'sku', 'lotAtt09', 'qty', 'qtyAllocated', 'qtyPa', 'editTime']
var slotIdx = 1
while(slotIdx <= batchSize * maxPages){
    for(fieldName in dataFields){
        placeholderData[fieldName + '_' + slotIdx] = ''
    }
    slotIdx = slotIdx + 1
}
return placeholderData
// import log
// import com.dtflys.forest.Forest

// const aimsHost = Forest.config().getVariableValue("aimsHost")
// const stationCode = Forest.config().getVariableValue("stationCode")

// if(articleId == null || (articleId + '').trim() == ''){
//     return {
//         success: false,
//         status: 400,
//         message: 'articleId不能为空'
//     }
// }
// articleId = (articleId + '').trim()

// try {
//     let response = Forest.delete(`http://${aimsHost}/articles/article`)
//         .addQuery("articleId", articleId)
//         .addQuery("stationCode", stationCode)
//         .executeAsResponse()
//     let statusCode = response.statusCode()
//     let success = statusCode >= 200 && statusCode < 300
//     if(success){
//         log.info('AIMS delete article success, articleId=' + articleId + ', status=' + statusCode)
//     } else {
//         log.warn('AIMS delete article failed, articleId=' + articleId + ', status=' + statusCode + ', response=' + response.readAsString())
//     }
//     return {
//         success: success,
//         status: statusCode,
//         articleId: articleId
//     }
// } catch (err) {
//     log.warn('AIMS delete article error, articleId=' + articleId + ', message=' + err.message)
//     return {
//         success: false,
//         status: 500,
//         articleId: articleId,
//         message: err.message
//     }
// }
