{
  "properties" : { },
  "id" : "7c8d2f2d8be2452aa1c9f86f6e0f56a1",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "控制巷道灯设备开关",
  "createTime" : null,
  "updateTime" : null,
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
  "requestBody" : "{\n  \"ledId\": \"3A:69:7A:08:D0:A5\",\n  \"command\": \"ON\",\n  \"port\": \"GREEN\",\n  \"timeoutMs\": 3000\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": {\n    \"ledId\": \"3A:69:7A:08:D0:A5\",\n    \"deviceIp\": \"192.168.2.102\",\n    \"command\": \"ON\",\n    \"port\": \"GREEN\",\n    \"executedCount\": 1\n  },\n  \"timestamp\": 0,\n  \"executeTime\": 0\n}",
  "description" : "按设备 + 指令 + 端口直接控制巷道灯。"
}
================================
import log
import com.dtflys.forest.Forest

if (body == null) {
    exit 400, '请求体不能为空'
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
if (command != 'ON' && command != 'OFF') {
    exit 400, 'command仅支持ON/OFF'
}

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
    timeoutMs = 3000
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
    ports.add('RED')
    ports.add('YELLOW')
    ports.add('GREEN')
} else {
    ports.add(port)
}

let pluginUrl = null
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

let executed = []
let failed = []

for (color in ports) {
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
    let dataCommand = null
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

    let row = {
        color: color,
        dataCommand: dataCommand,
        status: statusCode,
        responseText: responseText
    }

    if (statusCode >= 200 && statusCode < 300) {
        executed.add(row)
    } else {
        failed.add(row)
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
    ledId: ledId,
    deviceIp: deviceIp,
    command: command,
    port: port,
    timeoutMs: timeoutMs,
    executedCount: executed.size(),
    executed: executed,
    failed: failed
}
