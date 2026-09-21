{
  "properties" : { },
  "id" : "f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "列表查询",
  "createTime" : null,
  "updateTime" : 1779416616936,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "status",
    "value" : "",
    "description" : "状态过滤: 1=绑定中, 0=已解绑, 不传则全部",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "keyword",
    "value" : "",
    "description" : "关键词搜索(匹配batchId或epc)",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
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
  "responseBody" : "{\n  \"success\": true,\n  \"data\": {\n    \"list\": [],\n    \"total\": 0\n  }\n}",
  "description" : "分页查询批次EPC绑定记录。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select id, batch_id, epc, status, bind_time, unbind_time, location_id, sku, lot_no
    from t_batch_epc
    <where>
    <if test="status != null and status != ''">
    and status = #{status}
    </if>
    <if test="keyword != null and keyword != ''">
    and (batch_id like concat('%', #{keyword}, '%') or epc like concat('%', #{keyword}, '%'))
    </if>
    </where>
    order by create_time desc
"""

return db.page(sql, pageSize, (page-1)*pageSize)