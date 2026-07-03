{
  "properties" : { },
  "id" : "9dvgr4la975s18sogzyialv59fawnb10",
  "script" : null,
  "groupId" : "qou8in0pu4dvva7g20trefax4lo26ye8",
  "name" : "亮灯灭灯控制",
  "createTime" : null,
  "updateTime" : 1783070347165,
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
  "requestBody" : "{\"waveNo\":\"1W2605210002\",\"userId\":\"DZJH\",\"mode\":2}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"指令发送成功\",\n    \"data\": null,\n    \"timestamp\": 1780578231894,\n    \"executeTime\": 84862\n}",
  "description" : "亮灯/灭灯控制接口。mode=1/2启用主单并重算灯态，mode=3/4禁用主单并重算灯态。",
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
import cn.hutool.json.JSONUtil
import log
import java.util.UUID
import com.dtflys.forest.Forest
import '@/aims/led/turnOffLedByArticleId' as turnOffLedByArticleId
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import '@/utils/getUserLightColor' as getUserLightColor
import '@/led/turnOnAisleLedByLotNo' as turnOnAisleLedByLotNo
import '@/led/turnOffAisleLedByLotNo' as turnOffAisleLedByLotNo

log.info("亮灯灭灯控制: {}", JSONUtil.toJsonStr(body))

if (body == null) {
    exit 400, '请求体不能为空'
}

const mixColor = Forest.config().getVariableValue("mixColor")
const stationCode = Forest.config().getVariableValue("stationCode")

const globalLockKey = `light:picking:upload:global_${body.waveNo}`
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

// 锁过期时间：比业务操作最大耗时稍长，超时自动释放避免死锁
const lockExpireMillis = 60000

let acquireGlobalLock = (lockKey, timeoutSeconds) => {
    let maxAttempts = 20
    if(timeoutSeconds != null && timeoutSeconds > 0){
        maxAttempts = timeoutSeconds * 20
    }
    let attempt = 0
    while(attempt < maxAttempts){
        attempt = attempt + 1
        try {
            if(redis.tryLock(lockKey, lockExpireMillis)){
                return true
            }
        } catch (redisLockErr) {
            log.warn(logPrefix + ', redis tryLock error: ' + redisLockErr.message)
        }
        try {
            java.lang.Thread.sleep(50)
        } catch (ignoreSleepErr) {
        }
    }
    return false
}

let releaseGlobalLock = (lockKey) => {
    try {
        redis.unlock(lockKey)
    } catch (redisUnlockErr) {
        log.warn(logPrefix + ', redis unlock error: ' + redisUnlockErr.message)
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

// 重算库位状态并写入 t_location_status，只统计启用主单(status=1)下的明细
let recalcLocationStatusBatch = (locationCodes) => {
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
        let inSql = ""
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
                inner join t_picking_upload m on m.id = d.bill_id
                where d.location_code in (""" + inSql + """)
                  and ifnull(d.location_code, '') <> ''
                  and ifnull(d.bill_id, '') <> ''
                  and ifnull(m.status, 1) = 1
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

    let updatedTime = new Date()
    for(codeChunk in chunks){
        let upsertSql = """
            insert into t_location_status (
                location_code,
                status0_user_count,
                status1_user_count,
                updated_time,
                version
            ) values
        """
        let upsertValuesSql = ""
        let upsertParams = {}
        let rowIndex = 0
        for(locationCode in codeChunk){
            let snapshot = statusSnapshotMap[locationCode]
            let status0UserCount = 0
            let status1UserCount = 0
            if(snapshot != null){
                if(snapshot.status0UserCount != null){
                    status0UserCount = snapshot.status0UserCount
                }
                if(snapshot.status1UserCount != null){
                    status1UserCount = snapshot.status1UserCount
                }
            }
            if(rowIndex > 0){
                upsertValuesSql = upsertValuesSql + ","
            }
            let suffix = "_" + rowIndex
            upsertValuesSql = upsertValuesSql + "("
                + "#{locationCode" + suffix + "},"
                + "#{status0UserCount" + suffix + "},"
                + "#{status1UserCount" + suffix + "},"
                + "#{updatedTime" + suffix + "},"
                + "1)"
            upsertParams["locationCode" + suffix] = locationCode
            upsertParams["status0UserCount" + suffix] = status0UserCount
            upsertParams["status1UserCount" + suffix] = status1UserCount
            upsertParams["updatedTime" + suffix] = updatedTime
            rowIndex = rowIndex + 1
        }
        db.update(upsertSql + upsertValuesSql + """
            on duplicate key update
                status0_user_count = values(status0_user_count),
                status1_user_count = values(status1_user_count),
                updated_time = values(updated_time),
                version = version + 1
        """, upsertParams)
    }

    return statusSnapshotMap
}

// 查询单用户库位的归属用户（只查启用主单）
let querySingleUnpickedUserMap = (locationCodes) => {
    let result = {}
    if(locationCodes == null || locationCodes.size() == 0){
        return result
    }

    let params = {}
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

    let rows = db.select("""
        select distinct
            d.location_code as location_code,
            m.user_id as user_id
        from t_picking_upload_detail d
        straight_join t_picking_upload m on m.id = d.bill_id
        where d.location_code in (""" + inSql + """)
          and ifnull(d.status, 0) <> 1
          and ifnull(m.status, 1) = 1
    """, params)

    if(rows != null){
        for(row in rows){
            let locationCode = row.location_code
            if(locationCode == null || locationCode == ''){
                locationCode = row.locationCode
            }
            let userId = row.user_id
            if(userId == null || userId == ''){
                userId = row.userId
            }
            if(locationCode != null && locationCode != '' && userId != null && userId != ''){
                result[locationCode] = userId
            }
        }
    }
    return result
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
    let affectedLedColorKeys = []
    let affectedLedColorKeySet = {}
    let pairRepresentativeLocationMap = {}
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
                led_id,
                color
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
                let color = row.color
                if(color != null && color != ''){
                    color = (color + '').trim().toUpperCase()
                }
                if(locationCode != null && locationCode != '' && ledId != null && ledId != ''){
                    result.locationLedMap[locationCode] = ledId
                    addUnique(affectedLedIds, affectedLedIdSet, ledId)
                    if(color != null && color != ''){
                        let pairKey = ledId + '|' + color
                        if(pairRepresentativeLocationMap[pairKey] == null){
                            pairRepresentativeLocationMap[pairKey] = locationCode
                        }
                        addUnique(affectedLedColorKeys, affectedLedColorKeySet, pairKey)
                    } else {
                        addUnique(result.candidateLocationCodes, result.candidateLocationCodeSet, locationCode)
                    }
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
                upper(color) as color,
                sum(case when ifnull(status, 0) = 1 then 1 else 0 end) as active_cnt,
                min(location_code) as representative_location_code
            from t_location_led
            where led_id in (""" + inData.inSql + """)
            group by led_id, upper(color)
        """, inData.params)

        if(rows != null){
            for(row in rows){
                let ledId = row.led_id
                if(ledId == null || ledId == ''){
                    ledId = row.ledId
                }
                let color = row.color
                if(color != null && color != ''){
                    color = (color + '').trim().toUpperCase()
                }
                let pairKey = ledId + '|' + color
                if(affectedLedColorKeySet[pairKey] == null){
                    continue
                }
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
                let representativeLocationCode = pairRepresentativeLocationMap[pairKey]
                if(representativeLocationCode == null || representativeLocationCode == ''){
                    representativeLocationCode = row.representativeLocationCode
                }
                if(representativeLocationCode == null || representativeLocationCode == ''){
                    representativeLocationCode = row.representative_location_code
                }
                if(representativeLocationCode != null && representativeLocationCode != ''){
                    addUnique(result.candidateLocationCodes, result.candidateLocationCodeSet, representativeLocationCode)
                }
            }
        }
    }
    return result
}

// 根据重算后的 t_location_status 决定过道 LED 开关（按库位独立判断，不同颜色互不影响）
let resolveAisleDispatchPlan = (affectedLocationCodes) => {
    let result = {
        turnOnLocationCodes: [],
        turnOffLocationCodes: [],
        turnOnLocationCodeSet: {},
        turnOffLocationCodeSet: {}
    }
    if(affectedLocationCodes == null || affectedLocationCodes.size() == 0){
        return result
    }

    // 每个库位的巷道灯颜色独立，按库位自身的 status0_user_count 决定亮灭
    let chunks = splitLocationCodeChunks(affectedLocationCodes, locationChunkSize)
    for(codeChunk in chunks){
        let params = {}
        let inSql = ""
        let idx = 0
        for(locationCode in codeChunk){
            let key = 'locationCode' + idx
            params[key] = locationCode
            if(idx > 0){
                inSql = inSql + ','
            }
            inSql = inSql + '#{' + key + '}'
            idx = idx + 1
        }

        let rows = db.select("""
            select location_code,
                   status0_user_count
            from t_location_status
            where location_code in (""" + inSql + """)
        """, params)

        let activeSet = {}
        if(rows != null){
            for(row in rows){
                let locationCode = row.locationCode
                if(locationCode == null || locationCode == ''){ locationCode = row.locationCode }
                let status0 = row.status0_user_count
                if(status0 == null){ status0 = row.status0UserCount }
                if(status0 == null){ status0 = 0 }
                if(locationCode != null && locationCode != '' && status0 > 0){
                    activeSet[locationCode] = 1
                }
            }
        }

        for(locationCode in codeChunk){
            if(activeSet[locationCode] != null){
                addUnique(result.turnOnLocationCodes, result.turnOnLocationCodeSet, locationCode)
            } else {
                addUnique(result.turnOffLocationCodes, result.turnOffLocationCodeSet, locationCode)
            }
        }
    }
    return result
}

let recordLightControlResult = (action, result) => {
    if(result == null){
        log.warn(logPrefix + ', stage=light_control_failed, action=' + action + ', reason=返回为空')
        return false
    }
    if(result.success == false){
        let message = '灯控调用失败(' + action + ')'
        if(result.message != null && result.message != ''){
            message = message + ': ' + result.message
        }
        log.warn(logPrefix + ', stage=light_control_failed, action=' + action + ', status=' + result.status + ', message=' + message + ', responseText=' + result.responseText)
        return false
    }
    return true
}

// ────────── 参数校验 ──────────

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
if (modeVal == 3 || modeVal == 4) {
    durationVal = '0'
} else if (durationVal == null || durationVal == '') {
    durationVal = '60m'
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

    // 获取受影响库位
    let affectedLocationCodes = resolveAffectedLocationCodes(payload)
    let allLocationCodes = queryWaveLocationCodes(payload.waveNo, affectedLocationCodes)
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

    // 更新主单状态：亮灯启用，灭灯禁用
    let newMasterStatus = 1
    if(modeVal == 3 || modeVal == 4){
        newMasterStatus = 0
    }
    db.update("""
        update t_picking_upload
           set status = #{status}
         where wave_no = #{waveNo}
           and user_id = #{userId}
    """, {
        status: newMasterStatus,
        waveNo: payload.waveNo,
        userId: payload.userId
    })
    log.info(logPrefix + ', stage=master_status_updated, newStatus=' + newMasterStatus)

    // 锁内事务：重算 t_location_status（只统计启用主单的明细）
    let transState = anyline.beginTrans()
    let statusSnapshotMap = recalcLocationStatusBatch(allLocationCodes)
    anyline.commitTrans(transState)
    transState = null

    // 亮灯时长：mode=3/4 重算后仍可能有亮灯库位（其他用户），使用默认时长
    let turnOnDuration = durationVal
    if(modeVal == 3 || modeVal == 4){
        turnOnDuration = '60m'
    }

    // 按重算结果分类库位
    let turnOffLocationCodes = []
    let multiUserLocationCodes = []
    let singleUserCandidateCodes = []
    for(locationCode in allLocationCodes){
        let snapshot = statusSnapshotMap[locationCode]
        let status0UserCount = 0
        if(snapshot != null && snapshot.status0UserCount != null){
            status0UserCount = snapshot.status0UserCount
        }
        if(status0UserCount <= 0){
            turnOffLocationCodes.add(locationCode)
        } else if(status0UserCount > 1){
            multiUserLocationCodes.add(locationCode)
        } else {
            singleUserCandidateCodes.add(locationCode)
        }
    }

    // 查询单用户库位归属，确定亮灯颜色
    let singleUserLocationOwnerMap = querySingleUnpickedUserMap(singleUserCandidateCodes)
    let singleColorLocationMap = {}
    let singleColorKeys = []
    let singleColorKeySet = {}
    for(locationCode in singleUserCandidateCodes){
        let ownerUserId = singleUserLocationOwnerMap[locationCode]
        let singleColor = 'GREEN'
        if(ownerUserId != null && ownerUserId != ''){
            let mappedColor = getUserLightColor(ownerUserId)
            if(mappedColor != null && mappedColor != ''){
                singleColor = mappedColor
            }
        }
        if(singleColorLocationMap[singleColor] == null){
            singleColorLocationMap[singleColor] = []
            addUnique(singleColorKeys, singleColorKeySet, singleColor)
        }
        singleColorLocationMap[singleColor].add(locationCode)
    }

    // 汇总所有需要亮灯的库位
    let turnOnLocationCodes = []
    let turnOnLocationCodeSet = {}
    for(locationCode in multiUserLocationCodes){
        addUnique(turnOnLocationCodes, turnOnLocationCodeSet, locationCode)
    }
    for(locationCode in singleUserCandidateCodes){
        addUnique(turnOnLocationCodes, turnOnLocationCodeSet, locationCode)
    }

    let turnOnAisleResult = resolveAisleLedTurnOnCandidates(turnOnLocationCodes)
    let turnOffAisleResult = resolveAisleLedTurnOffCandidates(turnOffLocationCodes)

    // 多用户分派（按 LED 去重）
    let turnOnMultiDispatchCodes = []
    let turnOnMultiDispatchCodeSet = {}
    let ledMultiSet = {}
    let turnOnLocationLedMap = turnOnAisleResult.locationLedMap
    let representativeByLed = turnOnAisleResult.representativeByLed
    for(locationCode in multiUserLocationCodes){
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

    // 单用户分派（标签灯按 articleId 独立分派，不需要按 LED 去重）
    let singleLocationCodeSet = {}
    let singleColorDispatchMap = {}
    let singleColorDispatchKeys = []
    let singleColorDispatchKeySet = {}
    for(locationCode in singleUserCandidateCodes){
        let ownerUserId = singleUserLocationOwnerMap[locationCode]
        let singleColor = 'GREEN'
        if(ownerUserId != null && ownerUserId != ''){
            let mappedColor = getUserLightColor(ownerUserId)
            if(mappedColor != null && mappedColor != ''){
                singleColor = mappedColor
            }
        }
        if(singleLocationCodeSet[locationCode] != null){
            continue
        }
        singleLocationCodeSet[locationCode] = 1
        if(singleColorDispatchMap[singleColor] == null){
            singleColorDispatchMap[singleColor] = []
            addUnique(singleColorDispatchKeys, singleColorDispatchKeySet, singleColor)
        }
        singleColorDispatchMap[singleColor].add(locationCode)
    }

    let turnOffDispatchCodes = turnOffAisleResult.candidateLocationCodes

    // 锁外执行灯控，缩短全局锁占用时间
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
        lockAcquired = false
    }

    // 执行灭灯
    if(turnOffLocationCodes.size() > 0){
        try {
            let turnOffResult = turnOffLedByArticleId({
                stationCode: stationCode,
                articleIds: turnOffLocationCodes,
                traceId: traceId,
                waveNo: payload.waveNo,
                userId: payload.userId
            })
            recordLightControlResult('TURN_OFF', turnOffResult)
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_turn_off_failed, message=' + lightErr.message)
        }
    }

    // 执行多用户亮灯
    if(turnOnMultiDispatchCodes.size() > 0){
        try {
            let multiColor = mixColor
            if(multiColor == null || multiColor == ''){
                multiColor = 'CYAN'
            }
            let turnOnMultiResult = turnOnLedByArticleId({
                stationCode: stationCode,
                color: multiColor,
                duration: turnOnDuration,
                articleIds: turnOnMultiDispatchCodes,
                traceId: traceId,
                waveNo: payload.waveNo,
                userId: payload.userId
            })
            recordLightControlResult('TURN_ON_MULTI', turnOnMultiResult)
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_turn_on_multi_failed, message=' + lightErr.message)
        }
    }

    // 执行单用户亮灯（按颜色分组）
    for(colorKey in singleColorDispatchKeys){
        let locationList = singleColorDispatchMap[colorKey]
        if(locationList != null && locationList.size() > 0){
            try {
                let turnOnSingleResult = turnOnLedByArticleId({
                    stationCode: stationCode,
                    color: colorKey,
                    duration: turnOnDuration,
                    articleIds: locationList,
                    traceId: traceId,
                    waveNo: payload.waveNo,
                    userId: payload.userId
                })
                recordLightControlResult('TURN_ON_SINGLE', turnOnSingleResult)
            } catch (lightErr) {
                log.warn(logPrefix + ', stage=dispatch_turn_on_single_failed, color=' + colorKey + ', message=' + lightErr.message)
            }
        }
    }

    // 过道灯调度（基于重算后的 t_location_status）
    let aisleDispatchPlan = resolveAisleDispatchPlan(allLocationCodes)
    for(locationCode in aisleDispatchPlan.turnOffLocationCodes){
        try {
            turnOffAisleLedByLotNo({
                mode: 'client',
                lotNo: locationCode
            })
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_aisle_turn_off_failed, locationCode=' + locationCode + ', message=' + lightErr.message)
        }
        try { Thread.sleep(500) } catch (ignore) {}
    }
    for(locationCode in aisleDispatchPlan.turnOnLocationCodes){
        try {
            turnOnAisleLedByLotNo({
                mode: 'client',
                lotNo: locationCode
            })
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_aisle_turn_on_failed, locationCode=' + locationCode + ', message=' + lightErr.message)
        }
        try { Thread.sleep(500) } catch (ignore) {}
    }

    log.info(logPrefix + ', stage=dispatch_done, turnOff=' + turnOffLocationCodes.size() + ', turnOnMulti=' + turnOnMultiDispatchCodes.size() + ', singleColors=' + singleColorDispatchKeys.size())
    exit 200, '指令发送成功'
} catch (e) {
    log.error('api=control, waveNo=' + payload.waveNo + ', userId=' + payload.userId + ', mode=' + modeVal + ', traceId=' + traceId + ', stage=error, message=' + e.message)
    exit 500, '指令发送失败: ' + e.message
} finally {
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
    }
}
