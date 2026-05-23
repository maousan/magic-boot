{
  "properties" : { },
  "id" : "pku202605220003add1234567890abcdef03",
  "script" : null,
  "groupId" : "pk202605220001mgmt1234567890abcdef",
  "name" : "新增单据",
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
  "requestBody" : "{\n  \"waveNo\": \"WV202605220001\",\n  \"userId\": \"USER001\",\n  \"userName\": \"张三\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "",
  "description" : "新增拣货单主表记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import java.util.UUID

let waveNo = body.waveNo
let userId = body.userId
let userName = body.userName

if(waveNo == null || (waveNo + '').trim() == ''){
    exit 400, '波次号不能为空'
}
if(userId == null || (userId + '').trim() == ''){
    exit 400, '用户ID不能为空'
}

waveNo = (waveNo + '').trim()
userId = (userId + '').trim()

let id = UUID.randomUUID().toString().replace('-', '')
let now = new Date()

db.update("""
    insert into t_picking_upload (id, wave_no, user_id, user_name, detail_count, update_time, create_time)
    values (#{id}, #{waveNo}, #{userId}, #{userName}, 0, #{updateTime}, #{createTime})
""", {
    id: id,
    waveNo: waveNo,
    userId: userId,
    userName: userName != null ? userName : '',
    updateTime: now.getTime(),
    createTime: now
})

return id
