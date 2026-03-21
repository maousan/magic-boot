{
  "properties" : { },
  "id" : "4399f2eb199b46448aa98682fe792667",
  "script" : null,
  "groupId" : "4f0230049d7e4f39b1e0897cc0f46f9a",
  "name" : "列表",
  "createTime" : null,
  "updateTime" : 1773053015120,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "user:view",
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
  "responseBody" : "{\n    \"code\": 402,\n    \"message\": \"凭证已过期\",\n    \"data\": null,\n    \"timestamp\": 1773047921309,\n    \"executeTime\": null\n}",
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
        "value" : "7",
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
            "value" : "6fa07e9364534ffeb56f9e38bda9de33",
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
            "name" : "username",
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
            "name" : "isLogin",
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
            "name" : "phone",
            "value" : "1111",
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
            "value" : "2024-03-10 22:45:40",
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
            "name" : "officeId",
            "value" : "4c37a80226134bce8bc91c8fc04a7d2f",
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
            "name" : "avatar",
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
            "name" : "officeName",
            "value" : "马小东总公司",
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
            "name" : "roles",
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
      "value" : "1772967544219",
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
      "value" : "28",
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
        distinct
        su.id,
        su.username,
        su.name,
        su.is_login,
        su.phone,
        su.create_date,
        su.office_id,
        su.avatar,
        su.email,
        su.status,
        su.gender,
        so.name office_name,
        (
            select group_concat(sr.name)
            from sys_role sr
            left join sys_user_role sur2 on sr.id = sur2.role_id
            where sr.is_del = 0
            and sur2.user_id = su.id
        ) roles
    from sys_user su
    left join sys_office so on su.office_id = so.id
    left join sys_user_role sur on su.id = sur.user_id

    <where>
        and su.is_del = 0

        <if test="roleIdList != null and roleIdList.size() > 0">
            and sur.role_id in
            <foreach collection="roleIdList" item="item" open="(" separator="," close=")">
                #{item}
            </foreach>
        </if>

        <if test="officeIdList != null and officeIdList.size() > 0">
            and su.office_id in
            <foreach collection="officeIdList" item="item" open="(" separator="," close=")">
                #{item}
            </foreach>
        </if>

        <if test="username != null and username != ''">
            and su.username like concat('%', #{username}, '%')
        </if>

        <if test="name != null and name != ''">
            and su.name like concat('%', #{name}, '%')
        </if>

        <if test="phone != null and phone != ''">
            and su.phone = #{phone}
        </if>

        <if test="isLogin != null">
            and su.is_login = #{isLogin}
        </if>
    </where>
    order by su.sort, su.create_date desc
"""

const page = db.page(sql)

trans.translate(page.list, {
    "status": {
        "target": "statusName",
        "dicts": trans.getDictItems("common_status")
    },
    "gender": {
        "dicts": trans.getDictItems("gender")
    }
})

return page