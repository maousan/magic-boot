{
  "properties" : { },
  "id" : "05d000175c8146cbaece010efdd9473e",
  "script" : null,
  "groupId" : "2ud9c5419ag5484mbxxsafin0b0myji0",
  "name" : "未定义名称",
  "createTime" : 1775215053219,
  "updateTime" : 1780476073040,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/test",
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"OrdNo\": \"2025122365-1 01\",\n        \"Sup\": \"Ep刷\",\n        \"SupCode\": \"印刷\",\n        \"Mtl\": \"2.01.26.01.20306\",\n        \"MtlCode\": \"2.01.26.01.20306\",\n        \"MtIDesc\": \"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R-C49\",\n        \"Lot\": \"20260113_2025122365-1 01(1)_YS12_YS-JK-001_002\",\n        \"Qty\": \"170\",\n        \"PD\": \"2026-01-13 07:09:13\",\n        \"Exp\": \"2036-01-11 07:09:13\"\n    },\n    \"timestamp\": 1780476052517,\n    \"executeTime\": 92\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest
import '@/aims/led/turnOnLedByLabelCode' as turnOnLedByLabelCode
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import '@/aims/led/turnOffLedByLabelCode' as turnOffLedByLabelCode
import '@/aims/led/turnOffLedByArticleId' as turnOffLedByArticleId
import '@/utils/parseLot' as parseLot

const stationCode = Forest.config().getVariableValue("stationCode")

return parseLot('"OrdNo":"2025122365-1+01";"Sup":"Ep刷";"SupCode":"印刷";"Mtl":"2.01.26.01.20306";"MtlCode":"2.01.26.01.20306";"MtIDesc":"辽宁工行沈阳市市本级公务卡贷记IC金卡(C4362)251229确认版黑色高磁0.82mmPVC两面光面YL贷记卡签名条/金标天线:RG401R-C49";"Lot":"20260113_2025122365-1+01(1)_YS12_YS-JK-001_002";"Qty":"170";"PD":"2026-01-13 07:09:13";"Exp":"2036-01-11 07:09:13"')


// turnOnLedByLabelCode({
//     color: "GREEN",
//     labelCodes: [
//         "06B89377B69B"
//     ]
// })

// turnOffLedByLabelCode({
//     labelCodes: [
//         "03E2D88CE1DC"
//     ]
// })


// turnOnLedByArticleId({
//     color: "GREEN",
//     stationCode,
//     articleIds: [
//         "A1-11-01-01-01",
//         "A1-11-02-01-01"
//     ]
// })

// turnOffLedByArticleId({
//     stationCode,
//     articleIds: [
//         "A1-11-01-01-01",
//         "A1-11-02-01-01"
//     ]
// })