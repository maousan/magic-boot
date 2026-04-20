{
  "properties" : { },
  "id" : "6b3f2d9a8c1e4f7a9d2b6c3e5f1a7d9b",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "获取波次用户数量",
  "createTime" : null,
  "updateTime" : null,
  "lock" : "0",
  "createBy" : "admin",
  "updateBy" : null,
  "path" : "/getWaveUserCount",
  "description" : "根据波次号查询t_picking_upload_user.user_count，无记录返回0",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "waveNo",
    "value" : null,
    "description" : "波次号",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
if(waveNo == null || waveNo == ''){
    exit 400, 'waveNo不能为空'
}

let countVal = db.selectInt("""
    select user_count
      from t_picking_upload_user
     where wave_no = #{waveNo}
     limit 1
""", {
    waveNo: waveNo
})

if(countVal == null || countVal < 0){
    return 0
}

return countVal

