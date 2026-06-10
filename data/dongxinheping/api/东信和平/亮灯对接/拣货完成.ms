{
  "properties" : { },
  "id" : "9h2zcovwd71mwjgisuy0yw72lvj6xlup",
  "script" : null,
  "groupId" : "l5kl0q2xiqa4ryz349c3fkzj3hzm07vw",
  "name" : "拣货完成",
  "createTime" : null,
  "updateTime" : 1780563039307,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/complete",
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
  "requestBody" : "{\n    \"waveNo\": \"WV202603250001\",\n    \"userId\": \"USER001\",\n    \"details\": [\n        {\n            \"id\": \"DTL001\",\n            \"materialCode\": \"MAT001\",\n            \"batchNo\": \"B20260325\",\n            \"locationCode\": \"A1-11-02-01-01\",\n            \"planQuantity\": 100,\n            \"actualQuantity\": 100,\n            \"updateTime\": 1648195200000\n        }\n    ]\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"拣货完成同步成功\",\n    \"data\": null,\n    \"timestamp\": 1777018646441,\n    \"executeTime\": 93\n}",
  "description" : "拣货完成接口，更新明细状态后自动重算库位状态并执行灯控（亮灯/灭灯）。",
  "requestBodyDefinition" : {
    "name" : "root",
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
      "value" : "USER002",
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
      "name" : "details",
      "value" : "",
      "description" : "完成明细",
      "required" : true,
      "dataType" : "Array",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ {
        "name" : "",
        "value" : "",
        "description" : "明细项",
        "required" : true,
        "dataType" : "Object",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ {
          "name" : "id",
          "value" : "DTL001",
          "description" : "明细ID",
          "required" : true,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "materialCode",
          "value" : "MAT001",
          "description" : "物料代码",
          "required" : true,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "batchNo",
          "value" : "B20260325",
          "description" : "批次号",
          "required" : true,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "locationCode",
          "value" : "A1-11-01-01-01",
          "description" : "库位号",
          "required" : true,
          "dataType" : "String",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "planQuantity",
          "value" : "100",
          "description" : "计划拣货数量",
          "required" : true,
          "dataType" : "Integer",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "actualQuantity",
          "value" : "100",
          "description" : "实际拣货数量",
          "required" : true,
          "dataType" : "Integer",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "updateTime",
          "value" : "1648195200000",
          "description" : "更新时间（毫秒）",
          "required" : true,
          "dataType" : "Long",
          "type" : null,
          "defaultValue" : null,
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        } ]
      } ]
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
      "value" : "拣货完成同步成功",
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

log.info("拣货完成body: {}", JSONUtil.toJsonStr(body))

const mixColor = Forest.config().getVariableValue("mixColor")
const stationCode = Forest.config().getVariableValue("stationCode")

