{
  "properties" : { },
  "id" : "95cdf93be8814d77a909b57688ba04c4",
  "script" : null,
  "groupId" : "6d28d3be72754dc090a8b7e5cc935b53",
  "name" : "有效会员统计",
  "createTime" : 1772517795007,
  "updateTime" : 1772780609724,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/bamboo/yxhy",
  "method" : "GET",
  "parameters" : [ {
    "name" : "reportDate",
    "value" : "202601",
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": [\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"440103\",\n            \"orgName\": \"广粤天地店\",\n            \"activeMemberCount\": 1267\n        },\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"440106\",\n            \"orgName\": \"广州文立方\",\n            \"activeMemberCount\": 377\n        },\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"440107\",\n            \"orgName\": \"广州四海城\",\n            \"activeMemberCount\": 117\n        },\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"440110\",\n            \"orgName\": \"广州粤海天地\",\n            \"activeMemberCount\": 265\n        },\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"440201\",\n            \"orgName\": \"东莞星河店\",\n            \"activeMemberCount\": 355\n        },\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"440302\",\n            \"orgName\": \"广佛新世界店\",\n            \"activeMemberCount\": 360\n        },\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"460102\",\n            \"orgName\": \"海口华彩店\",\n            \"activeMemberCount\": 235\n        },\n        {\n            \"reportDate\": \"202601\",\n            \"orgCode\": \"460201\",\n            \"orgName\": \"三亚夏日百货店\",\n            \"activeMemberCount\": 869\n        }\n    ],\n    \"timestamp\": 1772529396146,\n    \"executeTime\": 11310\n}",
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
// import com.dtflys.forest.Forest;

let sql = """
    select report_date,org_code,org_name,active_member_count 
    from `bamboo-scrm`.view_active_member_summary 
    where report_date = #{reportDate}
"""

// Forest.get("http://localhost:8089/swagger-ui.html").execute()

return db.myjl.select(sql)