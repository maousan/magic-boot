{
  "properties" : { },
  "id" : "9dvgr4la975s18sogzyialv59fawnb10",
  "script" : null,
  "groupId" : "qou8in0pu4dvva7g20trefax4lo26ye8",
  "name" : "亮灯灭灯控制",
  "createTime" : null,
  "updateTime" : 1779260526313,
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
  "requestBody" : "{\n  \"waveNo\": \"1W2604180001\", \n  \"userId\": \"WZK\", \n  \"mode\": \"1\", \n  \"color\": \"GREEN\", \n  \"duration\": \"inf\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"指令发送成功\",\n    \"data\": null,\n    \"timestamp\": 1777000634749,\n    \"executeTime\": 140\n}",
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
import com.dtflys.forest.Forest
import '@/aims/led/turnOffLedByArticleId' as turnOffLedByArticleId
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import '@/led/turnOnAisleLedByLotNo' as turnOnAisleLedByLotNo
import '@/led/turnOffAisleLedByLotNo' as turnOffAisleLedByLotNo

const mixColor = Forest.config().getVariableValue("mixColor")
let stationCode = Forest.config().getVariableValue("stationCode")
if(stationCode == null || stationCode == ''){
    stationCode = '10001'
}
const globalLockKey = 'light:picking:upload:global'
const lockTimeoutSeconds = 10
const locationChunkSize = 200

let ensureTraceId = (traceId) => {
    if(traceId == null || traceId == ''){
        return UUID.randomUUID().toString().replace('-', '')
    }
    return traceId
}

let addUnique = (list, setMap, value) => {
    if(value == null || value == ''){
        return
    }
    if(setMap[value] == null){
        setMap[value] = 1
        list.add(value)
    }
}

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

let splitLocationCodeChunks = (locationCodes, chunkSize) => {
    let chunks = []
    if(locationCodes == null || locationCodes.size() == 0){
        return chunks
    }
    let size = chunkSize
    if(size == null || size <= 0){
        size = 200
    }
    let current = []
    for(locationCode in locationCodes){
        current.add(locationCode)
        if(current.size() >= size){
            chunks.add(current)
            current = []
        }
    }
    if(current.size() > 0){
        chunks.add(current)
    }
    return chunks
}

let acquireGlobalLock = (lockKey, timeoutSeconds) => {
    let maxAttempts = 20
    if(timeoutSeconds != null && timeoutSeconds > 0){
        maxAttempts = timeoutSeconds * 20
    }
    let attempt = 0
    while(attempt < maxAttempts){
        attempt = attempt + 1
        try {
            let rowObj = db.selectOne("""
                select get_lock(#{lockKey}, 0) as lock_result
            """, {
                lockKey: lockKey
            })
            let lockResult = null
            if(rowObj != null){
                try {
                    lockResult = rowObj.lock_result
                } catch (ignore) {
                }
                if(lockResult == null){
                    try {
                        lockResult = rowObj.lockResult
                    } catch (ignore) {
                    }
                }
                if(lockResult == null){
                    try {
                        lockResult = rowObj['lock_result']
                    } catch (ignore) {
                    }
                }
                if(lockResult == null){
                    lockResult = rowObj
                }
            }
            if(lockResult != null && (lockResult + '') == '1'){
                return true
            }
        } catch (ignoreLockQueryErr) {
        }
        try{
            db.selectOne("""
                select sleep(0.05) as wait_result
            """)
        } catch (ignoreSleepErr) {
        }
    }
    return false
}

let releaseGlobalLock = (lockKey) => {
    try {
        db.selectOne("""
            select release_lock(#{lockKey}) as release_result
        """, {
            lockKey: lockKey
        })
    } catch (ignore) {
    }
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
            addUnique(result, uniq, code)
        }
    }
    return result
}

let queryWaveLocationCodes = (waveNo, targetLocationCodes) => {
    let params = {
        waveNo: waveNo
    }
    let sql = """
        select distinct d.location_code
          from t_picking_upload_detail d
          left join t_picking_upload m on m.id = d.bill_id
         where (d.wave_no = #{waveNo} or m.wave_no = #{waveNo})
           and ifnull(d.location_code, '') <> ''
    """
    if(targetLocationCodes != null && targetLocationCodes.size() > 0){
        let inSql = ''
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
        sql = sql + ' and d.location_code in (' + inSql + ')'
    }
    let rows = db.select(sql, params)
    let result = []
    let uniq = {}
    if(rows != null){
        for(row in rows){
            let locationCode = row.location_code
            if(locationCode == null || locationCode == ''){
                locationCode = row.locationCode
            }
            addUnique(result, uniq, locationCode)
        }
    }
    return result
}

