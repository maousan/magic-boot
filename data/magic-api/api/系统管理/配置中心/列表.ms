{
  "properties" : { },
  "id" : "3add3a4fbc924d00911677c8edac01f7",
  "script" : null,
  "groupId" : "0295f2b4af9145f5a0ea29fa4b797214",
  "name" : "列表",
  "createTime" : null,
  "updateTime" : 1772977805385,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "configure:view",
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"total\": 2,\n        \"list\": [\n            {\n                \"id\": \"5646fe2ba5674a5e89e74fcd65e8c42d\",\n                \"configureCondition\": null,\n                \"configureKey\": \"super-password\",\n                \"configureValue\": \"1xx\",\n                \"configureName\": \"通用密码\",\n                \"configureType\": \"1\",\n                \"remark\": \"万能登录密码，所有用户都能进行登录\",\n                \"createDate\": \"2022-05-21 12:41:51\"\n            },\n            {\n                \"id\": \"a2cac5d9036b41ecaed4496b2f40085c\",\n                \"configureCondition\": \"\",\n                \"configureKey\": \"verification-code.enable\",\n                \"configureValue\": \"false\",\n                \"configureName\": \"是否验证“验证码”\",\n                \"configureType\": \"0\",\n                \"remark\": \"1、开发模式下验证码可以不用输入，即可登录\",\n                \"createDate\": \"2022-05-21 11:29:24\"\n            }\n        ]\n    },\n    \"timestamp\": 1772956809876,\n    \"executeTime\": 10\n}",
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
        "value" : "2",
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
            "value" : "5646fe2ba5674a5e89e74fcd65e8c42d",
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
            "name" : "configureCondition",
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
            "name" : "configureKey",
            "value" : "super-password",
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
            "name" : "configureValue",
            "value" : "1xx",
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
            "name" : "configureName",
            "value" : "通用密码",
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
            "name" : "configureType",
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
            "name" : "remark",
            "value" : "万能登录密码，所有用户都能进行登录",
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
            "value" : "2022-05-21 12:41:51",
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
      "value" : "1772956809876",
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
      "value" : "10",
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
        configure_condition,
        configure_key,
        configure_value,
        configure_name,
        configure_type,
        remark,
        create_date
    from sys_configure

    <where>
        is_del = 0

        <if test="configureCondition != null and configureCondition != ''">
            and configure_condition like concat('%', #{configureCondition}, '%')
        </if>

        <if test="configureKey != null and configureKey != ''">
            and configure_key like concat('%', #{configureKey}, '%')
        </if>

        <if test="configureValue != null and configureValue != ''">
            and configure_value like concat('%', #{configureValue}, '%')
        </if>
    </where>

    order by create_date desc
"""
return db.page(sql)