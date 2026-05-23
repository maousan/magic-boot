{
  "properties" : { },
  "id" : "wl202405220001abcdefghij1234567890",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "库位列表查询",
  "createTime" : null,
  "updateTime" : 1779420467178,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/warehouse-location/list",
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
    "name" : "warehouseCode",
    "value" : "",
    "description" : "仓库编码（模糊查询）",
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
    "value" : "",
    "description" : "库位ID（模糊查询）",
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
  "responseBody" : "{\n    \"code\": 500,\n    \"message\": \"系统内部出现错误，错误ID: cb781772266e49c68d18250b24c4613a\",\n    \"data\": \"cb781772266e49c68d18250b24c4613a\",\n    \"timestamp\": 1779418143306,\n    \"executeTime\": 245\n}",
  "description" : "分页查询库位列表",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select id, warehouse_code, location_id, create_time, update_time
    from t_location_warehouse
    <where>
        <if test="warehouseCode != null and warehouseCode != ''">
            and warehouse_code like concat('%', #{warehouseCode}, '%')
        </if>
        <if test="locationId != null and locationId != ''">
            and location_id like concat('%', #{locationId}, '%')
        </if>
    </where>
    order by create_time desc
"""

return db.page(sql, pageSize, (page-1)*pageSize)