let queryLocationStatusBatch = (locationCodes) => {
    let statusSnapshotMap = {}
    if(locationCodes == null || locationCodes.size() == 0){
        return statusSnapshotMap
    }

    for(locationCode in locationCodes){
        statusSnapshotMap[locationCode] = {
            status0UserCount: 0,
            status1UserCount: 0
        }
    }

    let chunks = splitLocationCodeChunks(locationCodes, locationChunkSize)
    for(codeChunk in chunks){
        let params = {}
        let inSql = ''
        let idx = 0
        for(locationCode in codeChunk){
            let key = 'statusLocationCode' + idx
            params[key] = locationCode
            if(idx > 0){
                inSql = inSql + ','
            }
            inSql = inSql + '#{' + key + '}'
            idx = idx + 1
        }

        let rows = db.select("""
            select
                t.location_code,
                sum(case when t.unpicked_cnt > 0 then 1 else 0 end) as status0_user_count,
                sum(case when t.unpicked_cnt = 0 then 1 else 0 end) as status1_user_count
            from (
                select
                    d.location_code,
                    d.bill_id,
                    sum(case when ifnull(d.status, 0) <> 1 then 1 else 0 end) as unpicked_cnt
                from t_picking_upload_detail d
                where d.location_code in (""" + inSql + """)
                  and ifnull(d.location_code, '') <> ''
                  and ifnull(d.bill_id, '') <> ''
                group by d.location_code, d.bill_id
            ) t
            group by t.location_code
        """, params)

        if(rows != null){
            for(row in rows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){
                    locationCode = row.locationCode
                }
                if(locationCode == null || locationCode == ''){
                    continue
                }
                let status0UserCount = row.status0_user_count
                if(status0UserCount == null){
                    status0UserCount = row.status0UserCount
                }
                if(status0UserCount == null){
                    status0UserCount = 0
                }
                let status1UserCount = row.status1_user_count
                if(status1UserCount == null){
                    status1UserCount = row.status1UserCount
                }
                if(status1UserCount == null){
                    status1UserCount = 0
                }
                statusSnapshotMap[locationCode] = {
                    status0UserCount: status0UserCount,
                    status1UserCount: status1UserCount
                }
            }
        }
    }

    return statusSnapshotMap
}

let assertLightControlSuccess = (action, result) => {
    if(result == null){
        throw new RuntimeException('灯控调用失败(' + action + '): result is null')
    }
    if(result.success == false){
        let failMessage = result.message
        if(failMessage == null || failMessage == ''){
            failMessage = result.msg
        }
        if(failMessage == null || failMessage == ''){
            failMessage = 'unknown'
        }
        throw new RuntimeException('灯控调用失败(' + action + '): ' + failMessage)
    }
}

let buildInSqlParams = (values, paramPrefix) => {
    let inSql = ""
    let params = {}
    let idx = 0
    for(value in values){
        let key = paramPrefix + idx
        params[key] = value
        if(idx > 0){
            inSql = inSql + ','
        }
        inSql = inSql + '#{' + key + '}'
        idx = idx + 1
    }
    return {
        inSql: inSql,
        params: params
    }
}

let resolveAisleLedTurnOnCandidates = (turnOnLocationCodes) => {
    let result = {
        locationLedMap: {},
        representativeByLed: {}
    }
    if(turnOnLocationCodes == null || turnOnLocationCodes.size() == 0){
        return result
    }

    let chunks = splitLocationCodeChunks(turnOnLocationCodes, locationChunkSize)
    for(codeChunk in chunks){
        let inData = buildInSqlParams(codeChunk, 'aisleOnLocationCode')
        db.update("""
            update t_location_led
               set status = 1
             where location_code in (""" + inData.inSql + """)
        """, inData.params)

        let rows = db.select("""
            select
                location_code,
                led_id
            from t_location_led
            where location_code in (""" + inData.inSql + """)
        """, inData.params)
        if(rows != null){
            for(row in rows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){
                    locationCode = row.locationCode
                }
                let ledId = row.led_id
                if(ledId == null || ledId == ''){
                    ledId = row.ledId
                }
                if(locationCode != null && locationCode != '' && ledId != null && ledId != ''){
                    result.locationLedMap[locationCode] = ledId
                }
            }
        }
    }

    for(locationCode in turnOnLocationCodes){
        let ledId = result.locationLedMap[locationCode]
        if(ledId != null && ledId != '' && result.representativeByLed[ledId] == null){
            result.representativeByLed[ledId] = locationCode
        }
    }
    return result
}

