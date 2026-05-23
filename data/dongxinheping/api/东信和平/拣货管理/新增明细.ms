{
  "properties" : { },
  "id" : "pku202605220006dtla123456789abcdef06",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "新增明细",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/detail/add",
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
  "requestBody" : "{\n  \"billId\": \"xxx\",\n  \"materialCode\": \"MAT001\",\n  \"batchNo\": \"B001\",\n  \"locationCode\": \"A1-11-02\",\n  \"planQuantity\": 10,\n  \"actualQuantity\": 0,\n  \"status\": 0\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "新增拣货单明细记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import java.util.UUID

let billId = body.billId
if(billId == null || (billId + '').trim() == ''){
    exit 400, 'billId不能为空'
}
if(body.materialCode == null || (body.materialCode + '').trim() == ''){
    exit 400, '物料代码不能为空'
}
if(body.batchNo == null || (body.batchNo + '').trim() == ''){
    exit 400, '批次号不能为空'
}
if(body.locationCode == null || (body.locationCode + '').trim() == ''){
    exit 400, '库位号不能为空'
}
if(body.planQuantity == null){
    exit 400, '计划数量不能为空'
}

let master = db.selectOne("select id, wave_no from t_picking_upload where id = #{id}", { id: billId })
if(master == null){
    exit 404, '主表记录不存在'
}

let id = UUID.randomUUID().toString().replace('-', '')
let now = new Date()

db.update("""
    insert into t_picking_upload_detail (id, wave_no, bill_id, material_code, batch_no, location_code, plan_quantity, actual_quantity, status, create_time)
    values (#{id}, #{waveNo}, #{billId}, #{materialCode}, #{batchNo}, #{locationCode}, #{planQuantity}, #{actualQuantity}, #{status}, #{createTime})
""", {
    id: id,
    waveNo: master.wave_no != null ? master.wave_no : master.waveNo,
    billId: billId,
    materialCode: body.materialCode,
    batchNo: body.batchNo,
    locationCode: body.locationCode,
    planQuantity: body.planQuantity,
    actualQuantity: body.actualQuantity != null ? body.actualQuantity : 0,
    status: body.status != null ? body.status : 0,
    createTime: now
})

db.update("update t_picking_upload set detail_count = detail_count + 1 where id = #{id}", { id: billId })

return id
