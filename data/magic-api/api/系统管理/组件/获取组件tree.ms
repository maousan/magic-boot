{
  "properties" : { },
  "id" : "8ba14d61082e4ba69324fdc9de080f38",
  "script" : null,
  "groupId" : "6f106ebdee21489db34b956f7770ff03",
  "name" : "获取组件tree",
  "createTime" : null,
  "updateTime" : 1772957257510,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/tree",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "component:tree",
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"list\": [\n            {\n                \"id\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                \"name\": \"system\",\n                \"pid\": \"0\",\n                \"isGroup\": 1,\n                \"children\": [\n                    {\n                        \"id\": \"c2e46f717eba4fa69f96358dc1587b77\",\n                        \"name\": \"configure\",\n                        \"pid\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                        \"isGroup\": 1,\n                        \"children\": [\n                            {\n                                \"id\": \"b94688b24b724a50945b55899d6199bc\",\n                                \"name\": \"configure-list\",\n                                \"pid\": \"c2e46f717eba4fa69f96358dc1587b77\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            }\n                        ]\n                    },\n                    {\n                        \"id\": \"d17f4964dc664a4bafbd7af6a7e0596d\",\n                        \"name\": \"dict\",\n                        \"pid\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                        \"isGroup\": 1,\n                        \"children\": [\n                            {\n                                \"id\": \"c20b952d5b2747a3bc393bdaa7e86ef1\",\n                                \"name\": \"dict-items\",\n                                \"pid\": \"d17f4964dc664a4bafbd7af6a7e0596d\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"28e3f82164de4bbf98a342c2a94da076\",\n                                \"name\": \"dict-list\",\n                                \"pid\": \"d17f4964dc664a4bafbd7af6a7e0596d\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            }\n                        ]\n                    },\n                    {\n                        \"id\": \"fc19e04fa384462d9948c18092f30287\",\n                        \"name\": \"menu\",\n                        \"pid\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                        \"isGroup\": 1,\n                        \"children\": [\n                            {\n                                \"id\": \"c0ad38bca2164af08f1c2b2bc90245df\",\n                                \"name\": \"menu-form\",\n                                \"pid\": \"fc19e04fa384462d9948c18092f30287\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"ef58200e5ca649f8a3c6f1d3ad66f250\",\n                                \"name\": \"menu-icons\",\n                                \"pid\": \"fc19e04fa384462d9948c18092f30287\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"63227c407c5d40f98df2486390a6f841\",\n                                \"name\": \"menu-list\",\n                                \"pid\": \"fc19e04fa384462d9948c18092f30287\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            }\n                        ]\n                    },\n                    {\n                        \"id\": \"88c17402-d92a-11ee-9675-c2b02ed3977b\",\n                        \"name\": \"monitor\",\n                        \"pid\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                        \"isGroup\": 1,\n                        \"children\": [\n                            {\n                                \"id\": \"c64a8b964c6e40dfbe4c3908fdea9553\",\n                                \"name\": \"druid\",\n                                \"pid\": \"88c17402-d92a-11ee-9675-c2b02ed3977b\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"9809888c-d92a-11ee-9675-c2b02ed3977b\",\n                                \"name\": \"login-log\",\n                                \"pid\": \"88c17402-d92a-11ee-9675-c2b02ed3977b\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"fd89dd1d66ae429689bdd978162b4caf\",\n                                \"name\": \"online-user\",\n                                \"pid\": \"88c17402-d92a-11ee-9675-c2b02ed3977b\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"a3bdf806e89b4a5a9d9436b012d57ce9\",\n                                \"name\": \"oper-log\",\n                                \"pid\": \"88c17402-d92a-11ee-9675-c2b02ed3977b\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            }\n                        ]\n                    },\n                    {\n                        \"id\": \"e9cf6dae-d929-11ee-9675-c2b02ed3977b\",\n                        \"name\": \"office\",\n                        \"pid\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                        \"isGroup\": 1,\n                        \"children\": [\n                            {\n                                \"id\": \"fe533026-d929-11ee-9675-c2b02ed3977b\",\n                                \"name\": \"office-list\",\n                                \"pid\": \"e9cf6dae-d929-11ee-9675-c2b02ed3977b\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            }\n                        ]\n                    },\n                    {\n                        \"id\": \"9c302653077d47dc9374ba48b9fda579\",\n                        \"name\": \"role\",\n                        \"pid\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                        \"isGroup\": 1,\n                        \"children\": [\n                            {\n                                \"id\": \"e50d9d844de247fc825c8f4d4509da79\",\n                                \"name\": \"role-assign-permissions\",\n                                \"pid\": \"9c302653077d47dc9374ba48b9fda579\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"8c426b7d397b4ffeb4c9f89b4c318fbb\",\n                                \"name\": \"role-list\",\n                                \"pid\": \"9c302653077d47dc9374ba48b9fda579\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            }\n                        ]\n                    },\n                    {\n                        \"id\": \"fc061efbc39948d59aafc3905591c935\",\n                        \"name\": \"user\",\n                        \"pid\": \"29d82244-d928-11ee-9675-c2b02ed3977b\",\n                        \"isGroup\": 1,\n                        \"children\": [\n                            {\n                                \"id\": \"d31791c8a14f4e648f3d01d5171c817c\",\n                                \"name\": \"user-center\",\n                                \"pid\": \"fc061efbc39948d59aafc3905591c935\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"c99bf0b5780e4f1996211c70e56f49f7\",\n                                \"name\": \"user-form\",\n                                \"pid\": \"fc061efbc39948d59aafc3905591c935\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            },\n                            {\n                                \"id\": \"bc38eb5038ce44aab4bd586fb32d79bb\",\n                                \"name\": \"user-list\",\n                                \"pid\": \"fc061efbc39948d59aafc3905591c935\",\n                                \"isGroup\": 0,\n                                \"children\": []\n                            }\n                        ]\n                    }\n                ]\n            }\n        ],\n        \"total\": 1\n    },\n    \"timestamp\": 1772782853227,\n    \"executeTime\": 10\n}",
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
            "value" : "29d82244-d928-11ee-9675-c2b02ed3977b",
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
            "value" : "system",
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
            "name" : "pid",
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
            "name" : "isGroup",
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
            "name" : "children",
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
                "value" : "c2e46f717eba4fa69f96358dc1587b77",
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
                "value" : "configure",
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
                "name" : "pid",
                "value" : "29d82244-d928-11ee-9675-c2b02ed3977b",
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
                "name" : "isGroup",
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
                "name" : "children",
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
                    "value" : "b94688b24b724a50945b55899d6199bc",
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
                    "value" : "configure-list",
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
                    "name" : "pid",
                    "value" : "c2e46f717eba4fa69f96358dc1587b77",
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
                    "name" : "isGroup",
                    "value" : "0",
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
                    "name" : "children",
                    "value" : "",
                    "description" : "",
                    "required" : false,
                    "dataType" : "Array",
                    "type" : null,
                    "defaultValue" : null,
                    "validateType" : "",
                    "error" : "",
                    "expression" : "",
                    "children" : [ ]
                  } ]
                } ]
              } ]
            } ]
          } ]
        } ]
      }, {
        "name" : "total",
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
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1772782813633",
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
      "value" : "16",
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
        dc.id,
        (case when dc.remark is not null and dc.remark != '' then concat(dc.name,'(',dc.remark, ')') else dc.name end) name,
        dc.pid,
        (case when dc.type = 0 then 1 else 0 end) is_group
    from sys_dynamic_component dc
    where dc.is_del = 0 
    order by dc.name
"""

var toTree = (list,pid) => select t.*,toTree(list,t.id) children from list t where t.pid = pid

var list = toTree(db.select(sql),'0')

return {
    list: list,
    total: list.getLength()
}