{
  "properties" : { },
  "id" : "led202605220001abcdefghijklmnopqrs",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "LED设备分页查询",
  "createTime" : null,
  "updateTime" : null,
  "lock" : null,
  "createBy" : null,
  "updateBy" : null,
  "path" : "/led-device/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "page",
    "value" : "1",
    "description" : "页码",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "pageSize",
    "value" : "20",
    "description" : "每页条数",
    "required" : false,
    "dataType" : "Integer",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "macAddress",
    "value" : "",
    "description" : "MAC地址（模糊查询）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "ip",
    "value" : "",
    "description" : "IP地址（模糊查询）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "remark",
    "value" : "",
    "description" : "备注（模糊查询）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"total\": 1,\n        \"list\": [\n            {\n                \"macAddress\": \"3A:69:7A:08:D0:A5\",\n                \"ip\": \"192.168.2.102\",\n                \"remark\": \"Netty客户端自动注册\",\n                \"online\": true,\n                \"remoteAddress\": \"192.168.2.102:51853\"\n            }\n        ],\n        \"onlineTotal\": 1,\n        \"nettyRunning\": true\n    }\n}",
  "description" : "分页查询LED设备列表，附加实时在线状态（依据zintis-led-plugin的Netty活跃客户端）。online=true在线/false离线，灯控服务不可用时为null(未知)；onlineTotal为全量设备在线数；nettyRunning表示Netty服务是否运行中",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import com.dtflys.forest.Forest
import org.ssssssss.magicapi.utils.JsonUtils
import java.util.Map

let sql = """
    select mac_address, ip, remark
    from t_led_device
    <where>
        <if test="macAddress != null and macAddress != ''">
            and mac_address like concat('%', #{macAddress}, '%')
        </if>
        <if test="ip != null and ip != ''">
            and ip like concat('%', #{ip}, '%')
        </if>
        <if test="remark != null and remark != ''">
            and remark like concat('%', #{remark}, '%')
        </if>
    </where>
    order by mac_address
"""

let pageResult = db.page(sql, pageSize, (page-1)*pageSize)

// MAC 归一化：去掉分隔符转大写，用于与 Netty 上报的 MAC 比对
let normalizeMac = (value) => {
    if (value == null || value == '') {
        return ''
    }
    return (value + '').replace(':', '').replace('-', '').replace(' ', '').trim().toUpperCase()
}

// 查询 Netty 活跃客户端；灯控服务不可用时在线状态置为未知(null)，不影响列表返回
let nettyRunning = false
let onlineKnown = false
let onlineMacSet = {}
let remoteAddressMap = {}
try {
    let clientsUrl = Forest.config().getVariableValue('zintisLedNettyClientsUrl')
    if (clientsUrl == null || clientsUrl == '') {
        clientsUrl = 'http://127.0.0.1:8090/plugin/zintis-led-plugin/api/netty/server/clients'
    }
    let response = Forest.get(clientsUrl)
        .addHeader('Accept', 'application/json;charset=UTF-8')
        .executeAsResponse()
    let statusCode = response.statusCode()
    if (statusCode < 200 || statusCode >= 300) {
        log.warn('LED设备分页查询: 查询Netty活跃客户端失败, status=' + statusCode)
    } else {
        let responseMap = JsonUtils.readValue(response.readAsString(), Map.class)
        let payload = responseMap
        if (responseMap["data"] != null) {
            payload = responseMap["data"]
        }
        nettyRunning = payload["running"] == true
        let details = payload["clientDetails"]
        if (details != null) {
            for (client in details) {
                let mac = normalizeMac(client["macAddress"])
                if (mac != '') {
                    onlineMacSet[mac] = 1
                    remoteAddressMap[mac] = client["remoteAddress"]
                }
            }
        }
        onlineKnown = nettyRunning
    }
} catch (clientsErr) {
    log.warn('LED设备分页查询: 查询Netty活跃客户端异常, message=' + clientsErr.message)
}

let rows = pageResult.list
if (rows != null) {
    for (row in rows) {
        let mac = normalizeMac(row.macAddress)
        row.online = onlineKnown ? (onlineMacSet[mac] != null) : null
        row.remoteAddress = remoteAddressMap[mac]
    }
}

// 全量设备的在线数（设备表数据量小，直接全查）
let onlineTotal = null
if (onlineKnown) {
    onlineTotal = 0
    let allRows = db.select("select mac_address from t_led_device")
    for (row in allRows) {
        if (onlineMacSet[normalizeMac(row.macAddress)] != null) {
            onlineTotal = onlineTotal + 1
        }
    }
}

return {
    total: pageResult.total,
    list: rows,
    onlineTotal: onlineTotal,
    nettyRunning: nettyRunning
}
