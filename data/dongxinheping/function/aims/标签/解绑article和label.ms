{
  "properties" : { },
  "id" : "3f1e7b2d9c5a4f6e8d1b2a3c4e5f6a7b",
  "script" : null,
  "groupId" : "a18fa4b15beb4aedbac4201627d9175f",
  "name" : "解绑article和label",
  "createTime" : null,
  "updateTime" : 1775542800000,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/unlinkArticleAndLabel",
  "description" : "调用AIMS /labels/unlink 解绑article和label",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "data",
    "value" : null,
    "description" : "请求对象，包含labelCode",
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

if(data == null){
    return {
        success: false,
        status: 400,
        message: 'data不能为空'
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

let url = 'http://119.29.52.245:9002/labels/unlink?labelCode=' + labelCode
let response = Forest.post(url)
    .contentType('application/json;charset=UTF-8')
    .addHeader('Accept', 'application/json;charset=UTF-8')
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
    response: responseText
}
