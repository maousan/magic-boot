{
  "properties" : { },
  "id" : "ulc20260522001add1234567890abcdefg",
  "script" : null,
  "groupId" : "ulc20260522001mgmt1234567890abcdef",
  "name" : "新增",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/add",
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
  "requestBody" : "{\n  \"userId\": \"USER001\",\n  \"color\": \"RED\",\n  \"enabled\": 1,\n  \"remark\": \"\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "新增用户灯光颜色映射",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let userId = body.userId
let color = body.color

if(userId == null || (userId + '').trim() == ''){
    exit 400, '用户ID不能为空'
}
if(color == null || (color + '').trim() == ''){
    exit 400, '颜色不能为空'
}

userId = (userId + '').trim()
color = (color + '').trim().toUpperCase()

let validColors = ['RED', 'GREEN', 'BLUE', 'YELLOW', 'CYAN']
let colorValid = false
for(c in validColors){
    if(c == color){
        colorValid = true
    }
}
if(!colorValid){
    exit 400, '颜色仅支持 RED/GREEN/BLUE/YELLOW/CYAN'
}

let exists = db.selectValue("""
    select count(1) from t_user_light_color where user_id = #{userId}
""", { userId: userId })

if(exists > 0){
    exit 400, '该用户ID已存在映射'
}

db.update("""
    insert into t_user_light_color (user_id, color, enabled, remark)
    values (#{userId}, #{color}, #{enabled}, #{remark})
""", {
    userId: userId,
    color: color,
    enabled: body.enabled != null ? body.enabled : 1,
    remark: body.remark != null ? body.remark : ''
})

return '新增成功'
