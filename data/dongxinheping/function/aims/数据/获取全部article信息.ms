{
  "properties" : { },
  "id" : "c1f2a3b4d5e6f7091a2b3c4d5e6f7081",
  "script" : null,
  "groupId" : "03a2d754c617417cb68306951d6f8cf6",
  "name" : "获取全部article信息",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : "admin",
  "updateBy" : null,
  "path" : "/getAllArticlesInfo",
  "description" : "适配 OpenAPI Get all articles information，默认请求 AIMS /articles",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "endpointPath",
    "value" : null,
    "description" : "可选，自定义接口路径，默认 /articles",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : "/articles",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "articleCode",
    "value" : null,
    "description" : "可选，文章编码过滤",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "articleName",
    "value" : null,
    "description" : "可选，文章名称过滤",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "pageNo",
    "value" : null,
    "description" : "可选，页码",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : "1",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "pageSize",
    "value" : null,
    "description" : "可选，每页条数",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : "100",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "stationCode",
    "value" : null,
    "description" : "可选，工位编码",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "rawQuery",
    "value" : null,
    "description" : "可选，附加原始查询串，不含 ?，例如 status=1&sort=desc",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "authorization",
    "value" : null,
    "description" : "可选，Authorization 头（如 Bearer xxx）",
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
import java.net.URLEncoder
import java.util.ArrayList

const aimsHost = Forest.config().getVariableValue("aimsHost")
const aimsPort = Forest.config().getVariableValue("aimsPort")

if(aimsHost == null || aimsHost == ''){
    return {
        success: false,
        status: 500,
        message: '缺少配置 aimsHost'
    }
}

let targetPath = endpointPath
if(targetPath == null || targetPath == ''){
    targetPath = '/articles'
}
if(!targetPath.startsWith('/')){
    targetPath = '/' + targetPath
}

let queryList = new ArrayList()

let addQuery = (name, value) => {
    if(value == null || value == ''){
        return
    }
    queryList.add(name + '=' + URLEncoder.encode(String(value), 'UTF-8'))
}

addQuery('articleCode', articleCode)
addQuery('articleName', articleName)
addQuery('pageNo', pageNo)
addQuery('pageSize', pageSize)
addQuery('stationCode', stationCode)

if(rawQuery != null && rawQuery != ''){
    queryList.add(rawQuery)
}

let hostWithPort = aimsHost + ':9002'
if(aimsPort != null && aimsPort != ''){
    hostWithPort = aimsHost + ':' + aimsPort
}

let queryString = ''
for(item in queryList){
    if(queryString != ''){
        queryString = queryString + '&'
    }
    queryString = queryString + item
}

let url = `http://${hostWithPort}${targetPath}`
if(queryString != ''){
    url = url + '?' + queryString
}

let request = Forest.get(url)
    .addHeader('Accept', 'application/json;charset=UTF-8')

if(authorization != null && authorization != ''){
    request.addHeader('Authorization', authorization)
}

let response = request.executeAsResponse()
let statusCode = response.statusCode()
let responseText = response.readAsString()
let success = statusCode >= 200 && statusCode < 300

if(success){
    log.info('AIMS get all articles info success, status=' + statusCode + ', url=' + url)
} else {
    log.warn('AIMS get all articles info failed, status=' + statusCode + ', url=' + url + ', response=' + responseText)
}

return {
    success: success,
    status: statusCode,
    url: url,
    response: responseText
}
