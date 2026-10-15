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
    select u.id, u.username, u.name, u.is_login, u.create_date
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

// 2026-10-15 改因：角色列改为真实绑定角色（多角色顿号连接，无绑定兜底"运营"）；
// group_concat 子查询 magic-api SQL 解析不支持，改脚本内组装
var codeMap = {}
var nameMap = {}
var userIds = []
for (row in pageResult.list) {
    userIds.add(row.id)
}
if (userIds.size() > 0) {
    var bindRows = db.select("""
        select sur.user_id, r.code, r.name
        from sys_user_role sur
        join sys_role r on r.id = sur.role_id
        where sur.user_id in (#{userIds}) and r.is_del = 0
        order by r.create_date
    """, { userIds: userIds })
    for (b in bindRows) {
        var bindUserId = b.userId
        if (bindUserId == null) {
            bindUserId = b.user_id
        }
        if (codeMap[bindUserId] == null) {
            codeMap[bindUserId] = []
            nameMap[bindUserId] = []
        }
        codeMap[bindUserId].add(b.code)
        nameMap[bindUserId].add(b.name)
    }
}
for (row in pageResult.list) {
    var codes = codeMap[row.id]
    if (codes == null || codes.size() == 0) {
        row.role = '运营'
        row.roleCodes = []
    } else {
        var displayName = ''
        var names = nameMap[row.id]
        for (n in names) {
            displayName = displayName == '' ? n : displayName + '、' + n
        }
        row.role = displayName
        row.roleCodes = codes
    }
}
return pageResult
