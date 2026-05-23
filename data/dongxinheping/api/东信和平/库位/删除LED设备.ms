{
  "properties" : { },
  "id" : "led202605220004defghijklmnopqrstuvw",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "删除LED设备",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/led-device",
  "method" : "DELETE",
  "parameters" : [ {
    "name" : "macAddress",
    "value" : null,
    "description" : "设备MAC地址",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": null\n}",
  "description" : "根据MAC地址删除LED设备记录",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
if (macAddress == null || (macAddress + '').trim() == '') {
    exit 0, 'MAC地址不能为空'
}

macAddress = (macAddress + '').trim()

let rows = db.update("""
    delete from t_led_device where mac_address = #{macAddress}
""", { macAddress: macAddress })

if (rows == 0) {
    exit 0, '设备不存在'
}

return '删除成功'
