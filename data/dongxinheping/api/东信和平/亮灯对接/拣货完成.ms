{
  "properties" : { },
  "id" : "9h2zcovwd71mwjgisuy0yw72lvj6xlup",
  "script" : null,
  "groupId" : "l5kl0q2xiqa4ryz349c3fkzj3hzm07vw",
  "name" : "拣货完成",
  "createTime" : null,
  "updateTime" : 1776672835524,
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
  "requestBody" : "{\n    \"waveNo\": \"WV202603250001\",\n    \"userId\": \"USER002\",\n    \"details\": [\n        {\n            \"id\": \"DTL001\",\n            \"materialCode\": \"MAT001\",\n            \"batchNo\": \"B20260325\",\n            \"locationCode\": \"A1-11-02-01-01\",\n            \"planQuantity\": 100,\n            \"actualQuantity\": 100,\n            \"updateTime\": 1648195200000\n        }\n    ]\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"拣货完成同步成功\",\n    \"data\": null,\n    \"timestamp\": 1776672767274,\n    \"executeTime\": 153\n}",
  "description" : "当用户完成某一个物料拣选后，WMS 向亮灯系统同步完成信息。",
  "requestBodyDefinition" : {
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
      "name" : "waveNo",
      "value" : "WV202603250001",
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
      "name" : "userId",
      "value" : "USER002",
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
      "name" : "details",
      "value" : "",
      "description" : "",
      "required" : false,
      "dataType" : "Array",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ {
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
          "name" : "id",
          "value" : "DTL001",
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
          "name" : "materialCode",
          "value" : "MAT001",
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
          "name" : "batchNo",
          "value" : "B20260325",
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
          "name" : "locationCode",
          "value" : "A1-11-01-01-01",
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
          "name" : "planQuantity",
          "value" : "100",
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
          "name" : "actualQuantity",
          "value" : "100",
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
          "name" : "updateTime",
          "value" : "1648195200000",
          "description" : "",
          "required" : false,
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
    }, {
      "name" : "timestamp",
      "value" : "1775532377580",
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
      "value" : "93",
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
import org.ssssssss.magicboot.model.CommonCountMap

if(body == null){
    exit 400, '请求体不能为空'
}

let payload = body
let details = payload.details

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
let locationCodeSet = {}
for(item in details){
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
    if(locationCodeSet[item.locationCode] == null){
        locationCodeSet[item.locationCode] = 1
    }
    index = index + 1
}

let waveNo = payload.waveNo
let userId = payload.userId
let now = new Date()
let transState = null
let shouldTurnOffLocationCodes = []
let shouldTurnOnLocationCodes = []
let singleUserColor = 'GREEN'
let multiUserColor = 'BLUE'
let stationCode = Forest.config().getVariableValue("stationCode")
if(stationCode == null || stationCode == ''){
    stationCode = '10001'
}
let oldUnpickedCountMap = {}
let incrementLocations = []
let decrementLocations = []
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

try {
    transState = anyline.beginTrans()

    let master = db.table('t_picking_upload').where().eq('wave_no', waveNo).eq('user_id', userId).selectOne()
    if(master == null || master.id == null || master.id == ''){
        anyline.rollbackTrans(transState)
        exit 400, '未找到对应拣货上传主单'
    }

    let masterId = master.id
    let currentLocationCodes = []
    for(locationCode in locationCodeSet.keySet()){
        currentLocationCodes.add(locationCode)
        oldUnpickedCountMap[locationCode] = 0
    }
    if(currentLocationCodes.size() > 0){
        let oldCountSql = """
            select location_code, count(1) as unpicked_count
              from t_picking_upload_detail
             where bill_id = #{billId}
               and ifnull(status, 0) <> 1
        """
        let oldCountParams = {
            billId: masterId
        }
        let oldInSql = ""
        let oldIndex = 0
        for(locationCode in currentLocationCodes){
            let paramKey = 'locationCode' + oldIndex
            oldCountParams[paramKey] = locationCode
            if(oldIndex > 0){
                oldInSql = oldInSql + ','
            }
            oldInSql = oldInSql + '#{' + paramKey + '}'
            oldIndex = oldIndex + 1
        }
        oldCountSql = oldCountSql + " and location_code in (" + oldInSql + ") group by location_code"
        let oldCountRows = db.select(oldCountSql, oldCountParams)
        if(oldCountRows != null){
            for(row in oldCountRows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){
                    locationCode = row.locationCode
                }
                let unpickedCount = row.unpicked_count
                if(unpickedCount == null){
                    unpickedCount = row.unpickedCount
                }
                if(locationCode != null && locationCode != ''){
                    oldUnpickedCountMap[locationCode] = unpickedCount
                }
            }
        }
    }

    for(item in details){
        let actualQty = item.actualQuantity
        let planQty = item.planQuantity
        let statusVal = 0
        if(actualQty >= planQty){
            statusVal = 1
        }
        log.info('detail status calculate, waveNo=' + waveNo + ', locationCode=' + item.locationCode + ', materialCode=' + item.materialCode + ', actualQty=' + actualQty + ', planQty=' + planQty + ', status=' + statusVal)

        // let detailId = UUID.randomUUID().toString().replace('-', '')
        let detailId = item.id
        db.update("""
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
            ) values (
                #{id},
                #{waveNo},
                #{billId},
                #{materialCode},
                #{batchNo},
                #{locationCode},
                #{planQuantity},
                #{actualQuantity},
                #{status},
                #{createTime}
            )
            on duplicate key update
                plan_quantity = values(plan_quantity),
                actual_quantity = values(actual_quantity),
                status = values(status)
        """, {
            id: detailId,
            waveNo: waveNo,
            billId: masterId,
            materialCode: item.materialCode,
            batchNo: item.batchNo,
            locationCode: item.locationCode,
            planQuantity: planQty,
            actualQuantity: actualQty,
            status: statusVal,
            createTime: now
        })
    }

    let latestUpdateTime = 0
    for(item in details){
        if(item.updateTime != null && item.updateTime > latestUpdateTime){
            latestUpdateTime = item.updateTime
        }
    }
    if(latestUpdateTime == 0){
        latestUpdateTime = new Date().getTime()
    }

    anyline.update('t_picking_upload', {
        id: masterId,
        wave_no: waveNo,
        user_id: userId,
        user_name: payload.userName,
        detail_count: details.size(),
        update_time: latestUpdateTime
    })

    let mappedColor = getUserLightColor(userId)
    if(mappedColor != null && mappedColor != ''){
        singleUserColor = mappedColor
    }

    let waveLocationUnpickedMap = {}
    let billLocationUnpickedMap = {}
    if(currentLocationCodes.size() > 0){
        let inSql = ""
        let index1 = 0
        let locationInParams = {}
        for(locationCode in currentLocationCodes){
            let paramKey = 'locationCode' + index1
            locationInParams[paramKey] = locationCode
            if(index1 > 0){
                inSql = inSql + ','
            }
            inSql = inSql + '#{' + paramKey + '}'
            index1 = index1 + 1
        }

        let waveCountSql = """
            select location_code, count(1) as unpicked_count
              from t_picking_upload_detail
             where wave_no = #{waveNo}
               and ifnull(status, 0) <> 1
               and location_code in (""" + inSql + """)
             group by location_code
        """
        let waveCountParams = {
            waveNo: waveNo
        }
        for(paramKey in locationInParams.keySet()){
            waveCountParams[paramKey] = locationInParams[paramKey]
        }
        let waveCountRows = db.select(waveCountSql, waveCountParams)
        if(waveCountRows != null){
            for(row in waveCountRows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){
                    locationCode = row.locationCode
                }
                let unpickedCount = row.unpicked_count
                if(unpickedCount == null){
                    unpickedCount = row.unpickedCount
                }
                if(locationCode != null && locationCode != ''){
                    waveLocationUnpickedMap[locationCode] = unpickedCount
                }
            }
        }

        let billCountSql = """
            select location_code, count(1) as unpicked_count
              from t_picking_upload_detail
             where bill_id = #{billId}
               and ifnull(status, 0) <> 1
               and location_code in (""" + inSql + """)
             group by location_code
        """
        let billCountParams = {
            billId: masterId
        }
        for(paramKey in locationInParams.keySet()){
            billCountParams[paramKey] = locationInParams[paramKey]
        }
        let billCountRows = db.select(billCountSql, billCountParams)
        if(billCountRows != null){
            for(row in billCountRows){
                let locationCode = row.location_code
                if(locationCode == null || locationCode == ''){
                    locationCode = row.locationCode
                }
                let unpickedCount = row.unpicked_count
                if(unpickedCount == null){
                    unpickedCount = row.unpickedCount
                }
                if(locationCode != null && locationCode != ''){
                    billLocationUnpickedMap[locationCode] = unpickedCount
                }
            }
        }
    }

    for(locationCode in currentLocationCodes){
        let unpickedCount = waveLocationUnpickedMap[locationCode]
        if(unpickedCount == null){
            unpickedCount = 0
        }
        log.info('location evaluate, waveNo=' + waveNo + ', locationCode=' + locationCode + ', unpickedCount=' + unpickedCount)
        if(unpickedCount == 0){
            shouldTurnOffLocationCodes.add(locationCode)
        } else {
            shouldTurnOnLocationCodes.add(locationCode)
        }

        let newUnpickedCount = billLocationUnpickedMap[locationCode]
        if(newUnpickedCount == null){
            newUnpickedCount = 0
        }

        let oldUnpickedCount = oldUnpickedCountMap[locationCode]
        if(oldUnpickedCount == null){
            oldUnpickedCount = 0
        }
        if(oldUnpickedCount > 0 && newUnpickedCount == 0){
            decrementLocations.add(locationCode)
        } else if(oldUnpickedCount == 0 && newUnpickedCount > 0){
            incrementLocations.add(locationCode)
        }
    }
    log.info('light decision, waveNo=' + waveNo + ', turnOn=' + formatLocationLog(shouldTurnOnLocationCodes, 20) + ', turnOff=' + formatLocationLog(shouldTurnOffLocationCodes, 20) + ', increment=' + formatLocationLog(incrementLocations, 20) + ', decrement=' + formatLocationLog(decrementLocations, 20))

    anyline.commitTrans(transState)

    let affectedLocationCodeSet = {}
    let affectedLocationCodes = []
    for(locationCode in incrementLocations){
        if(affectedLocationCodeSet[locationCode] == null){
            affectedLocationCodeSet[locationCode] = 1
            affectedLocationCodes.add(locationCode)
        }
    }
    for(locationCode in decrementLocations){
        if(affectedLocationCodeSet[locationCode] == null){
            affectedLocationCodeSet[locationCode] = 1
            affectedLocationCodes.add(locationCode)
        }
    }
    // 本次完成接口涉及的库位（用于处理差量=0但缓存为空的重启场景）
    for(locationCode in locationCodeSet.keySet()){
        if(affectedLocationCodeSet[locationCode] == null){
            affectedLocationCodeSet[locationCode] = 1
            affectedLocationCodes.add(locationCode)
        }
    }
    log.info('common count reconcile start(complete), waveNo=' + waveNo + ', userId=' + userId + ', increment=' + formatLocationLog(incrementLocations, 20) + ', decrement=' + formatLocationLog(decrementLocations, 20) + ', affected=' + formatLocationLog(affectedLocationCodes, 20))
    // 自愈逻辑：按数据库实值批量回填 CommonCountMap（防止重启后缓存为空）
    let locationUserCountMap = {}
    if(affectedLocationCodes.size() > 0){
        let reconcileSql = """
            select d.location_code as location_code, count(distinct d.bill_id) as user_count
              from t_picking_upload_detail d
             where ifnull(d.status, 0) <> 1
        """
        let reconcileParams = {}
        let inSql2 = ""
        let index3 = 0
        for(locationCode in affectedLocationCodes){
            let paramKey = 'locationCode' + index3
            reconcileParams[paramKey] = locationCode
            if(index3 > 0){
                inSql2 = inSql2 + ','
            }
            inSql2 = inSql2 + '#{' + paramKey + '}'
            index3 = index3 + 1
        }
        reconcileSql = reconcileSql + " and d.location_code in (" + inSql2 + ") group by d.location_code"
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
            log.info('common count reconcile remove(complete), locationCode=' + locationCode + ', dbCount=0, memCount=' + CommonCountMap.get(locationCode))
        } else {
            CommonCountMap.set(locationCode, locationUserCount)
            log.info('common count reconcile set(complete), locationCode=' + locationCode + ', dbCount=' + locationUserCount + ', memCount=' + CommonCountMap.get(locationCode))
        }
    }
    log.info('common count reconcile done(complete), size=' + CommonCountMap.size() + ', total=' + CommonCountMap.totalCount())

    if(shouldTurnOffLocationCodes.size() > 0){
        try {
            log.info('dispatch async turnOff, waveNo=' + waveNo + `, stationCode=${stationCode}, articleIds=` + shouldTurnOffLocationCodes)
            let turnOffFuture = async turnOffLedByArticleId({
                stationCode,
                articleIds: shouldTurnOffLocationCodes
            })
        } catch (asyncErr) {
            log.error('异步灭灯调用触发失败: ' + asyncErr.message)
        }
    }

    let singleUserLocationCodes = []
    let multiUserLocationCodes = []
    for(locationCode in shouldTurnOnLocationCodes){
        let locationUserCount = CommonCountMap.get(locationCode)
        if(locationUserCount > 1){
            multiUserLocationCodes.add(locationCode)
        } else {
            singleUserLocationCodes.add(locationCode)
        }
    }

    if(singleUserLocationCodes.size() > 0){
        try {
            log.info('dispatch async turnOn(single user) from complete, waveNo=' + waveNo + `, stationCode=${stationCode}, color=` + singleUserColor + ', articleIds=' + singleUserLocationCodes)
            let turnOnSingleFuture = async turnOnLedByArticleId({
                stationCode,
                color: singleUserColor,
                articleIds: singleUserLocationCodes
            })
        } catch (asyncErr2) {
            log.error('异步亮灯调用触发失败(单用户色): ' + asyncErr2.message)
        }
    }

    if(multiUserLocationCodes.size() > 0){
        try {
            log.info('dispatch async turnOn(multi user) from complete, waveNo=' + waveNo + `, stationCode=${stationCode}, color=` + multiUserColor + ', articleIds=' + multiUserLocationCodes)
            let turnOnMultiFuture = async turnOnLedByArticleId({
                stationCode,
                color: multiUserColor,
                articleIds: multiUserLocationCodes
            })
        } catch (asyncErr3) {
            log.error('异步亮灯调用触发失败(多用户色): ' + asyncErr3.message)
        }
    }

    exit 200, '拣货完成同步成功'
} catch (e) {
    if(transState != null){
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {
        }
    }
    log.error('拣货完成同步异常: ' + e.message)
    exit 500, '拣货完成同步失败: ' + e.message
}
