{
  "properties" : { },
  "id" : "lblget0123456789abcdef0123456789ab",
  "script" : null,
  "groupId" : "a18fa4b15beb4aedbac4201627d9175f",
  "name" : "获取标签列表",
  "createTime" : null,
  "updateTime" : 1779875199709,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/getLabelList",
  "description" : "调用AIMS GET /labels获取标签列表（分页）",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "page",
    "value" : null,
    "description" : "页码（从0开始）",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : "0",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "size",
    "value" : null,
    "description" : "每页条数",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : "100",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import log
import com.dtflys.forest.Forest
import cn.hutool.json.JSONUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONArray
import java.util.ArrayList

const aimsHost = Forest.config().getVariableValue("aimsHost")
const stationCode = Forest.config().getVariableValue("stationCode")

if(aimsHost == null || aimsHost == ''){
    return {
        success: false,
        status: 500,
        labels: new ArrayList(),
        lastPage: true
    }
}

let url = `http://${aimsHost}/labels`

let response = Forest.get(url)
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addQuery('page', page)
    .addQuery('size', size)
    .addQuery('sort', 'code,ASC')
    .addQuery('stationCode', stationCode)
    .connectTimeout(10000)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()
let success = statusCode >= 200 && statusCode < 300

if(!success){
    log.warn('AIMS get labels failed, status=' + statusCode + ', url=' + url + ', response=' + responseText)
    return {
        success: false,
        status: statusCode,
        labels: new ArrayList(),
        lastPage: true
    }
}

let labelArray = JSONUtil.parseArray(responseText)
if(labelArray == null || labelArray.size() == 0){
    return {
        success: true,
        status: statusCode,
        labels: new ArrayList(),
        lastPage: true
    }
}

let labels = new ArrayList()
let len = labelArray.size()
let i = 0
while(i < len){
    let labelObj = labelArray.getJSONObject(i)
    let labelCode = labelObj.getStr("labelCode")
    if(labelCode == null || labelCode == ''){
        labelCode = labelObj.getStr("code")
    }
    let articleList = labelObj.getJSONArray("articleList")
    let articles = new ArrayList()
    if(articleList != null){
        let alen = articleList.size()
        let j = 0
        while(j < alen){
            let article = articleList.getJSONObject(j)
            let articleId = article.getStr("id")
            if(articleId != null && articleId != ''){
                articles.add(articleId)
            }
            j = j + 1
        }
    }
    labels.add({
        labelCode: labelCode,
        articles: articles
    })
    i = i + 1
}

return {
    success: true,
    status: statusCode,
    labels: labels,
    lastPage: len < size
}
