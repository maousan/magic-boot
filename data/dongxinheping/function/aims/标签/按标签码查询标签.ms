{
  "properties" : { },
  "id" : "c79dd5afaa1e4309846110af476c6cfd",
  "script" : null,
  "groupId" : "a18fa4b15beb4aedbac4201627d9175f",
  "name" : "按标签码查询标签",
  "createTime" : null,
  "updateTime" : 1792463942852,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/getLabelByCode",
  "description" : "调用AIMS GET /labels/{labelCode}：按电子标签码查询标签详情（含其绑定的articleList，articleId即库位locationId，可由此反查库位）",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "labelCode",
    "value" : null,
    "description" : "电子标签码",
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
import com.dtflys.forest.Forest
import com.alibaba.fastjson.JSONObject

const aimsHost = Forest.config().getVariableValue("aimsHost")
// 2026-10-20 兜底：未配置 aimsHost 时用 127.0.0.1，避免拼出 http://null/
let host = (aimsHost == null || (aimsHost + '').trim() == '') ? '127.0.0.1' : aimsHost

if(labelCode == null || (labelCode + '').trim() == ''){
    return {
        success: false,
        status: 400,
        message: 'labelCode不能为空',
        label: null
    }
}
labelCode = (labelCode + '').trim()

let url = `http://${host}/labels/${labelCode}`
let response = Forest.get(url)
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addQuery('contents', false)
    .connectTimeout(10000)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()
let success = statusCode >= 200 && statusCode < 300

if(!success){
    log.warn('AIMS get label by code failed, labelCode=' + labelCode + ', status=' + statusCode + ', response=' + responseText)
    return {
        success: false,
        status: statusCode,
        message: responseText,
        label: null
    }
}

log.info('AIMS get label by code ok, labelCode=' + labelCode)

return {
    success: true,
    status: statusCode,
    // 2026-10-20 用 fastjson 解析：hutool 会把 null 包成 JSONNull，经 Jackson 序列化接口直接 500；fastjson 的 null 是普通 null 无此问题
    label: JSONObject.parseObject(responseText)
}
