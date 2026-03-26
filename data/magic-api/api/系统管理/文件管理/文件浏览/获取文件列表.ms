{
  "properties" : { },
  "id" : "filemgrlist001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "获取文件列表",
  "createTime" : null,
  "updateTime" : 1774492612832,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ {
    "name" : "parentId",
    "value" : "",
    "description" : "父节点ID（优先）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "dirPath",
    "value" : "",
    "description" : "目录路径（兼容参数）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : "/",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "storageKey",
    "value" : "",
    "description" : "存储源标识（可选）",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : "",
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"total\": 1,\n        \"list\": [\n            {\n                \"id\": \"8041547fc4434022b9b6931bebc44940\",\n                \"storageKey\": \"local\",\n                \"filePath\": \"/111/\",\n                \"parentId\": null,\n                \"fileName\": \"111\",\n                \"fileType\": \"DIR\",\n                \"fileSize\": 0,\n                \"contentType\": null,\n                \"fileExt\": null,\n                \"url\": null,\n                \"md5\": null,\n                \"createTime\": \"2026-03-26 09:30:45\",\n                \"createBy\": \"system\",\n                \"hasChild\": 1\n            }\n        ]\n    },\n    \"timestamp\": 1774492609408,\n    \"executeTime\": 15\n}",
  "description" : "获取指定目录下的直属文件列表",
  "requestBodyDefinition" : null,
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
      "value" : "success",
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
        "name" : "total",
        "value" : "1",
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
        "name" : "list",
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
            "value" : "8041547fc4434022b9b6931bebc44940",
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
            "name" : "storageKey",
            "value" : "local",
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
            "name" : "filePath",
            "value" : "/111/",
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
            "name" : "parentId",
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
            "name" : "fileName",
            "value" : "111",
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
            "name" : "fileType",
            "value" : "DIR",
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
            "name" : "fileSize",
            "value" : "0",
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
            "name" : "contentType",
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
            "name" : "fileExt",
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
            "name" : "url",
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
            "name" : "md5",
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
            "name" : "createTime",
            "value" : "2026-03-26 09:30:45",
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
            "name" : "createBy",
            "value" : "system",
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
            "name" : "hasChild",
            "value" : "1",
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
        } ]
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1774492544150",
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
      "value" : "37",
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
import cn.hutool.core.util.StrUtil
import request
import file

var storageKeyValues = request.getValues('storageKey')
var resolvedStorageKey = (storageKeyValues != null && storageKeyValues.size() > 0) ? storageKeyValues[0] : ''

if (StrUtil.isBlank(resolvedStorageKey)) {
    resolvedStorageKey = file.getDefaultPlatform()
}

var resolvedParentId = null

if(StrUtil.isNotBlank(parentId)){
    resolvedParentId = parentId
}else if(StrUtil.isNotBlank(dirPath)){
    var normalizedDirPath = dirPath
    if(!normalizedDirPath.startsWith('/')){
        normalizedDirPath = '/' + normalizedDirPath
    }
    if(!normalizedDirPath.endsWith('/')){
        normalizedDirPath = normalizedDirPath + '/'
    }

    var parentQuery = """
        select id
        from sys_file
        where is_deleted = 0
          and storage_key = #{resolvedStorageKey}
          and file_path = #{dirPath}
        limit 1
    """
    var parent = db.selectOne(parentQuery, {
        resolvedStorageKey: resolvedStorageKey,
        dirPath: normalizedDirPath
    })
    resolvedParentId = parent != null ? parent.id : null
}

let sql = """
    select
        f.id,
        f.storage_key,
        f.file_path,
        f.parent_id,
        f.file_name,
        f.file_type,
        f.file_size,
        f.content_type,
        f.file_ext,
        f.url,
        f.md5,
        f.create_time,
        f.create_by,
        case
            when exists(
                select 1
                from sys_file c
                where c.is_deleted = 0
                  and c.storage_key = f.storage_key
                  and c.parent_id = f.id
                limit 1
            ) then 1
            else 0
        end as has_child
    from sys_file f
    <where>
        f.is_deleted = 0
        and f.storage_key = #{resolvedStorageKey}
        <if test="resolvedParentId != null and resolvedParentId != ''">
            and f.parent_id = #{resolvedParentId}
        </if>
        <if test="resolvedParentId == null or resolvedParentId == ''">
            and f.parent_id is null
        </if>
        <if test="fileName != null and fileName != ''">
            and f.file_name like concat('%', #{fileName}, '%')
        </if>
        <if test="fileType != null and fileType != ''">
            and f.file_type = #{fileType}
        </if>
    </where>
    order by f.file_type desc, f.create_time desc
"""

return db.page(sql, {
    resolvedStorageKey: StrUtil.isNotBlank(resolvedStorageKey) ? resolvedStorageKey : '',
    resolvedParentId: resolvedParentId,
    fileName: fileName,
    fileType: fileType
})
