{
  "properties": {},
  "id": "51ba7352f65e46bca9d06be933747680",
  "script": null,
  "groupId": "42a6420987574453b6f134d489a58945",
  "name": "列表",
  "createTime": null,
  "updateTime": 1790062297434,
  "lock": "0",
  "createBy": null,
  "updateBy": null,
  "path": "/list",
  "method": "GET",
  "parameters": [
    {
      "name": "page",
      "value": "1",
      "description": "页码",
      "required": false,
      "dataType": "Integer",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "pageSize",
      "value": "20",
      "description": "每页条数",
      "required": false,
      "dataType": "Integer",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "username",
      "value": null,
      "description": "用户名（模糊）",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    },
    {
      "name": "name",
      "value": null,
      "description": "姓名（模糊）",
      "required": false,
      "dataType": "String",
      "type": null,
      "defaultValue": null,
      "validateType": null,
      "error": null,
      "expression": null,
      "children": null
    }
  ],
  "options": [],
  "requestBody": "",
  "headers": [],
  "paths": [],
  "responseBody": "",
  "description": ""
}
================================
let sql = """
    select u.id, u.username, u.name, u.is_login, u.create_date,
        (select count(1) from sys_user_role sur
          join sys_role r on r.id = sur.role_id
          where sur.user_id = u.id and r.code = 'admin' and r.is_del = 0) as admin_binding
    from sys_user u
    <where>
    u.is_del = 0
    <if test="username != null and username != ''">
    and u.username like concat('%', #{username}, '%')
    </if>
    <if test="name != null and name != ''">
    and u.name like concat('%', #{name}, '%')
    </if>
    </where>
    order by u.create_date desc
"""
var pageResult = db.page(sql, pageSize, (page - 1) * pageSize)
for (row in pageResult.list) {
    row.role = row.adminBinding > 0 ? 'admin' : 'ops'
    row.remove('adminBinding')
}
return pageResult
