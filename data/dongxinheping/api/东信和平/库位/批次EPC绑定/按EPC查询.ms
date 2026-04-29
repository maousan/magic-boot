{
  "properties" : { },
  "id" : "e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "按EPC查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/query-by-epc",
  "method" : "GET",
  "parameters" : [ {
    "name" : "epc",
    "value" : "",
    "description" : "RFID标签EPC",
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
  "responseBody" : "{\n  \"success\": true,\n  \"data\": null\n}",
  "description" : "根据EPC查询当前绑定的批次ID。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if(epc == null || (epc + '').trim() == ''){
    exit 400, 'EPC不能为空'
}
epc = (epc + '').trim()

let record = db.selectOne("""
    select id, batch_id, epc, bind_time from t_batch_epc
    where status = 1 and epc = #{epc}
""", {epc: epc})

if(record == null){
    return {success: true, data: null}
}

return {
    success: true,
    data: {
        id: record.id,
        batchId: record.batch_id,
        epc: record.epc,
        bindTime: record.bind_time
    }
}