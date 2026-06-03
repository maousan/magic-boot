{
  "properties" : { },
  "id" : "pku202605220002dtl1234567890abcdef02",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "查看详情",
  "createTime" : null,
  "updateTime" : 1780470588510,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/detail",
  "method" : "GET",
  "parameters" : [ {
    "name" : "id",
    "value" : "",
    "description" : "主表ID",
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
  "responseBody" : null,
  "description" : "查询拣货单主表及其明细",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

let master = db.selectOne("""
    select id, wave_no, user_id, user_name,
           detail_count, update_time, create_time
    from t_picking_upload where id = #{id}
""", { id: id })

if(master == null){
    exit 404, '记录不存在'
}

let details = db.select("""
    select id, wave_no, bill_id, material_code,
           batch_no, location_code,
           plan_quantity, actual_quantity,
           status, create_time
    from t_picking_upload_detail
    where bill_id = #{id}
    order by id
""", { id: id })

return { master: master, details: details }
