{
  "properties" : { },
  "id" : "ulc20260522001upd1234567890abcdefg",
  "script" : null,
  "groupId" : "ulc20260522001mgmt1234567890abcdef",
  "name" : "修改",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/update",
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
  "requestBody" : "{\n  \"id\": 1,\n  \"userId\": \"USER001\",\n  \"color\": \"GREEN\",\n  \"enabled\": 1,\n  \"remark\": \"备注\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "修改用户灯光颜色映射",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
if(id == null){
    exit 400, 'id不能为空'
}

let exists = db.selectValue("select count(1) from t_user_light_color where id = #{id}", { id: id })
if(exists == 0){
    exit 404, '记录不存在'
}

let sets = []
let params = { id: id }

if(body.userId != null){
    let userId = (body.userId + '').trim()
    let dupCount = db.selectValue("""
        select count(1) from t_user_light_color where user_id = #{userId} and id != #{id}
    """, { userId: userId, id: id })
    if(dupCount > 0){
        exit 400, '该用户ID已被其他记录使用'
    }
    sets.add('user_id = #{userId}')
    params.userId = userId
}
if(body.color != null){
    let color = (body.color + '').trim().toUpperCase()
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
    sets.add('color = #{color}')
    params.color = color
}
if(body.enabled != null){
    sets.add('enabled = #{enabled}')
    params.enabled = body.enabled
}
if(body.remark != null){
    sets.add('remark = #{remark}')
    params.remark = body.remark
}

if(sets.size() == 0){
    exit 400, '没有需要更新的字段'
}

let setSql = ''
for(item in sets){
    if(setSql != ''){
        setSql = setSql + ', '
    }
    setSql = setSql + item
}

db.update("update t_user_light_color set " + setSql + " where id = #{id}", params)

return '修改成功'
