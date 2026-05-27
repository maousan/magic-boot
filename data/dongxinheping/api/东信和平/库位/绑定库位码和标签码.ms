{
  "properties" : { },
  "id" : "2b7e8d3f4a5c6e1d9f0a2b3c4d5e6f7a",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "绑定库位码和标签码",
  "createTime" : 1775549000000,
  "updateTime" : 1779876757032,
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
  "requestBody" : "{\n  \"locationCode\": \"\\\"OrdNo\\\":\\\"2025122365-1+01\\\";\\\"Sup\\\":\\\"Ep刷\\\";\\\"SupCode\\\":\\\"印刷\\\";\\\"Mtl\\\":\\\"2.01.26.01.20306\\\";\\\"MtlCode\\\":\\\"2.01.26.01.20306\\\";\\\"MtIDesc\\\":\\\"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R- C49\\\";\\\"Lot\\\":\\\"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002\\\";\\\"Qty\\\":\\\"170\\\";\\\"PD\\\":\\\"2026-01-13 07:09:13\\\";\\\"Exp\\\":\\\"2036-01-11 07:09:13\\\"\",\n  \"labelCode\": \"03E2D88CE1DC\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": 1,\n    \"timestamp\": 1775555774312,\n    \"executeTime\": 119\n}",
  "description" : "call AIMS link api only, no local db operation",
  "requestBodyDefinition" : {
    "name" : "root",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "locationCode",
      "value" : "\"OrdNo\":\"2025122365-1+01\";\"Sup\":\"Ep刷\";\"SupCode\":\"印刷\";\"Mtl\":\"2.01.26.01.20306\";\"MtlCode\":\"2.01.26.01.20306\";\"MtIDesc\":\"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R- C49\";\"Lot\":\"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002\";\"Qty\":\"170\";\"PD\":\"2026-01-13 07:09:13\";\"Exp\":\"2036-01-11 07:09:13\"",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "labelCode",
      "value" : "03E2D88CE1DC",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  },
  "responseBodyDefinition" : null
}
================================
import log
import java.util.UUID
import java.util.ArrayList
import com.dtflys.forest.Forest
import '@/utils/parseLot' as parseLot
import '@/aims/labels/linkArticleToLabel' as linkArticleToLabel

let parsedLocationCode = parseLot(body.locationCode)
let locationCode = parsedLocationCode != null && parsedLocationCode.Lot != null ? parsedLocationCode.Lot : parsedLocationCode

let labelCode = body.labelCode

if(locationCode == null || locationCode == ''){
    exit 400, 'locationCode不能为空'
}
if(labelCode == null || labelCode == ''){
    exit 400, 'labelCode不能为空'
}

let articleIdList = new ArrayList()

articleIdList.add(locationCode)


const { success, message } = linkArticleToLabel(labelCode, articleIdList, null)

if (!success) {
    exit 500, message
}

log.info('AIMS link success, stationCode=' + stationCode + ', locationCode=' + locationCode + ', labelCode=' + labelCode + ', status=' + statusCode)

db.table("t_location_label").where().eq("label_code", labelCode).delete()
db.table("t_location_label").insert({
    id: UUID.randomUUID().toString().replace('-', ''),
    location_code: locationCode,
    label_code: labelCode,
    bind_time: now().format("yyyy-MM-dd HH:mm:ss")
})

return 1
