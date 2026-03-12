{
  "properties" : { },
  "id" : "fad935fc375144af93f0db5405323cc2",
  "script" : null,
  "groupId" : "51e64213e7fe4441b0713dbf0794ee14",
  "name" : "获取数据源列表",
  "createTime" : 1773054391984,
  "updateTime" : 1773057680078,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "该接口需要登录才允许访问",
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"total\": 3,\n        \"list\": [\n            {\n                \"id\": 1,\n                \"name\": \"系统库\",\n                \"dbType\": \"mysql\",\n                \"poolType\": \"Hikari\",\n                \"host\": \"192.168.199.18\",\n                \"port\": 18119,\n                \"databaseName\": \"mf_system\",\n                \"remark\": null,\n                \"createBy\": null,\n                \"createTime\": \"2026-03-09 19:11:40\",\n                \"updateBy\": null,\n                \"updateTime\": \"2026-03-09 19:11:40\",\n                \"dbTypeName\": \"mysql\"\n            },\n            {\n                \"id\": 2,\n                \"name\": \"测试商城\",\n                \"dbType\": \"mysql\",\n                \"poolType\": \"Hikari\",\n                \"host\": \"192.168.199.18\",\n                \"port\": 18119,\n                \"databaseName\": \"email\",\n                \"remark\": null,\n                \"createBy\": null,\n                \"createTime\": \"2026-03-09 19:11:40\",\n                \"updateBy\": null,\n                \"updateTime\": \"2026-03-09 19:11:40\",\n                \"dbTypeName\": \"mysql\"\n            },\n            {\n                \"id\": 3,\n                \"name\": \"认证库\",\n                \"dbType\": \"mysql\",\n                \"poolType\": \"Hikari\",\n                \"host\": \"192.168.199.18\",\n                \"port\": 18119,\n                \"databaseName\": \"mf_oauth\",\n                \"remark\": \"认证中心数据库\",\n                \"createBy\": null,\n                \"createTime\": \"2026-03-09 19:11:40\",\n                \"updateBy\": null,\n                \"updateTime\": \"2026-03-09 19:11:40\",\n                \"dbTypeName\": \"mysql\"\n            }\n        ]\n    },\n    \"timestamp\": 1773057470621,\n    \"executeTime\": 22\n}",
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
        "value" : "3",
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
            "dataType" : "Integer",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          }, {
            "name" : "connectionName",
            "value" : "系统库",
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
            "name" : "dbType",
            "value" : "mysql",
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
            "name" : "poolType",
            "value" : "Hikari",
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
            "name" : "host",
            "value" : "192.168.199.18",
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
            "name" : "port",
            "value" : "18119",
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
            "name" : "databaseName",
            "value" : "mf_system",
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
            "name" : "createBy",
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
            "name" : "createTime",
            "value" : "2026-03-09 19:11:40",
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
            "name" : "updateBy",
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
            "name" : "updateTime",
            "value" : "2026-03-09 19:11:40",
            "description" : "",
            "required" : false,
            "dataType" : "String",
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
      "value" : "1773054716214",
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
      "value" : "27",
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
    select *
    from sys_database
"""

const page = db.page(sql)

trans.translate(page.list, {
    "dbType": {
        "dicts": trans.getDictItems("db_type")
    },
    "status": {
        "dicts": trans.getDictItems("common_status")
    }
})

return page