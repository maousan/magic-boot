{
  "properties" : { },
  "id" : "h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3",
  "script" : null,
  "groupId" : "profilelog001",
  "name" : "查询日志",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/logs",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "分页查询当前用户的操作日志",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import cn.dev33.satoken.stp.StpUtil

var userId = StpUtil.getLoginId()

let sql = """
    select
        id,
        api_name as apiName,
        api_path as apiPath,
        api_method as apiMethod,
        user_ip as userIp,
        cost_time as costTime,
        case when cost_time >= 0 then 'success' else 'fail' end as status,
        create_date as createTime
    from sys_oper_log
    where create_by = #{userId}

    <if test="keyword != null and keyword != ''">
        and (api_name like concat('%', #{keyword}, '%') or api_path like concat('%', #{keyword}, '%'))
    </if>

    <if test="startTime != null">
        and create_date >= from_unixtime(#{startTime} / 1000)
    </if>

    <if test="endTime != null">
        and create_date <= from_unixtime(#{endTime} / 1000)
    </if>

    order by create_date desc
"""

return db.page(sql)
