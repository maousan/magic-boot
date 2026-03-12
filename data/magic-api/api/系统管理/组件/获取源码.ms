{
  "properties" : { },
  "id" : "2e3a230acf004dcdb679ff3a46a8a20a",
  "script" : null,
  "groupId" : "6f106ebdee21489db34b956f7770ff03",
  "name" : "获取源码",
  "createTime" : null,
  "updateTime" : 1773115479174,
  "lock" : null,
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/getSourceCode",
  "method" : "GET",
  "parameters" : [ {
    "name" : "id",
    "value" : "28e3f82164de4bbf98a342c2a94da076",
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
    "value" : "component:get:code",
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
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": \"<template>\\n    <div class=\\\"mb-list\\\">\\n        <div class=\\\"mb-search\\\">\\n            <mb-search :where=\\\"tableOptions.where\\\" @search=\\\"reloadTable\\\"/>\\n        </div>\\n        <div class=\\\"mb-toolbar\\\">\\n            <n-space>\\n                <n-button :size=\\\"$global.uiSize.value\\\" v-permission=\\\"'dict:save'\\\" type=\\\"primary\\\" @click=\\\"handleCreate\\\">\\n                    <mb-icon icon=\\\"AddOutline\\\" />\\n                    添加字典\\n                </n-button>\\n            </n-space>\\n        </div>\\n        <div class=\\\"mb-table\\\">\\n            <mb-table ref=\\\"table\\\" v-bind=\\\"tableOptions\\\"/>\\n        </div>\\n\\n        <mb-modal :size=\\\"$global.uiSize.value\\\" ref=\\\"dictDialog\\\" :title=\\\"dialogTitle\\\" width=\\\"600px\\\" @confirm=\\\"save($event)\\\">\\n            <n-form :size=\\\"$global.uiSize.value\\\" ref=\\\"dataForm\\\" :rules=\\\"rules\\\" :model=\\\"temp\\\" label-placement=\\\"left\\\" label-width=\\\"80px\\\">\\n                <n-grid :cols=\\\"24\\\">\\n                    <n-gi :span=\\\"12\\\">\\n                        <n-form-item label=\\\"字典类型\\\" path=\\\"dictType\\\">\\n                            <mb-select v-model=\\\"temp.dictType\\\" type=\\\"dict_type\\\"/>\\n                        </n-form-item>\\n                    </n-gi>\\n                    <n-gi :span=\\\"12\\\">\\n                        <n-form-item label=\\\"类型\\\" path=\\\"type\\\">\\n                            <n-input v-model:value=\\\"temp.type\\\"/>\\n                        </n-form-item>\\n                    </n-gi>\\n                </n-grid>\\n                <n-grid :cols=\\\"24\\\">\\n                    <n-gi :span=\\\"12\\\">\\n                        <n-form-item label=\\\"描述\\\" path=\\\"descRibe\\\">\\n                            <n-input v-model:value=\\\"temp.descRibe\\\"/>\\n                        </n-form-item>\\n                    </n-gi>\\n                    <n-gi :span=\\\"12\\\">\\n                        <n-form-item label=\\\"备注\\\" path=\\\"remarks\\\">\\n                            <n-input v-model:value=\\\"temp.remarks\\\"/>\\n                        </n-form-item>\\n                    </n-gi>\\n                </n-grid>\\n            </n-form>\\n        </mb-modal>\\n\\n        <mb-modal ref=\\\"dictItemsDialog\\\" title=\\\"字典项\\\" width=\\\"1400px\\\" :show-footer=\\\"false\\\">\\n            <dict-items v-model:dict-id=\\\"dictId\\\"/>\\n        </mb-modal>\\n\\n    </div>\\n</template>\\n\\n<script setup>\\n\\nimport {ref, reactive, nextTick} from 'vue'\\nimport {useDictStore} from \\\"@/store/modules/dictStore\\\";\\n\\nconst dictStore = useDictStore()\\n\\nconst tableOptions = reactive({\\n    id: 'dict-list',\\n    url: '/system/dict/list',\\n    page: true,\\n    where: {\\n        type: {\\n            label: '类型'\\n        },\\n        dictType: {\\n            component: 'select',\\n            label: '字典类型',\\n            props: {\\n                'all-option': true,\\n                type: 'dict_type'\\n            }\\n        }\\n    },\\n    cols: [\\n        {\\n            field: 'type',\\n            label: '类型'\\n        },\\n        {\\n            field: 'descRibe',\\n            label: '描述'\\n        },\\n        {\\n            field: 'dictType',\\n            label: '字典类型',\\n            width: 200,\\n            dictType: 'dict_type'\\n        },\\n        {\\n            field: 'createDate',\\n            label: '创建时间'\\n        },\\n        {\\n            field: 'remarks',\\n            label: '备注',\\n            width: 200\\n        },\\n        {\\n            label: '操作',\\n            type: 'buttons',\\n            width: 220,\\n            fixed: 'right',\\n            buttons: [\\n                {\\n                    permission: 'dict:save',\\n                    label: '修改',\\n                    link: true,\\n                    click: (row) => {\\n                        handleUpdate(row)\\n                    }\\n                },\\n                {\\n                    permission: 'dict:delete',\\n                    label: '删除',\\n                    link: true,\\n                    click: (row) => {\\n                        $common.handleDelete({\\n                            url: '/system/dict/delete',\\n                            id: row.id,\\n                            done: () => {\\n                                reloadTable()\\n                                dictStore.getDictData()\\n                            }\\n                        })\\n                    }\\n                },\\n                {\\n                    permission: 'dict:items:view',\\n                    label: '字典项',\\n                    link: true,\\n                    click: (row) => {\\n                        dictItemsDialog.value.show()\\n                        dictId.value = row.id\\n                    }\\n                }\\n            ]\\n        }\\n    ]\\n})\\n\\nconst dictId = ref('')\\nconst temp = ref(getTemp())\\nconst dialogTitle = ref('')\\nconst rules = reactive({\\n    dictType: {required: true, message: '请选择字典类型', trigger: 'change'},\\n    type: {required: true, message: '请输入类型', trigger: 'change'},\\n    descRibe: {required: true, message: '请输入描述', trigger: 'change'}\\n})\\nconst table = ref()\\nconst dictDialog = ref()\\nconst dataForm = ref()\\nconst dictItemsDialog = ref()\\n\\nfunction getTemp() {\\n    return {\\n        id: '',\\n        dictType: '',\\n        type: '',\\n        descRibe: '',\\n        remarks: ''\\n    }\\n}\\n\\nfunction reloadTable() {\\n    table.value.reload()\\n}\\n\\nfunction handleCreate() {\\n    temp.value = getTemp()\\n    dialogTitle.value = '添加'\\n    dictDialog.value.show()\\n    nextTick(() => {\\n        dataForm.value.restoreValidation()\\n    })\\n}\\n\\nfunction save(d) {\\n    dataForm.value.validate((errors) => {\\n        if (!errors) {\\n            d.loading()\\n            $common.post('/system/dict/save', temp.value).then((response) => {\\n                d.hideLoading()\\n                temp.value.id = response.data\\n                dictDialog.value.hide()\\n                $message.success(dialogTitle.value + '成功')\\n                reloadTable()\\n                dictStore.getDictData()\\n            }).catch(() => d.hideLoading())\\n        }\\n    })\\n}\\n\\nfunction handleUpdate(row) {\\n    $common.objAssign(temp.value, row)\\n    dialogTitle.value = '修改'\\n    dictDialog.value.show()\\n    nextTick(() => {\\n        dataForm.value.restoreValidation()\\n    })\\n}\\n\\n</script>\\n\",\n    \"timestamp\": 1773115478141,\n    \"executeTime\": 64\n}",
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
      "value" : "<template>\n    <div class=\"mb-list\">\n        <div class=\"mb-search\">\n            <mb-search :where=\"tableOptions.where\" @search=\"reloadTable\"/>\n        </div>\n        <div class=\"mb-toolbar\">\n            <n-space>\n                <n-button :size=\"$global.uiSize.value\" v-permission=\"'dict:save'\" type=\"primary\" @click=\"handleCreate\">\n                    <mb-icon icon=\"AddOutline\" />\n                    添加字典\n                </n-button>\n            </n-space>\n        </div>\n        <div class=\"mb-table\">\n            <mb-table ref=\"table\" v-bind=\"tableOptions\"/>\n        </div>\n\n        <mb-modal :size=\"$global.uiSize.value\" ref=\"dictDialog\" :title=\"dialogTitle\" width=\"600px\" @confirm=\"save($event)\">\n            <n-form :size=\"$global.uiSize.value\" ref=\"dataForm\" :rules=\"rules\" :model=\"temp\" label-placement=\"left\" label-width=\"80px\">\n                <n-grid :cols=\"24\">\n                    <n-gi :span=\"12\">\n                        <n-form-item label=\"字典类型\" path=\"dictType\">\n                            <mb-select v-model=\"temp.dictType\" type=\"dict_type\"/>\n                        </n-form-item>\n                    </n-gi>\n                    <n-gi :span=\"12\">\n                        <n-form-item label=\"类型\" path=\"type\">\n                            <n-input v-model:value=\"temp.type\"/>\n                        </n-form-item>\n                    </n-gi>\n                </n-grid>\n                <n-grid :cols=\"24\">\n                    <n-gi :span=\"12\">\n                        <n-form-item label=\"描述\" path=\"descRibe\">\n                            <n-input v-model:value=\"temp.descRibe\"/>\n                        </n-form-item>\n                    </n-gi>\n                    <n-gi :span=\"12\">\n                        <n-form-item label=\"备注\" path=\"remarks\">\n                            <n-input v-model:value=\"temp.remarks\"/>\n                        </n-form-item>\n                    </n-gi>\n                </n-grid>\n            </n-form>\n        </mb-modal>\n\n        <mb-modal ref=\"dictItemsDialog\" title=\"字典项\" width=\"1400px\" :show-footer=\"false\">\n            <dict-items v-model:dict-id=\"dictId\"/>\n        </mb-modal>\n\n    </div>\n</template>\n\n<script setup>\n\nimport {ref, reactive, nextTick} from 'vue'\nimport {useDictStore} from \"@/store/modules/dictStore\";\n\nconst dictStore = useDictStore()\n\nconst tableOptions = reactive({\n    id: 'dict-list',\n    url: '/system/dict/list',\n    page: true,\n    where: {\n        type: {\n            label: '类型'\n        },\n        dictType: {\n            component: 'select',\n            label: '字典类型',\n            props: {\n                'all-option': true,\n                type: 'dict_type'\n            }\n        }\n    },\n    cols: [\n        {\n            field: 'type',\n            label: '类型'\n        },\n        {\n            field: 'descRibe',\n            label: '描述'\n        },\n        {\n            field: 'dictType',\n            label: '字典类型',\n            width: 200,\n            dictType: 'dict_type'\n        },\n        {\n            field: 'createDate',\n            label: '创建时间'\n        },\n        {\n            field: 'remarks',\n            label: '备注',\n            width: 200\n        },\n        {\n            label: '操作',\n            type: 'buttons',\n            width: 220,\n            fixed: 'right',\n            buttons: [\n                {\n                    permission: 'dict:save',\n                    label: '修改',\n                    link: true,\n                    click: (row) => {\n                        handleUpdate(row)\n                    }\n                },\n                {\n                    permission: 'dict:delete',\n                    label: '删除',\n                    link: true,\n                    click: (row) => {\n                        $common.handleDelete({\n                            url: '/system/dict/delete',\n                            id: row.id,\n                            done: () => {\n                                reloadTable()\n                                dictStore.getDictData()\n                            }\n                        })\n                    }\n                },\n                {\n                    permission: 'dict:items:view',\n                    label: '字典项',\n                    link: true,\n                    click: (row) => {\n                        dictItemsDialog.value.show()\n                        dictId.value = row.id\n                    }\n                }\n            ]\n        }\n    ]\n})\n\nconst dictId = ref('')\nconst temp = ref(getTemp())\nconst dialogTitle = ref('')\nconst rules = reactive({\n    dictType: {required: true, message: '请选择字典类型', trigger: 'change'},\n    type: {required: true, message: '请输入类型', trigger: 'change'},\n    descRibe: {required: true, message: '请输入描述', trigger: 'change'}\n})\nconst table = ref()\nconst dictDialog = ref()\nconst dataForm = ref()\nconst dictItemsDialog = ref()\n\nfunction getTemp() {\n    return {\n        id: '',\n        dictType: '',\n        type: '',\n        descRibe: '',\n        remarks: ''\n    }\n}\n\nfunction reloadTable() {\n    table.value.reload()\n}\n\nfunction handleCreate() {\n    temp.value = getTemp()\n    dialogTitle.value = '添加'\n    dictDialog.value.show()\n    nextTick(() => {\n        dataForm.value.restoreValidation()\n    })\n}\n\nfunction save(d) {\n    dataForm.value.validate((errors) => {\n        if (!errors) {\n            d.loading()\n            $common.post('/system/dict/save', temp.value).then((response) => {\n                d.hideLoading()\n                temp.value.id = response.data\n                dictDialog.value.hide()\n                $message.success(dialogTitle.value + '成功')\n                reloadTable()\n                dictStore.getDictData()\n            }).catch(() => d.hideLoading())\n        }\n    })\n}\n\nfunction handleUpdate(row) {\n    $common.objAssign(temp.value, row)\n    dialogTitle.value = '修改'\n    dictDialog.value.show()\n    nextTick(() => {\n        dataForm.value.restoreValidation()\n    })\n}\n\n</script>\n",
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
      "name" : "timestamp",
      "value" : "1772782807761",
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
      "value" : "13",
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
    select source_code from sys_dynamic_component where is_del = 0 and id = #{id}
"""

return db.selectValue(sql)