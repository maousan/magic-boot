{
  "properties" : { },
  "id" : "9dvgr4la975s18sogzyialv59fawnb10",
  "script" : null,
  "groupId" : "qou8in0pu4dvva7g20trefax4lo26ye8",
  "name" : "亮灯灭灯控制",
  "createTime" : null,
  "updateTime" : 1776744217958,
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
  "requestBody" : "{\n  \"waveNo\": \"WV202603250001\",\n  \"userId\": \"USER001\",\n  \"mode\": 3,\n  \"color\": \"GREEN\",\n  \"duration\": \"60m\"\n}",
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
      "value" : "60m",
      "description" : "亮灯时长，默认60m",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : "60m",
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
import '@/utils/saveLightTraceLog' as saveLightTraceLog
import org.ssssssss.magicboot.model.CommonCountMap

const mixColor = Forest.config().getVariableValue("mixColor")
let stationCode = Forest.config().getVariableValue("stationCode")
if(stationCode == null || stationCode == ''){
    stationCode = '10001'
}
const controlDispatchAsync = Forest.config().getVariableValue("controlDispatchAsync")
let controlAsyncEnabled = false
if(controlDispatchAsync != null && (controlDispatchAsync + '').toLowerCase() == 'true'){
    controlAsyncEnabled = true
}

if (body == null) {
    exit 400, '请求体不能为空'
}

let payload = body
let traceId = payload.traceId
if(traceId == null || traceId == ''){
    traceId = UUID.randomUUID().toString().replace('-', '')
}
payload.traceId = traceId

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
if(modeVal == 3 || modeVal == 4){
    if(durationVal == null || durationVal == ''){
        exit 400, 'mode=3/4时，duration必填且仅支持0或0s'
    }
    if(durationVal != '0' && durationVal != '0s'){
        exit 400, 'mode=3/4时，duration仅支持0或0s'
    }
    if(durationVal == '0s'){
        durationVal = '0'
    }
} else {
    if (durationVal == null || durationVal == '') {
        durationVal = '60m'
    }
}

if (colorVal != 'RED' && colorVal != 'GREEN' && colorVal != 'BLUE' && colorVal != 'YELLOW' && colorVal != 'CYAN') {
    exit 400, 'color仅支持RED/GREEN/BLUE/YELLOW/CYAN'
}

if (modeVal == 1 || modeVal == 2) {
    if (durationVal != '10s' && durationVal != '30s' && durationVal != '1m' && durationVal != '2m' && durationVal != '5m' && durationVal != '10m' && durationVal != '15m' && durationVal != '20m' && durationVal != '30m' && durationVal != '60m' && durationVal != 'inf') {
        exit 400, 'mode=1/2时，duration仅支持10s/30s/1m/2m/5m/10m/15m/20m/30m/60m/inf'
    }
}

let actionType = 'OFF'
if (modeVal == 1 || modeVal == 2) {
    actionType = 'ON'
}
let traceActionTypeFromMode = 'turn_off'
if (modeVal == 1 || modeVal == 2) {
    traceActionTypeFromMode = 'turn_on'
}

let requestJson = JsonUtils.toJsonString(payload)
let logPrefix = 'traceId=' + traceId + ', api=control, waveNo=' + payload.waveNo + ', userId=' + payload.userId
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
let mergeLocationCodes = (firstCodes, secondCodes) => {
    let result = []
    let uniq = {}
    if(firstCodes != null){
        for(code in firstCodes){
            if(code != null && code != '' && uniq[code] == null){
                uniq[code] = 1
                result.add(code)
            }
        }
    }
    if(secondCodes != null){
        for(code in secondCodes){
            if(code != null && code != '' && uniq[code] == null){
                uniq[code] = 1
                result.add(code)
            }
        }
    }
    return result
}
let persistTraceLog = (stage, actionTypeValue, locationCodes, extraData) => {
    let ok = saveLightTraceLog(traceId, 'control', stage, actionTypeValue, payload.waveNo, payload.userId, locationCodes, extraData)
    if(!ok){
        log.error(logPrefix + ', stage=trace_persist_error, persistOk=false')
    }
}
let resolveDispatchSuccess = (resultObj) => {
    if(resultObj == null){
        return 'unknown'
    }
    if(resultObj.success == null){
        return 'unknown'
    }
    return resultObj.success
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

let queryLocationUserCountMap = (waveNo, locationCodes) => {
    let locationUserCountMap = {}
    if(locationCodes == null || locationCodes.size() == 0){
        return locationUserCountMap
    }
    let sql = """
        select d.location_code as location_code, count(distinct d.bill_id) as user_count
          from t_picking_upload_detail d
         where d.wave_no = #{waveNo}
           and ifnull(d.status, 0) <> 1
    """
    let params = {
        waveNo: waveNo
    }
    let inSql = ""
    let idx = 0
    for(locationCode in locationCodes){
        let key = 'locationCode' + idx
        params[key] = locationCode
        if(idx > 0){
            inSql = inSql + ','
        }
        inSql = inSql + '#{' + key + '}'
        idx = idx + 1
    }
    sql = sql + " and d.location_code in (" + inSql + ") group by d.location_code"
    let rows = db.select(sql, params)
    if(rows != null){
        for(row in rows){
            let locationCode = row.location_code
            if(locationCode == null || locationCode == ''){
                locationCode = row.locationCode
            }
            let userCount = row.user_count
            if(userCount == null){
                userCount = row.userCount
            }
            if(locationCode != null && locationCode != ''){
                locationUserCountMap[locationCode] = userCount
            }
        }
    }
    return locationUserCountMap
}

try {
    log.info(logPrefix + ', stage=receive, mode=' + modeVal + ', color=' + colorVal + ', duration=' + durationVal)
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

    let affectedLocationCodes = resolveAffectedLocationCodes(payload)
    let locationSummary = queryLocationSummary(payload.waveNo, affectedLocationCodes)
    let allLocationCodes = locationSummary.allLocationCodes
    let unpickedLocationSet = locationSummary.unpickedLocationSet
    if(affectedLocationCodes == null || affectedLocationCodes.size() == 0){
        affectedLocationCodes = allLocationCodes
    }
    log.info(logPrefix + ', stage=reconcile_start, mode=' + modeVal + ', affected=' + formatLocationLog(affectedLocationCodes, 20))
    let locationUserCountMap = queryLocationUserCountMap(payload.waveNo, affectedLocationCodes)
    for(locationCode in affectedLocationCodes){
        let locationUserCount = locationUserCountMap[locationCode]
        if(locationUserCount == null || locationUserCount <= 0){
            CommonCountMap.remove(locationCode)
            log.info(logPrefix + ', stage=reconcile_remove, locationCode=' + locationCode + ', dbCount=0, memCount=' + CommonCountMap.get(locationCode))
        } else {
            CommonCountMap.set(locationCode, locationUserCount)
            log.info(logPrefix + ', stage=reconcile_set, locationCode=' + locationCode + ', dbCount=' + locationUserCount + ', memCount=' + CommonCountMap.get(locationCode))
        }
    }
    log.info(logPrefix + ', stage=reconcile_done, size=' + CommonCountMap.size() + ', total=' + CommonCountMap.totalCount())

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

    log.info(logPrefix + ', stage=light_decision, mode=' + modeVal + ', affected=' + formatLocationLog(affectedLocationCodes, 20) + ', all=' + formatLocationLog(allLocationCodes, 20) + ', turnOnSingle=' + formatLocationLog(turnOnSingleLocationCodes, 20) + ', turnOnMulti=' + formatLocationLog(turnOnMultiLocationCodes, 20) + ', turnOff=' + formatLocationLog(turnOffLocationCodes, 20))

    if (modeVal == 3 || modeVal == 4) {
        if(turnOffLocationCodes.size() > 0){
            try {
                if(controlAsyncEnabled){
                    log.info('dispatch async turnOff from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', articleIds=' + turnOffLocationCodes)
                    let turnOffFuture = async turnOffLedByArticleId({
                        stationCode: stationCode,
                        articleIds: turnOffLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                } else {
                    log.info('dispatch sync turnOff from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', articleIds=' + turnOffLocationCodes)
                    let turnOffResult = turnOffLedByArticleId({
                        stationCode: stationCode,
                        articleIds: turnOffLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                    log.info(logPrefix + ', stage=dispatch_result, action=turnOff, success=' + resolveDispatchSuccess(turnOffResult))
                }
            } catch (asyncErr1) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOff, message=' + asyncErr1.message)
            }
        }
        if(turnOnSingleLocationCodes.size() > 0){
            try {
                if(controlAsyncEnabled){
                    log.info('dispatch async turnOn(single user) after turnOff, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=GREEN, articleIds=' + turnOnSingleLocationCodes)
                    let turnOnSingleFuture = async turnOnLedByArticleId({
                        stationCode: stationCode,
                        color: 'GREEN',
                        duration: durationVal,
                        articleIds: turnOnSingleLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                } else {
                    log.info('dispatch sync turnOn(single user) after turnOff, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=GREEN, articleIds=' + turnOnSingleLocationCodes)
                    let turnOnSingleResult = turnOnLedByArticleId({
                        stationCode: stationCode,
                        color: 'GREEN',
                        duration: durationVal,
                        articleIds: turnOnSingleLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                    log.info(logPrefix + ', stage=dispatch_result, action=turnOnSingleAfterOff, success=' + resolveDispatchSuccess(turnOnSingleResult))
                }
            } catch (asyncErr2) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOnSingleAfterOff, message=' + asyncErr2.message)
            }
        }
        if(turnOnMultiLocationCodes.size() > 0){
            try {
                if(controlAsyncEnabled){
                    log.info('dispatch async turnOn(multi user) after turnOff, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + multiUserColor + ', articleIds=' + turnOnMultiLocationCodes)
                    let turnOnMultiFuture = async turnOnLedByArticleId({
                        stationCode: stationCode,
                        color: multiUserColor,
                        duration: durationVal,
                        articleIds: turnOnMultiLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                } else {
                    log.info('dispatch sync turnOn(multi user) after turnOff, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + multiUserColor + ', articleIds=' + turnOnMultiLocationCodes)
                    let turnOnMultiResult = turnOnLedByArticleId({
                        stationCode: stationCode,
                        color: multiUserColor,
                        duration: durationVal,
                        articleIds: turnOnMultiLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                    log.info(logPrefix + ', stage=dispatch_result, action=turnOnMultiAfterOff, success=' + resolveDispatchSuccess(turnOnMultiResult))
                }
            } catch (asyncErr3) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOnMultiAfterOff, message=' + asyncErr3.message)
            }
        }
    } else {
        if(turnOnSingleLocationCodes.size() > 0){
            try {
                if(controlAsyncEnabled){
                    log.info('dispatch async turnOn(single user) from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + colorVal + ', articleIds=' + turnOnSingleLocationCodes)
                    let turnOnSingleFuture2 = async turnOnLedByArticleId({
                        stationCode,
                        color: colorVal,
                        duration: durationVal,
                        articleIds: turnOnSingleLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                } else {
                    log.info('dispatch sync turnOn(single user) from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + colorVal + ', articleIds=' + turnOnSingleLocationCodes)
                    let turnOnSingleResult2 = turnOnLedByArticleId({
                        stationCode,
                        color: colorVal,
                        duration: durationVal,
                        articleIds: turnOnSingleLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                    log.info(logPrefix + ', stage=dispatch_result, action=turnOnSingle, success=' + resolveDispatchSuccess(turnOnSingleResult2))
                }
            } catch (asyncErr4) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOnSingle, message=' + asyncErr4.message)
            }
        }
        if(turnOnMultiLocationCodes.size() > 0){
            try {
                if(controlAsyncEnabled){
                    log.info('dispatch async turnOn(multi user) from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + multiUserColor + ', articleIds=' + turnOnMultiLocationCodes)
                    let turnOnMultiFuture2 = async turnOnLedByArticleId({
                        stationCode,
                        color: multiUserColor,
                        duration: durationVal,
                        articleIds: turnOnMultiLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                } else {
                    log.info('dispatch sync turnOn(multi user) from control, waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', color=' + multiUserColor + ', articleIds=' + turnOnMultiLocationCodes)
                    let turnOnMultiResult2 = turnOnLedByArticleId({
                        stationCode,
                        color: multiUserColor,
                        duration: durationVal,
                        articleIds: turnOnMultiLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                    log.info(logPrefix + ', stage=dispatch_result, action=turnOnMulti, success=' + resolveDispatchSuccess(turnOnMultiResult2))
                }
            } catch (asyncErr5) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOnMulti, message=' + asyncErr5.message)
            }
        }
        // 点亮模式下，也把不该亮的库位灭掉，避免残灯
        if(turnOffLocationCodes.size() > 0){
            try {
                if(controlAsyncEnabled){
                    log.info('dispatch async turnOff from control(on mode), waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', articleIds=' + turnOffLocationCodes)
                    let turnOffFuture2 = async turnOffLedByArticleId({
                        stationCode: stationCode,
                        articleIds: turnOffLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                } else {
                    log.info('dispatch sync turnOff from control(on mode), waveNo=' + payload.waveNo + ', stationCode=' + stationCode + ', articleIds=' + turnOffLocationCodes)
                    let turnOffResult2 = turnOffLedByArticleId({
                        stationCode: stationCode,
                        articleIds: turnOffLocationCodes,
                        traceId: traceId,
                        waveNo: payload.waveNo,
                        userId: payload.userId
                    })
                    log.info(logPrefix + ', stage=dispatch_result, action=turnOffOnMode, success=' + resolveDispatchSuccess(turnOffResult2))
                }
            } catch (asyncErr6) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOffOnMode, message=' + asyncErr6.message)
            }
        }
    }

    log.info(logPrefix + ', stage=done, commonCountSize=' + CommonCountMap.size() + ', commonCountTotal=' + CommonCountMap.totalCount())
    let hasTurnOn = turnOnSingleLocationCodes.size() > 0 || turnOnMultiLocationCodes.size() > 0
    let hasTurnOff = turnOffLocationCodes.size() > 0
    let controlActionType = 'none'
    let controlTraceLocationCodes = []
    if(hasTurnOn && hasTurnOff){
        controlActionType = 'mix'
        controlTraceLocationCodes = mergeLocationCodes(mergeLocationCodes(turnOnSingleLocationCodes, turnOnMultiLocationCodes), turnOffLocationCodes)
    } else if(hasTurnOn){
        controlActionType = 'turn_on'
        controlTraceLocationCodes = mergeLocationCodes(turnOnSingleLocationCodes, turnOnMultiLocationCodes)
    } else if(hasTurnOff){
        controlActionType = 'turn_off'
        controlTraceLocationCodes = turnOffLocationCodes
    } else {
        controlTraceLocationCodes = []
    }
    persistTraceLog('done', controlActionType, controlTraceLocationCodes, {
        mode: modeVal,
        allCount: allLocationCodes.size(),
        turnOnSingle: turnOnSingleLocationCodes,
        turnOnMulti: turnOnMultiLocationCodes,
        turnOff: turnOffLocationCodes,
        affected: affectedLocationCodes,
        commonCountSize: CommonCountMap.size(),
        commonCountTotal: CommonCountMap.totalCount()
    })
    exit 200, '指令发送成功'
} catch (e) {
    if (transState != null) {
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {}
    }
    log.error(logPrefix + ', stage=error, message=' + e.message)
    persistTraceLog('error', 'error', [], {
        mode: modeVal,
        actionTypeFromMode: traceActionTypeFromMode,
        errorMessage: e.message
    })
    exit 500, '指令发送失败: ' + e.message
}
