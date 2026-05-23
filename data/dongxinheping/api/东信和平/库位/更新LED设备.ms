{
  "properties" : { },
  "id" : "led202605220003cdefghijklmnopqrstuv",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "更新LED设备",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/led-device",
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
  "requestBody" : "{\n  \"macAddress\": \"3A:69:7A:08:D0:A5\",\n  \"ip\": \"192.168.2.102\",\n  \"remark\": \"1号巷道灯\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "更新LED设备的IP和备注",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let macAddress = body.macAddress
let ip = body.ip
let remark = body.remark

if (macAddress == null || (macAddress + '').trim() == '') {
    exit 0, 'MAC地址不能为空'
}

macAddress = (macAddress + '').trim()

let exists = db.selectValue("""
    select count(1) from t_led_device where mac_address = #{macAddress}
""", { macAddress: macAddress })

if (exists == 0) {
    exit 0, '设备不存在'
}

let updates = []
let params = { macAddress: macAddress }

if (ip != null) {
    ip = (ip + '').trim()
    updates.add('ip = #{ip}')
    params.ip = ip
}
if (remark != null) {
    remark = (remark + '').trim()
    updates.add('remark = #{remark}')
    params.remark = remark
}

if (updates.size() == 0) {
    exit 0, '没有需要更新的字段'
}

let sql = 'update t_led_device set ' + updates.join(', ') + ' where mac_address = #{macAddress}'
db.update(sql, params)

return '更新成功'
