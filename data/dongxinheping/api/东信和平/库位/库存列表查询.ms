{
  "properties" : { },
  "id" : "a3f7b2c8d9e1401582a6b3c7d4e5f601",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "库存列表查询",
  "createTime" : 1777459200000,
  "updateTime" : 1779415444791,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/inventory/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "page",
    "value" : "1",
    "description" : "页码",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "pageSize",
    "value" : "20",
    "description" : "每页条数",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : null,
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
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "locationId",
    "value" : null,
    "description" : "库位码（模糊匹配）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "sku",
    "value" : null,
    "description" : "SKU编码（模糊匹配）",
    "required" : false,
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
    "description" : "批次属性",
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
  "responseBody" : "{\n    \"code\": 500,\n    \"message\": \"系统内部出现错误，错误ID: 61d70081840247cbac2d2a985eacc098\",\n    \"data\": \"61d70081840247cbac2d2a985eacc098\",\n    \"timestamp\": 1779415437387,\n    \"executeTime\": 111\n}",
  "description" : "分页查询库存数据（本地表 t_inventory）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select warehouse_id, location_id, sku, lot_att09,
           qty, qty_allocated, qty_pa, edit_time, update_time
    from t_inventory
    <where>
    <if test="warehouseId != null and warehouseId != ''">
    and warehouse_id = #{warehouseId}
    </if>
    <if test="locationId != null and locationId != ''">
    and location_id like concat('%', #{locationId}, '%')
    </if>
    <if test="sku != null and sku != ''">
    and sku like concat('%', #{sku}, '%')
    </if>
    <if test="lotAtt09 != null and lotAtt09 != ''">
    and lot_att09 = #{lotAtt09}
    </if>
    </where>
    order by update_time desc
"""

return db.page(sql, pageSize, (page-1)*pageSize)

