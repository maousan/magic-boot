{
  "properties" : { },
  "id" : "2b7e8d3f4a5c6e1d9f0a2b3c4d5e6f7a",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "绑定库位码和标签码",
  "createTime" : 1775549000000,
  "updateTime" : 1775555778231,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/label-mapping",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "api access requires login",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\n  \"locationCode\": \"\\\"OrdNo\\\":\\\"2025122365-1+01\\\";\\\"Sup\\\":\\\"Ep刷\\\";\\\"SupCode\\\":\\\"印刷\\\";\\\"Mtl\\\":\\\"2.01.26.01.20306\\\";\\\"MtlCode\\\":\\\"2.01.26.01.20306\\\";\\\"MtIDesc\\\":\\\"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R- C49\\\";\\\"Lot\\\":\\\"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002\\\";\\\"Qty\\\":\\\"170\\\";\\\"PD\\\":\\\"2026-01-13 07:09:13\\\";\\\"Exp\\\":\\\"2036-01-11 07:09:13\\\"\",\n  \"labelCode\": \"03E2D88CE1DC\",\n  \"stationCode\": \"10001\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": 1,\n    \"timestamp\": 1775555774312,\n    \"executeTime\": 119\n}",
  "description" : "call AIMS link api only, no local db operation",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import java.util.ArrayList
import com.dtflys.forest.Forest
import '@/utils/parseLot' as parseLot

let parsedLocationCode = parseLot(body.locationCode)
let locationCode = parsedLocationCode != null && parsedLocationCode.Lot != null ? parsedLocationCode.Lot : parsedLocationCode
let labelCode = body.labelCode
let stationCode = body.stationCode
if(stationCode == null || stationCode == ''){
    stationCode = '10001'
}

if(locationCode == null || locationCode == ''){
    exit 400, 'locationCode不能为空'
}
if(labelCode == null || labelCode == ''){
    exit 400, 'labelCode不能为空'
}

let articleIdList = new ArrayList()
articleIdList.add(locationCode)

let requestBody = {
    labelCode: labelCode,
    articleIdList: articleIdList,
    templateName: body.templateName,
    arrow: body.arrow,
    addInfo2: body.addInfo2,
    addInfo3: body.addInfo3,
    addInfo4: body.addInfo4,
    addInfo5: body.addInfo5,
    skipChecksumValidation: body.skipChecksumValidation,
    arrowDirection: body.arrowDirection
}

let url = 'http://119.29.52.245:9002/labels/link/' + stationCode
let response = Forest.post(url)
    .contentType('application/json;charset=UTF-8')
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addBody(requestBody)
    .executeAsResponse()

let statusCode = response.statusCode()
let responseText = response.readAsString()

if(statusCode < 200 || statusCode >= 300){
    log.warn('AIMS link failed, stationCode=' + stationCode + ', locationCode=' + locationCode + ', labelCode=' + labelCode + ', status=' + statusCode + ', response=' + responseText)
    exit 502, 'AIMS绑定失败: ' + responseText
}

log.info('AIMS link success, stationCode=' + stationCode + ', locationCode=' + locationCode + ', labelCode=' + labelCode + ', status=' + statusCode)
return 1
