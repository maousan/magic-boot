{
  "properties" : { },
  "id" : "b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7",
  "script" : null,
  "groupId" : "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
  "name" : "绑定",
  "createTime" : null,
  "updateTime" : 1777469964965,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/bind",
  "method" : "POST",
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
  "requestBody" : "{\r\n    \"batchId\":\"\",\r\n    \"epc\":\"\"\r\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 400,\n    \"message\": \"批次ID不能为空\",\n    \"data\": null,\n    \"timestamp\": 1777468592637,\n    \"executeTime\": 157\n}",
  "description" : "绑定业务批次ID与RFID EPC的一对一关系。",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import log
import cn.hutool.core.util.IdUtil

let batchId = body.batchId
let epc = body.epc

if(batchId == null || (batchId + '').trim() == ''){
    exit 400, '批次ID不能为空'
}
if(epc == null || (epc + '').trim() == ''){
    exit 400, 'EPC不能为空'
}
batchId = (batchId + '').trim()
epc = (epc + '').trim()

db.update("""
    create table if not exists t_batch_epc (
        id          varchar(32)  not null primary key comment '主键',
        batch_id    varchar(128) not null comment '业务批次ID',
        epc         varchar(128) not null comment 'RFID标签EPC',
        status      tinyint      not null default 1 comment '状态: 1=绑定中, 0=已解绑',
        bind_time   datetime     not null default current_timestamp comment '绑定时间',
        unbind_time datetime     null comment '解绑时间',
        create_time datetime     not null default current_timestamp comment '创建时间',
        update_time datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
        index idx_batch_id (batch_id),
        index idx_epc (epc),
        index idx_status (status)
    ) comment='批次EPC绑定关系表'
""")

let existing = db.select("""
    select batch_id, epc from t_batch_epc where status = 1 and (batch_id = #{batchId} or epc = #{epc})
""", {batchId: batchId, epc: epc})

if(existing != null && existing.size() > 0){
    let conflict = existing.get(0)
    if(conflict.batch_id == batchId){
        exit 400, '批次ID已绑定EPC: ' + conflict.epc
    }
    exit 400, 'EPC已绑定批次: ' + conflict.batch_id
}

let id = IdUtil.fastSimpleUUID()
db.update("""
    insert into t_batch_epc (id, batch_id, epc, status, bind_time)
    values (#{id}, #{batchId}, #{epc}, 1, now())
""", {id: id, batchId: batchId, epc: epc})

log.info('batch-epc bind: batchId=' + batchId + ', epc=' + epc)

return {
    id: id,
    batchId: batchId,
    epc: epc,
    bindTime: db.selectValue("select bind_time from t_batch_epc where id = #{id}", {id: id})
}