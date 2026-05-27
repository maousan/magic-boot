{
  "properties" : { },
  "id" : "rfidlog001a2b3c4d5e6f7a8b9c0d1e2f",
  "script" : null,
  "groupId" : "rfidmng001a2b3c4d5e6f7a8b9c0d1e2",
  "name" : "查询RFID记录",
  "createTime" : null,
  "updateTime" : 1779853839832,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/records",
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
  "responseBody" : "{\n    \"code\": 500,\n    \"message\": \"系统内部出现错误，错误ID: ead54b61d86f4ed5b5f0549def873123\",\n    \"data\": \"ead54b61d86f4ed5b5f0549def873123\",\n    \"timestamp\": 1779853830682,\n    \"executeTime\": 98\n}",
  "description" : "分页查询RFID接收记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select id, device_id, epc, rssi, read_time
    from rfid_record
    order by read_time desc
"""

return db.page(sql, pageSize, (page-1)*pageSize)
