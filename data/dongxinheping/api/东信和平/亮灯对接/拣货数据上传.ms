{
  "properties" : { },
  "id" : "oj8xw89pfd3jefa8dwaqv4ye3lqtblwf",
  "script" : null,
  "groupId" : "l5kl0q2xiqa4ryz349c3fkzj3hzm07vw",
  "name" : "拣货数据上传",
  "createTime" : null,
  "updateTime" : 1777020545208,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/upload",
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
  "requestBody" : "{\n  \"waveNo\": \"WV202603250001\",\n  \"userId\": \"USER001\",\n  \"userName\": \"张三\",\n  \"updateTime\": 1648195200000,\n  \"details\": [\n    {\n      \"id\": \"DTL001\",\n      \"materialCode\": \"MAT001\",\n      \"batchNo\": \"B20260325\",\n      \"locationCode\": \"A1-11-02-01-01\",\n      \"planQuantity\": 100,\n      \"actualQuantity\": 100,\n      \"status\": 0\n    },\n    {\n      \"id\": \"D3003\",\n      \"materialCode\": \"MAT-013\",\n      \"batchNo\": \"BATCH-ERR-3\",\n      \"locationCode\": \"A1-11-01-01-01\",\n      \"planQuantity\": 6,\n      \"actualQuantity\": 6,\n      \"status\": 0\n    }\n  ]\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"拣货数据上传成功\",\n    \"data\": null,\n    \"timestamp\": 1776999112417,\n    \"executeTime\": 1499\n}",
  "description" : "当用户进入拣选页面时，上传波次内待拣明细并刷新主从表数据。",
  "requestBodyDefinition" : {
    "name" : "root",
    "value" : "",
    "description" : "拣货数据上传请求",
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
      "name" : "userName",
      "value" : "张三",
      "description" : "用户姓名",
      "required" : false,
      "dataType" : "String",
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
    }, {
      "name" : "details",
      "value" : "",
      "description" : "拣货明细",
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
          "value" : "A-01-02",
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
          "value" : "0",
          "description" : "实际拣货数量",
          "required" : false,
          "dataType" : "Integer",
          "type" : null,
          "defaultValue" : "0",
          "validateType" : "",
          "error" : "",
          "expression" : "",
          "children" : [ ]
        }, {
          "name" : "status",
          "value" : "0",
          "description" : "状态：0-未拣 1-已拣",
          "required" : true,
          "dataType" : "Integer",
          "type" : null,
          "defaultValue" : "0",
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
      "value" : "拣货数据上传成功",
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
    }, {
      "name" : "timestamp",
      "value" : "1775538425101",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "96",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
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
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import '@/aims/led/turnOffLedByArticleId' as turnOffLedByArticleId
import '@/utils/getUserLightColor' as getUserLightColor
import '@/led/turnOnAisleLedByLotNo' as turnOnAisleLedByLotNo
import '@/led/turnOffAisleLedByLotNo' as turnOffAisleLedByLotNo

const stationCode = Forest.config().getVariableValue("stationCode")
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

let queryDetailLocationsByBillId = (billId) => {
    let locationCodes = []
    let locationCodeSet = {}
    let rows = db.select("""
        select distinct location_code
          from t_picking_upload_detail
         where bill_id = #{billId}
           and ifnull(location_code, '') <> ''
    """, {
        billId: billId
    })
    if(rows != null){
        for(row in rows){
            let locationCode = row.location_code
            if(locationCode == null || locationCode == ''){
                locationCode = row.locationCode
            }
            addUnique(locationCodes, locationCodeSet, locationCode)
        }
    }
    return locationCodes
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

    let ledIds = []
    let ledIdSet = {}
    let locationChunks = splitLocationCodeChunks(affectedLocationCodes, locationChunkSize)
    for(codeChunk in locationChunks){
        let inData = buildInSqlParams(codeChunk, 'aisleLocationCode')
        let rows = db.select("""
            select distinct led_id
              from t_location_led
             where location_code in (""" + inData.inSql + """)
               and ifnull(led_id, '') <> ''
        """, inData.params)
        if(rows != null){
            for(row in rows){
                let ledId = row.led_id
                if(ledId == null || ledId == ''){
                    ledId = row.ledId
                }
                addUnique(ledIds, ledIdSet, ledId)
            }
        }
    }

    if(ledIds.size() == 0){
        return result
    }

    let ledChunks = splitLocationCodeChunks(ledIds, locationChunkSize)
    for(ledChunk in ledChunks){
        let inData = buildInSqlParams(ledChunk, 'aisleLedId')
        let rows = db.select("""
            select
                ll.led_id as led_id,
                sum(case when ifnull(ls.status0_user_count, 0) > 0 then 1 else 0 end) as active_location_count,
                min(case when ifnull(ls.status0_user_count, 0) > 0 then ll.location_code end) as on_location_code,
                min(ll.location_code) as any_location_code
            from t_location_led ll
            left join t_location_status ls on ls.location_code = ll.location_code
            where ll.led_id in (""" + inData.inSql + """)
            group by ll.led_id
        """, inData.params)
        if(rows != null){
            for(row in rows){
                let activeLocationCount = row.active_location_count
                if(activeLocationCount == null){
                    activeLocationCount = row.activeLocationCount
                }
                if(activeLocationCount == null){
                    activeLocationCount = 0
                }

                let onLocationCode = row.on_location_code
                if(onLocationCode == null || onLocationCode == ''){
                    onLocationCode = row.onLocationCode
                }
                let anyLocationCode = row.any_location_code
                if(anyLocationCode == null || anyLocationCode == ''){
                    anyLocationCode = row.anyLocationCode
                }

                if(activeLocationCount > 0){
                    addUnique(result.turnOnLocationCodes, result.turnOnLocationCodeSet, onLocationCode)
                } else {
                    addUnique(result.turnOffLocationCodes, result.turnOffLocationCodeSet, anyLocationCode)
                }
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
    log.info(logPrefix + ', stage=light_control_success, action=' + action + ', status=' + result.status + ', message=' + result.message)
    return true
}

if(body == null){
    exit 400, '请求体不能为空'
}

let payload = body
let details = payload.details
let traceId = ensureTraceId(payload.traceId)
payload.traceId = traceId

if(!payload.waveNo){
    exit 400, 'waveNo不能为空'
}
if(!payload.userId){
    exit 400, 'userId不能为空'
}
if(payload.updateTime == null){
    exit 400, 'updateTime不能为空'
}
if(details == null || details.size() == 0){
    exit 400, 'details不能为空'
}

let index = 0
let detailIdUniqMap = {}
for(item in details){
    if(!item.id){
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
    if(item.planQuantity < 0){
        exit 400, 'details[' + index + '].planQuantity不能小于0'
    }
    if(item.actualQuantity != null && item.actualQuantity < 0){
        exit 400, 'details[' + index + '].actualQuantity不能小于0'
    }
    if(item.status == null){
        exit 400, 'details[' + index + '].status不能为空'
    }
    if(item.status != 0 && item.status != 1){
        exit 400, 'details[' + index + '].status仅支持0或1'
    }
    if(detailIdUniqMap[item.id] != null){
        exit 400, 'details[' + index + '].id重复'
    }
    detailIdUniqMap[item.id] = 1
    index = index + 1
}

let waveNo = payload.waveNo
let userId = payload.userId
let now = new Date()
let logPrefix = 'traceId=' + traceId + ', api=upload, waveNo=' + waveNo + ', userId=' + userId

let transState = null
let lockAcquired = false
let affectedLocationCodes = []
let affectedLocationCodeSet = {}

try {
    log.info(logPrefix + ', stage=receive, detailCount=' + details.size())

    // 事务1：拣货数据落库
    transState = anyline.beginTrans()

    let existingMaster = db.table('t_picking_upload').where().eq('wave_no', waveNo).eq('user_id', userId).selectOne()
    let masterId = UUID.randomUUID().toString().replace('-', '')
    if(existingMaster != null && existingMaster.id != null && existingMaster.id != ''){
        masterId = existingMaster.id
        let oldLocationCodes = queryDetailLocationsByBillId(masterId)
        for(oldLocationCode in oldLocationCodes){
            addUnique(affectedLocationCodes, affectedLocationCodeSet, oldLocationCode)
        }
    }

    db.table('t_picking_upload_detail').where().eq('bill_id', masterId).delete()

    let masterRow = {
        id: masterId,
        wave_no: waveNo,
        user_id: payload.userId,
        user_name: payload.userName,
        detail_count: details.size(),
        update_time: payload.updateTime,
        create_time: now
    }
    if(existingMaster != null && existingMaster.id != null && existingMaster.id != ''){
        anyline.update('t_picking_upload', masterRow)
    } else {
        anyline.insert('t_picking_upload', masterRow)
    }

    let detailRows = []
    for(item in details){
        let actualQty = item.actualQuantity
        if(actualQty == null){
            actualQty = 0
        }
        let detailId = item.id
        if(detailId == null || detailId == ''){
            detailId = UUID.randomUUID().toString().replace('-', '')
        }

        let statusVal = 0
        if(actualQty >= item.planQuantity){
            statusVal = 1
        }
        item.status = statusVal

        let locationCode = item.locationCode
        addUnique(affectedLocationCodes, affectedLocationCodeSet, locationCode)

        detailRows.add({
            id: detailId,
            wave_no: waveNo,
            bill_id: masterId,
            material_code: item.materialCode,
            batch_no: item.batchNo,
            location_code: locationCode,
            plan_quantity: item.planQuantity,
            actual_quantity: actualQty,
            status: statusVal,
            create_time: now
        })
    }

    if(detailRows.size() > 0){
        let batchInsertSql = """
            insert into t_picking_upload_detail (
                id,
                wave_no,
                bill_id,
                material_code,
                batch_no,
                location_code,
                plan_quantity,
                actual_quantity,
                status,
                create_time
            ) values
        """
        let batchInsertValuesSql = ""
        let batchInsertParams = {}
        let rowIndex = 0
        for(detailRow in detailRows){
            if(rowIndex > 0){
                batchInsertValuesSql = batchInsertValuesSql + ","
            }
            let suffix = "_" + rowIndex
            batchInsertValuesSql = batchInsertValuesSql + "("
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
            batchInsertParams["id" + suffix] = detailRow.id
            batchInsertParams["waveNo" + suffix] = detailRow.wave_no
            batchInsertParams["billId" + suffix] = detailRow.bill_id
            batchInsertParams["materialCode" + suffix] = detailRow.material_code
            batchInsertParams["batchNo" + suffix] = detailRow.batch_no
            batchInsertParams["locationCode" + suffix] = detailRow.location_code
            batchInsertParams["planQuantity" + suffix] = detailRow.plan_quantity
            batchInsertParams["actualQuantity" + suffix] = detailRow.actual_quantity
            batchInsertParams["status" + suffix] = detailRow.status
            batchInsertParams["createTime" + suffix] = detailRow.create_time
            rowIndex = rowIndex + 1
        }
        db.update(batchInsertSql + batchInsertValuesSql, batchInsertParams)
    }

    anyline.commitTrans(transState)
    transState = null

    log.info(logPrefix + ', stage=persist_done, affectedCount=' + affectedLocationCodes.size())

    // 全局同步锁
    lockAcquired = acquireGlobalLock(globalLockKey, lockTimeoutSeconds)
    if(!lockAcquired){
        log.warn(logPrefix + ', stage=lock_busy, lockKey=' + globalLockKey)
        exit 503, '系统繁忙，请稍后重试'
    }
    log.info(logPrefix + ', stage=lock_acquired, lockKey=' + globalLockKey)

    // 锁内事务2：更新 t_location_status
    transState = anyline.beginTrans()
    let statusSnapshotMap = recalcLocationStatusBatch(affectedLocationCodes)
    anyline.commitTrans(transState)
    transState = null

    log.info(logPrefix + ', stage=status_updated, affectedCount=' + affectedLocationCodes.size())

    // 锁内同步灯控
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

    // 锁外执行灯控，缩短全局锁占用时间
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
        lockAcquired = false
    }

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

    if(multiUserLocationCodes.size() > 0){
        try {
            let turnOnMultiResult = turnOnLedByArticleId({
                stationCode: stationCode,
                color: 'BLUE',
                articleIds: multiUserLocationCodes,
                traceId: traceId,
                waveNo: waveNo,
                userId: userId
            })
            recordLightControlResult('TURN_ON_MULTI', turnOnMultiResult)
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_turn_on_multi_failed, message=' + lightErr.message)
        }
    }

    for(colorKey in singleColorKeys){
        let locationList = singleColorLocationMap[colorKey]
        if(locationList != null && locationList.size() > 0){
            try {
                let turnOnSingleResult = turnOnLedByArticleId({
                    stationCode: stationCode,
                    color: colorKey,
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

    let aisleDispatchPlan = resolveAisleDispatchPlan(affectedLocationCodes)
    for(locationCode in aisleDispatchPlan.turnOffLocationCodes){
        try {
            turnOffAisleLedByLotNo({
                lotNo: locationCode
            })
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_aisle_turn_off_failed, locationCode=' + locationCode + ', message=' + lightErr.message)
        }
    }
    for(locationCode in aisleDispatchPlan.turnOnLocationCodes){
        try {
            turnOnAisleLedByLotNo({
                lotNo: locationCode
            })
        } catch (lightErr) {
            log.warn(logPrefix + ', stage=dispatch_aisle_turn_on_failed, locationCode=' + locationCode + ', message=' + lightErr.message)
        }
    }

    log.info(logPrefix + ', stage=dispatch_done, turnOff=' + turnOffLocationCodes.size() + ', turnOnMulti=' + multiUserLocationCodes.size() + ', turnOnSingle=' + singleUserCandidateCodes.size())
    exit 200, '拣货数据上传成功'
} catch (e) {
    if(transState != null){
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {
        }
    }
    log.error(logPrefix + ', stage=error, message={}', e)
    exit 500, '拣货数据上传失败: ' + e.message
} finally {
    if(lockAcquired){
        releaseGlobalLock(globalLockKey)
        log.info(logPrefix + ', stage=lock_released, lockKey=' + globalLockKey)
    }
}
