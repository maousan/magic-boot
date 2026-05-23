{
  "properties" : { },
  "id" : "inv202605220002bcdefghijklmnopqr",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除库存记录",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/inventory/delete",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "warehouseId",
    "value" : null,
    "description" : "仓库ID",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "locationId",
    "value" : null,
    "description" : "库位码",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "lotAtt09",
    "value" : null,
    "description" : "批次号",
    "required" : true,
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
    "description" : "",
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
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "根据仓库ID+库位码+批次号删除库存记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if (warehouseId == null || (warehouseId + '').trim() == '') {
    exit 0, '仓库ID不能为空'
}
if (locationId == null || (locationId + '').trim() == '') {
    exit 0, '库位码不能为空'
}
if (lotAtt09 == null || (lotAtt09 + '').trim() == '') {
    exit 0, '批次号不能为空'
}

let rows = db.update("""
    delete from t_inventory
    where warehouse_id = #{warehouseId} and location_id = #{locationId} and lot_att09 = #{lotAtt09}
""", {
    warehouseId: warehouseId,
    locationId: locationId,
    lotAtt09: lotAtt09
})

if (rows == 0) {
    exit 0, '记录不存在'
}

return '删除成功'
