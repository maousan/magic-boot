{
  "properties" : { },
  "id" : "1f4c933e90a4466aa623b3f57d7613a1",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "获取用户灯光颜色",
  "createTime" : 1775600000000,
  "updateTime" : 1775618885019,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/getUserLightColor",
  "description" : "根据用户ID查询灯光颜色映射，未命中返回null",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "userId",
    "value" : null,
    "description" : "用户ID",
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
if(userId == null || userId == ''){
    return null
}

let row = db.table('t_user_light_color')
    .where()
    .eq('user_id', userId)
    .eq('enabled', 1)
    .selectOne()

if(row == null || row.color == null || row.color == ''){
    return null
}

let colorVal = row.color.trim().toUpperCase()
if (colorVal != 'RED' && colorVal != 'GREEN' && colorVal != 'BLUE' && colorVal != 'YELLOW' && colorVal != 'CYAN') {
    return null
}

return colorVal