let resolveAisleLedTurnOffCandidates = (turnOffLocationCodes) => {
    let result = {
        candidateLocationCodes: [],
        candidateLocationCodeSet: {},
        locationLedMap: {}
    }
    if(turnOffLocationCodes == null || turnOffLocationCodes.size() == 0){
        return result
    }

    let affectedLedIds = []
    let affectedLedIdSet = {}
    let chunks = splitLocationCodeChunks(turnOffLocationCodes, locationChunkSize)
    for(codeChunk in chunks){
        let inData = buildInSqlParams(codeChunk, 'aisleOffLocationCode')
        db.update("""
            update t_location_led
               set status = 0
             where location_code in (""" + inData.inSql + """)
        """, inData.params)

        let rows = db.select("""
            select
                location_code,
                led_id
            from t_location_led
            where location_code in (""" + inData.inSql + """)
        """, inData.params)
        if(rows != null){
            for(row in rows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){
                    locationCode = row.locationCode
                }
                let ledId = row.led_id
                if(ledId == null || ledId == ''){
                    ledId = row.ledId
                }
                if(locationCode != null && locationCode != '' && ledId != null && ledId != ''){
                    result.locationLedMap[locationCode] = ledId
                    addUnique(affectedLedIds, affectedLedIdSet, ledId)
                }
            }
        }
    }

    for(locationCode in turnOffLocationCodes){
        let ledId = result.locationLedMap[locationCode]
        if(ledId == null || ledId == ''){
            addUnique(result.candidateLocationCodes, result.candidateLocationCodeSet, locationCode)
        }
    }

    if(affectedLedIds.size() == 0){
        return result
    }

    let ledChunks = splitLocationCodeChunks(affectedLedIds, locationChunkSize)
    for(ledChunk in ledChunks){
        let inData = buildInSqlParams(ledChunk, 'aisleLedId')
        let rows = db.select("""
            select
                led_id,
                sum(case when ifnull(status, 0) = 1 then 1 else 0 end) as active_cnt,
                min(location_code) as representative_location_code
            from t_location_led
            where led_id in (""" + inData.inSql + """)
            group by led_id
        """, inData.params)

        if(rows != null){
            for(row in rows){
                let activeCnt = row.active_cnt
                if(activeCnt == null){
                    activeCnt = row.activeCnt
                }
                if(activeCnt == null){
                    activeCnt = 0
                }
                if(activeCnt > 0){
                    continue
                }
                let representativeLocationCode = row.representative_location_code
                if(representativeLocationCode == null || representativeLocationCode == ''){
                    representativeLocationCode = row.representativeLocationCode
                }
                if(representativeLocationCode != null && representativeLocationCode != ''){
                    addUnique(result.candidateLocationCodes, result.candidateLocationCodeSet, representativeLocationCode)
                }
            }
        }
    }
    return result
}

if (body == null) {
    exit 400, '请求体不能为空'
}

let payload = body
let traceId = ensureTraceId(payload.traceId)
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

let lockAcquired = false

