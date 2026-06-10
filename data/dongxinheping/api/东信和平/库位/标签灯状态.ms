{
  "properties" : { },
  "id" : "lbllightstatus20260604abcdefghijk",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "标签灯状态",
  "createTime" : null,
  "updateTime" : 1780541977615,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/label/led-status",
  "method" : "GET",
  "parameters" : [ {
    "name" : "labelCode",
    "value" : null,
    "description" : "标签编码",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "查询标签当前拣货信息及指示灯状态，代理AIMS GET /picking/label/{labelCode}",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import cn.hutool.json.JSONUtil
import com.dtflys.forest.Forest

const aimsHost = Forest.config().getVariableValue("aimsHost")

labelCode = labelCode.trim()

let url = `http://${aimsHost}/picking/label/${labelCode}`

try {
    let response = Forest.get(url)
        .addHeader('Accept', 'application/json;charset=UTF-8')
        .connectTimeout(10000)
        .executeAsResponse()

    let statusCode = response.statusCode()
    let responseText = response.readAsString()

    if(statusCode == 404){
        exit 404, '标签不存在或无拣货信息'
    }

    if(statusCode < 200 || statusCode >= 300){
        log.warn('AIMS label led-status failed, labelCode=' + labelCode + ', status=' + statusCode + ', response=' + responseText)
        exit 500, '查询标签状态失败'
    }

    return JSONUtil.parseObj(responseText||'{}')
} catch (e) {
    log.warn('AIMS label led-status error, labelCode=' + labelCode + ', message=' + e.message)
    exit 500, '查询标签状态失败: ' + e.message
}
