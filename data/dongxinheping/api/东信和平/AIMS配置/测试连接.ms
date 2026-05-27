{
  "properties" : { },
  "id" : "aimstest001a2b3c4d5e6f7a8b9c0d1e2",
  "script" : null,
  "groupId" : "aimscfg001a2b3c4d5e6f7a8b9c0d1e2f",
  "name" : "测试连接",
  "createTime" : null,
  "updateTime" : 1779868817233,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/test",
  "method" : "POST",
  "parameters" : [ ],
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
  "responseBody" : "{\n  \"success\": true,\n  \"statusCode\": 200,\n  \"message\": \"连接成功\"\n}",
  "description" : "测试 AIMS 服务连接是否可用",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest

const aimsHost = Forest.config().getVariableValue("aimsHost")

if(aimsHost == null || aimsHost == ''){
    exit 400, '请先配置 aimsHost'
}

let url = `http://${aimsHost}/articles`

try {
    let response = Forest.get(url)
        .addHeader('Accept', 'application/json;charset=UTF-8')
        .connectTimeout(5000)
        .executeAsResponse()
    let statusCode = response.statusCode()
    let success = statusCode >= 200 && statusCode < 300
    let msg = ''
    if(success){
        msg = '连接成功，状态码: ' + statusCode
    } else {
        msg = '连接响应异常，状态码: ' + statusCode
    }
    exit 200, msg
} catch(e) {
    exit 500, '连接失败: ' + e.message
}
