{
  "properties" : { },
  "id" : "lblsync0123456789abcdef0123456789a",
  "script" : null,
  "groupId" : "999006442fb54aebbb2015e4a23bdba8",
  "name" : "定时同步标签数据",
  "createTime" : null,
  "updateTime" : 1779875091353,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/sync-labels",
  "cron" : "0 0 */1 * * ?",
  "enabled" : true,
  "description" : "每小时运行一次，同步AIMS标签数据到t_location_label",
  "params" : "",
  "misfirePolicy" : "SMART",
  "concurrent" : false,
  "dependsOn" : null,
  "maxRetry" : 0,
  "timeout" : 0,
  "jobType" : "script",
  "clazz" : null
}
================================
import log
import java.util.List
import java.util.ArrayList
import cn.hutool.core.util.IdUtil
import cn.hutool.core.date.DateUtil
import '@/aims/labels/getLabelList' as getLabelList

const pageSize = 50

let normalizeText = (value) => {
    if(value == null){
        return ''
    }
    return (value + '').trim()
}

let startedAt = new Date()
let totalLabels = 0
let savedBindings = 0
let skippedBindings = 0
let failedPages = []

try {
    let allLabels = new ArrayList()
    let page = 0
    while(true){
        let result = getLabelList(page, pageSize)
        if(result == null || result.success == false){
            let message = '调用AIMS标签接口失败'
            if(result != null && result.status != null){
                message = 'AIMS标签接口返回失败: status=' + result.status
            }
            failedPages.add({
                page: page,
                message: message
            })
            break
        }

        let labels = result.labels
        if(labels == null || labels.size() == 0){
            break
        }

        allLabels.addAll(labels)

        let lastPage = result.lastPage
        if(lastPage == null || lastPage == true){
            break
        }
        page = page + 1
    }

    if(failedPages.size() > 0){
        log.error('AIMS label sync aborted, failed to fetch labels, failedPages=' + failedPages.size())
        return {
            success: false,
            startedAt: startedAt,
            finishedAt: new Date(),
            totalLabels: 0,
            savedBindings: 0,
            skippedBindings: 0,
            failedPages: failedPages,
            message: '获取标签数据失败，跳过同步'
        }
    }

    totalLabels = allLabels.size()

    db.update("delete from t_location_label")

    let bindTime = DateUtil.now()
    for(label in allLabels){
        let labelCode = normalizeText(label.labelCode)
        if(labelCode == ''){
            skippedBindings = skippedBindings + 1
            continue
        }

        let articles = label.articles
        if(articles == null || articles.size() == 0){
            continue
        }

        let locationCode = normalizeText(articles.get(0))
        if(locationCode == ''){
            skippedBindings = skippedBindings + 1
            continue
        }

        db.update("""
            insert into t_location_label (id, location_code, label_code, bind_time)
            values (#{id}, #{locationCode}, #{labelCode}, #{bindTime})
        """, {
            id: IdUtil.fastSimpleUUID(),
            locationCode: locationCode,
            labelCode: labelCode,
            bindTime: bindTime
        })

        savedBindings = savedBindings + 1
    }

    let success = failedPages.size() == 0
    log.info('AIMS label sync done, success=' + success + ', totalLabels=' + totalLabels + ', savedBindings=' + savedBindings + ', skippedBindings=' + skippedBindings)

    return {
        success: success,
        startedAt: startedAt,
        finishedAt: new Date(),
        totalLabels: totalLabels,
        savedBindings: savedBindings,
        skippedBindings: skippedBindings,
        failedPages: failedPages
    }
} catch (e) {
    log.error('AIMS label sync failed, message={}', e)
    return {
        success: false,
        startedAt: startedAt,
        finishedAt: new Date(),
        totalLabels: totalLabels,
        savedBindings: savedBindings,
        skippedBindings: skippedBindings,
        failedPages: failedPages,
        message: '定时同步标签数据失败: ' + e.message
    }
}
