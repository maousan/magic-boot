{
  "properties" : { },
  "id" : "pku202605220007dtlu123456789abcdef07",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "修改明细",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/detail/update",
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
  "requestBody" : "{\n  \"id\": \"xxx\",\n  \"materialCode\": \"MAT001\",\n  \"batchNo\": \"B001\",\n  \"locationCode\": \"A1-11-02\",\n  \"planQuantity\": 10,\n  \"actualQuantity\": 5,\n  \"status\": 0\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "修改拣货单明细记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

let exists = db.selectValue("select count(1) from t_picking_upload_detail where id = #{id}", { id: id })
if(exists == 0){
    exit 404, '明细记录不存在'
}

let sets = []
let params = { id: id }

if(body.materialCode != null){
    sets.add('material_code = #{materialCode}')
    params.materialCode = body.materialCode
}
if(body.batchNo != null){
    sets.add('batch_no = #{batchNo}')
    params.batchNo = body.batchNo
}
if(body.locationCode != null){
    sets.add('location_code = #{locationCode}')
    params.locationCode = body.locationCode
}
if(body.planQuantity != null){
    sets.add('plan_quantity = #{planQuantity}')
    params.planQuantity = body.planQuantity
}
if(body.actualQuantity != null){
    sets.add('actual_quantity = #{actualQuantity}')
    params.actualQuantity = body.actualQuantity
}
if(body.status != null){
    sets.add('status = #{status}')
    params.status = body.status
}

if(sets.size() == 0){
    exit 400, '没有需要更新的字段'
}

let setSql = ''
for(item in sets){
    if(setSql != ''){
        setSql = setSql + ', '
    }
    setSql = setSql + item
}

db.update("update t_picking_upload_detail set " + setSql + " where id = #{id}", params)

return '修改成功'
