{
  "properties" : { },
  "id" : "198e833b6d95417db17c68e62510394c",
  "script" : null,
  "groupId" : "981acd20d49f474b8f9a91aa0f862170",
  "name" : "上传运行日志",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : null,
  "path" : "/upload",
  "method" : "POST",
  "parameters" : [ {
    "name" : "deviceId",
    "value" : null,
    "description" : "设备唯一ID（App首次启动生成并持久化，同时作为落盘目录名，仅作元数据记录，脚本经 request.getParameter 读取）",
    "required" : true,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "appVersion",
    "value" : null,
    "description" : "App 展示用版本号，如 1.2",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "description" : "PDA App 运行日志 zip 包上传（multipart：file + deviceId + appVersion），原始包落盘 upload.dir/app-log/{deviceId}/ 并写索引 t_app_device_log；按设备保留最近 20 个包。不经 magic-api-plugin-file 的 getFile，兼容无该插件的旧 jar；包内含内网报文，userfiles 公网映射之外",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import org.ssssssss.magicboot.model.Global
import org.ssssssss.magicboot.utils.WebUtils
import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.FileUtil
import java.io.File

// PDA App 运行日志 zip 上传（multipart/form-data：file + deviceId + appVersion），无重试无补传，校验从宽
// 注意：request.getFile 由 magic-api-plugin-file 提供，现场旧 jar 未含该插件（调用报"对象为空"），
// 这里经 WebUtils 取原生 jakarta request 用 getPart 接文件，任何版本的 jar 都可用
var req = WebUtils.getHttpServletRequest()
var part = req.getPart('file')
if(part == null || part.getSize() == 0){
    exit 400, '请上传日志压缩包'
}
if(part.getSize() > 50 * 1024 * 1024){
    exit 400, '日志压缩包超过50MB限制'
}

// multipart 表单文本字段统一走 request.getParameter（不依赖 magic-api 的参数绑定行为）
let formParam = (name) => {
    let v = req.getParameter(name)
    return v == null ? null : (v + '').trim()
}

// deviceId 是客户端输入且会落盘为目录名，白名单校验防路径穿越
let devId = formParam('deviceId')
if(devId == null || devId == '' || !devId.matches('[A-Za-z0-9_-]{1,64}')){
    exit 400, 'deviceId不能为空且只能包含字母/数字/下划线/中划线'
}
let appVer = formParam('appVersion')

let ts = DateUtil.format(new Date(), 'yyyyMMdd-HHmmss')
let savedName = devId + '-' + ts + '.zip'
// 不放 userfiles/ 下：userfiles 有公网 URL 映射，日志含内网报文只落盘不提供下载
let dirPath = Global.getUserFilesBaseDir() + 'app-log/' + devId + '/'
let dirFile = new File(dirPath)
if(!dirFile.exists()){
    dirFile.mkdirs()
}
let dest = new File(dirPath + savedName)
if(dest.exists()){
    dest.delete()
}
FileUtil.writeFromStream(part.getInputStream(), dest)

db.update("""
    insert into t_app_device_log (
        device_id,
        app_version,
        file_name,
        file_size,
        uploaded_at
    ) values (
        #{devId},
        #{appVer},
        #{savedName},
        #{fileSize},
        now()
    )
""", {
    devId: devId,
    appVer: appVer,
    savedName: savedName,
    fileSize: part.getSize()
})

// 保留策略：按设备仅保留最近 20 个包（上传为设置页手动触发，频率低）
let olds = db.select("""
    select id, file_name as fileName from t_app_device_log
    where device_id = #{devId}
    order by id desc
    limit 20, 20
""", {devId: devId})
if(olds != null && olds.size() > 0){
    for(row in olds){
        try {
            let oldFile = new File(dirPath + row.fileName)
            if(oldFile.exists()){
                oldFile.delete()
            }
        } catch(e2) {
            log.warn('app log upload: delete old zip failed, file={}, err={}', row.fileName, e2.message)
        }
        db.update('delete from t_app_device_log where id = #{id}', {id: row.id})
    }
}

log.info('app log upload: deviceId={}, appVersion={}, file={}, size={}', devId, appVer, savedName, part.getSize())

return {
    deviceId: devId,
    fileName: savedName,
    fileSize: part.getSize()
}
