{
  "properties" : { },
  "id" : "389860ae51c841518c1a8adfa38c4096",
  "script" : null,
  "groupId" : "981acd20d49f474b8f9a91aa0f862170",
  "name" : "日志列表",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "deviceId",
    "value" : null,
    "description" : "按设备ID精确过滤，可选",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "description" : "分页查询 App 上传日志索引（db.page 读 pageNo/pageSize），附带 zip 文件是否仍在盘的 fileExists 标记（保留策略会清理文件）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import org.ssssssss.magicboot.model.Global
import java.io.File

let sql = """
    select id, device_id, app_version, file_name, file_size, uploaded_at, create_time
    from t_app_device_log
    <where>
        <if test="deviceId != null and deviceId != ''">
            and device_id = #{deviceId}
        </if>
    </where>
    order by id desc
"""

var pageResult = db.page(sql)

// 标记 zip 是否仍在盘（按设备保留最近 20 个，超限会被清理）
let dirBase = Global.getUserFilesBaseDir() + 'app-log/'
if(pageResult != null && pageResult.list != null){
    for(row in pageResult.list){
        row.fileExists = row.fileName != null && new File(dirBase + row.deviceId + '/' + row.fileName).exists()
    }
}

return pageResult
