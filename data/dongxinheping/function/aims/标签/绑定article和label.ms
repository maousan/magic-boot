{
  "properties" : { },
  "id" : "7b3e9f2a4c6d1e8f0a2b4c6d8e1f3a5b",
  "script" : null,
  "groupId" : "a18fa4b15beb4aedbac4201627d9175f",
  "name" : "绑定article和label",
  "createTime" : null,
  "updateTime" : 1775541022838,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/linkArticleToLabel",
  "description" : "调用AIMS /labels/link/{stationCode} 绑定article和label",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "data",
    "value" : null,
    "description" : "请求对象，包含stationCode、labelCode、articleIdList等",
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
import java.util.List
import java.util.ArrayList
import com.dtflys.forest.Forest

if(data == null){
    return {
        success: false,
        status: 400,
        message: 'data不能为空'
    }
}

let stationCode = data.stationCode
if(stationCode == null || stationCode == ''){
    return {
        success: false,
        status: 400,
        message: 'stationCode不能为空'
    }
}

let labelCode = data.labelCode
if(labelCode == null || labelCode == ''){
    return {
        success: false,
        status: 400,
        message: 'labelCode不能为空'
    }
}

let articleIdList = data.articleIdList
if(articleIdList == null || !(articleIdList instanceof List) || articleIdList.size() == 0){
    return {
        success: false,
        status: 400,
        message: 'articleIdList不能为空，且必须为数组'
    }
}

let articleIdChecked = new ArrayList()
let idx = 0
for(articleId in articleIdList){
    idx = idx + 1
    if(articleId == null || articleId == ''){
        return {
            success: false,
            status: 400,
            message: 'articleIdList[' + idx + ']不能为空'
        }
    }
    articleIdChecked.add(articleId)
}

let requestBody = {
    customBatchId: data.customBatchId,
    labelCode: labelCode,
    articleIdList: articleIdChecked,
    templateName: data.templateName,
    arrow: data.arrow,
    addInfo2: data.addInfo2,
    addInfo3: data.addInfo3,
    addInfo4: data.addInfo4,
    addInfo5: data.addInfo5,
    skipChecksumValidation: data.skipChecksumValidation,
    arrowDirection: data.arrowDirection
}

let url = 'http://119.29.52.245:9002/labels/link/' + stationCode
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
    response: responseText
}
