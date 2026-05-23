{
  "properties" : { },
  "id" : "led202605220002bcdefghijklmnopqrst",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "新增LED设备",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/led-device",
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
  "requestBody" : "{\n  \"macAddress\": \"3A:69:7A:08:D0:A5\",\n  \"ip\": \"192.168.2.102\",\n  \"remark\": \"1号巷道灯\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "新增LED设备记录",
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
if (ip == null || (ip + '').trim() == '') {
    exit 0, 'IP地址不能为空'
}

macAddress = (macAddress + '').trim()
ip = (ip + '').trim()
if (remark != null) {
    remark = (remark + '').trim()
}

let exists = db.selectValue("""
    select count(1) from t_led_device where mac_address = #{macAddress}
""", { macAddress: macAddress })

if (exists > 0) {
    exit 0, '该MAC地址已存在'
}

db.update("""
    insert into t_led_device (mac_address, ip, remark)
    values (#{macAddress}, #{ip}, #{remark})
""", {
    macAddress: macAddress,
    ip: ip,
    remark: remark
})

return '添加成功'
