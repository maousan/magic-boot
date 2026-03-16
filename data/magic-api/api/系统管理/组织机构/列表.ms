{
  "properties" : { },
  "id" : "f7fa03a5d8b64dbaa39387c185a21adb",
  "script" : null,
  "groupId" : "8295fc13678d4144bf7363c465247a50",
  "name" : "列表",
  "createTime" : 1646490239494,
  "updateTime" : 1773384826055,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/tree",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "office:view",
    "description" : "允许拥有该权限的访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\n\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"list\": [\n            {\n                \"id\": \"4c37a80226134bce8bc91c8fc04a7d2f\",\n                \"name\": \"马小东总公司\",\n                \"pid\": \"0\",\n                \"type\": \"2\",\n                \"sort\": 10,\n                \"code\": \"mxd\",\n                \"createDate\": \"2022-01-09 21:59:34\",\n                \"children\": [\n                    {\n                        \"id\": \"81b5493cdb31477f9ceea85b2cd08f15\",\n                        \"name\": \"内蒙分公司\",\n                        \"pid\": \"4c37a80226134bce8bc91c8fc04a7d2f\",\n                        \"type\": \"2\",\n                        \"sort\": 10,\n                        \"code\": \"nm\",\n                        \"createDate\": \"2022-01-09 21:59:47\",\n                        \"children\": [\n                            {\n                                \"id\": \"a8b62a271c2a4492bd5c0653cac8f33d\",\n                                \"name\": \"扎兰屯办事处\",\n                                \"pid\": \"81b5493cdb31477f9ceea85b2cd08f15\",\n                                \"type\": \"2\",\n                                \"sort\": 10,\n                                \"code\": \"zlt\",\n                                \"createDate\": \"2022-01-16 13:15:46\",\n                                \"children\": []\n                            }\n                        ]\n                    },\n                    {\n                        \"id\": \"31b365008f994e93850186126fce87cb\",\n                        \"name\": \"东北分公司\",\n                        \"pid\": \"4c37a80226134bce8bc91c8fc04a7d2f\",\n                        \"type\": \"2\",\n                        \"sort\": 20,\n                        \"code\": \"db\",\n                        \"createDate\": \"2022-01-09 22:12:11\",\n                        \"children\": []\n                    }\n                ]\n            }\n        ],\n        \"total\": 1\n    },\n    \"timestamp\": 1772881017234,\n    \"executeTime\": 16\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "code",
      "value" : "402",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "凭证已过期",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "null",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "timestamp",
      "value" : "1645694060610",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "null",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
let sql = """
    select id,name,pid,type,status,sort,code,remark,create_date from sys_office 
    <where>
    and is_del = 0
    <if test="name != null and name != ''">
    and name like concat('%', #{name}, '%')
    </if>
    </where>
    order by sort
"""
var toTree = (list, pid) => select t.*,
    toTree(list, t.id) children from list t where t.pid = pid

const rows = db.select(sql)
trans.translate(rows, {
    "status": {
        "target":"statusName",
        "dicts": [{
            "value": 1,
            "label": "启用"
        }, {
            "value": 0,
            "label": "禁用"
        }]
    }
})
var list = toTree(rows, '0')

return {
    list: list,
    total: list.getLength()
}