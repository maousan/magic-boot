{
  "properties" : { },
  "id" : "oj8xw89pfd3jefa8dwaqv4ye3lqtblwf",
  "script" : null,
  "groupId" : "l5kl0q2xiqa4ryz349c3fkzj3hzm07vw",
  "name" : "拣货数据上传",
  "createTime" : null,
  "updateTime" : 1776702303642,
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
  "requestBody" : "{\n  \"waveNo\": \"WV202603250001\",\n  \"userId\": \"USER001\",\n  \"userName\": \"张三\",\n  \"updateTime\": 1648195200000,\n  \"details\": [\n    {\n      \"id\": \"DTL001\",\n      \"materialCode\": \"MAT001\",\n      \"batchNo\": \"B20260325\",\n      \"locationCode\": \"A1-11-02-01-01\",\n      \"planQuantity\": 100,\n      \"actualQuantity\": 60,\n      \"status\": 0\n    },\n    {\n      \"id\": \"D3003\",\n      \"materialCode\": \"MAT-013\",\n      \"batchNo\": \"BATCH-ERR-3\",\n      \"locationCode\": \"A1-11-01-01-01\",\n      \"planQuantity\": 6,\n      \"actualQuantity\": 0,\n      \"status\": 0\n    }\n  ]\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"拣货数据上传成功\",\n    \"data\": null,\n    \"timestamp\": 1776672498381,\n    \"executeTime\": 122\n}",
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
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import '@/utils/getUserLightColor' as getUserLightColor
import '@/utils/saveLightTraceLog' as saveLightTraceLog
import org.ssssssss.magicboot.model.CommonCountMap
import com.dtflys.forest.Forest

const stationCode = Forest.config().getVariableValue("stationCode")

if(body == null){
    exit 400, '请求体不能为空'
}

let payload = body
let details = payload.details
let traceId = payload.traceId
if(traceId == null || traceId == ''){
    traceId = UUID.randomUUID().toString().replace('-', '')
}
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
let uploadLocationCodeSet = {}
let uploadLocationCodes = []
let oldUnpickedLocationCodeSet = {}
let oldUnpickedLocationCodes = []
let singleUserLocationCodes = []
let multiUserLocationCodes = []
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
let persistTraceLog = (stage, actionType, locationCodes, extraData) => {
    let ok = saveLightTraceLog(traceId, 'upload', stage, actionType, waveNo, userId, locationCodes, extraData)
    if(!ok){
        log.error(logPrefix + ', stage=trace_persist_error, persistOk=false')
    }
}

let transState = null
try {
    log.info(logPrefix + ', stage=receive, detailCount=' + details.size())
    transState = anyline.beginTrans()

    let existingMaster = db.table('t_picking_upload').where().eq('wave_no', waveNo).eq('user_id', userId).selectOne()
    let masterId = UUID.randomUUID().toString().replace('-', '')
    if(existingMaster != null && existingMaster.id != null && existingMaster.id != ''){
        masterId = existingMaster.id
        let oldRows = db.select("""
            select distinct location_code
              from t_picking_upload_detail
             where bill_id = #{billId}
               and ifnull(status, 0) <> 1
        """, {
            billId: masterId
        })
        if(oldRows != null){
            for(row in oldRows){
                let oldLocationCode = row.location_code
                if(oldLocationCode == null || oldLocationCode == ''){
                    oldLocationCode = row.locationCode
                }
                if(oldLocationCode != null && oldLocationCode != '' && oldUnpickedLocationCodeSet[oldLocationCode] == null){
                    oldUnpickedLocationCodeSet[oldLocationCode] = 1
                    oldUnpickedLocationCodes.add(oldLocationCode)
                }
            }
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
    }else{
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

        item.status = item.planQuantity > actualQty ? 0 : 1

        let detailRow = {
            id: detailId,
            wave_no: waveNo,
            bill_id: masterId,
            material_code: item.materialCode,
            batch_no: item.batchNo,
            location_code: item.locationCode,
            plan_quantity: item.planQuantity,
            actual_quantity: actualQty,
            status: item.status,
            create_time: now
        }

        detailRows.add(detailRow)
        if(item.status != 1 && uploadLocationCodeSet[item.locationCode] == null){
            uploadLocationCodeSet[item.locationCode] = 1
            uploadLocationCodes.add(item.locationCode)
        }
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

    let addedLocationCodes = []
    let removedLocationCodes = []
    for(locationCode in uploadLocationCodes){
        if(oldUnpickedLocationCodeSet[locationCode] == null){
            addedLocationCodes.add(locationCode)
        }
    }
    for(oldLocationCode in oldUnpickedLocationCodes){
        if(uploadLocationCodeSet[oldLocationCode] == null){
            removedLocationCodes.add(oldLocationCode)
        }
    }

    let affectedLocationCodeSet = {}
    let affectedLocationCodes = []
    // 差量有变化的库位
    for(locationCode in addedLocationCodes){
        if(affectedLocationCodeSet[locationCode] == null){
            affectedLocationCodeSet[locationCode] = 1
            affectedLocationCodes.add(locationCode)
        }
    }
    for(locationCode in removedLocationCodes){
        if(affectedLocationCodeSet[locationCode] == null){
            affectedLocationCodeSet[locationCode] = 1
            affectedLocationCodes.add(locationCode)
        }
    }
    // 本次上报涉及的库位（用于处理差量=0但缓存为空的重启场景）
    for(locationCode in uploadLocationCodes){
        if(affectedLocationCodeSet[locationCode] == null){
            affectedLocationCodeSet[locationCode] = 1
            affectedLocationCodes.add(locationCode)
        }
    }
    for(locationCode in oldUnpickedLocationCodes){
        if(affectedLocationCodeSet[locationCode] == null){
            affectedLocationCodeSet[locationCode] = 1
            affectedLocationCodes.add(locationCode)
        }
    }
    if(affectedLocationCodes.size() == 0){
        for(item in details){
            let detailLocationCode = item.locationCode
            if(detailLocationCode != null && detailLocationCode != '' && affectedLocationCodeSet[detailLocationCode] == null){
                affectedLocationCodeSet[detailLocationCode] = 1
                affectedLocationCodes.add(detailLocationCode)
            }
        }
    }
    log.info(logPrefix + ', stage=reconcile_start, added=' + formatLocationLog(addedLocationCodes, 20) + ', removed=' + formatLocationLog(removedLocationCodes, 20) + ', affected=' + formatLocationLog(affectedLocationCodes, 20))
    // 自愈逻辑：不依赖差量结果，按数据库实值批量回填 CommonCountMap（防止重启后缓存为空）
    let locationUserCountMap = {}
    if(affectedLocationCodes.size() > 0){
        let reconcileSql = """
            select d.location_code as location_code, count(distinct d.bill_id) as user_count
              from t_picking_upload_detail d
             where ifnull(d.status, 0) <> 1
        """
        let reconcileParams = {}
        let inSql = ""
        let index2 = 0
        for(locationCode in affectedLocationCodes){
            let paramKey = 'locationCode' + index2
            reconcileParams[paramKey] = locationCode
            if(index2 > 0){
                inSql = inSql + ','
            }
            inSql = inSql + '#{' + paramKey + '}'
            index2 = index2 + 1
        }
        reconcileSql = reconcileSql + " and d.location_code in (" + inSql + ") group by d.location_code"
        let reconcileRows = db.select(reconcileSql, reconcileParams)
        if(reconcileRows != null){
            for(row in reconcileRows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){
                    locationCode = row.locationCode
                }
                let locationUserCount = row.user_count
                if(locationUserCount == null){
                    locationUserCount = row.userCount
                }
                if(locationCode != null && locationCode != ''){
                    locationUserCountMap[locationCode] = locationUserCount
                }
            }
        }
    }
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

    if(uploadLocationCodes.size() > 0){
        let singleUserColor = 'GREEN'
        let multiUserColor = 'BLUE'
        let mappedColor = getUserLightColor(userId)
        if(mappedColor != null && mappedColor != ''){
            singleUserColor = mappedColor
        }

        singleUserLocationCodes = []
        multiUserLocationCodes = []
        for(locationCode in uploadLocationCodes){
            let locationUserCount = CommonCountMap.get(locationCode)
            if(locationUserCount > 1){
                multiUserLocationCodes.add(locationCode)
            } else {
                singleUserLocationCodes.add(locationCode)
            }
        }

        if(singleUserLocationCodes.size() > 0){
            try {
                log.info('dispatch async turn on from upload(single user), waveNo=' + waveNo + `, stationCode=${stationCode}, color=` + singleUserColor + ', articleIds=' + singleUserLocationCodes)
                let turnOnSingleFuture = async turnOnLedByArticleId({
                    stationCode,
                    color: singleUserColor,
                    articleIds: singleUserLocationCodes,
                    traceId: traceId,
                    waveNo: waveNo,
                    userId: userId
                })
            } catch (asyncErr) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOnSingle, message=' + asyncErr.message)
            }
        }

        if(multiUserLocationCodes.size() > 0){
            try {
                log.info('dispatch async turn on from upload(multi user), waveNo=' + waveNo + `, stationCode=${stationCode}, color=` + multiUserColor + ', articleIds=' + multiUserLocationCodes)
                let turnOnMultiFuture = async turnOnLedByArticleId({
                    stationCode,
                    color: multiUserColor,
                    articleIds: multiUserLocationCodes,
                    traceId: traceId,
                    waveNo: waveNo,
                    userId: userId
                })
            } catch (asyncErr2) {
                log.error(logPrefix + ', stage=dispatch_error, action=turnOnMulti, message=' + asyncErr2.message)
            }
        }
    }

    // log.info("CommonCountMap: {}", CommonCountMap.snapshot())

    log.info(logPrefix + ', stage=done, turnOnSingle=' + formatLocationLog(singleUserLocationCodes, 20) + ', turnOnMulti=' + formatLocationLog(multiUserLocationCodes, 20))
    let uploadActionType = 'none'
    let traceLocationCodes = []
    let hasTurnOn = singleUserLocationCodes.size() > 0 || multiUserLocationCodes.size() > 0
    let hasTurnOff = removedLocationCodes.size() > 0
    if(hasTurnOn && hasTurnOff){
        uploadActionType = 'mix'
        traceLocationCodes = mergeLocationCodes(uploadLocationCodes, removedLocationCodes)
    } else if(hasTurnOn){
        uploadActionType = 'turn_on'
        traceLocationCodes = uploadLocationCodes
    } else if(hasTurnOff){
        uploadActionType = 'turn_off'
        traceLocationCodes = removedLocationCodes
    } else {
        traceLocationCodes = affectedLocationCodes
    }
    persistTraceLog('done', uploadActionType, traceLocationCodes, {
        turnOnSingle: singleUserLocationCodes,
        turnOnMulti: multiUserLocationCodes,
        removed: removedLocationCodes,
        affected: affectedLocationCodes,
        commonCountSize: CommonCountMap.size(),
        commonCountTotal: CommonCountMap.totalCount()
    })
    exit 200, '拣货数据上传成功'
} catch (e) {
    if(transState != null){
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {
        }
    }
    log.error(logPrefix + ', stage=error, message=' + e.message)
    persistTraceLog('error', 'turn_on', [], {
        errorMessage: e.message
    })
    exit 500, '拣货数据上传失败: ' + e.message
}
