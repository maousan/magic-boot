{
  "properties" : { },
  "id" : "0a92b435435f43a9b10334c18e35dc23",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "按库位查询库存返回",
  "createTime" : 1777017543541,
  "updateTime" : 1778048261138,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/queryInventoryByLocations",
  "description" : "DZJH004：按库位查询库存HTTP调用函数",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "warehouseId",
    "value" : null,
    "description" : "仓库ID",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "locationId",
    "value" : null,
    "description" : "库位编码，多个库位用逗号分隔，最多50个",
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
import org.ssssssss.magicapi.utils.JsonUtils
import log
import java.util.ArrayList
import java.util.List
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.dtflys.forest.Forest

let fail = (message) => {
    return {
        success: false,
        status: 400,
        message: message
    }
}

let valueOrConfig = (configName, defaultValue) => {
    let value = Forest.config().getVariableValue(configName)
    if(value == null || value == '' || (value + '').trim() == '' || (value + '').trim() == 'null'){
        return defaultValue
    }
    return value
}

let addLocationId = (locations, uniq, rawCode) => {
    if(rawCode == null || rawCode == ''){
        return
    }
    let text = (rawCode + '').replace('，', ',')
    for(part in text.split(',')){
        let code = (part + '').trim()
        if(code == ''){
            continue
        }
        if(uniq[code] == null){
            uniq[code] = 1
            locations.add({
                "locationId": code
            })
        }
    }
}

let normalizeLocationIds = (rawLocationId) => {
    let locations = new ArrayList()
    let uniq = {}
    if(rawLocationId == null || rawLocationId == ''){
        return locations
    }
    if(rawLocationId instanceof List || rawLocationId.getClass().isArray()){
        for(item in rawLocationId){
            addLocationId(locations, uniq, item)
        }
        return locations
    }
    addLocationId(locations, uniq, rawLocationId)
    return locations
}

let encodeParam = (value) => {
    if(value == null){
        return ''
    }
    return URLEncoder.encode(value + '', StandardCharsets.UTF_8.name())
}

let appendQuery = (url, name, value) => {
    let separator = url.contains('?') ? '&' : '?'
    return url + separator + encodeParam(name) + '=' + encodeParam(value)
}

let targetUrl = valueOrConfig('Url', 'http://192.168.1.237:18080/datahub/FluxWmsDzjh/')
if(targetUrl == null || targetUrl == ''){
    return fail('缺少配置 forest.variables.Url')
}

//接口名称
let interfaceId = 'DZJH004'

if(warehouseId == null || warehouseId == ''){
    return fail('warehouseId不能为空')
}
if(locationId == null || locationId == ''){
    return fail('locationId不能为空')
}

let locations = normalizeLocationIds(locationId)
if(locations.size() == 0){
    return fail('locationId不能为空')
}
if(locations.size() > 50){
    return fail('库位查询条件超出50组限制')
}

let normalizedLocationId = ''
let idx = 0
for(code in locations){
    if(idx > 0){
        normalizedLocationId = normalizedLocationId + ','
    }
    normalizedLocationId = normalizedLocationId + code
    idx = idx + 1
}

let queryUrl = targetUrl
queryUrl = appendQuery(queryUrl, 'method', interfaceId)
// queryUrl = appendQuery(queryUrl, 'warehouseId', warehouseId)
// queryUrl = appendQuery(queryUrl, 'locationId', normalizedLocationId)

const postData = {
    header: {
        warehouseId,
        locationdetail: locations
    }
}


try {
    let response = Forest.post(queryUrl)
        .contentTypeJson()
        .addBody("data", postData)
        .executeAsResponse()

    let statusCode = response.statusCode()
    let responseText = response.readAsString()
    let success = statusCode >= 200 && statusCode < 300
    if(!success){
        // log.warn('DZJH004 inventory query failed, status=' + statusCode + ', targetUrl=' + targetUrl + ', response=' + responseText)
        return {
            success,
            message: responseText
        }
    }
    const response = JsonUtils.readValue(responseText,Map.class)
    success = success && response["Response"]["return"]["returnCode"] === "0000"
    return {
        success,
        totalCount: success ? response["Response"]["return"]["totalCount_Field"]::int : 0,
        items: success ? response["Response"]["items"]["item"] : []
    }
} catch (e) {
    log.error('DZJH004 inventory query error, targetUrl=' + targetUrl + ', message=' + e.message)
    return {
        success: false,
        message: '调用DZJH004接口失败: ' + e.message
    }
}
