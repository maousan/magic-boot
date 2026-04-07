{
  "properties" : { },
  "id" : "f608811b430f4a3cb82feed4067cde71",
  "script" : null,
  "groupId" : "67b2ce258e24491194b74992958c74aa",
  "name" : "查询登录用户菜单",
  "createTime" : null,
  "updateTime" : 1774942761370,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/current/menus",
  "method" : "POST",
  "parameters" : [ {
    "name" : "Authorization",
    "value" : "Bearer aS_1p9NMtFbT1TsB3_YyjIwHqt8U0HyHFJ__",
    "description" : null,
    "required" : false,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "{\n\n}",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 500,\n    \"message\": \"系统内部出现错误\",\n    \"data\": null,\n    \"timestamp\": 1772954366774,\n    \"executeTime\": 541\n}",
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
          "name" : "title",
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
          "name" : "isShow",
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
          "name" : "url",
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
          "name" : "icon",
          "value" : "settings",
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
          "name" : "componentName",
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
          "name" : "openMode",
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
          "name" : "component",
          "value" : "Layout",
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
          "children" : [ {
            "name" : "title",
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
            "name" : "icon",
            "value" : "settings",
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
            "value" : "false",
            "description" : "",
            "required" : false,
            "dataType" : "Boolean",
            "type" : null,
            "defaultValue" : null,
            "validateType" : "",
            "error" : "",
            "expression" : "",
            "children" : [ ]
          } ]
        }, {
          "name" : "redirect",
          "value" : "noRedirect",
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
          "name" : "alwaysShow",
          "value" : "true",
          "description" : "",
          "required" : false,
          "dataType" : "Boolean",
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
              "name" : "title",
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
              "name" : "isShow",
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
              "name" : "url",
              "value" : "/system/menu/menu-list",
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
              "name" : "icon",
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
              "name" : "componentName",
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
              "name" : "openMode",
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
              "name" : "component",
              "value" : "/system/menu/menu-list",
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
              "value" : "/system/menu/menu-list",
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
              "children" : [ {
                "name" : "title",
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
                "name" : "icon",
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
                "name" : "keepAlive",
                "value" : "true",
                "description" : "",
                "required" : false,
                "dataType" : "Boolean",
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
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1657368503505",
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
      "value" : "18",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
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
import 'cn.dev33.satoken.stp.StpUtil';

var userId = StpUtil.getLoginId()

// 查出所有隐藏或者打开方式为“页签”和“iframe”的菜单（排除http地址和静态页面）

// 查出所有显示的菜单
var menus = db.select("""
    select
        sm.id,
        sm.title,
        ifnull(sm.name,sm.id) as name,
        sm.pid,
        sm.component,
        sm.path,
        sm.sort,
        sm.icon,
        sm.keep_alive,
        sm.meta,
        sm.type,
        sm.category
    from sys_menu sm left join sys_dynamic_component sdc on sdc.id = sm.component_id
    where 1=1
    ?{userId != '1',
        and sm.id in (
            select menu_id from sys_role_menu where role_id in (
                select role_id from sys_user_role where user_id = #{userId}
            )
        )
    }
    ?{type != null, and sm.type = #{type}}
    and sm.is_del = 0 and sm.type != 'button' and sm.status = 1 order by sm.sort
""")

for(menu in menus){
    if(!menu.meta) {
        menu.meta = {}
    } else {
        menu.meta = JsonUtils.readValue(menu.meta, Map.class)
    }
    menu.meta.title = menu.title
    menu.meta.icon = menu.icon
    menu.meta.keepAlive = (menu.keepAlive === 1 ? true : false)
}
var nodes = menus.toMap(it => it.id)
nodes.each((key, node) => {
    if (nodes.containsKey(node.pid)) {
        // nodes[node.pid].redirect = "noRedirect";
        // nodes[node.pid].component = "BasicLayout";
        // nodes[node.pid].alwaysShow = true;
        if(!node.category){
            node.category = nodes[node.pid].category || inferCategory(node.path)
        }
        if(!nodes[node.pid].children){
            nodes[node.pid].children = []
        }
        nodes[node.pid].children.push(node)
    }
})
var layoutMenus = []
nodes.each((key, node) => {
    if(node.pid === '0') {
        if(!node.category){
            node.category = inferCategory(node.path)
        }
        // if(node.component != 'BasicLayout'){
        //     node = {
        //         icon: node.icon,
        //         component: 'BasicLayout',
        //         redirect: node.path,
        //         children: [node]
        //     }
        // }
        layoutMenus.push(node)
    }
})

return layoutMenus
