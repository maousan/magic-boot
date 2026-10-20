{
  "properties" : { },
  "id" : "552999ede3014a72be04e2fee36eeaf7",
  "script" : null,
  "groupId" : "6ce8544b21954e189dc7aeb484aeddfc",
  "name" : "查询article标签绑定",
  "createTime" : null,
  "updateTime" : 1792474563845,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/article-label/query",
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
  "requestBody" : "{\"keyword\":\"A1-11-01-01-03\"}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n  \"code\": 200,\n  \"message\": \"success\",\n  \"data\": [\n    {\n      \"articleId\": \"B2-12-01-01-01\",\n      \"labelCode\": \"0BCFE99BB699\",\n      \"sLabelStatus\": {\n        \"aliveStatus\": \"ONLINE\",\n        \"signalStrength\": \"GOOD\",\n        \"battery\": \"GOOD\",\n        \"batteryLevel\": \"FF\",\n        \"temperature\": 24,\n        \"firmwareVersion\": 32,\n        \"updateStatus\": \"SUCCESS\"\n      },\n      \"accessPoint\": {\n        \"name\": \"GW_10.33.1.242\",\n        \"status\": \"CONNECTED\",\n        \"ipAddress\": \"10.33.1.242\",\n        \"macAddress\": \"D0-25-44-21-3C-46\",\n        \"code\": \"D02544FFFE213C46\",\n        \"port\": 80,\n        \"id\": 1303,\n        \"version\": \"N1.3.7.0\",\n        \"groupId\": \"1234\",\n        \"uriScheme\": \"HTTP\",\n        \"lastConnectionDate\": \"2026-10-20T05:40:49.705+0000\",\n        \"endDevicesCountByState\": { \"UNASSIGNED\": 153, \"SUCCESS\": 176, \"PROCESSING\": 2, \"TIMEOUT\": 5 }\n      }\n    }\n  ],\n  \"timestamp\": 1792465600000,\n  \"executeTime\": 94\n}",
  "description" : "查询AIMS中article(库位)与电子标签的绑定关系：keyword 单串，符合库位码格式（如 A1-11-01-01-03）按 articleId 查该库位绑定的标签，否则按 labelCode 查该标签绑定的库位；返回数组 [{articleId, labelCode, sLabelStatus, accessPoint}]，sLabelStatus/accessPoint 为 AIMS 原样字段透传（缺省 null），扫未绑定标签返回单条且 articleId 为空串，标签在 AIMS 不存在返回空数组",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import java.util.regex.Pattern
import com.alibaba.fastjson.JSONObject
import '@/aims/labels/getLabelsByArticle' as getLabelsByArticle
import '@/aims/labels/getLabelByCode' as getLabelByCode


// 【临时测试桩（用户 2026-10-20 加，测 App 卡片用）：扫 04C5FF2CB899 返回未绑定样例（articleId 空串 + accessPoint null，App 判未绑定）；
// 其余任意 keyword 返回已绑定真实样例（B2-12-01-01-01/0BCFE99BB699）；测完删除本段（含两个 return 到此注释下方空行）即恢复真实查询】
let stubKw = (body.keyword == null ? '' : (body.keyword + '').trim())
if(stubKw == '04C5FF2CB899'){
    return [
        {
            articleId: '',
            labelCode: '04C5FF2CB899',
            sLabelStatus: {
                aliveStatus: 'ONLINE',
                signalStrength: 'FAIR',
                battery: 'GOOD',
                batteryLevel: 'E7',
                temperature: 26,
                firmwareVersion: 32,
                updateStatus: 'SUCCESS'
            },
            accessPoint: null
        }
    ]
}
return [
    {
        articleId: 'B2-12-01-01-01',
        labelCode: '0BCFE99BB699',
        sLabelStatus: {
            aliveStatus: 'ONLINE',
            signalStrength: 'GOOD',
            battery: 'GOOD',
            batteryLevel: 'FF',
            temperature: 24,
            firmwareVersion: 32,
            updateStatus: 'SUCCESS'
        },
        accessPoint: {
            name: 'GW_10.33.1.242',
            status: 'CONNECTED',
            ipAddress: '10.33.1.242',
            macAddress: 'D0-25-44-21-3C-46',
            code: 'D02544FFFE213C46',
            port: 80,
            id: 1303,
            version: 'N1.3.7.0',
            groupId: '1234',
            uriScheme: 'HTTP',
            lastConnectionDate: '2026-10-20T05:40:49.705+0000',
            endDevicesCountByState: {
                UNASSIGNED: 153,
                SUCCESS: 176,
                PROCESSING: 2,
                TIMEOUT: 5
            }
        }
    }
]

// 2026-10-20 返回信息优化：AIMS 404（article/标签不存在）视为无绑定返回 []；其它错误提取 returnMsg 给干净提示
let failClean = (result) => {
    if(result.status == 404){
        return null
    }
    let failMsg = result.message
    if(failMsg != null && (failMsg + '').startsWith('{')){
        try{
            let parsed = JSONObject.parseObject(failMsg)
            if(parsed != null && parsed.getString('returnMsg') != null){
                failMsg = parsed.getString('returnMsg')
            }
        }catch(e){
        }
    }
    return '查询AIMS标签失败: ' + (failMsg == null || failMsg == '' ? ('status=' + result.status) : failMsg)
}

let keyword = body.keyword
if(keyword == null || (keyword + '').trim() == ''){
    exit 400, 'keyword不能为空'
}
keyword = (keyword + '').trim()

// 2026-10-20 用户口径：符合库位码格式（字母数字-两位-两位-两位-两位，如 A1-11-01-01-03）按 articleId 查，否则按 labelCode 查
let isArticleId = Pattern.compile('^[A-Za-z][0-9]+-[0-9]{2}-[0-9]{2}-[0-9]{2}-[0-9]{2}$').matcher(keyword).matches()

let pairs = []
if(isArticleId){
    let result = getLabelsByArticle(keyword)
    if(!result.success){
        log.warn('article-label query by articleId failed, articleId=' + keyword + ', status=' + result.status + ', message=' + result.message)
        let cleanMsg = failClean(result)
        if(cleanMsg == null){
            return pairs
        }
        exit 500, cleanMsg
    }
    if(result.labels != null){
        for(label in result.labels){
            if(label.labelCode != null && label.labelCode != ''){
                // 2026-10-20 改因（App issue 02）：卡片需要标签状态/网关信息，绑定对透传 AIMS 原样字段 sLabelStatus、accessPoint（缺省 null，App 容错）
                // pairs.add({
                //     articleId: keyword,
                //     labelCode: label.labelCode
                // })
                pairs.add({
                    articleId: keyword,
                    labelCode: label.labelCode,
                    sLabelStatus: label.sLabelStatus,
                    accessPoint: label.accessPoint
                })
            }
        }
    }
}else{
    let result = getLabelByCode(keyword)
    if(!result.success){
        log.warn('article-label query by labelCode failed, labelCode=' + keyword + ', status=' + result.status + ', message=' + result.message)
        let cleanMsg = failClean(result)
        if(cleanMsg == null){
            return pairs
        }
        exit 500, cleanMsg
    }
    let label = result.label
    if(label != null){
        // 2026-10-20 改因（App issue 02）：扫未绑定标签（articleList 空）也返回单条记录，articleId 稳定为空串，App 以 articleId 为空判定未绑定，状态字段照常返回
        // if(label != null && label.articleList != null){
        //     for(article in label.articleList){
        //         if(article.id != null && article.id != ''){
        //             pairs.add({
        //                 articleId: article.id,
        //                 labelCode: keyword
        //             })
        //         }
        //     }
        // }
        if(label.articleList == null || label.articleList.size() == 0){
            pairs.add({
                articleId: '',
                labelCode: keyword,
                sLabelStatus: label.sLabelStatus,
                accessPoint: label.accessPoint
            })
        }else{
            for(article in label.articleList){
                if(article.id != null && article.id != ''){
                    pairs.add({
                        articleId: article.id,
                        labelCode: keyword,
                        sLabelStatus: label.sLabelStatus,
                        accessPoint: label.accessPoint
                    })
                }
            }
        }
    }
}

return pairs
