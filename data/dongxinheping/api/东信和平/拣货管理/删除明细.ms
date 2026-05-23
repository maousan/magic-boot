{
  "properties" : { },
  "id" : "pku202605220008dtld123456789abcdef08",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "删除明细",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/detail/delete",
  "method" : "POST",
  "parameters" : [ ],
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
  "requestBody" : "{\n  \"id\": \"xxx\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "删除拣货单明细记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

let detail = db.selectOne("select bill_id from t_picking_upload_detail where id = #{id}", { id: id })
if(detail == null){
    exit 404, '明细记录不存在'
}

let billId = detail.bill_id != null ? detail.bill_id : detail.billId

db.update("delete from t_picking_upload_detail where id = #{id}", { id: id })
db.update("update t_picking_upload set detail_count = greatest(detail_count - 1, 0) where id = #{id}", { id: billId })

return '删除成功'
