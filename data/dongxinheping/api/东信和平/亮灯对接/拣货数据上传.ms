{
  "properties" : { },
  "id" : "oj8xw89pfd3jefa8dwaqv4ye3lqtblwf",
  "script" : null,
  "groupId" : "l5kl0q2xiqa4ryz349c3fkzj3hzm07vw",
  "name" : "拣货数据上传",
  "createTime" : null,
  "updateTime" : 1776672510979,
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
import org.ssssssss.magicboot.model.CommonCountMap
import com.dtflys.forest.Forest

const stationCode = Forest.config().getVariableValue("stationCode")

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
let uploadLocationCodeSet = {}
let uploadLocationCodes = []
let oldUnpickedLocationCodeSet = {}
let oldUnpickedLocationCodes = []

let transState = null
try {
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

        anyline.insert('t_picking_upload_detail', detailRow)
        if(item.status != 1 && uploadLocationCodeSet[item.locationCode] == null){
            uploadLocationCodeSet[item.locationCode] = 1
            uploadLocationCodes.add(item.locationCode)
        }
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
    log.info('common count reconcile start(upload), waveNo=' + waveNo + ', userId=' + userId + ', added=' + addedLocationCodes + ', removed=' + removedLocationCodes + ', affected=' + affectedLocationCodes)
    // 自愈逻辑：不依赖差量结果，按数据库实值回填 CommonCountMap（防止重启后缓存为空）
    for(locationCode in affectedLocationCodes){
        let locationUserCount = db.selectInt("""
            select count(distinct d.bill_id)
              from t_picking_upload_detail d
             where d.location_code = #{locationCode}
               and ifnull(d.status, 0) <> 1
        """, {
            locationCode: locationCode
        })
        if(locationUserCount == null || locationUserCount <= 0){
            CommonCountMap.remove(locationCode)
            log.info('common count reconcile remove(upload), locationCode=' + locationCode + ', dbCount=0, memCount=' + CommonCountMap.get(locationCode))
        } else {
            CommonCountMap.set(locationCode, locationUserCount)
            log.info('common count reconcile set(upload), locationCode=' + locationCode + ', dbCount=' + locationUserCount + ', memCount=' + CommonCountMap.get(locationCode))
        }
    }
    log.info('common count reconcile done(upload), snapshot=' + CommonCountMap.snapshot() + ', size=' + CommonCountMap.size() + ', total=' + CommonCountMap.totalCount())

    if(uploadLocationCodes.size() > 0){
        let singleUserColor = 'GREEN'
        let multiUserColor = 'BLUE'
        let mappedColor = getUserLightColor(userId)
        if(mappedColor != null && mappedColor != ''){
            singleUserColor = mappedColor
        }

        let singleUserLocationCodes = []
        let multiUserLocationCodes = []
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
                    articleIds: singleUserLocationCodes
                })
            } catch (asyncErr) {
                log.error('异步亮灯调用触发失败(单用户色): ' + asyncErr.message)
            }
        }

        if(multiUserLocationCodes.size() > 0){
            try {
                log.info('dispatch async turn on from upload(multi user), waveNo=' + waveNo + `, stationCode=${stationCode}, color=` + multiUserColor + ', articleIds=' + multiUserLocationCodes)
                let turnOnMultiFuture = async turnOnLedByArticleId({
                    stationCode,
                    color: multiUserColor,
                    articleIds: multiUserLocationCodes
                })
            } catch (asyncErr2) {
                log.error('异步亮灯调用触发失败(多用户色): ' + asyncErr2.message)
            }
        }
    }

    log.info("CommonCountMap: {}", CommonCountMap.snapshot())

    exit 200, '拣货数据上传成功'
} catch (e) {
    if(transState != null){
        try {
            anyline.rollbackTrans(transState)
        } catch (ignore) {
        }
    }
    log.error('拣货数据上传事务异常: ' + e.message)
    exit 500, '拣货数据上传失败: ' + e.message
}
