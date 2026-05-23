{
  "properties" : { },
  "id" : "lc20260522002bcdefghijklmnopqrsu",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "新增LED颜色",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/led-color",
  "method" : "POST",
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
  "requestBody" : "{\n  \"code\": \"01\",\n  \"color\": \"RED\",\n  \"remark\": \"红色控制码\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "新增LED颜色与控制指令映射",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let code = body.code
let color = body.color
let remark = body.remark

if (code == null || (code + '').trim() == '') {
    exit 0, '颜色代码不能为空'
}
if (color == null || (color + '').trim() == '') {
    exit 0, '颜色不能为空'
}

code = (code + '').trim()
color = (color + '').trim()
if (remark != null) {
    remark = (remark + '').trim()
}

let exists = db.selectValue("""
    select count(1) from t_led_color where code = #{code}
""", { code: code })

if (exists > 0) {
    exit 0, '该颜色代码已存在'
}

db.update("""
    insert into t_led_color (code, color, remark)
    values (#{code}, #{color}, #{remark})
""", {
    code: code,
    color: color,
    remark: remark
})

return '添加成功'