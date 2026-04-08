{
  "properties" : { },
  "id" : "oj8xw89pfd3jefa8dwaqv4ye3lqtblwf",
  "script" : null,
  "groupId" : "l5kl0q2xiqa4ryz349c3fkzj3hzm07vw",
  "name" : "拣货数据上传",
  "createTime" : null,
  "updateTime" : 1775538426584,
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
  "requestBody" : "{\n  \"waveNo\": \"WV202603250001\",\n  \"userId\": \"USER002\",\n  \"userName\": \"张三\",\n  \"updateTime\": 1648195200000,\n  \"details\": [\n    {\n      \"id\": \"DTL001\",\n      \"materialCode\": \"MAT001\",\n      \"batchNo\": \"B20260325\",\n      \"locationCode\": \"A1-11-02-01-01\",\n      \"planQuantity\": 100,\n      \"actualQuantity\": 0,\n      \"status\": 0\n    },\n    {\n      \"id\": \"D3003\",\n      \"materialCode\": \"MAT-013\",\n      \"batchNo\": \"BATCH-ERR-3\",\n      \"locationCode\": \"A1-11-01-01-01\",\n      \"planQuantity\": 6,\n      \"actualQuantity\": 0,\n      \"status\": 1\n    }\n  ]\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"拣货数据上传成功\",\n    \"data\": null,\n    \"timestamp\": 1775538425101,\n    \"executeTime\": 96\n}",
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
import redis
import java.util.UUID
import '@/aims/led/turnOnLedByArticleId' as turnOnLedByArticleId
import '@/utils/getUserLightColor' as getUserLightColor

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

let transState = null
try {
    transState = anyline.beginTrans()

    let existingMaster = db.table('t_picking_upload').where().eq('wave_no', waveNo).eq('user_id', userId).selectOne()
    let masterId = UUID.randomUUID().toString().replace('-', '')
    if(existingMaster != null && existingMaster.id != null && existingMaster.id != ''){
        masterId = existingMaster.id
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

    let userCount = db.selectInt("select count(distinct user_id) from t_picking_upload where wave_no = #{waveNo}", {waveNo: waveNo})
    db.update("""
        insert into t_picking_upload_user (wave_no, user_count)
        values (#{waveNo}, #{userCount})
        on duplicate key update user_count = values(user_count)
    """, {waveNo: waveNo, userCount: userCount})
    // redis.default.hashPut("picking:"+waveNo, userId, 1)
    anyline.commitTrans(transState)

    if(uploadLocationCodes.size() > 0){
        let workColor = 'GREEN'
        let mappedColor = getUserLightColor(userId)
        if(mappedColor != null && mappedColor != ''){
            workColor = mappedColor
        } else if(userCount != null && userCount > 1){
            workColor = 'BLUE'
        }

        let shouldTurnOnLocationCodes = []
        let unpickedLocationSet = {}
        let unpickedRows = db.select("""
            select location_code, count(1) as unpicked_count
              from t_picking_upload_detail
             where wave_no = #{waveNo}
               and ifnull(status, 0) <> 1
             group by location_code
        """, {
            waveNo: waveNo
        })
        if(unpickedRows != null){
            for(row in unpickedRows){
                log.info("{}", row)
                if(rowLocationCode != null){
                    unpickedLocationSet[row.locationCode] = 1
                }
            }
        }
        for(locationCode in uploadLocationCodes){
            if(unpickedLocationSet[locationCode] != null){
                shouldTurnOnLocationCodes.add(locationCode)
            }
        }

        if(shouldTurnOnLocationCodes.size() > 0){
            try {
                log.info('dispatch async turnOn from upload, waveNo=' + waveNo + ', stationCode=10001, color=' + workColor + ', articleIds=' + shouldTurnOnLocationCodes)
                let turnOnFuture = async turnOnLedByArticleId({
                    stationCode: "10001",
                    color: workColor,
                    articleIds: shouldTurnOnLocationCodes
                })
            } catch (asyncErr) {
                log.error('异步亮灯调用触发失败: ' + asyncErr.message)
            }
        }
    }

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
