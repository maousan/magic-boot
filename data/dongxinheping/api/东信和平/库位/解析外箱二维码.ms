{
  "properties" : { },
  "id" : "66ab55708a0746f6acd079899258de21",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "解析外箱二维码",
  "createTime" : 1775200817414,
  "updateTime" : 1775214987886,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/parse-qrcode",
  "method" : "GET",
  "parameters" : [ {
    "name" : "raw",
    "value" : "\"OrdNo\":\"2025122365-1+01\";\"Sup\":\"Ep刷\";\"SupCode\":\"印刷\";\"Mtl\":\"2.01.26.01.20306\";\"MtlCode\":\"2.01.26.01.20306\";\"MtIDesc\":\"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R- C49\";\"Lot\":\"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002\";\"Qty\":\"170\";\"PD\":\"2026-01-13 07:09:13\";\"Exp\":\"2036-01-11 07:09:13\"",
    "description" : null,
    "required" : false,
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
    "description" : "该接口需要登录才允许访问",
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"OrdNo\": \"2025122365-1+01\",\n        \"Sup\": \"Ep刷\",\n        \"SupCode\": \"印刷\",\n        \"Mtl\": \"2.01.26.01.20306\",\n        \"MtlCode\": \"2.01.26.01.20306\",\n        \"MtIDesc\": \"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R- C49\",\n        \"Lot\": \"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002\",\n        \"Qty\": \"170\",\n        \"PD\": \"2026-01-13 07:09:13\",\n        \"Exp\": \"2036-01-11 07:09:13\"\n    },\n    \"timestamp\": 1775200821327,\n    \"executeTime\": 4\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import '@/utils/parseLot' as parseLot


return parseLot(raw)