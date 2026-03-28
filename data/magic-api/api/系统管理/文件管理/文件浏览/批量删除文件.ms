{
  "properties" : { },
  "id" : "filemgrbatchdelete001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "批量删除文件",
  "createTime" : null,
  "updateTime" : 1774532118078,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/batch-delete",
  "method" : "DELETE",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "{\n  \"fileIds\": [\n    \"id-1\",\n    \"id-2\"\n  ],\n  \"filePaths\": [\n    \"/test/a.txt\",\n    \"/test/b.txt\"\n  ],\n  \"storageKey\": \"local\"\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 0,\n    \"message\": \"storageKey cannot be blank\",\n    \"data\": null,\n    \"timestamp\": 1774516871898,\n    \"executeTime\": 7\n}",
  "description" : "Batch logical delete in sys_file. Body only.",
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
      "name" : "fileIds",
      "value" : "",
      "description" : "Array of file IDs to logical delete",
      "required" : false,
      "dataType" : "Array",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "filePaths",
      "value" : "",
      "description" : "Array of file paths to logical delete",
      "required" : false,
      "dataType" : "Array",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "storageKey",
      "value" : "",
      "description" : "Storage key (required)",
      "required" : true,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  },
  "responseBodyDefinition" : null
}
================================
import cn.hutool.core.util.StrUtil

const {
    fileIds,
    storageKey
} = body

var resolvedStorageKey = StrUtil.isNotBlank(storageKey) ? storageKey.trim() : ''
if(StrUtil.isBlank(resolvedStorageKey)){
    exit 0, 'storageKey cannot be blank'
}

var idSource = fileIds
if(idSource == null){
    idSource = []
}

var pathSource = filePaths
if(pathSource == null){
    pathSource = []
}

var uniqueId = {}
var normalizedIds = []
for(id in idSource){
    var idValue = id == null ? '' : ('' + id).trim()
    if(StrUtil.isBlank(idValue)){
        continue
    }
    if(uniqueId[idValue] == null){
        uniqueId[idValue] = true
        normalizedIds.push(idValue)
    }
}

var uniquePath = {}
var normalizedPaths = []
for(path in pathSource){
    var pathValue = path == null ? '' : ('' + path).trim()
    if(StrUtil.isBlank(pathValue)){
        continue
    }
    if(uniquePath[pathValue] == null){
        uniquePath[pathValue] = true
        normalizedPaths.push(pathValue)
    }
}

if(normalizedIds.size() == 0 && normalizedPaths.size() == 0){
    exit 0, 'fileIds/filePaths cannot both be empty'
}

var deleted = []
var failed = []
var notFound = []
var deletedIds = []
var deletedPaths = []
var failedIds = []
var failedPaths = []
var notFoundIds = []
var notFoundPaths = []

for(fileId in normalizedIds){
    var row = db.selectOne("""
        select id, storage_key, file_path
        from sys_file
        where is_deleted = 0
          and id = #{id}
        limit 1
    """, {id: fileId})

    if(row == null){
        var notFoundById = {id: fileId, filePath: null, storageKey: null, reason: 'ID_NOT_FOUND'}
        notFound.push(notFoundById)
        notFoundIds.push(fileId)
        continue
    }

    var rowStorageKey = row.storage_key != null ? row.storage_key : row.storageKey
    var rowFilePath = row.file_path != null ? row.file_path : row.filePath

    if(resolvedStorageKey != rowStorageKey){
        var notFoundByStorage = {id: fileId, filePath: rowFilePath, storageKey: rowStorageKey, reason: 'STORAGE_KEY_MISMATCH'}
        notFound.push(notFoundByStorage)
        notFoundIds.push(fileId)
        continue
    }

    var updatedById = db.update("""
        update sys_file
        set is_deleted = 1,
            update_time = now()
        where is_deleted = 0
          and id = #{id}
          and storage_key = #{storageKey}
    """, {id: fileId, storageKey: rowStorageKey})

    if(updatedById > 0){
        var deletedItemById = {id: fileId, filePath: rowFilePath, storageKey: rowStorageKey}
        deleted.push(deletedItemById)
        deletedIds.push(fileId)
        deletedPaths.push(rowFilePath)
    }else{
        var failedItemById = {id: fileId, filePath: rowFilePath, storageKey: rowStorageKey, reason: 'DB_UPDATE_FAILED'}
        failed.push(failedItemById)
        failedIds.push(fileId)
        failedPaths.push(rowFilePath)
    }
}

for(path in normalizedPaths){
    var updatedByPath = db.update("""
        update sys_file
        set is_deleted = 1,
            update_time = now()
        where is_deleted = 0
          and storage_key = #{storageKey}
          and file_path = #{filePath}
    """, {storageKey: resolvedStorageKey, filePath: path})

    if(updatedByPath > 0){
        var deletedItemByPath = {id: null, filePath: path, storageKey: resolvedStorageKey}
        deleted.push(deletedItemByPath)
        deletedPaths.push(path)
    }else{
        var notFoundByDirectPath = {id: null, filePath: path, storageKey: resolvedStorageKey, reason: 'PATH_NOT_FOUND'}
        notFound.push(notFoundByDirectPath)
        notFoundPaths.push(path)
    }
}

var total = normalizedIds.size() + normalizedPaths.size()

return {
    total: total,
    inputIdCount: normalizedIds.size(),
    inputPathCount: normalizedPaths.size(),
    deletedCount: deleted.size(),
    failedCount: failed.size(),
    notFoundCount: notFound.size(),
    allSuccess: failed.size() == 0 && notFound.size() == 0,
    deleted: deleted,
    failed: failed,
    notFound: notFound,
    deletedIds: deletedIds,
    deletedPaths: deletedPaths,
    failedIds: failedIds,
    failedPaths: failedPaths,
    notFoundIds: notFoundIds,
    notFoundPaths: notFoundPaths
}