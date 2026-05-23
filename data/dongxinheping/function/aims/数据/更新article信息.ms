{
  "properties" : { },
  "id" : "8a1f7b4d2c6e4f0a9b3d5e7c1a2b4d6f",
  "script" : null,
  "groupId" : "03a2d754c617417cb68306951d6f8cf6",
  "name" : "更新article信息",
  "createTime" : null,
  "updateTime" : 1778051189315,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/updateArticleInfo",
  "description" : "调用AIMS /articles接口更新article信息",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "dataList",
    "value" : null,
    "description" : "请求体对象，参考ArticleParam",
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
import com.google.common.collect.Lists
import com.dtflys.forest.Forest

const aimsHost = Forest.config().getVariableValue("aimsHost")
const batchSize = 100

if(dataList == null){
    return {
        success: false,
        status: 400,
        message: 'dataList不能为空'
    }
}

if(!(dataList instanceof List) || dataList.size() == 0){
    return {
        success: false,
        status: 400,
        message: 'dataList不能为空，且必须为数组'
    }
}

let idx = 0
for(item in dataList){
    idx = idx + 1
    if(item == null){
        return {
            success: false,
            status: 400,
            message: 'dataList[' + idx + ']不能为空'
        }
    }
    if(item.id == null || item.id == ''){
        return {
            success: false,
            status: 400,
            message: 'dataList[' + idx + '].id不能为空'
        }
    }
    if(item.data == null){
        return {
            success: false,
            status: 400,
            message: 'dataList[' + idx + '].data不能为空'
        }
    }
}

let callUpdateArticleBatch = (batchDataList, batchIndex) => {
    let response = Forest.post(`http://${aimsHost}/articles`)
        .contentTypeJson()
        .addBody("dataList", batchDataList)
        .executeAsResponse()

    let statusCode = response.statusCode()
    let responseText = response.readAsString()
    let success = statusCode >= 200 && statusCode < 300
    if(success){
        log.info('AIMS update article batch success, batchIndex=' + batchIndex + ', batchSize=' + batchDataList.size() + ', status=' + statusCode)
    } else {
        log.warn('AIMS update article batch failed, batchIndex=' + batchIndex + ', batchSize=' + batchDataList.size() + ', status=' + statusCode + ', response=' + responseText)
    }
    return {
        success: success,
        batchIndex: batchIndex,
        batchSize: batchDataList.size(),
        status: statusCode,
        response: responseText
    }
}

let batches = Lists.partition(dataList, batchSize)
let successCount = 0
let failedBatches = []
let batchIndex = 0

for(batch in batches){
    batchIndex = batchIndex + 1
    try {
        let batchResult = callUpdateArticleBatch(batch, batchIndex)
        if(batchResult.success){
            successCount = successCount + batch.size()
        } else {
            failedBatches.add(batchResult)
        }
    } catch (batchErr) {
        log.warn('AIMS update article batch error, batchIndex=' + batchIndex + ', batchSize=' + batch.size() + ', message=' + batchErr.message)
        failedBatches.add({
            success: false,
            batchIndex: batchIndex,
            batchSize: batch.size(),
            status: 500,
            response: batchErr.message
        })
    }
}

return {
    success: failedBatches.size() == 0,
    status: failedBatches.size() == 0 ? 200 : 500,
    totalCount: dataList.size(),
    batchCount: batches.size(),
    successCount: successCount,
    failCount: dataList.size() - successCount,
    failedBatches: failedBatches
}
