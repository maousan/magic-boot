{
  "properties" : { },
  "id" : "ulc20260522001list1234567890abcdef",
  "script" : null,
  "groupId" : "ulc20260522001mgmt1234567890abcdef",
  "name" : "列表查询",
  "createTime" : null,
  "updateTime" : 1790058251093,
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
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "分页查询用户灯光颜色映射",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select id, user_id, color, case when enabled = 1 then 1 else 0 end as enabled, remark,
           create_time, update_time
    from t_user_light_color
    <where>
        <if test="userId != null and userId != ''">
            and user_id like concat('%', #{userId}, '%')
        </if>
    </where>
    order by update_time desc
"""

return db.page(sql, pageSize, (page-1)*pageSize)
