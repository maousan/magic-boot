{
  "properties" : { },
  "id" : "f6b4a4c1f5f84a0cad02fb9b55f50aa1",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "拣货上传公共逻辑",
  "createTime" : 1777000000000,
  "updateTime" : 1777000000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/pickingUploadUtils",
  "description" : "拣货上传接口公共逻辑：校验、持久化、差量计算、缓存回填、灯控分流与trace包装",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "action",
    "value" : null,
    "description" : "动作标识",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "data",
    "value" : null,
    "description" : "动作数据",
    "required" : false,
    "dataType" : "Object",
    "type" : "java.util.Map",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
import log
import java.util.UUID
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import '@/aims/led/turnOffLedByArticleId' as turnOffLedByArticleId
import '@/utils/getUserLightColor' as getUserLightColor
import '@/utils/locationStatusUtils' as locationStatusUtils

let ensureTraceId = (traceId) => {
    if(traceId == null || traceId == ''){
        return UUID.randomUUID().toString().replace('-', '')
    }
    return traceId
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

let preparePayload = (body) => {
    if(body == null){
        return {ok: false, code: 400, message: '请求体不能为空'}
    }
    let payload = body
    let traceId = ensureTraceId(payload.traceId)
    payload.traceId = traceId
    let details = payload.details
    return {
        ok: true,
        payload: payload,
        details: details,
        traceId: traceId
    }
}

let validatePayload = (payload, details) => {
    if(!payload.waveNo){
        return {ok: false, code: 400, message: 'waveNo不能为空'}
    }
    if(!payload.userId){
        return {ok: false, code: 400, message: 'userId不能为空'}
    }
    if(payload.updateTime == null){
        return {ok: false, code: 400, message: 'updateTime不能为空'}
    }
    if(details == null || details.size() == 0){
        return {ok: false, code: 400, message: 'details不能为空'}
    }

    let index = 0
    let detailIdUniqMap = {}
    for(item in details){
        if(!item.id){
            return {ok: false, code: 400, message: 'details[' + index + '].id不能为空'}
        }
        if(!item.materialCode){
            return {ok: false, code: 400, message: 'details[' + index + '].materialCode不能为空'}
        }
        if(!item.batchNo){
            return {ok: false, code: 400, message: 'details[' + index + '].batchNo不能为空'}
        }
        if(!item.locationCode){
            return {ok: false, code: 400, message: 'details[' + index + '].locationCode不能为空'}
        }
        if(item.planQuantity == null){
            return {ok: false, code: 400, message: 'details[' + index + '].planQuantity不能为空'}
        }
        if(item.planQuantity < 0){
            return {ok: false, code: 400, message: 'details[' + index + '].planQuantity不能小于0'}
        }
        if(item.actualQuantity != null && item.actualQuantity < 0){
            return {ok: false, code: 400, message: 'details[' + index + '].actualQuantity不能小于0'}
        }
        if(item.status == null){
            return {ok: false, code: 400, message: 'details[' + index + '].status不能为空'}
        }
        if(item.status != 0 && item.status != 1){
            return {ok: false, code: 400, message: 'details[' + index + '].status仅支持0或1'}
        }
        if(detailIdUniqMap[item.id] != null){
            return {ok: false, code: 400, message: 'details[' + index + '].id重复'}
        }
        detailIdUniqMap[item.id] = 1
        index = index + 1
    }
    return {ok: true}
}

let collectDistinctUnpickedLocationsByBillId = (billId) => {
    let codes = []
    let codeSet = {}
    let oldRows = db.select("""
        select distinct location_code
          from t_picking_upload_detail
         where bill_id = #{billId}
           and ifnull(status, 0) <> 1
    """, {
        billId: billId
    })
    if(oldRows == null){
        return {
            list: codes,
            set: codeSet
        }
    }
    for(row in oldRows){
        let oldLocationCode = row.location_code
        if(oldLocationCode == null || oldLocationCode == ''){
            oldLocationCode = row.locationCode
        }
        if(oldLocationCode != null && oldLocationCode != '' && codeSet[oldLocationCode] == null){
            codeSet[oldLocationCode] = 1
            codes.add(oldLocationCode)
        }
    }
    return {
        list: codes,
        set: codeSet
    }
}

let buildDetailRows = (payload, masterId, now) => {
    let rows = []
    let uploadLocationCodeSet = {}
    let uploadLocationCodes = []
    for(item in payload.details){
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
            wave_no: payload.waveNo,
            bill_id: masterId,
            material_code: item.materialCode,
            batch_no: item.batchNo,
            location_code: item.locationCode,
            plan_quantity: item.planQuantity,
            actual_quantity: actualQty,
            status: item.status,
            create_time: now
        }
        rows.add(detailRow)
        if(item.status != 1 && uploadLocationCodeSet[item.locationCode] == null){
            uploadLocationCodeSet[item.locationCode] = 1
            uploadLocationCodes.add(item.locationCode)
        }
    }
    return {
        detailRows: rows,
        uploadLocationCodes: uploadLocationCodes,
        uploadLocationCodeSet: uploadLocationCodeSet
    }
}

let batchInsertDetailRows = (detailRows) => {
    if(detailRows == null || detailRows.size() == 0){
        return
    }
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

let persistUploadSnapshot = (payload, now) => {
    let waveNo = payload.waveNo
    let userId = payload.userId
    let existingMaster = db.table('t_picking_upload').where().eq('wave_no', waveNo).eq('user_id', userId).selectOne()
    let masterId = UUID.randomUUID().toString().replace('-', '')
    let oldUnpickedLocationCodes = []
    let oldUnpickedLocationCodeSet = {}

    if(existingMaster != null && existingMaster.id != null && existingMaster.id != ''){
        masterId = existingMaster.id
        let oldResult = collectDistinctUnpickedLocationsByBillId(masterId)
        oldUnpickedLocationCodes = oldResult.list
        oldUnpickedLocationCodeSet = oldResult.set
    }

    db.table('t_picking_upload_detail').where().eq('bill_id', masterId).delete()

    let masterRow = {
        id: masterId,
        wave_no: waveNo,
        user_id: payload.userId,
        user_name: payload.userName,
        detail_count: payload.details.size(),
        update_time: payload.updateTime,
        create_time: now
    }
    if(existingMaster != null && existingMaster.id != null && existingMaster.id != ''){
        anyline.update('t_picking_upload', masterRow)
    }else{
        anyline.insert('t_picking_upload', masterRow)
    }

    let buildResult = buildDetailRows(payload, masterId, now)
    batchInsertDetailRows(buildResult.detailRows)

    return {
        masterId: masterId,
        uploadLocationCodes: buildResult.uploadLocationCodes,
        uploadLocationCodeSet: buildResult.uploadLocationCodeSet,
        oldUnpickedLocationCodes: oldUnpickedLocationCodes,
        oldUnpickedLocationCodeSet: oldUnpickedLocationCodeSet
    }
}

let buildUploadLocationDiff = (uploadLocationCodes, uploadLocationCodeSet, oldUnpickedLocationCodes, oldUnpickedLocationCodeSet, details) => {
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

    return {
        addedLocationCodes: addedLocationCodes,
        removedLocationCodes: removedLocationCodes,
        affectedLocationCodes: affectedLocationCodes
    }
}

let reconcileLocationStatus = (affectedLocationCodes, logPrefix) => {
    let normalized = locationStatusUtils('normalizeLocationCodes', {
        locationCodes: affectedLocationCodes
    })
    if(normalized == null || normalized.size() == 0){
        return {}
    }
    let refreshedMap = locationStatusUtils('refreshLocationStatuses', {
        locationCodes: normalized
    })
    return refreshedMap
}

let resolveTurnOnColors = (userId) => {
    let singleUserColor = 'GREEN'
    let multiUserColor = 'BLUE'
    let mappedColor = getUserLightColor(userId)
    if(mappedColor != null && mappedColor != ''){
        singleUserColor = mappedColor
    }
    return {
        singleUserColor: singleUserColor,
        multiUserColor: multiUserColor
    }
}

let splitTurnOnLocations = (uploadLocationCodes) => {
    return locationStatusUtils('splitTurnOnLocations', {
        locationCodes: uploadLocationCodes
    })
}

let resolveTurnOffLocations = (candidateLocationCodes) => {
    return locationStatusUtils('resolveTurnOffLocations', {
        locationCodes: candidateLocationCodes
    })
}

let dispatchTurnOff = (stationCode, waveNo, userId, traceId, turnOffLocationCodes, logPrefix) => {
    if(turnOffLocationCodes == null || turnOffLocationCodes.size() == 0){
        return
    }
    try {
        log.info('dispatch async turn off from upload, waveNo=' + waveNo + `, stationCode=${stationCode}, articleIds=` + turnOffLocationCodes)
        let turnOffFuture = async turnOffLedByArticleId({
            stationCode,
            articleIds: turnOffLocationCodes,
            traceId: traceId,
            waveNo: waveNo,
            userId: userId
        })
    } catch (asyncErr) {
        log.error(logPrefix + ', stage=dispatch_error, action=turnOff, message=' + asyncErr.message)
    }
}

let dispatchTurnOn = (stationCode, waveNo, userId, traceId, singleUserColor, multiUserColor, singleUserLocationCodes, multiUserLocationCodes, logPrefix) => {
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

if(action == 'preparePayload'){
    return preparePayload(data.body)
}

if(action == 'validatePayload'){
    return validatePayload(data.payload, data.details)
}

if(action == 'persistUploadSnapshot'){
    return persistUploadSnapshot(data.payload, data.now)
}

if(action == 'buildUploadLocationDiff'){
    return buildUploadLocationDiff(data.uploadLocationCodes, data.uploadLocationCodeSet, data.oldUnpickedLocationCodes, data.oldUnpickedLocationCodeSet, data.details)
}

if(action == 'reconcileLocationStatus'){
    return reconcileLocationStatus(data.affectedLocationCodes, data.logPrefix)
}

if(action == 'resolveTurnOnColors'){
    return resolveTurnOnColors(data.userId)
}

if(action == 'splitTurnOnLocations'){
    return splitTurnOnLocations(data.uploadLocationCodes)
}

if(action == 'resolveTurnOffLocations'){
    return resolveTurnOffLocations(data.candidateLocationCodes)
}

if(action == 'dispatchTurnOff'){
    dispatchTurnOff(data.stationCode, data.waveNo, data.userId, data.traceId, data.turnOffLocationCodes, data.logPrefix)
    return {ok: true}
}

if(action == 'dispatchTurnOn'){
    dispatchTurnOn(data.stationCode, data.waveNo, data.userId, data.traceId, data.singleUserColor, data.multiUserColor, data.singleUserLocationCodes, data.multiUserLocationCodes, data.logPrefix)
    return {ok: true}
}

if(action == 'formatLocationLog'){
    return formatLocationLog(data.locationCodes, data.limit)
}

if(action == 'mergeLocationCodes'){
    return mergeLocationCodes(data.firstCodes, data.secondCodes)
}

return {
    ok: false,
    message: 'unsupported action: ' + action
}
