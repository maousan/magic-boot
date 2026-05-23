{
  "properties" : { },
  "id" : "lc20260522003bcdefghijklmnopqrsv",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "更新LED颜色",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/led-color",
  "method" : "PUT",
  "parameters" : [ ],
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
  "requestBody" : "{\n  \"id\": 1,\n  \"code\": \"01\",\n  \"color\": \"RED\",\n  \"remark\": \"红色控制码\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "更新LED颜色与控制指令映射",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
let code = body.code
let color = body.color
let remark = body.remark

if (id == null) {
    exit 0, 'ID不能为空'
}

let exists = db.selectValue("""
    select count(1) from t_led_color where id = #{id}
""", { id: id })

if (exists == 0) {
    exit 0, '记录不存在'
}

let updates = []
let params = { id: id }

if (code != null) {
    code = (code + '').trim()
    updates.add('code = #{code}')
    params.code = code
}
if (color != null) {
    color = (color + '').trim()
    updates.add('color = #{color}')
    params.color = color
}
if (remark != null) {
    remark = (remark + '').trim()
    updates.add('remark = #{remark}')
    params.remark = remark
}

if (updates.size() == 0) {
    exit 0, '没有需要更新的字段'
}

let sql = 'update t_led_color set ' + updates.join(', ') + ' where id = #{id}'
db.update(sql, params)

return '更新成功'