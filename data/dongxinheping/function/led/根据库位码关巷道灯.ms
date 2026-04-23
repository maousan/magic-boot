{
  "properties" : { },
  "id" : "86d5b938530542d5a75c8f5e8a2f4ac1",
  "script" : null,
  "groupId" : "628f77553ba14e47ae84391269b008bb",
  "name" : "根据库位码关巷道灯",
  "createTime" : 1777000000000,
  "updateTime" : 1777000000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/turnOffAisleLedByLotNo",
  "description" : "传入库位码，自动根据绑定关系查设备IP与颜色命令并调用巷道灯关灯。",
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
import log
import com.dtflys.forest.Forest
import '@/utils/parseLot' as parseLot

if (data == null) {
    exit 400, 'data不能为空'
}

let lotNo = data.lotNo
let timeoutMs = data.timeoutMs

const parsedLot = parseLot(lotNo)
lotNo = parsedLot != null && parsedLot.Lot != null ? parsedLot.Lot : parsedLot
if (lotNo == null || lotNo == '') {
    exit 400, 'lotNo不能为空'
}

if (timeoutMs == null) {
    timeoutMs = 3000
}
if (timeoutMs <= 0) {
    exit 400, 'timeoutMs必须大于0'
}

const mapping = db.table("t_location_led").where().eq("location_code", lotNo).selectOne()
if (mapping == null) {
    exit 404, '未找到库位码对应的巷道灯绑定关系'
}

let ledId = mapping.ledId || mapping.led_id
let color = mapping.color
if (ledId == null || ledId == '') {
    exit 500, '绑定关系缺少ledId'
}
if (color == null || color == '') {
    exit 500, '绑定关系缺少color'
}
color = (color + '').trim().toUpperCase()

const device = db.selectOne("""
    select
        mac_address as macAddress,
        ip
    from t_led_device
    where mac_address = #{ledId}
    limit 1
""", {ledId: ledId})

if (device == null) {
    exit 404, '未找到绑定巷道灯设备信息'
}

let deviceIp = device.ip
if (deviceIp == null || deviceIp == '') {
    exit 500, '设备IP为空'
}

let ipText = (deviceIp + '').trim()
let lastDotIndex = ipText.lastIndexOf('.')
if (lastDotIndex < 0 || lastDotIndex >= ipText.length() - 1) {
    exit 500, '设备IP格式非法: ' + ipText
}
let hostAddress = ipText.substring(lastDotIndex + 1)
hostAddress = (hostAddress + '').trim()
if (hostAddress == '') {
    exit 500, '无法计算hostAddress'
}
if (!hostAddress.matches('^[0-9]+$')) {
    exit 500, 'hostAddress不是有效数字: ' + hostAddress
}
hostAddress = Integer.parseInt(hostAddress)
if (hostAddress <= 0 || hostAddress > 255) {
    exit 500, 'hostAddress不合法'
}

const colorRow = db.selectOne("""
    select
        code,
        color
    from t_led_color
    where color = #{color}
    order by id desc
    limit 1
""", {color: color})

if (colorRow == null) {
    exit 404, '未找到颜色对应的控制命令'
}

let commandRaw = colorRow.code
if (commandRaw == null || (commandRaw + '').trim() == '') {
    exit 500, '颜色命令code为空'
}

let commandText = (commandRaw + '').trim()
let dataCommand = null
if (commandText.startsWith('0x') || commandText.startsWith('0X')) {
    dataCommand = Integer.parseInt(commandText.substring(2), 16)
} else if (commandText.matches('^[0-9]+$')) {
    dataCommand = Integer.parseInt(commandText)
} else if (commandText.matches('^[0-9a-fA-F]+$')) {
    dataCommand = Integer.parseInt(commandText, 16)
} else {
    exit 500, '颜色命令code格式不支持: ' + commandText
}

if (dataCommand <= 0 || dataCommand > 255) {
    exit 500, 'dataCommand超出有效范围(1-255)'
}

let pluginControlOffUrl = Forest.config().getVariableValue("zintisLedControlOffUrl")
if (pluginControlOffUrl == null || pluginControlOffUrl == '') {
    pluginControlOffUrl = 'http://127.0.0.1:8090/plugin/zintis-led-plugin/api/control/off'
}

const requestBody = {
    deviceIp: deviceIp,
    devicePort: 9527,
    hostAddress: hostAddress,
    dataCommand: dataCommand,
    timeoutMs: timeoutMs
}

const response = Forest.post(pluginControlOffUrl)
    .contentType('application/json;charset=UTF-8')
    .addHeader('Accept', 'application/json;charset=UTF-8')
    .addBody(requestBody)
    .executeAsResponse()

const statusCode = response.statusCode()
const responseText = response.readAsString()
if (statusCode < 200 || statusCode >= 300) {
    log.warn('turnOffAisleLedByLotNo failed, lotNo=' + lotNo + ', ledId=' + ledId + ', color=' + color + ', status=' + statusCode + ', response=' + responseText)
    exit 502, '调用巷道灯插件失败: ' + responseText
}

log.info('turnOffAisleLedByLotNo success, lotNo=' + lotNo + ', ledId=' + ledId + ', color=' + color + ', ip=' + deviceIp + ', dataCommand=' + dataCommand)
return {
    lotNo: lotNo,
    ledId: ledId,
    color: color,
    deviceIp: deviceIp,
    devicePort: 9527,
    hostAddress: hostAddress,
    dataCommand: dataCommand,
    pluginResponseText: responseText
}

