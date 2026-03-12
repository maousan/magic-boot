{
  "properties" : { },
  "id" : "530c445f0e69494581d1420cd5311440",
  "script" : null,
  "groupId" : "6f106ebdee21489db34b956f7770ff03",
  "name" : "获取最后一版代码",
  "createTime" : null,
  "updateTime" : 1773115405911,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/getLastCode",
  "method" : "GET",
  "parameters" : [ {
    "name" : "componentId",
    "value" : "63227c407c5d40f98df2486390a6f841",
    "description" : null,
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ {
    "name" : "permission",
    "value" : "component:get:last:code",
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
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"sourceCode\": \"<template>\\n    <div class=\\\"mb-list\\\">\\n        <div class=\\\"mb-search\\\">\\n            <n-space>\\n                <n-input v-model:value=\\\"searchValue\\\" :size=\\\"$global.uiSize.value\\\" @keyup.enter=\\\"searchMenu\\\" placeholder=\\\"菜单名称、链接、权限标识、关联组件\\\"\\n                         style=\\\"width: 270px\\\"></n-input>\\n                <n-button type=\\\"primary\\\" :size=\\\"$global.uiSize.value\\\" @click=\\\"searchMenu\\\">\\n                    <mb-icon icon=\\\"Search\\\" />\\n                    搜索\\n                </n-button>\\n                <n-button :size=\\\"$global.uiSize.value\\\" @click=\\\"() => { searchValue = ''; searchMenu() }\\\">\\n                    <mb-icon icon=\\\"TrashOutline\\\" />\\n                    清空\\n                </n-button>\\n            </n-space>\\n        </div>\\n        <div class=\\\"mb-toolbar\\\">\\n            <n-space>\\n                <n-button :size=\\\"$global.uiSize.value\\\" type=\\\"primary\\\" @click=\\\"addSubMenu('0')\\\" v-permission=\\\"'menu:save'\\\">\\n                    <mb-icon icon=\\\"AddOutline\\\" />\\n                    添加菜单\\n                </n-button>\\n                <n-button :size=\\\"$global.uiSize.value\\\" type=\\\"primary\\\" @click=\\\"() => table.toggleExpand()\\\">\\n                    <mb-icon icon=\\\"ArrowDownOutline\\\" />\\n                    展开/折叠\\n                </n-button>\\n            </n-space>\\n        </div>\\n        <div class=\\\"mb-table\\\">\\n            <mb-table ref=\\\"table\\\" v-bind=\\\"tableOptions\\\"/>\\n        </div>\\n        <mb-modal ref=\\\"menuFormDialog\\\" width=\\\"650px\\\" :title=\\\"dialogTitle\\\" @confirm=\\\"menuFormRef.save($event)\\\">\\n            <menu-form ref=\\\"menuFormRef\\\" :menu-tree=\\\"menuTree\\\" :menu-data=\\\"menuData\\\" @reload-table=\\\"reloadTable\\\"/>\\n        </mb-modal>\\n    </div>\\n</template>\\n\\n<script setup>\\n\\nimport {ref, reactive, onMounted, nextTick, watch, h} from 'vue'\\nimport MbIcon from '@/components/magic/basic/mb-icon.vue';\\nimport { isEmpty } from 'lodash-es'\\n\\n\\nlet menuTree = ref([])\\nconst menuData = ref([])\\nlet searchValue = ref('')\\nconst table = ref()\\nconst tableOptions = reactive({\\n    id: 'menu-list',\\n    virtualScroll: true,\\n    loading: false,\\n    showNo: false,\\n    page: false,\\n    cols: [\\n        {\\n            field: 'name',\\n            label: '菜单名称',\\n            type: 'html'\\n        },\\n        {\\n            field: 'url',\\n            label: '路径',\\n            type: 'html'\\n        },\\n        {\\n            field: 'permission',\\n            label: '权限标识',\\n            width: 150,\\n            type: 'html'\\n        },\\n        {\\n            field: 'componentName',\\n            label: '关联组件',\\n            width: 150,\\n            type: 'html'\\n        },\\n        {\\n            field: 'icon',\\n            label: '图标',\\n            render(row) {\\n                if (row.icon) {\\n                    return h(MbIcon, { icon: row.icon })\\n                }\\n            }\\n        },\\n        {\\n            field: 'sort',\\n            label: '序号',\\n        },\\n        {\\n            label: '排序',\\n            type: 'buttons',\\n            width: 180,\\n            buttons: [\\n                {\\n                    label: '上移',\\n                    link: true,\\n                    click: (row) => {\\n                        $common.get('/system/menu/sort/up', {\\n                            id: row.id,\\n                            pid: row.pid,\\n                            sort: row.sort\\n                        }).then(() => {\\n                            reloadTable()\\n                        })\\n                    }\\n                },\\n                {\\n                    label: '下移',\\n                    link: true,\\n                    click: (row) => {\\n                        $common.get('/system/menu/sort/down', {\\n                            id: row.id,\\n                            pid: row.pid,\\n                            sort: row.sort\\n                        }).then(() => {\\n                            reloadTable()\\n                        })\\n                    }\\n                }\\n            ]\\n        },\\n        {\\n            field: 'isShow',\\n            label: '是否显示',\\n            type: 'switch',\\n            width: 100,\\n            change: (row) => {\\n                $common.get('/system/menu/change', {\\n                    id: row.id,\\n                    isShow: row.isShow\\n                })\\n            }\\n        },\\n        {\\n            field: 'keepAlive',\\n            label: '是否缓存',\\n            type: 'switch',\\n            width: 100,\\n            if: (row) => {\\n                if (row.isShow == 0) {\\n                    return false\\n                }\\n                if (!isEmpty(row.children) && row.children.some(it => it.isShow == 1)) {\\n                    return false\\n                }\\n                return row.url ? true : false\\n            },\\n            change: (row) => {\\n                $common.get('/system/menu/change', {\\n                    id: row.id,\\n                    keepAlive: row.keepAlive\\n                })\\n            }\\n        },\\n        {\\n            label: '操作',\\n            type: 'buttons',\\n            width: 280,\\n            fixed: 'right',\\n            align: 'left',\\n            buttons: [\\n                {\\n                    label: '添加下级菜单',\\n                    link: true,\\n                    permission: 'menu:save',\\n                    click: (row) => {\\n                        addSubMenu(row.id)\\n                    }\\n                },\\n                {\\n                    label: '修改',\\n                    link: true,\\n                    permission: 'menu:save',\\n                    click: (row) => {\\n                        handleUpdate(row)\\n                    }\\n                },\\n                {\\n                    label: '删除',\\n                    link: true,\\n                    permission: 'menu:delete',\\n                    click: (row) => {\\n                        $common.handleDelete({\\n                            url: '/system/menu/delete',\\n                            id: row.id,\\n                            done: () => reloadTable()\\n                        })\\n                    }\\n                }\\n            ]\\n        }\\n    ]\\n})\\nconst dialogTitle = ref('')\\nconst menuFormDialog = ref()\\nconst menuFormRef = ref()\\n\\nfunction reloadTable() {\\n    tableOptions.loading = true\\n    $common.get('/system/menu/tree').then(res => {\\n        menuData.value = res.data.list\\n        tableOptions.data = menuData.value\\n        tableOptions.loading = false\\n    })\\n}\\n\\nfunction searchMenu() {\\n    table.value.expand()\\n    if (searchValue.value) {\\n        tableOptions.data = $treeTable.recursionSearch(['name', 'url', 'permission', 'componentName'], $common.copyNew(menuData.value), searchValue.value)\\n    } else {\\n        tableOptions.data = menuData.value\\n    }\\n}\\n\\nfunction addSubMenu(id) {\\n    dialogTitle.value = '添加'\\n    menuFormDialog.value.show()\\n    nextTick(() => {\\n        menuFormRef.value.addSubMenu(id)\\n    })\\n}\\n\\nfunction handleUpdate(row) {\\n    dialogTitle.value = '修改'\\n    menuFormDialog.value.show()\\n    nextTick(() => {\\n        menuFormRef.value.getInfo(row);\\n    })\\n}\\n\\nonMounted(() => reloadTable())\\n\\nwatch(menuData, () => {\\n    menuTree.value = [{\\n        label: '根节点',\\n        key: '0',\\n        children: $treeTable.genTree(menuData.value)\\n    }]\\n})\\n\\n</script>\\n\",\n        \"createDate\": \"2026-03-04 11:04:23\",\n        \"createBy\": \"admin\"\n    },\n    \"timestamp\": 1772782843131,\n    \"executeTime\": 5\n}",
  "description" : "",
  "requestBodyDefinition" : null,
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
        "name" : "sourceCode",
        "value" : "<template>\n    <div class=\"mb-list\">\n        <div class=\"mb-search\">\n            <n-space>\n                <n-input v-model:value=\"searchValue\" :size=\"$global.uiSize.value\" @keyup.enter=\"searchMenu\" placeholder=\"菜单名称、链接、权限标识、关联组件\"\n                         style=\"width: 270px\"></n-input>\n                <n-button type=\"primary\" :size=\"$global.uiSize.value\" @click=\"searchMenu\">\n                    <mb-icon icon=\"Search\" />\n                    搜索\n                </n-button>\n                <n-button :size=\"$global.uiSize.value\" @click=\"() => { searchValue = ''; searchMenu() }\">\n                    <mb-icon icon=\"TrashOutline\" />\n                    清空\n                </n-button>\n            </n-space>\n        </div>\n        <div class=\"mb-toolbar\">\n            <n-space>\n                <n-button :size=\"$global.uiSize.value\" type=\"primary\" @click=\"addSubMenu('0')\" v-permission=\"'menu:save'\">\n                    <mb-icon icon=\"AddOutline\" />\n                    添加菜单\n                </n-button>\n                <n-button :size=\"$global.uiSize.value\" type=\"primary\" @click=\"() => table.toggleExpand()\">\n                    <mb-icon icon=\"ArrowDownOutline\" />\n                    展开/折叠\n                </n-button>\n            </n-space>\n        </div>\n        <div class=\"mb-table\">\n            <mb-table ref=\"table\" v-bind=\"tableOptions\"/>\n        </div>\n        <mb-modal ref=\"menuFormDialog\" width=\"650px\" :title=\"dialogTitle\" @confirm=\"menuFormRef.save($event)\">\n            <menu-form ref=\"menuFormRef\" :menu-tree=\"menuTree\" :menu-data=\"menuData\" @reload-table=\"reloadTable\"/>\n        </mb-modal>\n    </div>\n</template>\n\n<script setup>\n\nimport {ref, reactive, onMounted, nextTick, watch, h} from 'vue'\nimport MbIcon from '@/components/magic/basic/mb-icon.vue';\nimport { isEmpty } from 'lodash-es'\n\n\nlet menuTree = ref([])\nconst menuData = ref([])\nlet searchValue = ref('')\nconst table = ref()\nconst tableOptions = reactive({\n    id: 'menu-list',\n    virtualScroll: true,\n    loading: false,\n    showNo: false,\n    page: false,\n    cols: [\n        {\n            field: 'name',\n            label: '菜单名称',\n            type: 'html'\n        },\n        {\n            field: 'url',\n            label: '路径',\n            type: 'html'\n        },\n        {\n            field: 'permission',\n            label: '权限标识',\n            width: 150,\n            type: 'html'\n        },\n        {\n            field: 'componentName',\n            label: '关联组件',\n            width: 150,\n            type: 'html'\n        },\n        {\n            field: 'icon',\n            label: '图标',\n            render(row) {\n                if (row.icon) {\n                    return h(MbIcon, { icon: row.icon })\n                }\n            }\n        },\n        {\n            field: 'sort',\n            label: '序号',\n        },\n        {\n            label: '排序',\n            type: 'buttons',\n            width: 180,\n            buttons: [\n                {\n                    label: '上移',\n                    link: true,\n                    click: (row) => {\n                        $common.get('/system/menu/sort/up', {\n                            id: row.id,\n                            pid: row.pid,\n                            sort: row.sort\n                        }).then(() => {\n                            reloadTable()\n                        })\n                    }\n                },\n                {\n                    label: '下移',\n                    link: true,\n                    click: (row) => {\n                        $common.get('/system/menu/sort/down', {\n                            id: row.id,\n                            pid: row.pid,\n                            sort: row.sort\n                        }).then(() => {\n                            reloadTable()\n                        })\n                    }\n                }\n            ]\n        },\n        {\n            field: 'isShow',\n            label: '是否显示',\n            type: 'switch',\n            width: 100,\n            change: (row) => {\n                $common.get('/system/menu/change', {\n                    id: row.id,\n                    isShow: row.isShow\n                })\n            }\n        },\n        {\n            field: 'keepAlive',\n            label: '是否缓存',\n            type: 'switch',\n            width: 100,\n            if: (row) => {\n                if (row.isShow == 0) {\n                    return false\n                }\n                if (!isEmpty(row.children) && row.children.some(it => it.isShow == 1)) {\n                    return false\n                }\n                return row.url ? true : false\n            },\n            change: (row) => {\n                $common.get('/system/menu/change', {\n                    id: row.id,\n                    keepAlive: row.keepAlive\n                })\n            }\n        },\n        {\n            label: '操作',\n            type: 'buttons',\n            width: 280,\n            fixed: 'right',\n            align: 'left',\n            buttons: [\n                {\n                    label: '添加下级菜单',\n                    link: true,\n                    permission: 'menu:save',\n                    click: (row) => {\n                        addSubMenu(row.id)\n                    }\n                },\n                {\n                    label: '修改',\n                    link: true,\n                    permission: 'menu:save',\n                    click: (row) => {\n                        handleUpdate(row)\n                    }\n                },\n                {\n                    label: '删除',\n                    link: true,\n                    permission: 'menu:delete',\n                    click: (row) => {\n                        $common.handleDelete({\n                            url: '/system/menu/delete',\n                            id: row.id,\n                            done: () => reloadTable()\n                        })\n                    }\n                }\n            ]\n        }\n    ]\n})\nconst dialogTitle = ref('')\nconst menuFormDialog = ref()\nconst menuFormRef = ref()\n\nfunction reloadTable() {\n    tableOptions.loading = true\n    $common.get('/system/menu/tree').then(res => {\n        menuData.value = res.data.list\n        tableOptions.data = menuData.value\n        tableOptions.loading = false\n    })\n}\n\nfunction searchMenu() {\n    table.value.expand()\n    if (searchValue.value) {\n        tableOptions.data = $treeTable.recursionSearch(['name', 'url', 'permission', 'componentName'], $common.copyNew(menuData.value), searchValue.value)\n    } else {\n        tableOptions.data = menuData.value\n    }\n}\n\nfunction addSubMenu(id) {\n    dialogTitle.value = '添加'\n    menuFormDialog.value.show()\n    nextTick(() => {\n        menuFormRef.value.addSubMenu(id)\n    })\n}\n\nfunction handleUpdate(row) {\n    dialogTitle.value = '修改'\n    menuFormDialog.value.show()\n    nextTick(() => {\n        menuFormRef.value.getInfo(row);\n    })\n}\n\nonMounted(() => reloadTable())\n\nwatch(menuData, () => {\n    menuTree.value = [{\n        label: '根节点',\n        key: '0',\n        children: $treeTable.genTree(menuData.value)\n    }]\n})\n\n</script>\n",
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
        "name" : "createDate",
        "value" : "2026-03-04 11:04:23",
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
        "name" : "createBy",
        "value" : "admin",
        "description" : "",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1772782843131",
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
      "value" : "5",
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
let sql = """
    select 
        source_code,
        create_date,
        (select username from sys_user where id = sdch.create_by) create_by
    from sys_dynamic_component_history sdch where component_id = #{componentId}
    order by create_date desc limit 1
"""

return db.selectOne(sql)