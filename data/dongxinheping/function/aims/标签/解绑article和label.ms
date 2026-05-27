{
  "properties" : { },
  "id" : "3f1e7b2d9c5a4f6e8d1b2a3c4e5f6a7b",
  "script" : null,
  "groupId" : "a18fa4b15beb4aedbac4201627d9175f",
  "name" : "解绑article和label",
  "createTime" : null,
  "updateTime" : 1779876007095,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/unlinkArticleAndLabel",
  "description" : "调用AIMS /labels/unlink 解绑article和label",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "labelCode",
    "value" : null,
    "description" : "标签条码",
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
import com.dtflys.forest.Forest

const aimsHost = Forest.config().getVariableValue("aimsHost")

if(labelCode == null || labelCode == ''){
    return {
        success: false,
        status: 400,
        message: 'labelCode不能为空'
    }
}

let url = `http://${aimsHost}/labels/unlink`
let response = Forest.post(url)
    .addQuery('labelCode', labelCode)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()
let success = statusCode >= 200 && statusCode < 300

if(success){
    log.info('AIMS unlink article and label success, labelCode=' + labelCode + ', status=' + statusCode)
} else {
    log.warn('AIMS unlink article and label failed, labelCode=' + labelCode + ', status=' + statusCode + ', response=' + responseText)
}

return {
    success: success,
    status: statusCode,
    message: responseText
}
