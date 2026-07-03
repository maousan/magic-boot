{
  "properties" : { },
  "id" : "7c8d2f2d8be2452aa1c9f86f6e0f56a1",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "控制巷道灯设备开关",
  "createTime" : null,
  "updateTime" : 1783050609238,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/led-device-control",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "该接口需要登录才允许访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\"mode\":\"client\",\"ledId\":\"3A:69:7A:08:D0:A5\",\"command\":\"ON\",\"port\":\"GREEN\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"mode\": \"client\",\n        \"ledId\": \"3A:69:7A:08:D0:A5\",\n        \"deviceIp\": \"192.168.2.102\",\n        \"remoteAddress\": \"192.168.2.102:52026\",\n        \"command\": \"ON\",\n        \"port\": \"GREEN\",\n        \"timeoutMs\": 10000,\n        \"executedCount\": 1,\n        \"executed\": [\n            {\n                \"color\": \"GREEN\",\n                \"dataCommand\": 186,\n                \"remoteAddress\": \"192.168.2.102:52026\",\n                \"macAddress\": \"3A:69:7A:08:D0:A5\",\n                \"frame\": [\n                    102,\n                    53,\n                    186,\n                    60,\n                    7\n                ],\n                \"sendResult\": {\n                    \"code\": 200,\n                    \"message\": \"success\",\n                    \"status\": 200,\n                    \"responseText\": \"{\\\"success\\\":true,\\\"message\\\":\\\"Send completed with response\\\",\\\"totalTargets\\\":1,\\\"successCount\\\":1,\\\"failedCount\\\":0,\\\"failedTargets\\\":[],\\\"waitResponse\\\":true,\\\"responseCount\\\":1,\\\"responses\\\":[{\\\"remoteAddress\\\":\\\"192.168.2.102:52026\\\",\\\"received\\\":true,\\\"timeout\\\":false,\\\"rawResponseHex\\\":\\\"6635BA3C07\\\",\\\"payloadAscii\\\":\\\"\\\",\\\"macAddress\\\":\\\"\\\",\\\"ipAddress\\\":\\\"\\\",\\\"crc\\\":\\\"3C07\\\"}]}\"\n                }\n            }\n        ],\n        \"failed\": []\n    },\n    \"timestamp\": 1783041428114,\n    \"executeTime\": 382\n}",
  "description" : "按设备 + 指令 + 端口直接控制巷道灯。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest
import org.ssssssss.magicapi.utils.JsonUtils
import java.util.Map
import java.nio.charset.StandardCharsets
import '@/led/sendToClient' as sendToClient

if (body == null) {
    exit 400, '请求体不能为空'
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

let buildFrame = (hostAddress, controlCommand, dataCommand, payload) => {
    let frame = [hostAddress & 0xFF, controlCommand & 0xFF, dataCommand & 0xFF]
    if (payload != null) {
        for (item in payload) {
            frame.add(item & 0xFF)
        }
    }
    let crc = crc16Modbus(frame)
    frame.add((crc >> 8) & 0xFF)
    frame.add(crc & 0xFF)
    return frame
}

let toPrefixedHex = (items) => {
    if (items == null) {
        return ''
    }
    let text = ''
    for (item in items) {
        if (text != '') {
            text = text + ' '
        }
        let hex = Integer.toHexString(item & 0xFF).toUpperCase()
        if (hex.length() == 1) {
            hex = '0' + hex
        }
        text = text + '0x' + hex
    }
    return text
}

let asciiBytes = (text) => {
    if (text == null) {
        return []
    }
    let value = text + ''
    if (value == '') {
        return []
    }
    let rawBytes = value.getBytes(StandardCharsets.US_ASCII)
    let bytes = []
    for (item in rawBytes) {
        bytes.add(item & 0xFF)
    }
    return bytes
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

let mode = body.mode
if (mode == null || mode == '') {
    mode = 'server'
}
mode = (mode + '').trim().toUpperCase()
if (mode != 'SERVER' && mode != 'CLIENT') {
    exit 400, 'mode仅支持server/client'
}

let ledId = body.ledId
if (ledId == null || ledId == '') {
    ledId = body.macAddress
}
if (ledId == null || ledId == '') {
    exit 400, 'ledId不能为空'
}
ledId = (ledId + '').trim()

let command = body.command
if (command == null || command == '') {
    command = 'ON'
}
command = (command + '').trim().toUpperCase()

let advancedCommand = command
let lightCommand = command == 'ON' || command == 'OFF'

let port = body.port
if (port == null || port == '') {
    port = 'ALL'
}
port = (port + '').trim().toUpperCase()
if (port != 'ALL' && port != 'RED' && port != 'YELLOW' && port != 'GREEN') {
    exit 400, 'port仅支持ALL/RED/YELLOW/GREEN'
}

let timeoutMs = body.timeoutMs
if (timeoutMs == null) {
    timeoutMs = 10000
}
if (timeoutMs <= 0) {
    exit 400, 'timeoutMs必须大于0'
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
    exit 404, '未找到巷道灯设备'
}

let deviceIp = device.ip
if (deviceIp == null || deviceIp == '') {
    exit 500, '设备IP为空'
}
deviceIp = (deviceIp + '').trim()

let ipText = deviceIp
let lastDotIndex = ipText.lastIndexOf('.')
if (lastDotIndex < 0 || lastDotIndex >= ipText.length() - 1) {
    exit 500, '设备IP格式非法: ' + ipText
}
let hostAddress = ipText.substring(lastDotIndex + 1)
hostAddress = (hostAddress + '').trim()
if (hostAddress == '' || !hostAddress.matches('^[0-9]+$')) {
    exit 500, 'hostAddress不是有效数字: ' + hostAddress
}
hostAddress = Integer.parseInt(hostAddress)
if (hostAddress <= 0 || hostAddress > 255) {
    exit 500, 'hostAddress不合法'
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
    if (!lightCommand) {
        exit 400, '高级指令仅支持client模式'
    }
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
        log.warn('led-device-control client mode active client not found, ledId=' + ledId + ', result=' + activeClient)
        exit 404, activeClient.message + ': ' + activeClient.macAddress
    }
}

let executed = []
let failed = []

let parseSendResponse = (sendResult) => {
    let payload = {
        success: parseSendSuccess(sendResult),
        message: sendResult == null ? '' : sendResult.message,
        payloadAscii: '',
        payloadHex: '',
        rawResponseHex: ''
    }
    if (sendResult == null || sendResult.responseText == null || sendResult.responseText == '') {
        return payload
    }
    let responseMap = parseJsonMap(sendResult.responseText)
    payload.message = responseMap["message"]
    let responses = responseMap["responses"]
    if (responses != null && responses.size() > 0) {
        let first = responses[0]
        payload.payloadAscii = first["payloadAscii"] == null ? '' : first["payloadAscii"]
        payload.rawResponseHex = first["rawResponseHex"] == null ? '' : first["rawResponseHex"]
    }
    return payload
}

let waitResponse = body.waitResponse
if (waitResponse == null) {
    waitResponse = false
}

if (!lightCommand) {
    let frame = null

    if (advancedCommand == 'CONTROL_ON') {
        frame = buildControlFrame(hostAddress, 0x35, body.dataCommand == null ? 1 : body.dataCommand)
    } else if (advancedCommand == 'CONTROL_OFF') {
        frame = buildControlFrame(hostAddress, 0xFA, body.dataCommand == null ? 1 : body.dataCommand)
    } else if (advancedCommand == 'CONTROL_PULSE') {
        frame = buildControlFrame(hostAddress, 0x36, body.dataCommand == null ? 1 : body.dataCommand)
    } else if (advancedCommand == 'QUERY') {
        frame = buildControlFrame(hostAddress, 0xAB, body.dataCommand == null ? 1 : body.dataCommand)
    } else if (advancedCommand == 'SYSTEM_INFO') {
        frame = buildControlFrame(hostAddress, 0xAB, body.dataCommand == null ? 1 : body.dataCommand)
    } else if (advancedCommand == 'SIGNAL_STRENGTH') {
        frame = buildControlFrame(hostAddress, 0xAB, 0xBF)
    } else if (advancedCommand == 'NETWORK') {
        frame = buildControlFrame(hostAddress, 0xAB, 0x97)
    } else if (advancedCommand == 'TCP_SERVER_OPEN') {
        frame = buildControlFrame(hostAddress, 0xBF, 0x23)
    } else if (advancedCommand == 'TCP_SERVER_CLOSE') {
        frame = buildControlFrame(hostAddress, 0xBF, 0x2E)
    } else if (advancedCommand == 'TCP_CLIENT_OPEN') {
        let targetIp = body.targetIp
        let targetPort = body.targetPort
        if (targetIp == null || targetIp == '' || targetPort == null) {
            exit 400, '开启TCP客户端需要targetIp和targetPort'
        }
        frame = buildFrame(hostAddress, 0xBF, 0x33, asciiBytes((targetIp + '').trim() + ':' + targetPort))
    } else if (advancedCommand == 'TCP_CLIENT_CLOSE') {
        frame = buildControlFrame(hostAddress, 0xBF, 0x3E)
    } else if (advancedCommand == 'OTA_UPDATE') {
        let version = body.version
        if (version == null || version == '') {
            exit 400, 'OTA升级需要version'
        }
        let versionBytes = asciiBytes(version)
        if (versionBytes.size() == 0) {
            exit 400, 'version不能为空'
        }
        let payload = []
        if (versionBytes.size() > 1) {
            let versionIndex = 1
            while (versionIndex < versionBytes.size()) {
                payload.add(versionBytes[versionIndex])
                versionIndex = versionIndex + 1
            }
        }
        frame = buildFrame(hostAddress, 0x9F, versionBytes[0], payload)
    } else {
        exit 400, '不支持的指令: ' + advancedCommand
    }

    let sendResult = sendToClient(activeClient.remoteAddress, null, null, frame, 'json', !!waitResponse)
    let parsed = parseSendResponse(sendResult)
    if (parsed.success != true) {
        failed.add({ command: advancedCommand, remoteAddress: activeClient.remoteAddress, frame: frame, sendResult: sendResult })
        log.warn('led-device-control client command failed, ledId=' + ledId + ', command=' + advancedCommand + ', failed=' + failed)
        exit 502, '指令执行失败: ' + failed
    }
    return {
        mode: 'client',
        ledId: ledId,
        deviceIp: deviceIp,
        remoteAddress: activeClient.remoteAddress,
        command: advancedCommand,
        timeoutMs: timeoutMs,
        success: true,
        payloadAscii: parsed.payloadAscii,
        payloadHex: parsed.payloadHex,
        rawRequestHex: toPrefixedHex(frame),
        rawResponseHex: parsed.rawResponseHex,
        sendResult: sendResult
    }
}

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
        let sendResult = sendToClient(activeClient.remoteAddress, null, null, frame, 'json', waitResponse, color, command)
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
    log.warn('led-device-control failed, ledId=' + ledId + ', command=' + command + ', port=' + port + ', failed=' + failed)
    exit 502, '部分端口执行失败: ' + failed
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
    mode: mode.toLowerCase(),
    ledId: ledId,
    deviceIp: deviceIp,
    remoteAddress: activeClient == null ? null : activeClient.remoteAddress,
    command: command,
    port: port,
    timeoutMs: timeoutMs,
    executedCount: executed.size(),
    executed: executed,
    failed: failed
}
