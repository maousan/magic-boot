{
  "properties" : { },
  "id" : "filemgrlist001",
  "script" : null,
  "groupId" : "filemgr001",
  "name" : "获取文件列表",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/list",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "获取指定目录下的文件列表",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
let sql = """
    select
        id,
        storage_key,
        file_path,
        file_name,
        file_type,
        file_size,
        content_type,
        file_ext,
        url,
        md5,
        create_time,
        create_by
    from sys_file
    <where>
        is_deleted = 0
        <if test="dirPath != null and dirPath != ''">
            and file_path like concat(#{dirPath}, '%')
        </if>
        <if test="fileName != null and fileName != ''">
            and file_name like concat('%', #{fileName}, '%')
        </if>
        <if test="fileType != null and fileType != ''">
            and file_type = #{fileType}
        </if>
    </where>
    order by file_type desc, create_time desc
"""

return db.page(sql)
