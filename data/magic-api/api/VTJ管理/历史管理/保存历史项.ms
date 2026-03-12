{
  "properties" : { },
  "id" : "ab4edcca759d45b6a5fbdfd80919ff36",
  "script" : null,
  "groupId" : "48261ceac8854872a3b2e3ff932b34d3",
  "name" : "保存历史项",
  "createTime" : 1773297233893,
  "updateTime" : 1773320792035,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/items/save",
  "method" : "POST",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : "",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
// import org.ssssssss.magicapi.utils.JsonUtils

// const { fileId, item } = body

// const id = `${fileId}_${item.id}`

// let row = anyline.queryMap("sys_vtj_history", `++id='${fileId}'`)

// if (!row) {
//     exit 0, '历史记录不存在'
// }

// const historyContent = JsonUtils.readValue(row.content||'{}', Map.class)

// const historyItems = historyContent.item||[]

// if (historyItems.length === 0) {
//     historyItems.push(item)
// }

// for (index, historyItem in historyItems) {
//     const targetId = `${fileId}_${historyItem.id}`
//     if (targetId === id) {
//         historyItems[index] = historyItem
//         break
//     }
// }

// row.content = JsonUtils.toJsonString(historyItems)

// anyline.update("sys_vtj_history", row)

return true
