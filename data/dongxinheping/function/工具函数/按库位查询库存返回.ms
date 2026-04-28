{
  "properties" : { },
  "id" : "0a92b435435f43a9b10334c18e35dc23",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "按库位查询库存返回",
  "createTime" : null,
  "updateTime" : 1777012970949,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/queryInventoryByLocations",
  "description" : "DZJH004：按库位查询库存HTTP调用函数",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "EDIGROUPID",
    "value" : null,
    "description" : "接口ID",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : "DZJH004",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
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
import log
import java.util.ArrayList
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import com.dtflys.forest.Forest
import '@/utils/buildWmsInterfaceSign' as buildWmsInterfaceSign

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

let escapeJson = (value) => {
    if(value == null){
        return ''
    }
    return (value + '')
        .replace('\\', '\\\\')
        .replace('"', '\\"')
        .replace('\r', '\\r')
        .replace('\n', '\\n')
        .replace('\t', '\\t')
}

let normalizeLocationIds = (rawLocationId) => {
    let locations = new ArrayList()
    let uniq = {}
    if(rawLocationId == null || rawLocationId == ''){
        return locations
    }
    let text = (rawLocationId + '').replace('，', ',')
    for(part in text.split(',')){
        let code = (part + '').trim()
        if(code == ''){
            continue
        }
        if(uniq[code] == null){
            uniq[code] = 1
            locations.add(code)
        }
    }
    return locations
}

let encodeParam = (value) => {
    if(value == null){
        return ''
    }
    return URLEncoder.encode(value + '', StandardCharsets.UTF_8.name())
}

let targetUrl = valueOrConfig('Url', 'http://192.168.1.237:18080/datahub/FluxWmsDzjh')
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

let clientCustomerId = valueOrConfig('ClientCustomerId', null)
let clientDb = valueOrConfig('ClientDb', warehouseId)
let appToken = valueOrConfig('AppToken', null)
let appKey = valueOrConfig('AppKey', null)
let appSecret = valueOrConfig('AppSecret', null)

if(clientCustomerId == null || clientCustomerId == ''){
    return fail('缺少配置 forest.variables.ClientCustomerId')
}
if(clientDb == null || clientDb == ''){
    return fail('缺少配置 forest.variables.ClientDb')
}
if(appToken == null || appToken == ''){
    return fail('缺少配置 forest.variables.AppToken')
}
if(appKey == null || appKey == ''){
    return fail('缺少配置 forest.variables.AppKey')
}
if(appSecret == null || appSecret == ''){
    return fail('缺少配置 forest.variables.AppSecret')
}

let locationJson = ''
let idx = 0
for(code in locations){
    if(idx > 0){
        locationJson = locationJson + ','
    }
    locationJson = locationJson + '{"locationId":"' + escapeJson(code) + '"}'
    idx = idx + 1
}

let businessData = '{"data":{"header":{"EDIGROUPID":"' + escapeJson(interfaceId) + '","warehouseId":"' + escapeJson(warehouseId) + '","locationdetail":[' + locationJson + ']}}}'
let signResult = buildWmsInterfaceSign(appSecret, businessData)
if(signResult == null || signResult.success != true){
    let signMessage = '生成签名失败'
    if(signResult != null && signResult.message != null){
        signMessage = signResult.message
    }
    return fail(signMessage)
}

let timestamp = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss').format(new Date())
let formBody = ''
formBody = formBody + 'method=' + encodeParam(interfaceId)
formBody = formBody + '&client_customerid=' + encodeParam(clientCustomerId)
formBody = formBody + '&client_db=' + encodeParam(clientDb)
formBody = formBody + '&messageid=' + encodeParam(interfaceId)
formBody = formBody + '&apptoken=' + encodeParam(appToken)
formBody = formBody + '&appkey=' + encodeParam(appKey)
formBody = formBody + '&sign=' + signResult.sign
formBody = formBody + '&timestamp=' + encodeParam(timestamp)
formBody = formBody + '&format=json'
formBody = formBody + '&data=' + encodeParam(businessData)

try {
    let response = Forest.post(targetUrl)
        .contentType('application/x-www-form-urlencoded;charset=UTF-8')
        .addHeader('Accept', 'application/json;charset=UTF-8')
        .addBody(formBody)
        .executeAsResponse()

    let statusCode = response.statusCode()
    let responseText = response.readAsString()
    let success = statusCode >= 200 && statusCode < 300
    if(success){
        log.info('DZJH004 inventory query success, status=' + statusCode + ', targetUrl=' + targetUrl)
    } else {
        log.warn('DZJH004 inventory query failed, status=' + statusCode + ', targetUrl=' + targetUrl + ', response=' + responseText)
    }
    return {
        success: success,
        status: statusCode,
        requestData: businessData,
        response: responseText
    }
} catch (e) {
    log.error('DZJH004 inventory query error, targetUrl=' + targetUrl + ', message=' + e.message)
    return {
        success: false,
        status: 500,
        message: '调用DZJH004接口失败: ' + e.message,
        requestData: businessData
    }
}
