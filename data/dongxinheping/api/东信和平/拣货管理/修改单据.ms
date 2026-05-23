{
  "properties" : { },
  "id" : "pku202605220004upd1234567890abcdef04",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "修改单据",
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
  "requestBody" : "{\n  \"id\": \"xxx\",\n  \"waveNo\": \"WV202605220001\",\n  \"userId\": \"USER001\",\n  \"userName\": \"张三\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "修改拣货单主表记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let id = body.id
if(id == null || (id + '').trim() == ''){
    exit 400, 'id不能为空'
}

let exists = db.selectValue("""
    select count(1) from t_picking_upload where id = #{id}
""", { id: id })

if(exists == 0){
    exit 404, '记录不存在'
}

let sets = []
let params = { id: id }

if(body.waveNo != null){
    sets.add('wave_no = #{waveNo}')
    params.waveNo = body.waveNo
}
if(body.userId != null){
    sets.add('user_id = #{userId}')
    params.userId = body.userId
}
if(body.userName != null){
    sets.add('user_name = #{userName}')
    params.userName = body.userName
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

db.update("update t_picking_upload set " + setSql + " where id = #{id}", params)

return '修改成功'
