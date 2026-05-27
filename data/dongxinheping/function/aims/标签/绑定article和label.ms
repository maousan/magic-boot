{
  "properties" : { },
  "id" : "7b3e9f2a4c6d1e8f0a2b4c6d8e1f3a5b",
  "script" : null,
  "groupId" : "a18fa4b15beb4aedbac4201627d9175f",
  "name" : "绑定article和label",
  "createTime" : null,
  "updateTime" : 1779876517392,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/linkArticleToLabel",
  "description" : "调用AIMS /labels/link/{stationCode} 绑定article和label",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "labelCode",
    "value" : null,
    "description" : "",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "articleIdList",
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
  }, {
    "name" : "templateName",
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
  } ]
}
================================
import log
import java.util.List
import java.util.ArrayList
import com.dtflys.forest.Forest
import cn.hutool.core.lang.UUID

const aimsHost = Forest.config().getVariableValue("aimsHost")
const stationCode = Forest.config().getVariableValue("stationCode")

if(labelCode == null || labelCode == ''){
    return {
        success: false,
        status: 400,
        message: 'labelCode不能为空'
    }
}

if(articleIdList == null || !(articleIdList instanceof List) || articleIdList.size() == 0){
    return {
        success: false,
        status: 400,
        message: 'articleIdList不能为空，且必须为数组'
    }
}

let requestBody = {
    customBatchId: UUID.fastUUID().toString(),
    labelCode: labelCode,
    articleIdList,
    templateName
}

let url = `http://${aimsHost}/labels/link/${stationCode}`
let response = Forest.post(url)
    .contentType('application/json;charset=UTF-8')
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addBody(requestBody)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()
let success = statusCode >= 200 && statusCode < 300

if(success){
    log.info('AIMS link article to label success, stationCode=' + stationCode + ', labelCode=' + labelCode + ', status=' + statusCode)
} else {
    log.warn('AIMS link article to label failed, stationCode=' + stationCode + ', labelCode=' + labelCode + ', status=' + statusCode + ', response=' + responseText)
}

return {
    success: success,
    status: statusCode,
    message: responseText
}
