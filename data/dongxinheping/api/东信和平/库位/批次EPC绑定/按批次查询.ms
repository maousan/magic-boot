{
  "properties" : { },
  "id" : "d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "按批次查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/query-by-batch",
  "method" : "GET",
  "parameters" : [ {
    "name" : "batchId",
    "value" : "",
    "description" : "业务批次ID",
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
  "description" : "根据批次ID查询当前绑定的EPC。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if(batchId == null || (batchId + '').trim() == ''){
    exit 400, '批次ID不能为空'
}
batchId = (batchId + '').trim()

let record = db.selectOne("""
    select id, batch_id, epc, bind_time from t_batch_epc
    where status = 1 and batch_id = #{batchId}
""", {batchId: batchId})

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