const globalLockKey = `light:picking:upload:global_${body.waveNo}`
const lockTimeoutSeconds = 10
const locationChunkSize = 200

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
            select location_code, led_id
            from t_location_led
            where location_code in (""" + inData.inSql + """)
        """, inData.params)
        if(rows != null){
            for(row in rows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){ locationCode = row.locationCode }
                let ledId = row.led_id
                if(ledId == null || ledId == ''){ ledId = row.ledId }
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
            select location_code, led_id, color
            from t_location_led
            where location_code in (""" + inData.inSql + """)
        """, inData.params)
        if(rows != null){
            for(row in rows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){ locationCode = row.locationCode }
                let ledId = row.led_id
                if(ledId == null || ledId == ''){ ledId = row.ledId }
                let color = row.color
                if(color != null && color != ''){ color = (color + '').trim().toUpperCase() }
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

    if(affectedLedIds.size() == 0){ return result }

    let ledChunks = splitLocationCodeChunks(affectedLedIds, locationChunkSize)
    for(ledChunk in ledChunks){
        let inData = buildInSqlParams(ledChunk, 'aisleLedId')
        let rows = db.select("""
            select led_id, upper(color) as color,
                sum(case when ifnull(status, 0) = 1 then 1 else 0 end) as active_cnt,
                min(location_code) as representative_location_code
            from t_location_led
            where led_id in (""" + inData.inSql + """)
            group by led_id, upper(color)
        """, inData.params)
        if(rows != null){
            for(row in rows){
                let ledId = row.led_id
                if(ledId == null || ledId == ''){ ledId = row.ledId }
                let color = row.color
                if(color != null && color != ''){ color = (color + '').trim().toUpperCase() }
                let pairKey = ledId + '|' + color
                if(affectedLedColorKeySet[pairKey] == null){ continue }
                let activeCnt = row.active_cnt
                if(activeCnt == null){ activeCnt = row.activeCnt }
                if(activeCnt == null){ activeCnt = 0 }
                if(activeCnt > 0){ continue }
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
                let locationCode = row.location_code
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

if(body == null){
    exit 400, '请求体不能为空'
}

let payload = body
let details = payload.details
let traceId = payload.traceId
if(traceId == null || traceId == ''){
    traceId = UUID.randomUUID().toString().replace('-', '')
}

if(!payload.waveNo){
    exit 400, 'waveNo不能为空'
}
if(!payload.userId){
    exit 400, 'userId不能为空'
}
if(details == null || details.size() == 0){
    exit 400, 'details不能为空'
}

let index = 0
let detailUniqMap = {}
let affectedLocationCodes = []
let affectedLocationCodeSet = {}
for(item in details){
    if(!item.id) {
        exit 400, 'details[' + index + '].id不能为空'
    }
    if(!item.materialCode){
        exit 400, 'details[' + index + '].materialCode不能为空'
    }
    if(!item.batchNo){
        exit 400, 'details[' + index + '].batchNo不能为空'
    }
    if(!item.locationCode){
        exit 400, 'details[' + index + '].locationCode不能为空'
    }
    if(item.planQuantity == null){
        exit 400, 'details[' + index + '].planQuantity不能为空'
    }
    if(item.actualQuantity == null){
        exit 400, 'details[' + index + '].actualQuantity不能为空'
    }
    if(item.updateTime == null){
        exit 400, 'details[' + index + '].updateTime不能为空'
    }
    if(item.planQuantity < 0){
        exit 400, 'details[' + index + '].planQuantity不能小于0'
    }
    if(item.actualQuantity < 0){
        exit 400, 'details[' + index + '].actualQuantity不能小于0'
    }

    let uniqKey = item.materialCode + '|' + item.batchNo + '|' + item.locationCode
    if(detailUniqMap[uniqKey] != null){
        exit 400, 'details[' + index + ']与前序明细重复(物料+批次+库位)'
    }
    detailUniqMap[uniqKey] = 1
    addUnique(affectedLocationCodes, affectedLocationCodeSet, item.locationCode)
    index = index + 1
}

let waveNo = payload.waveNo
let userId = payload.userId
let now = new Date()
let logPrefix = 'traceId=' + traceId + ', api=complete, waveNo=' + waveNo + ', userId=' + userId

let transState = null
let lockAcquired = false

try {
    log.info(logPrefix + ', stage=receive, detailCount=' + details.size())

    // 事务1：完成明细落库
    transState = anyline.beginTrans()

    let master = db.table('t_picking_upload').where().eq('wave_no', waveNo).eq('user_id', userId).selectOne()
    if(master == null || master.id == null || master.id == ''){
        anyline.rollbackTrans(transState)
        transState = null
        exit 400, '未找到对应拣货上传主单'
    }

    let masterId = master.id
    let detailRows = []
    let latestUpdateTime = 0

    for(item in details){
        let actualQty = item.actualQuantity
        let planQty = item.planQuantity
        let statusVal = 0
        if(actualQty >= planQty){
            statusVal = 1
        }

        if(item.updateTime != null && item.updateTime > latestUpdateTime){
            latestUpdateTime = item.updateTime
        }

        detailRows.add({
            id: item.id,
            wave_no: waveNo,
            bill_id: masterId,
            material_code: item.materialCode,
            batch_no: item.batchNo,
            location_code: item.locationCode,
            plan_quantity: planQty,
            actual_quantity: actualQty,
            status: statusVal,
            create_time: now
        })
    }

    if(detailRows.size() > 0){
        let batchUpsertSql = """
            insert into t_picking_upload_detail (
                id, wave_no, bill_id, material_code, batch_no,
                location_code, plan_quantity, actual_quantity, status, create_time
            ) values
        """
        let batchUpsertValuesSql = ""
        let batchUpsertParams = {}
        let rowIndex = 0
        for(detailRow in detailRows){
            if(rowIndex > 0){
                batchUpsertValuesSql = batchUpsertValuesSql + ","
            }
            let suffix = "_" + rowIndex
            batchUpsertValuesSql = batchUpsertValuesSql + "("
                + "#{id" + suffix + "},"
                + "#{waveNo" + suffix + "},"
                + "#{billId" + suffix + "},"
                + "#{materialCode" + suffix + "},"
                + "#{batchNo" + suffix + "},"
                + "#{locationCode" + suffix + "},"
                + "#{planQuantity" + suffix + "},"
                + "#{actualQuantity" + suffix + "},"
                + "#{status" + suffix + "},"
                + "#{createTime" + suffix + "})"
            batchUpsertParams["id" + suffix] = detailRow.id
            batchUpsertParams["waveNo" + suffix] = detailRow.wave_no
            batchUpsertParams["billId" + suffix] = detailRow.bill_id
            batchUpsertParams["materialCode" + suffix] = detailRow.material_code
            batchUpsertParams["batchNo" + suffix] = detailRow.batch_no
            batchUpsertParams["locationCode" + suffix] = detailRow.location_code
            batchUpsertParams["planQuantity" + suffix] = detailRow.plan_quantity
            batchUpsertParams["actualQuantity" + suffix] = detailRow.actual_quantity
            batchUpsertParams["status" + suffix] = detailRow.status
            batchUpsertParams["createTime" + suffix] = detailRow.create_time
            rowIndex = rowIndex + 1
        }
        db.update(batchUpsertSql + batchUpsertValuesSql + """
            on duplicate key update
                plan_quantity = values(plan_quantity),
                actual_quantity = values(actual_quantity),
                status = values(status)
        """, batchUpsertParams)
    }

    if(latestUpdateTime == 0){
        latestUpdateTime = now.getTime()
    }

    anyline.update('t_picking_upload', {
        id: masterId,
        wave_no: waveNo,
        user_id: userId,
        user_name: payload.userName,
        detail_count: master.detail_count,
        update_time: latestUpdateTime
    })

    anyline.commitTrans(transState)
    transState = null

    // 全局同步锁
    lockAcquired = acquireGlobalLock(globalLockKey, lockTimeoutSeconds)
    if(!lockAcquired){
        log.warn(logPrefix + ', stage=lock_busy')
        exit 503, '系统繁忙，请稍后重试'
    }

    // 事务2：重算 t_location_status（主单不动，detail.status=1 的不再计入 status0）
    transState = anyline.beginTrans()
    let statusSnapshotMap = recalcLocationStatusBatch(affectedLocationCodes)
    anyline.commitTrans(transState)
    transState = null

    // 按重算结果分类库位
    let turnOffLocationCodes = []
    let multiUserLocationCodes = []
    let singleUserCandidateCodes = []
    for(locationCode in affectedLocationCodes){
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

    // 汇总亮灯库位
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
        if(singleLocationCodeSet[locationCode] != null){ continue }
        singleLocationCodeSet[locationCode] = 1
        if(singleColorDispatchMap[singleColor] == null){
            singleColorDispatchMap[singleColor] = []
            addUnique(singleColorDispatchKeys, singleColorDispatchKeySet, singleColor)
        }
        singleColorDispatchMap[singleColor].add(locationCode)
    }

    let turnOffDispatchCodes = turnOffAisleResult.candidateLocationCodes

    // 锁外执行灯控
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
        lockAcquired = false
    }

    // 灭灯
    if(turnOffLocationCodes.size() > 0){
        try {
            let turnOffResult = turnOffLedByArticleId({
                stationCode: stationCode,
                articleIds: turnOffLocationCodes,
                traceId: traceId,
                waveNo: waveNo,
                userId: userId
            })
            recordLightControlResult('TURN_OFF', turnOffResult)
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_turn_off_failed, message=' + lightErr.message)
        }
    }

    // 多用户亮灯
    if(turnOnMultiDispatchCodes.size() > 0){
        try {
            let multiColor = mixColor
            if(multiColor == null || multiColor == ''){ multiColor = 'CYAN' }
            let turnOnMultiResult = turnOnLedByArticleId({
                stationCode: stationCode,
                color: multiColor,
                duration: '60m',
                articleIds: turnOnMultiDispatchCodes,
                traceId: traceId,
                waveNo: waveNo,
                userId: userId
            })
            recordLightControlResult('TURN_ON_MULTI', turnOnMultiResult)
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_turn_on_multi_failed, message=' + lightErr.message)
        }
    }

    // 单用户亮灯（按颜色分组）
    for(colorKey in singleColorDispatchKeys){
        let locationList = singleColorDispatchMap[colorKey]
        if(locationList != null && locationList.size() > 0){
            try {
                let turnOnSingleResult = turnOnLedByArticleId({
                    stationCode: stationCode,
                    color: colorKey,
                    duration: '60m',
                    articleIds: locationList,
                    traceId: traceId,
                    waveNo: waveNo,
                    userId: userId
                })
                recordLightControlResult('TURN_ON_SINGLE', turnOnSingleResult)
            } catch (lightErr) {
                log.warn(logPrefix + ', stage=dispatch_turn_on_single_failed, color=' + colorKey + ', message=' + lightErr.message)
            }
        }
    }

    // 过道灯调度
    let aisleDispatchPlan = resolveAisleDispatchPlan(affectedLocationCodes)
    for(locationCode in aisleDispatchPlan.turnOffLocationCodes){
        try {
            turnOffAisleLedByLotNo({ mode: 'client', lotNo: locationCode })
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_aisle_turn_off_failed, locationCode=' + locationCode + ', message=' + lightErr.message)
        }
        try { Thread.sleep(500) } catch (ignore) {}
    }
    for(locationCode in aisleDispatchPlan.turnOnLocationCodes){
        try {
            turnOnAisleLedByLotNo({ mode: 'client', lotNo: locationCode })
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_aisle_turn_on_failed, locationCode=' + locationCode + ', message=' + lightErr.message)
        }
        try { Thread.sleep(500) } catch (ignore) {}
    }

    log.info(logPrefix + ', stage=dispatch_done, turnOff=' + turnOffLocationCodes.size() + ', turnOnMulti=' + turnOnMultiDispatchCodes.size() + ', singleColors=' + singleColorDispatchKeys.size())
    exit 200, '拣货完成同步成功'
} catch (e) {
    if(transState != null){
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {
        }
    }
    log.error(logPrefix + ', stage=error, message=' + e.message)
    exit 500, '拣货完成同步失败: ' + e.message
} finally {
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
    }
}
