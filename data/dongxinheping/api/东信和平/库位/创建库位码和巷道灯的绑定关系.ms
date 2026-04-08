{
  "properties" : { },
  "id" : "9f20a21239c64f33899f871b2f97ef22",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "创建库位码和巷道灯的绑定关系",
  "createTime" : 1775200322319,
  "updateTime" : 1775555781036,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/led-mapping",
  "method" : "POST",
  "parameters" : [ ],
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
  "requestBody" : "{\"lotNo\":\"\\\"OrdNo\\\":\\\"2025122365-1+01\\\";\\\"Sup\\\":\\\"Ep刷\\\";\\\"SupCode\\\":\\\"印刷\\\";\\\"Mtl\\\":\\\"2.01.26.01.20306\\\";\\\"MtlCode\\\":\\\"2.01.26.01.20306\\\";\\\"MtIDesc\\\":\\\"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R- C49\\\";\\\"Lot\\\":\\\"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002\\\";\\\"Qty\\\":\\\"170\\\";\\\"PD\\\":\\\"2026-01-13 07:09:13\\\";\\\"Exp\\\":\\\"2036-01-11 07:09:13\\\"\",\"ledId\":\"123\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 500,\n    \"message\": \"已经存在库位码的绑定关系: LED01\",\n    \"data\": null,\n    \"timestamp\": 1775555700972,\n    \"executeTime\": 74\n}",
  "description" : "",
  "requestBodyDefinition" : {
    "name" : "",
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
      "name" : "lotNo",
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
      "name" : "ledId",
      "value" : "123",
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
  "responseBodyDefinition" : {
    "name" : "",
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
      "name" : "code",
      "value" : "500",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "已经存在库位码的绑定关系: LED01",
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
      "name" : "data",
      "value" : "null",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "timestamp",
      "value" : "1775555700972",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "74",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
import '@/utils/parseLot' as parseLot

let {
    lotNo,
    ledId
} = body

const parsedLot = parseLot(lotNo)
lotNo = parsedLot.Lot
if(lotNo == null || lotNo == ''){
    exit 400, 'lotNo不能为空'
}
if(ledId == null || ledId == ''){
    exit 400, 'ledId不能为空'
}

const row = db.table("t_location_led").where().eq("location_code", lotNo).selectOne()
if (row) {
    exit 500, `已经存在库位码的绑定关系: ${row.ledId}`
}

return db.table("t_location_led").insert({
    locationCode: lotNo,
    ledId
})
