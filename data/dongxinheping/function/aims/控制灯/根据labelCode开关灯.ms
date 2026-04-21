{
  "properties" : { },
  "id" : "63901265701d461c939ccffdb9bc710d",
  "script" : null,
  "groupId" : "d7de530ea18542f2827146962f10bdb4",
  "name" : "根据labelCode开关灯",
  "createTime" : 1775209721631,
  "updateTime" : 1776138190375,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/controlByLabelCode",
  "description" : "",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "source",
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
  } ]
}
================================
import log
import cn.hutool.extra.spring.SpringUtil
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import com.dtflys.forest.Forest;

const aimsHost = Forest.config().getVariableValue("aimsHost")

if(source == null){
    return {
        success: false,
        status: 400,
        message: 'data不能为空，且必须为数组类型',
        request: []
    }
}

let ledItems = new ArrayList()

if(!(source instanceof List)){
    return {
        success: false,
        status: 400,
        message: 'data必须为数组类型',
        request: []
    }
}

let idx = 0
for(item in source){
    idx = idx + 1
    if(item == null){
        return {
            success: false,
            status: 400,
            message: 'data[' + idx + ']不能为空',
            request: []
        }
    }
    if(item.labelCode == null || item.labelCode == ''){
        return {
            success: false,
            status: 400,
            message: 'labelCode不能为空',
            request: []
        }
    }
    let row = new HashMap()
    row.put('labelCode', item.labelCode)
    let itemColor = item.color
    if(itemColor == null || itemColor == ''){
        return {
            success: false,
            status: 400,
            message: 'color不能为空',
            request: []
        }
    }
    row.put('color', itemColor)

    let itemDuration = item.duration
    if(itemDuration == null || itemDuration == ''){
        return {
            success: false,
            status: 400,
            message: 'duration不能为空',
            request: []
        }
    }
    if(itemDuration == 'inf'){
        itemDuration = '60m'
    }
    row.put('duration', itemDuration)

    if(item.patternId != null){
        row.put('patternId', item.patternId)
    }
    ledItems.add(row)
}

if(ledItems.size() == 0){
    return {
        success: false,
        status: 400,
        message: 'labelCode不能为空',
        request: []
    }
}

let response = Forest.put(`http://${aimsHost}/labels/contents/led`)
    .contentType('application/json;charset=UTF-8')
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addBody(ledItems)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()
let success = statusCode == 202
let msg = '调用失败'
if(success){
    msg = '调用成功'
}

if(!success){
    log.warn('AIMS亮灯调用失败, status=' + statusCode + ', response=' + responseText)
} else {
    log.info("AIMS亮灯调用成功")
}

return {success}
