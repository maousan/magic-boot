{
  "properties" : { },
  "id" : "kk111haoho315ydtm2gvs2o6n5o0mcyp",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "LED绑定列表查询",
  "createTime" : null,
  "updateTime" : 1779987530282,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/led-mapping/list",
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
  }, {
    "name" : "locationCode",
    "value" : "",
    "description" : "库位编码（模糊查询）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "ledId",
    "value" : "",
    "description" : "LED设备ID（模糊查询）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "color",
    "value" : "",
    "description" : "灯色（RED/YELLOW/GREEN）",
    "required" : false,
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"total\": 0,\n        \"list\": null\n    },\n    \"timestamp\": 1779419893015,\n    \"executeTime\": 130\n}",
  "description" : "分页查询库位-LED绑定记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select id, location_code, led_id, color, status, create_time
    from t_location_led
    <where>
        <if test="locationCode != null and locationCode != ''">
            and location_code like concat('%', #{locationCode}, '%')
        </if>
        <if test="ledId != null and ledId != ''">
            and led_id like concat('%', #{ledId}, '%')
        </if>
        <if test="color != null and color != ''">
            and color = #{color}
        </if>
    </where>
    order by location_code desc
"""

return db.page(sql, pageSize, (page-1)*pageSize)
