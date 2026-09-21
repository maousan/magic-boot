{
  "properties" : { },
  "id" : "bf2181af146f4ec0988db30403d1b358",
  "script" : null,
  "groupId" : "981acd20d49f474b8f9a91aa0f862170",
  "name" : "删除日志",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/delete/{id}",
  "method" : "DELETE",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ {
    "name" : "id",
    "description" : "t_app_device_log 主键",
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "description" : "删除一条 App 日志上传记录及其落盘 zip 文件（管理页清理用）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import org.ssssssss.magicboot.model.Global
import java.io.File

let row = db.selectOne("""
    select id, device_id, file_name
    from t_app_device_log where id = #{id}
""", {id: id})

if(row == null){
    exit 404, '记录不存在'
}

// 文件名/设备号均为服务端生成或白名单校验过的值，仍按白名单防呆
if(row.fileName != null && row.deviceId != null
    && (row.deviceId + '').matches('[A-Za-z0-9_-]{1,64}')
    && (row.fileName + '').matches('[A-Za-z0-9_.-]{1,128}')){
    try {
        let file = new File(Global.getUserFilesBaseDir() + 'app-log/' + row.deviceId + '/' + row.fileName)
        if(file.exists()){
            file.delete()
            log.info('app log delete: removed file {}', file.getPath())
        }
    } catch(e2) {
        log.warn('app log delete: remove file failed, fileName={}, err={}', row.fileName, e2.message)
    }
}

db.update('delete from t_app_device_log where id = #{id}', {id: id})

return true
