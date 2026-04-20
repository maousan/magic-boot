{
  "properties" : { },
  "id" : "9dvgr4la975s18sogzyialv59fawnb10",
  "script" : null,
  "groupId" : "qou8in0pu4dvva7g20trefax4lo26ye8",
  "name" : "亮灯灭灯控制",
  "createTime" : null,
  "updateTime" : 1776669187355,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/control",
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
  "requestBody" : "{\n  \"waveNo\": \"WV202603250001\",\n  \"userId\": \"USER001\",\n  \"mode\": 3,\n  \"color\": \"GREEN\",\n  \"duration\": \"inf\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"指令发送成功\",\n    \"data\": null,\n    \"timestamp\": 1775325451620,\n    \"executeTime\": 175\n}",
  "description" : "亮灯/灭灯控制接口，当前仅记录日志，三方对接逻辑预留。",
  "requestBodyDefinition" : {
    "name" : "root",
    "value" : "",
    "description" : "亮灯/灭灯请求",
    "required" : true,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "waveNo",
      "value" : "WV202603250001",
      "description" : "波次号",
      "required" : true,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "userId",
      "value" : "USER001",
      "description" : "用户ID",
      "required" : true,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "mode",
      "value" : "1",
      "description" : "控制模式(1自动亮灯/2手动亮灯/3自动灭灯/4手动灭灯)",
      "required" : true,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "color",
      "value" : "GREEN",
      "description" : "灯光颜色，默认GREEN",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : "GREEN",
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "duration",
      "value" : "inf",
      "description" : "亮灯时长，默认inf",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : "inf",
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  },
  "responseBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "code",
      "value" : "200",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "指令发送成功",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "null",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
import log
import java.util.UUID
import org.ssssssss.magicapi.utils.JsonUtils
import com.dtflys.forest.Forest
import '@/aims/led/turnOffLedByArticleId' as turnOffLedByArticleId
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import org.ssssssss.magicboot.model.CommonCountMap

const mixColor = Forest.config().getVariableValue("mixColor")
let stationCode = Forest.config().getVariableValue("stationCode")
if(stationCode == null || stationCode == ''){
    stationCode = '10001'
}

if (body == null) {
    exit 400, '请求体不能为空'
}

let payload = body

if (!payload.waveNo) {
    exit 400, 'waveNo不能为空'
}
if (!payload.userId) {
    exit 400, 'userId不能为空'
}
if (payload.mode == null) {
    exit 400, 'mode不能为空'
}

let modeVal = payload.mode
if (modeVal != 1 && modeVal != 2 && modeVal != 3 && modeVal != 4) {
    exit 400, 'mode仅支持1/2/3/4'
}

let colorVal = payload.color
if (colorVal == null || colorVal == '') {
    colorVal = 'GREEN'
}
let durationVal = payload.duration
if (durationVal == null || durationVal == '') {
    durationVal = 'inf'
}

if (colorVal != 'RED' && colorVal != 'GREEN' && colorVal != 'BLUE' && colorVal != 'YELLOW' && colorVal != 'CYAN') {
    exit 400, 'color仅支持RED/GREEN/BLUE/YELLOW/CYAN'
}

if (durationVal != '10s' && durationVal != '30s' && durationVal != '1m' && durationVal != '2m' && durationVal != '5m' && durationVal != '10m' && durationVal != '15m' && durationVal != '20m' && durationVal != '30m' && durationVal != '60m' && durationVal != 'inf') {
    exit 400, 'duration仅支持10s/30s/1m/2m/5m/10m/15m/20m/30m/60m/inf'
}

let actionType = 'OFF'
if (modeVal == 1 || modeVal == 2) {
    actionType = 'ON'
}

let requestJson = JsonUtils.toJsonString(payload)
let transState = null
let formatLocationLog = (locationCodes, limit) => {
    if(locationCodes == null){
        return 'size=0,sample=[]'
    }
    let max = limit
    if(max == null || max <= 0){
        max = 10
    }
    let sample = []
    let i = 0
    for(code in locationCodes){
        if(i >= max){
            break
        }
        sample.add(code)
        i = i + 1
    }
    return 'size=' + locationCodes.size() + ',sample=' + sample
}
let resolveAffectedLocationCodes = (payloadObj) => {
    let result = []
    let uniq = {}
    if(payloadObj == null){
        return result
    }
    let source = payloadObj.affectedLocationCodes
    if(source == null){
        source = payloadObj.locationCodes
    }
    if(source != null){
        for(code in source){
            if(code != null && code != '' && uniq[code] == null){
                uniq[code] = 1
                result.add(code)
            }
        }
    }
    return result
}

let queryLocationSummary = (waveNo, targetLocationCodes) => {
    let sql = """
        select location_code,
               sum(case when ifnull(status, 0) <> 1 then 1 else 0 end) as unpicked_count
          from t_picking_upload_detail
         where wave_no = #{waveNo}
           and ifnull(location_code, '') <> ''
    """
    let params = {
        waveNo: waveNo
    }
    if(targetLocationCodes != null && targetLocationCodes.size() > 0){
        let inSql = ""
        let idx = 0
        for(locationCode in targetLocationCodes){
            let key = 'locationCode' + idx
            params[key] = locationCode
            if(idx > 0){
                inSql = inSql + ','
            }
            inSql = inSql + '#{' + key + '}'
            idx = idx + 1
        }
        sql = sql + " and location_code in (" + inSql + ")"
    }
    sql = sql + " group by location_code"
    let rows = db.select(sql, params)
    let summary = {
        allLocationCodes: [],
        unpickedLocationSet: {}
    }
    if(rows != null){
        for(row in rows){
            let locationCode = row.location_code
            if(locationCode == null || locationCode == ''){
                locationCode = row.locationCode
            }
            if(locationCode == null || locationCode == ''){
                continue
            }
            summary.allLocationCodes.add(locationCode)
            let unpickedCount = row.unpicked_count
            if(unpickedCount == null){
                unpickedCount = row.unpickedCount
            }
            if(unpickedCount != null && unpickedCount > 0){
                summary.unpickedLocationSet[locationCode] = 1
            }
        }
    }
    return summary
}

let queryUserUnpickedLocationCodes = (waveNo, userId) => {
    let rows = db.select("""
        select distinct d.location_code
          from t_picking_upload_detail d
          left join t_picking_upload m on m.id = d.bill_id
         where m.wave_no = #{waveNo}
           and m.user_id = #{userId}
           and ifnull(d.status, 0) <> 1
           and ifnull(d.location_code, '') <> ''
    """, {
        waveNo: waveNo,
        userId: userId
    })
    let locationCodes = []
    if(rows != null){
        for(row in rows){
            let code = row.location_code
            if(code == null || code == ''){
                code = row.locationCode
            }
            if(code != null && code != ''){
                locationCodes.add(code)
            }
        }
    }
    return locationCodes
}

try {
    transState = anyline.beginTrans()

    anyline.insert('t_light_control_log', {
        id: UUID.randomUUID().toString().replace('-', ''),
        wave_no: payload.waveNo,
        user_id: payload.userId,
        mode: modeVal,
        color: colorVal,
        duration: durationVal,
        action_type: actionType,
        request_path: '/api/light/control',
        request_json: requestJson,
        create_time: new Date()
    })

    anyline.commitTrans(transState)
    transState = null

    // 以“当前用户在该波次下的未拣库位”为单位更新 CommonCountMap
    let userUnpickedLocationCodes = queryUserUnpickedLocationCodes(payload.waveNo, payload.userId)
    if(modeVal == 1 || modeVal == 2){
        for(locationCode in userUnpickedLocationCodes){
            let currentCount = CommonCountMap.increment(locationCode)
            log.info('common count increment from control, waveNo=' + payload.waveNo + ', userId=' + payload.userId + ', locationCode=' + locationCode + ', count=' + currentCount)
        }
    } else {
        for(locationCode in userUnpickedLocationCodes){
            let currentCount = CommonCountMap.decrement(locationCode)
            log.info('common count decrement from control, waveNo=' + payload.waveNo + ', userId=' + payload.userId + ', locationCode=' + locationCode + ', count=' + currentCount)
        }
    }

    let affectedLocationCodes = resolveAffectedLocationCodes(payload)
    let locationSummary = queryLocationSummary(payload.waveNo, affectedLocationCodes)
    let allLocationCodes = locationSummary.allLocationCodes
    let unpickedLocationSet = locationSummary.unpickedLocationSet

    let turnOffLocationCodes = []
    let turnOnSingleLocationCodes = []
    let turnOnMultiLocationCodes = []

    for(locationCode in allLocationCodes){
        let locationUserCount = CommonCountMap.get(locationCode)
        if(unpickedLocationSet[locationCode] == null || locationUserCount <= 0){
            turnOffLocationCodes.add(locationCode)
        } else if(locationUserCount > 1){
            turnOnMultiLocationCodes.add(locationCode)
        } else {
            turnOnSingleLocationCodes.add(locationCode)
        }
    }

    let multiUserColor = mixColor
    if(multiUserColor == null || multiUserColor == ''){
        multiUserColor = 'CYAN'
    }

    log.info('control location summary, waveNo=' + payload.waveNo + ', mode=' + modeVal + ', affected=' + formatLocationLog(affectedLocationCodes, 20) + ', all=' + formatLocationLog(allLocationCodes, 20))

    if (modeVal == 3 || modeVal == 4) {
        if(turnOffLocationCodes.size() > 0){
            try {
                log.info('dispatch async turnOff from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', articleIds=' + turnOffLocationCodes)
                let turnOffFuture = async turnOffLedByArticleId({
                    stationCode: stationCode,
                    articleIds: turnOffLocationCodes
                })
            } catch (asyncErr1) {
                log.error('异步灭灯调用触发失败: ' + asyncErr1.message)
            }
        }
        if(turnOnSingleLocationCodes.size() > 0){
            try {
                log.info('dispatch async turnOn(single user) after turnOff, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=GREEN, articleIds=' + turnOnSingleLocationCodes)
                let turnOnSingleFuture = async turnOnLedByArticleId({
                    stationCode: stationCode,
                    color: 'GREEN',
                    articleIds: turnOnSingleLocationCodes
                })
            } catch (asyncErr2) {
                log.error('异步补亮调用触发失败(单用户色): ' + asyncErr2.message)
            }
        }
        if(turnOnMultiLocationCodes.size() > 0){
            try {
                log.info('dispatch async turnOn(multi user) after turnOff, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + multiUserColor + ', articleIds=' + turnOnMultiLocationCodes)
                let turnOnMultiFuture = async turnOnLedByArticleId({
                    stationCode: stationCode,
                    color: multiUserColor,
                    articleIds: turnOnMultiLocationCodes
                })
            } catch (asyncErr3) {
                log.error('异步补亮调用触发失败(多用户色): ' + asyncErr3.message)
            }
        }
    } else {
        if(turnOnSingleLocationCodes.size() > 0){
            try {
                log.info('dispatch async turnOn(single user) from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + colorVal + ', articleIds=' + turnOnSingleLocationCodes)
                let turnOnSingleFuture2 = async turnOnLedByArticleId({
                    stationCode,
                    color: colorVal,
                    articleIds: turnOnSingleLocationCodes
                })
            } catch (asyncErr4) {
                log.error('异步亮灯调用触发失败(单用户色): ' + asyncErr4.message)
            }
        }
        if(turnOnMultiLocationCodes.size() > 0){
            try {
                log.info('dispatch async turnOn(multi user) from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + multiUserColor + ', articleIds=' + turnOnMultiLocationCodes)
                let turnOnMultiFuture2 = async turnOnLedByArticleId({
                    stationCode,
                    color: multiUserColor,
                    articleIds: turnOnMultiLocationCodes
                })
            } catch (asyncErr5) {
                log.error('异步亮灯调用触发失败(多用户色): ' + asyncErr5.message)
            }
        }
        // 点亮模式下，也把不该亮的库位灭掉，避免残灯
        if(turnOffLocationCodes.size() > 0){
            try {
                log.info('dispatch async turnOff from control(on mode), waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', articleIds=' + turnOffLocationCodes)
                let turnOffFuture2 = async turnOffLedByArticleId({
                    stationCode: stationCode,
                    articleIds: turnOffLocationCodes
                })
            } catch (asyncErr6) {
                log.error('异步灭灯调用触发失败(点亮模式): ' + asyncErr6.message)
            }
        }
    }

    log.info('CommonCountMap(control): ' + CommonCountMap.snapshot())
    exit 200, '指令发送成功'
} catch (e) {
    if (transState != null) {
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {}
    }
    log.error('亮灯控制接口异常: ' + e.message)
    exit 500, '指令发送失败: ' + e.message
}
