{
  "properties" : { },
  "id" : "yeko1lgerm2ofrxmz2dotnn7f59gnacv",
  "script" : null,
  "groupId" : "628f77553ba14e47ae84391269b008bb",
  "name" : "控制巷道灯设备开关",
  "createTime" : 1779427000000,
  "updateTime" : 1779427000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/controlAisleLedDevice",
  "description" : "按设备、模式、指令和端口控制巷道灯，支持server/client模式。",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "data",
    "value" : null,
    "description" : "入参对象，包含mode、ledId、command、port、timeoutMs、waitResponse",
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
import org.ssssssss.magicapi.utils.JsonUtils
import java.util.Map
import '@/led/sendToClient' as sendToClient

if (data == null) {
    return { code: 400, message: 'data不能为空' }
}

let normalizeMac = (value) => {
    if (value == null || value == '') {
        return ''
    }
    let text = (value + '').replace(':', '').replace('-', '').trim().toUpperCase()
    if (text.length() != 12) {
        return (value + '').trim().toUpperCase()
    }
    return text.substring(0, 2) + ':' + text.substring(2, 4) + ':' + text.substring(4, 6)
        + ':' + text.substring(6, 8) + ':' + text.substring(8, 10) + ':' + text.substring(10, 12)
}

let crc16Modbus = (items) => {
    let crc = 0xFFFF
    for (item in items) {
        crc = crc ^ (item & 0xFF)
        for (bit in [0, 1, 2, 3, 4, 5, 6, 7]) {
            if ((crc & 0x0001) != 0) {
                crc = (crc >>> 1) ^ 0xA001
            } else {
                crc = crc >>> 1
            }
        }
    }
    return crc & 0xFFFF
}

let buildControlFrame = (hostAddress, controlCommand, dataCommand) => {
    let frame = [hostAddress & 0xFF, controlCommand & 0xFF, dataCommand & 0xFF]
    let crc = crc16Modbus(frame)
    frame.add((crc >> 8) & 0xFF)
    frame.add(crc & 0xFF)
    return frame
}

let parseJsonMap = (text) => {
    if (text == null || text == '') {
        return {}
    }
    return JsonUtils.readValue(text, Map.class)
}

let findActiveClientByMac = (ledId) => {
    let clientsUrl = Forest.config().getVariableValue('zintisLedNettyClientsUrl')
    if (clientsUrl == null || clientsUrl == '') {
        clientsUrl = 'http://127.0.0.1:8090/plugin/zintis-led-plugin/api/netty/server/clients'
    }
    let response = Forest.get(clientsUrl)
        .addHeader('Accept', 'application/json;charset=UTF-8')
        .executeAsResponse()
    let statusCode = response.statusCode()
    let responseText = response.readAsString()
    if (statusCode < 200 || statusCode >= 300) {
        return { success: false, message: '查询LED Netty活跃客户端失败', status: statusCode, responseText: responseText }
    }
    let responseMap = parseJsonMap(responseText)
    let payload = responseMap
    if (responseMap["data"] != null) {
        payload = responseMap["data"]
    }
    let details = payload["clientDetails"]
    if (details == null) {
        details = []
    }
    let targetMac = normalizeMac(ledId)
    for (client in details) {
        if (normalizeMac(client["macAddress"]) == targetMac) {
            return {
                success: true,
                remoteAddress: client["remoteAddress"],
                macAddress: client["macAddress"],
                clientsResponseText: responseText
            }
        }
    }
    return { success: false, message: '未找到MAC对应的活跃TCP客户端', macAddress: targetMac, responseText: responseText }
}

let parseSendSuccess = (result) => {
    if (result == null) {
        return false
    }
    if (result.code != 200) {
        return false
    }
    let responseText = result.responseText
    if (responseText == null || responseText == '') {
        return true
    }
    let responseMap = parseJsonMap(responseText)
    if (responseMap["success"] == false) {
        return false
    }
    if (responseMap["failedCount"] != null && responseMap["failedCount"] > 0) {
        return false
    }
    return true
}

let mode = data.mode
if (mode == null || mode == '') {
    mode = 'server'
}
mode = (mode + '').trim().toUpperCase()
if (mode != 'SERVER' && mode != 'CLIENT') {
    return { code: 400, message: 'mode仅支持server/client' }
}

let ledId = data.ledId
if (ledId == null || ledId == '') {
    ledId = data.macAddress
}
if (ledId == null || ledId == '') {
    return { code: 400, message: 'ledId不能为空' }
}
ledId = (ledId + '').trim()

let command = data.command
if (command == null || command == '') {
    command = 'ON'
}
command = (command + '').trim().toUpperCase()
if (command != 'ON' && command != 'OFF') {
    return { code: 400, message: 'command仅支持ON/OFF' }
}

let port = data.port
if (port == null || port == '') {
    port = 'ALL'
}
port = (port + '').trim().toUpperCase()
if (port != 'ALL' && port != 'RED' && port != 'YELLOW' && port != 'GREEN') {
    return { code: 400, message: 'port仅支持ALL/RED/YELLOW/GREEN' }
}

let timeoutMs = data.timeoutMs
if (timeoutMs == null) {
    timeoutMs = 10000
}
if (timeoutMs <= 0) {
    return { code: 400, message: 'timeoutMs必须大于0' }
}

const device = db.selectOne("""
    select
        mac_address as macAddress,
        ip,
        remark
    from t_led_device
    where mac_address = #{ledId}
    limit 1
""", { ledId: ledId })

if (device == null) {
    return { code: 404, message: '未找到巷道灯设备' }
}

let deviceIp = device.ip
if (deviceIp == null || deviceIp == '') {
    return { code: 500, message: '设备IP为空' }
}
deviceIp = (deviceIp + '').trim()

let ipText = deviceIp
let lastDotIndex = ipText.lastIndexOf('.')
if (lastDotIndex < 0 || lastDotIndex >= ipText.length() - 1) {
    return { code: 500, message: '设备IP格式非法: ' + ipText }
}
let hostAddress = ipText.substring(lastDotIndex + 1)
hostAddress = (hostAddress + '').trim()
if (hostAddress == '' || !hostAddress.matches('^[0-9]+$')) {
    return { code: 500, message: 'hostAddress不是有效数字: ' + hostAddress }
}
hostAddress = Integer.parseInt(hostAddress)
if (hostAddress <= 0 || hostAddress > 255) {
    return { code: 500, message: 'hostAddress不合法' }
}

let ports = []
if (port == 'ALL') {
    ports.add('ALL')
} else {
    ports.add(port)
}

let pluginUrl = null
let activeClient = null
if (mode == 'SERVER') {
    if (command == 'ON') {
        pluginUrl = Forest.config().getVariableValue('zintisLedControlOnUrl')
        if (pluginUrl == null || pluginUrl == '') {
            pluginUrl = 'http://127.0.0.1:8090/plugin/zintis-led-plugin/api/control/on'
        }
    } else {
        pluginUrl = Forest.config().getVariableValue('zintisLedControlOffUrl')
        if (pluginUrl == null || pluginUrl == '') {
            pluginUrl = 'http://127.0.0.1:8090/plugin/zintis-led-plugin/api/control/off'
        }
    }
} else {
    activeClient = findActiveClientByMac(ledId)
    if (activeClient.success != true) {
        log.warn('controlAisleLedDevice client mode active client not found, ledId=' + ledId + ', result=' + activeClient)
        return { code: 404, message: activeClient.message + ': ' + activeClient.macAddress, detail: activeClient }
    }
}

let executed = []
let failed = []

for (color in ports) {
    let dataCommand = null
    let colorRow = db.selectOne("""
        select
            code,
            color
        from t_led_color
        where color = #{color}
        order by id desc
        limit 1
    """, { color: color })

    if (colorRow == null) {
        failed.add({ color: color, message: '未找到颜色对应的控制命令' })
        continue
    }

    let commandRaw = colorRow.code
    if (commandRaw == null || (commandRaw + '').trim() == '') {
        failed.add({ color: color, message: '颜色命令code为空' })
        continue
    }

    let commandText = (commandRaw + '').trim()
    if (commandText.startsWith('0x') || commandText.startsWith('0X')) {
        dataCommand = Integer.parseInt(commandText.substring(2), 16)
    } else if (commandText.matches('^[0-9]+$')) {
        dataCommand = Integer.parseInt(commandText)
    } else if (commandText.matches('^[0-9a-fA-F]+$')) {
        dataCommand = Integer.parseInt(commandText, 16)
    } else {
        failed.add({ color: color, message: '颜色命令code格式不支持: ' + commandText })
        continue
    }

    if (dataCommand <= 0 || dataCommand > 255) {
        failed.add({ color: color, message: 'dataCommand超出有效范围(1-255)' })
        continue
    }

    let row = {
        color: color,
        dataCommand: dataCommand
    }

    if (mode == 'SERVER') {
        let requestBody = {
            deviceIp: deviceIp,
            devicePort: 9527,
            hostAddress: hostAddress,
            dataCommand: dataCommand,
            timeoutMs: timeoutMs
        }

        let response = Forest.post(pluginUrl)
            .contentType('application/json;charset=UTF-8')
            .addHeader('Accept', 'application/json;charset=UTF-8')
            .addBody(requestBody)
            .executeAsResponse()

        let statusCode = response.statusCode()
        let responseText = response.readAsString()
        row.status = statusCode
        row.responseText = responseText

        if (statusCode >= 200 && statusCode < 300) {
            executed.add(row)
        } else {
            failed.add(row)
        }
    } else {
        let controlCommand = command == 'ON' ? 0x35 : 0xFA
        let frame = buildControlFrame(hostAddress, controlCommand, dataCommand)
        let sendResult = sendToClient(activeClient.remoteAddress, null, null, frame, 'json', data.waitResponse)
        row.remoteAddress = activeClient.remoteAddress
        row.macAddress = activeClient.macAddress
        row.frame = frame
        row.sendResult = sendResult

        if (parseSendSuccess(sendResult)) {
            executed.add(row)
        } else {
            failed.add(row)
        }
    }
}

if (failed.size() > 0) {
    log.warn('controlAisleLedDevice failed, ledId=' + ledId + ', command=' + command + ', port=' + port + ', failed=' + failed)
    return { code: 502, message: '部分端口执行失败: ' + failed, failed: failed, executed: executed }
}

let newStatus = 0
if (command == 'ON') {
    newStatus = 1
}
if (port == 'ALL') {
    db.update("""
        update t_location_led
           set status = #{status}
         where led_id = #{ledId}
    """, {
        status: newStatus,
        ledId: ledId
    })
} else {
    db.update("""
        update t_location_led
           set status = #{status}
         where led_id = #{ledId}
           and upper(color) = #{port}
    """, {
        status: newStatus,
        ledId: ledId,
        port: port
    })
}

return {
    code: 200,
    message: 'success',
    mode: mode.toLowerCase(),
    ledId: ledId,
    deviceIp: deviceIp,
    devicePort: 9527,
    hostAddress: hostAddress,
    remoteAddress: activeClient == null ? null : activeClient.remoteAddress,
    command: command,
    port: port,
    timeoutMs: timeoutMs,
    status: newStatus,
    executedCount: executed.size(),
    executed: executed,
    failed: failed
}