try {
    let logPrefix = 'api=control, waveNo=' + payload.waveNo + ', userId=' + payload.userId + ', mode=' + modeVal + ', traceId=' + traceId
    log.info(logPrefix + ', stage=receive, color=' + colorVal + ', duration=' + durationVal)

    let affectedLocationCodes = resolveAffectedLocationCodes(payload)
    let allLocationCodes = queryWaveLocationCodes(payload.waveNo, affectedLocationCodes)
    log.info(logPrefix + ', stage=location_resolved, affected=' + formatLocationLog(affectedLocationCodes, 20) + ', all=' + formatLocationLog(allLocationCodes, 20))
    if(allLocationCodes.size() == 0){
        if(modeVal == 2){
            log.warn(logPrefix + ', stage=no_location_for_manual_on')
            exit 404, '未找到可亮灯库位，请先上传拣货数据'
        }
        exit 200, '指令发送成功'
    }

    lockAcquired = acquireGlobalLock(globalLockKey, lockTimeoutSeconds)
    if(!lockAcquired){
        exit 503, '系统繁忙，请稍后重试'
    }

    // 控制接口仅负责灯控，不负责维护 t_location_status
    let statusSnapshotMap = queryLocationStatusBatch(allLocationCodes)
    log.info(logPrefix + ', stage=status_loaded, locationCount=' + allLocationCodes.size())

    let turnOffLocationCodes = []
    let turnOnSingleLocationCodes = []
    let turnOnMultiLocationCodes = []

    // mode=4 手动灭灯：强制按波次库位关灯，不再依赖状态判定
    if(modeVal == 4){
        for(locationCode in allLocationCodes){
            turnOffLocationCodes.add(locationCode)
        }
    // mode=2 手动亮灯：强制按波次库位亮灯，不再依赖状态判定
    } else if(modeVal == 2){
        for(locationCode in allLocationCodes){
            turnOnSingleLocationCodes.add(locationCode)
        }
    } else {
    for(locationCode in allLocationCodes){
        let snapshot = statusSnapshotMap[locationCode]
        let status0UserCount = 0
            if(snapshot != null && snapshot.status0UserCount != null){
                status0UserCount = snapshot.status0UserCount
            }
            if(status0UserCount <= 0){
                turnOffLocationCodes.add(locationCode)
            } else if(status0UserCount > 1){
                turnOnMultiLocationCodes.add(locationCode)
            } else {
                turnOnSingleLocationCodes.add(locationCode)
            }
        }
    }

    let multiUserColor = mixColor
    if(multiUserColor == null || multiUserColor == ''){
        multiUserColor = 'CYAN'
    }

    let singleColor = colorVal
    if(modeVal == 3 || modeVal == 4){
        singleColor = 'GREEN'
    }

    log.info(logPrefix + ', stage=decision_done, turnOff=' + formatLocationLog(turnOffLocationCodes, 20) + ', turnOnMulti=' + formatLocationLog(turnOnMultiLocationCodes, 20) + ', turnOnSingle=' + formatLocationLog(turnOnSingleLocationCodes, 20))

    let singleColorLocationMap = {}
    let singleColorKeys = []
    let singleColorKeySet = {}
    let turnOnLocationCodes = []
    let turnOnLocationCodeSet = {}
    for(locationCode in turnOnMultiLocationCodes){
        addUnique(turnOnLocationCodes, turnOnLocationCodeSet, locationCode)
    }
    for(locationCode in turnOnSingleLocationCodes){
        addUnique(turnOnLocationCodes, turnOnLocationCodeSet, locationCode)
    }

    let turnOnAisleResult = resolveAisleLedTurnOnCandidates(turnOnLocationCodes)
    let turnOffAisleResult = resolveAisleLedTurnOffCandidates(turnOffLocationCodes)

    let turnOnMultiDispatchCodes = []
    let turnOnMultiDispatchCodeSet = {}
    let ledMultiSet = {}
    let turnOnLocationLedMap = turnOnAisleResult.locationLedMap
    let representativeByLed = turnOnAisleResult.representativeByLed
    for(locationCode in turnOnMultiLocationCodes){
        let ledId = turnOnLocationLedMap[locationCode]
        if(ledId != null && ledId != ''){
            if(ledMultiSet[ledId] == null){
                ledMultiSet[ledId] = 1
                let representativeLocationCode = representativeByLed[ledId]
                if(representativeLocationCode == null || representativeLocationCode == ''){
                    representativeLocationCode = locationCode
                }
                addUnique(turnOnMultiDispatchCodes, turnOnMultiDispatchCodeSet, representativeLocationCode)
            }
        } else {
            addUnique(turnOnMultiDispatchCodes, turnOnMultiDispatchCodeSet, locationCode)
        }
    }

    let singleLedSet = {}
    let singleLocationCodeSet = {}
    for(locationCode in turnOnSingleLocationCodes){
        let ledId = turnOnLocationLedMap[locationCode]
        let dispatchLocationCode = locationCode
        if(ledId != null && ledId != ''){
            if(ledMultiSet[ledId] != null){
                continue
            }
            if(singleLedSet[ledId] != null){
                continue
            }
            singleLedSet[ledId] = 1
            let representativeLocationCode = representativeByLed[ledId]
            if(representativeLocationCode != null && representativeLocationCode != ''){
                dispatchLocationCode = representativeLocationCode
            }
        } else {
            if(singleLocationCodeSet[locationCode] != null){
                continue
            }
            singleLocationCodeSet[locationCode] = 1
        }
        if(singleColorLocationMap[singleColor] == null){
            singleColorLocationMap[singleColor] = []
            addUnique(singleColorKeys, singleColorKeySet, singleColor)
        }
        singleColorLocationMap[singleColor].add(dispatchLocationCode)
    }
    let turnOffDispatchCodes = turnOffAisleResult.candidateLocationCodes
    log.info(logPrefix + ', stage=dispatch_resolved, turnOffDispatch=' + formatLocationLog(turnOffDispatchCodes, 20) + ', turnOnMultiDispatch=' + formatLocationLog(turnOnMultiDispatchCodes, 20))

    // 锁外执行灯控，缩短全局锁占用时间
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
        lockAcquired = false
    }

    if(turnOffDispatchCodes.size() > 0){
        log.info(logPrefix + ', stage=dispatch_turn_off_start, locations=' + formatLocationLog(turnOffDispatchCodes, 20))
        let turnOffResult = turnOffLedByArticleId({
            stationCode: stationCode,
            articleIds: turnOffDispatchCodes,
            traceId: traceId,
            waveNo: payload.waveNo,
            userId: payload.userId
        })
        log.info(logPrefix + ', stage=dispatch_turn_off_result, success=' + turnOffResult.success + ', status=' + turnOffResult.status + ', message=' + turnOffResult.message)
        assertLightControlSuccess('TURN_OFF', turnOffResult)
    }

    if(turnOnMultiDispatchCodes.size() > 0){
        log.info(logPrefix + ', stage=dispatch_turn_on_multi_start, color=' + multiUserColor + ', duration=' + durationVal + ', locations=' + formatLocationLog(turnOnMultiDispatchCodes, 20))
        let turnOnMultiResult = turnOnLedByArticleId({
            stationCode: stationCode,
            color: multiUserColor,
            duration: durationVal,
            articleIds: turnOnMultiDispatchCodes,
            traceId: traceId,
            waveNo: payload.waveNo,
            userId: payload.userId
        })
        log.info(logPrefix + ', stage=dispatch_turn_on_multi_result, success=' + turnOnMultiResult.success + ', status=' + turnOnMultiResult.status + ', message=' + turnOnMultiResult.message)
        assertLightControlSuccess('TURN_ON_MULTI', turnOnMultiResult)
    }

    for(colorKey in singleColorKeys){
        let locationList = singleColorLocationMap[colorKey]
        if(locationList != null && locationList.size() > 0){
            log.info(logPrefix + ', stage=dispatch_turn_on_single_start, color=' + colorKey + ', duration=' + durationVal + ', locations=' + formatLocationLog(locationList, 20))
            let turnOnSingleResult = turnOnLedByArticleId({
                stationCode: stationCode,
                color: colorKey,
                duration: durationVal,
                articleIds: locationList,
                traceId: traceId,
                waveNo: payload.waveNo,
                userId: payload.userId
            })
            log.info(logPrefix + ', stage=dispatch_turn_on_single_result, color=' + colorKey + ', success=' + turnOnSingleResult.success + ', status=' + turnOnSingleResult.status + ', message=' + turnOnSingleResult.message)
            assertLightControlSuccess('TURN_ON_SINGLE', turnOnSingleResult)
        }
    }

    let turnOnAisleDispatchCodes = []
    let turnOnAisleDispatchCodeSet = {}
    for(locationCode in turnOnMultiDispatchCodes){
        addUnique(turnOnAisleDispatchCodes, turnOnAisleDispatchCodeSet, locationCode)
    }
    for(colorKey in singleColorKeys){
        let locationList = singleColorLocationMap[colorKey]
        if(locationList != null){
            for(locationCode in locationList){
                addUnique(turnOnAisleDispatchCodes, turnOnAisleDispatchCodeSet, locationCode)
            }
        }
    }

    for(locationCode in turnOffDispatchCodes){
        try {
            turnOffAisleLedByLotNo({
                lotNo: locationCode
            })
        } catch {

        }
    }
    for(locationCode in turnOnAisleDispatchCodes){
        try {
            turnOnAisleLedByLotNo({
                lotNo: locationCode
            })
        } catch {

        }
    }

    log.info(logPrefix + ', stage=dispatch_done, aisleTurnOff=' + turnOffDispatchCodes.size() + ', aisleTurnOn=' + turnOnAisleDispatchCodes.size())

    exit 200, '指令发送成功'
} catch (e) {
    log.error('api=control, waveNo=' + payload.waveNo + ', userId=' + payload.userId + ', mode=' + modeVal + ', traceId=' + traceId + ', stage=error, message=' + e.message)
    exit 500, '指令发送失败: ' + e.message
} finally {
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
    }
}

