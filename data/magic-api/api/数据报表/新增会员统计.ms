{
  "properties" : { },
  "id" : "53e0a1a517124b87a202d2233f38fecf",
  "script" : null,
  "groupId" : "6d28d3be72754dc090a8b7e5cc935b53",
  "name" : "新增会员统计",
  "createTime" : 1772517653497,
  "updateTime" : 1772780618591,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/bamboo/xzhy",
  "method" : "GET",
  "parameters" : [ {
    "name" : "startTime",
    "value" : "2026-01-01",
    "description" : null,
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "endTime",
    "value" : "2026-01-31",
    "description" : null,
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": [\n        {\n            \"regOrgId\": 1,\n            \"orgCode\": \"0\",\n            \"orgName\": \"每一角落总部\",\n            \"newVipCount\": 4\n        },\n        {\n            \"regOrgId\": 15,\n            \"orgCode\": \"0000\",\n            \"orgName\": \"每一角落连锁公司\",\n            \"newVipCount\": 120\n        },\n        {\n            \"regOrgId\": 7,\n            \"orgCode\": \"440103\",\n            \"orgName\": \"每一角落广粤店 Canton Place Shop\",\n            \"newVipCount\": 556\n        },\n        {\n            \"regOrgId\": 8,\n            \"orgCode\": \"440106\",\n            \"orgName\": \"每一角落文立方店 Man Lap Fong Shop\",\n            \"newVipCount\": 479\n        },\n        {\n            \"regOrgId\": 5,\n            \"orgCode\": \"440107\",\n            \"orgName\": \"每一角落四海城店 Four Seas City Shop\",\n            \"newVipCount\": 407\n        },\n        {\n            \"regOrgId\": 16,\n            \"orgCode\": \"440110\",\n            \"orgName\": \"每一角落粤海天地店 Yuehai Place Shop\",\n            \"newVipCount\": 505\n        },\n        {\n            \"regOrgId\": 12,\n            \"orgCode\": \"440201\",\n            \"orgName\": \"每一角落东莞星河店 Dynatown Shop\",\n            \"newVipCount\": 78\n        },\n        {\n            \"regOrgId\": 10,\n            \"orgCode\": \"440302\",\n            \"orgName\": \"每一角落广佛新世界店 Canton Place Foshan\",\n            \"newVipCount\": 144\n        },\n        {\n            \"regOrgId\": 6,\n            \"orgCode\": \"460102\",\n            \"orgName\": \"每一角落华彩店 Hua Cai Center Shop\",\n            \"newVipCount\": 288\n        },\n        {\n            \"regOrgId\": 14,\n            \"orgCode\": \"460201\",\n            \"orgName\": \"每一角落夏日百货店 Summer Mall Shop\",\n            \"newVipCount\": 848\n        }\n    ],\n    \"timestamp\": 1772780357996,\n    \"executeTime\": 469\n}",
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
          "name" : "regOrgId",
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
          "name" : "orgCode",
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
          "name" : "orgName",
          "value" : "每一角落总部",
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
          "name" : "newVipCount",
          "value" : "4",
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
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1772780357996",
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
      "value" : "469",
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
    select t2.reg_org_id,bo.org_code,bo.org_name,count(*) as new_vip_count
    from `bamboo-scrm`.vip_info t1 
    inner join vip_operation_info t2 on t1.vip_id =t2.vip_id and t1.opr_id =t2.opr_id 
    inner join base_org bo on bo.org_id =t2.reg_org_id  
    where t1.create_time between #{startTime} and #{endTime}
    and t1.opr_id =1
    group by t2.reg_org_id,bo.org_code,bo.org_name 
    order by bo.org_code 
"""

return db.bamboo.select(sql)