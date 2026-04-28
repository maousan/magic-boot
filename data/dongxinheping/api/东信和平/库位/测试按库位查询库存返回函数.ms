{
  "properties" : { },
  "id" : "8d3b7f2a6c914e0b9a5f1d2376e8c4b0",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "测试按库位查询库存返回函数",
  "createTime" : 1777248000000,
  "updateTime" : 1777248000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/inventory/query-by-location-function-test",
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
  "requestBody" : "{\n  \"warehouseId\": \"WMS001\",\n  \"locationId\": \"A1-11-01-01-01,A1-11-01-01-02\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"success\": true,\n  \"status\": 200,\n  \"requestData\": \"{...}\",\n  \"response\": \"{...}\"\n}",
  "description" : "测试接口：调用函数 /utils/queryInventoryByLocations，按仓库和库位查询库存返回。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import '@/utils/queryInventoryByLocations' as queryInventoryByLocations

if (body == null) {
    exit 400, '请求体不能为空'
}

let warehouseId = body.warehouseId
if (warehouseId == null || warehouseId == '') {
    exit 400, 'warehouseId不能为空'
}

let locationId = body.locationId
if (locationId == null || locationId == '') {
    exit 400, 'locationId不能为空'
}

const result = queryInventoryByLocations({
    EDIGROUPID: body.EDIGROUPID,
    warehouseId: warehouseId,
    locationId: locationId
})

return result
