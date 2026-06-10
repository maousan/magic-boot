{
  "properties" : { },
  "id" : "0fb7ff33e5f1440ca0269c84f4b3f5d1",
  "script" : null,
  "groupId" : "628f77553ba14e47ae84391269b008bb",
  "name" : "根据库位码亮巷道灯",
  "createTime" : 1777000000000,
  "updateTime" : 1780578652621,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/turnOnAisleLedByLotNo",
  "description" : "传入库位码，自动根据绑定关系查设备IP与颜色命令并调用巷道灯亮灯。",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "data",
    "value" : null,
    "description" : "入参对象，至少包含lotNo，可选timeoutMs",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import '@/utils/parseLot' as parseLot
import '@/led/controlAisleLedDevice' as controlAisleLedDevice
import log


if (data == null) {
    return { code: 400, message: 'data不能为空' }
}

let lotNo = data.lotNo
let timeoutMs = data.timeoutMs
let mode = data.mode
let waitResponse = data.waitResponse

const parsedLot = parseLot(lotNo)
lotNo = parsedLot != null && parsedLot.Lot != null ? parsedLot.Lot : parsedLot
if (lotNo == null || lotNo == '') {
    return { code: 400, message: 'lotNo不能为空' }
}

if (timeoutMs == null) {
    timeoutMs = 3000
}
if (timeoutMs <= 0) {
    return { code: 400, message: 'timeoutMs必须大于0' }
}

const mapping = db.table("t_location_led").where().eq("location_code", lotNo).selectOne()
if (mapping == null) {
    return { code: 404, message: '未找到库位码对应的巷道灯绑定关系' }
}

let ledId = mapping.ledId || mapping.led_id
let color = mapping.color
if (ledId == null || ledId == '') {
    return { code: 500, message: '绑定关系缺少ledId' }
}
if (color == null || color == '') {
    return { code: 500, message: '绑定关系缺少color' }
}
color = (color + '').trim().toUpperCase()

log.info("data: {}", ledId)

const controlResult = controlAisleLedDevice({
    mode: mode,
    ledId: ledId,
    command: 'ON',
    port: color,
    timeoutMs: timeoutMs,
    waitResponse: waitResponse
})

if (controlResult == null) {
    return { code: 500, message: '调用巷道灯控制函数无返回' }
}
if (controlResult.code != 200) {
    return controlResult
}

let firstExecuted = null
if (controlResult.executed != null && controlResult.executed.size() > 0) {
    firstExecuted = controlResult.executed[0]
}

return {
    code: 200,
    message: 'success',
    lotNo: lotNo,
    ledId: ledId,
    color: color,
    deviceIp: controlResult.deviceIp,
    devicePort: controlResult.devicePort,
    hostAddress: controlResult.hostAddress,
    dataCommand: firstExecuted == null ? null : firstExecuted.dataCommand,
    status: 1,
    pluginResponseText: firstExecuted == null ? null : firstExecuted.responseText,
    controlResult: controlResult
}
