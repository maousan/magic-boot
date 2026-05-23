{
  "properties" : { },
  "id" : "0a7c9e2d4b6f41c1a53e9782d90b6c11",
  "script" : null,
  "groupId" : "f2a9c31d6e8547b99a0d4c2e8f1357aa",
  "name" : "获取表列表",
  "createTime" : 1779504000000,
  "updateTime" : 1779504000000,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/tables",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "require_login",
    "value" : "false",
    "description" : "",
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
  "responseBody" : "[{\"name\":\"t_inventory\",\"type\":\"BASE TABLE\",\"rowCount\":100,\"columns\":[{\"name\":\"id\",\"type\":\"varchar\",\"pk\":1,\"notnull\":1,\"dfltValue\":null}]}]",
  "description" : "获取当前数据库所有表及其字段元数据（来自 information_schema）",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
var tables = db.select("""
    SELECT TABLE_NAME name, TABLE_TYPE type, TABLE_ROWS rowCount
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
    ORDER BY TABLE_NAME
""")

for (table in tables) {
    var tableName = table.name
    table.columns = db.select("""
        SELECT COLUMN_NAME name, DATA_TYPE type,
               IF(COLUMN_KEY = 'PRI', 1, 0) pk,
               IF(IS_NULLABLE = 'NO', 1, 0) notnull,
               COLUMN_DEFAULT dfltValue
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = #{tableName}
        ORDER BY ORDINAL_POSITION
    """)
}

return tables
