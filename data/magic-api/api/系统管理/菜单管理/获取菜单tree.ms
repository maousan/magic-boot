{
  "properties" : { },
  "id" : "ffb6988226d14e6789cc30193e008183",
  "script" : null,
  "groupId" : "67b2ce258e24491194b74992958c74aa",
  "name" : "获取菜单tree",
  "createTime" : null,
  "updateTime" : 1773477410492,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/tree",
  "method" : "GET",
  "parameters" : [ ],
  "options" : [ {
    "name" : "permission",
    "value" : "menu:view",
    "description" : "允许拥有该权限的访问",
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "requestBody" : "{\n\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 402,\n    \"message\": \"凭证已过期\",\n    \"data\": null,\n    \"timestamp\": 1773302129440,\n    \"executeTime\": null\n}",
  "description" : "",
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
    "children" : [ ]
  },
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
            "value" : "b1851d1b13594e71840103c11a37a669",
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
            "name" : "name",
            "value" : "系统设置",
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
            "name" : "pid",
            "value" : "0",
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
            "name" : "path",
            "value" : "/system",
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
            "name" : "component",
            "value" : "/system",
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
            "name" : "sort",
            "value" : "10",
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
            "name" : "permission",
            "value" : "",
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
            "name" : "description",
            "value" : "",
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
            "name" : "icon",
            "value" : "SettingsSharp",
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
            "name" : "keepAlive",
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
            "name" : "componentId",
            "value" : "",
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
            "name" : "meta",
            "value" : "",
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
            "name" : "type",
            "value" : "catalog",
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
            "name" : "typeName",
            "value" : "目录",
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
            "name" : "children",
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
                "value" : "39be13ef6f0745568c80bf35202ddb2b",
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
                "name" : "name",
                "value" : "菜单管理",
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
                "name" : "pid",
                "value" : "b1851d1b13594e71840103c11a37a669",
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
                "name" : "path",
                "value" : "/system/menu",
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
                "name" : "component",
                "value" : "/system/menu/list",
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
                "name" : "sort",
                "value" : "10",
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
                "name" : "permission",
                "value" : "",
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
                "name" : "description",
                "value" : "",
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
                "name" : "icon",
                "value" : "Menu",
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
                "name" : "keepAlive",
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
                "name" : "componentId",
                "value" : "63227c407c5d40f98df2486390a6f841",
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
                "name" : "meta",
                "value" : "",
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
                "name" : "type",
                "value" : "menu",
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
                "name" : "typeName",
                "value" : "菜单",
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
                "name" : "children",
                "value" : "",
                "description" : "",
                "required" : false,
                "dataType" : "Array",
                "type" : null,
                "defaultValue" : null,
                "validateType" : "",
                "error" : "",
                "expression" : "",
                "children" : [ ]
              } ]
            } ]
          } ]
        } ]
      }, {
        "name" : "total",
        "value" : "3",
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
    }, {
      "name" : "timestamp",
      "value" : "1772954879228",
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
      "value" : "475",
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
import org.ssssssss.magicapi.utils.JsonUtils

var toTree = (list,pid) => select t.*, toTree(list,t.id) children from list t where t.pid = pid

const menus = db.select("""
    select 
        sm.id,
        sm.name,
        sm.title,
        sm.pid,
        sm.path,
        sm.component,
        sm.sort,
        sm.permission,
        sm.description,
        sm.icon,
        sm.keep_alive,
        sm.component_id,
        sm.meta,
        sm.readonly,
        sm.status,
        sm.type
    from sys_menu sm 
    where sm.is_del = 0 and sm.type != 'button' order by sm.sort
""")

trans.translate(menus, {
    "status": {
        "target":"statusName",
        "dicts": [{
            "value": 1,
            "label": "启用"
        }, {
            "value": 0,
            "label": "禁用"
        }]
    },
    "type": {
        "dicts": [
            {
                "value": "menu",
                "label": "菜单"
            },
            {
                "value": "catalog",
                "label": "目录"
            },
            {
                "value": "link",
                "label": "外链"
            },
            {
                "value": "embedded",
                "label": "内嵌"
            }
        ]
    }
})

menus.forEach(menu => {
    if (menu.meta && menu.meta !== '') {
        menu.meta = JsonUtils.readValue(menu.meta, Map.class)
    } else {
        menu.meta = {}
    }
})

var list = toTree(menus,'0')

return {
    list: list,
    total: list.getLength()
}