{
  "properties" : { },
  "id" : "pku202605220001list1234567890abcdef01",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "列表查询",
  "createTime" : null,
  "updateTime" : 1779426476623,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
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
    "name" : "waveNo",
    "value" : "",
    "description" : "波次号（模糊查询）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "userId",
    "value" : "",
    "description" : "用户ID（模糊查询）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "userName",
    "value" : "",
    "description" : "用户姓名（模糊查询）",
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
  "responseBody" : null,
  "description" : "分页查询拣货单列表",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select id, wave_no, user_id, user_name,
           detail_count, update_time, create_time
    from t_picking_upload
    <where>
        <if test="waveNo != null and waveNo != ''">
            and wave_no like concat('%', #{waveNo}, '%')
        </if>
        <if test="userId != null and userId != ''">
            and user_id like concat('%', #{userId}, '%')
        </if>
        <if test="userName != null and userName != ''">
            and user_name like concat('%', #{userName}, '%')
        </if>
    </where>
    order by create_time desc
"""

return db.page(sql, pageSize, (page-1)*pageSize)
