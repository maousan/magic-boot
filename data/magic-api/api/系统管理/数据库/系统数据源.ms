{
  "properties" : { },
  "id" : "e35fbec91d8f476bbf026f34dcd8e3ac",
  "script" : null,
  "groupId" : "51e64213e7fe4441b0713dbf0794ee14",
  "name" : "系统数据源",
  "createTime" : 1773480325510,
  "updateTime" : 1773482379489,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/magic",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ ],
  "requestBody" : null,
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : null,
  "description" : null,
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : null
}
================================
import java.util.stream.Collectors
import cn.hutool.extra.spring.SpringUtil
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource

const dynamicDataSource = SpringUtil.getBean(MagicDynamicDataSource.class)

// 获取所有数据源 key
// List<String> keys = dynamicDataSource.datasourceNodes();

// 获取所有数据源详情
const nodes = dynamicDataSource.datasourceNodes();

const list = []

for (node in nodes) {
  if (node.getId()) {
    list.push({
      id: node.getId(),
      key: node.getKey(),
      name: node.getName()
    })
  }
}

return list