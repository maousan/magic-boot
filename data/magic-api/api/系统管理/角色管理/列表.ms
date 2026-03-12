{
  "properties" : { },
  "id" : "9a204a5c8a8243f7b07e842f87d4d65b",
  "script" : null,
  "groupId" : "89130d496f6f467c88b22ae4a7f688eb",
  "name" : "列表",
  "createTime" : null,
  "updateTime" : 1773052431733,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "role:view",
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"total\": 6,\n        \"list\": [\n            {\n                \"id\": \"1\",\n                \"name\": \"管理员\",\n                \"readonly\": \"1\",\n                \"code\": \"admin\",\n                \"permission\": \"0\",\n                \"remark\": \"管理员222\",\n                \"createDate\": null\n            },\n            {\n                \"id\": \"1d183eaec667423fa9adb20e24356a86\",\n                \"name\": \"全部\",\n                \"readonly\": \"0\",\n                \"code\": \"qb\",\n                \"permission\": \"0\",\n                \"remark\": null,\n                \"createDate\": \"2022-01-16 13:56:10\"\n            },\n            {\n                \"id\": \"3a408f5157c841ea8884ade4fa56d0f4\",\n                \"name\": \"自定义\",\n                \"readonly\": \"0\",\n                \"code\": \"zdy\",\n                \"permission\": \"1\",\n                \"remark\": null,\n                \"createDate\": \"2022-01-16 13:55:22\"\n            },\n            {\n                \"id\": \"d71c32ace3bb4d959a652ee99ecd1b26\",\n                \"name\": \"测试2\",\n                \"readonly\": \"0\",\n                \"code\": \"test2\",\n                \"permission\": \"1\",\n                \"remark\": null,\n                \"createDate\": \"2022-02-04 16:39:26\"\n            },\n            {\n                \"id\": \"e3448fc792ca463a95a34baf6f089dcd\",\n                \"name\": \"本级及子级\",\n                \"readonly\": \"0\",\n                \"code\": \"bjjzj\",\n                \"permission\": \"2\",\n                \"remark\": null,\n                \"createDate\": \"2022-01-16 13:55:45\"\n            },\n            {\n                \"id\": \"e619e4b665de42acbca1665fec150634\",\n                \"name\": \"本级\",\n                \"readonly\": \"0\",\n                \"code\": \"bj\",\n                \"permission\": \"3\",\n                \"remark\": null,\n                \"createDate\": \"2022-01-16 13:55:59\"\n            }\n        ]\n    },\n    \"timestamp\": 1772977776310,\n    \"executeTime\": 20\n}",
  "description" : "",
  "requestBodyDefinition" : {
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
    "children" : [ ]
  },
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
      "value" : "200",
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
      "value" : "success",
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
        "name" : "total",
        "value" : "6",
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
        "name" : "list",
        "value" : "",
        "description" : "",
        "required" : false,
        "dataType" : "Array",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ {
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
            "name" : "id",
            "value" : "1",
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
            "name" : "name",
            "value" : "管理员",
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
            "name" : "readonly",
            "value" : "1",
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
            "name" : "code",
            "value" : "admin",
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
            "name" : "permission",
            "value" : "0",
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
            "name" : "remark",
            "value" : "管理员222",
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
            "name" : "createDate",
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
        } ]
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1772977776310",
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
      "value" : "20",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
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
    select 
        id,
        name,
        readonly,
        code,
        status,
        permission,
        remark,
        sort,
        create_date
    from sys_role

    <where>
        is_del = 0
        <if test="name != null and name != ''">
            and name like concat('%', #{name}, '%')
        </if>
    </where>

    order by sort, create_date desc
"""

const page = db.page(sql)

trans.translate(page.list, {
    'status': {
        'dicts': trans.getDictItems('common_status')
    }
})

return page