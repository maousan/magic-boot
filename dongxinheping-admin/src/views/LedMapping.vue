<template>
  <n-card
    title="巷道灯绑定"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-space>
        <n-button @click="showImport = true">导入</n-button>
        <n-button type="primary" @click="showCreate = true">新增绑定</n-button>
      </n-space>
    </template>

    <div class="table-page-content">
      <n-space :size="12" class="table-page-toolbar">
        <n-input v-model:value="query.locationCode" placeholder="库位编码" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-input v-model:value="query.ledId" placeholder="设备ID" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-select v-model:value="query.color" :options="[{ label: '全部', value: '' }, ...colorOptions]" placeholder="灯色" clearable style="width: 120px" />
        <n-button type="primary" @click="handleSearch">查询</n-button>
      </n-space>

      <div ref="tableAreaRef" class="table-page-table led-mapping-table-area">
        <n-data-table
          :columns="columns"
          :data="bindings.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: LocationLedBinding) => row.id"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="bindings.total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <n-modal v-model:show="showCreate" title="新增绑定" preset="dialog">
      <n-space vertical>
        <n-form-item label="库位码">
          <n-input v-model:value="form.lotNo" placeholder="请输入库位码" />
        </n-form-item>
        <n-form-item label="设备ID">
          <n-select
            v-model:value="form.ledId"
            :options="deviceOptions"
            :loading="deviceLoading"
            placeholder="请选择设备"
            filterable
            clearable
          />
        </n-form-item>
        <n-form-item label="颜色">
          <n-select v-model:value="form.color" :options="colorOptions" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showCreate = false">取消</n-button>
        <n-button type="primary" :loading="creating" @click="handleCreate">确认</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <span>确定要删除该绑定记录吗？</span>
      <template #action>
        <n-button @click="showDelete = false">取消</n-button>
        <n-button type="error" :loading="deleting" @click="handleDelete">删除</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showImport" title="批量导入" preset="dialog" style="width: 540px; max-height: 80vh" @after-leave="resetImportState">
      <div style="max-height: calc(80vh - 120px); overflow-y: auto">
      <n-space vertical :size="12">
        <n-space>
          <n-button size="small" @click="downloadTemplate">下载模板</n-button>
        </n-space>
        <n-upload
          :max="1"
          accept=".xlsx,.xls,.csv"
          :default-upload="false"
          :show-file-list="true"
          @change="handleFileChange"
          directory-dnd
        >
          <n-upload-dragger>
            <n-text>点击或拖拽文件到此区域</n-text>
            <n-p depth="3" style="margin: 8px 0 0 0">
              支持 .xlsx / .xls / .csv 格式
            </n-p>
          </n-upload-dragger>
        </n-upload>
        <n-button
          type="primary"
          :loading="importing"
          :disabled="!pendingFile"
          @click="handleImportUpload"
          style="align-self: flex-end"
        >确认上传</n-button>
        <n-card v-if="importResult" title="导入结果" size="small">
          <n-descriptions :column="3" bordered>
            <n-descriptions-item label="总行数">{{ importResult.totalRows }}</n-descriptions-item>
            <n-descriptions-item label="成功">
              <n-text type="success">{{ importResult.successCount }}</n-text>
            </n-descriptions-item>
            <n-descriptions-item label="失败">
              <n-text type="error">{{ importResult.failCount }}</n-text>
            </n-descriptions-item>
          </n-descriptions>
          <n-data-table
            v-if="importResult.errors.length > 0"
            :columns="importErrorColumns"
            :data="importResult.errors"
            :bordered="true"
            size="small"
            style="margin-top: 12px"
          />
        </n-card>
      </n-space>
      </div>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onBeforeUnmount, onMounted, h, computed } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NFlex, NPagination,
  NInput, NSelect, NTag, NUpload, NUploadDragger, NDescriptions, NDescriptionsItem, NText, NP,
} from 'naive-ui'
import type { DataTableColumns, UploadFileInfo } from 'naive-ui'
import { getLedMappingList, createLedMapping, deleteLedMapping, importLedMappings } from '@/api/led-mapping'
import { getLedDeviceList } from '@/api/led-device'
import type { LedDevice, LocationLedBinding, ImportResult } from '@/types'

const loading = ref(false)
const creating = ref(false)
const deleting = ref(false)
const deviceLoading = ref(false)
const showCreate = ref(false)
const showDelete = ref(false)
const showImport = ref(false)
const importing = ref(false)
const importResult = ref<ImportResult | null>(null)
const pendingFile = ref<File | null>(null)
const deleteTarget = ref<{ id: string; locationCode: string } | null>(null)
const tableAreaRef = ref<HTMLElement | null>(null)
const tableBodyMaxHeight = ref(360)
let tableResizeObserver: ResizeObserver | null = null

const bindings = reactive<{ list: LocationLedBinding[]; total: number }>({
  list: [],
  total: 0,
})

const page = ref(1)
const pageSize = ref(20)
const ledDevices = ref<LedDevice[]>([])

const form = reactive<{ lotNo: string; ledId: string | null; color: string }>({
  lotNo: '',
  ledId: null,
  color: 'RED',
})

const query = reactive({ locationCode: '', ledId: '', color: '' as string })

const colorOptions = [
  { label: '红色', value: 'RED' },
  { label: '黄色', value: 'YELLOW' },
  { label: '绿色', value: 'GREEN' },
]

const deviceOptions = computed(() =>
  ledDevices.value.map((device) => ({
    label: device.macAddress,
    value: device.macAddress,
  })),
)

const colorTagMap: Record<string, 'error' | 'warning' | 'success'> = {
  RED: 'error',
  YELLOW: 'warning',
  GREEN: 'success',
}

const columns: DataTableColumns<LocationLedBinding> = [
  { title: '库位码', key: 'locationCode', width: 220 },
  { title: '设备ID', key: 'ledId', width: 220 },
  {
    title: '颜色',
    key: 'color',
    width: 200,
    render: (row) => h(NTag, { type: colorTagMap[row.color] || 'default', size: 'small' }, () => row.color),
  },
  {
    title: '操作',
    key: 'actions',
    render: (row) =>
      h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '删除'),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getLedMappingList({
      page: page.value,
      pageSize: pageSize.value,
      locationCode: query.locationCode || undefined,
      ledId: query.ledId || undefined,
      color: query.color || undefined,
    })
    bindings.list = res.list
    bindings.total = res.total
  } finally {
    loading.value = false
  }
}

async function fetchLedDevices() {
  deviceLoading.value = true
  try {
    const res = await getLedDeviceList({ page: 1, pageSize: 500 })
    ledDevices.value = res.list || []
  } finally {
    deviceLoading.value = false
  }
}

function updateTableHeight() {
  const areaHeight = tableAreaRef.value?.clientHeight || 0
  if (areaHeight > 0) {
    tableBodyMaxHeight.value = Math.max(areaHeight - 52, 160)
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

async function handleCreate() {
  if (!form.lotNo || !form.ledId) return
  creating.value = true
  try {
    await createLedMapping({ lotNo: form.lotNo, ledId: form.ledId, color: form.color })
    showCreate.value = false
    form.lotNo = ''
    form.ledId = null
    form.color = 'RED'
    await fetchData()
  } finally {
    creating.value = false
  }
}

const importErrorColumns: DataTableColumns<{ rowNo: number; message: string }> = [
  { title: '行号', key: 'rowNo', width: 80 },
  { title: '错误信息', key: 'message' },
]

function downloadTemplate() {
  const header = '库位码,设备ID,颜色\nA-01,AA:BB:CC:DD:EE:FF,RED\n'
  const blob = new Blob([header], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '巷道灯绑定导入模板.csv'
  a.click()
  URL.revokeObjectURL(url)
}

function handleFileChange({ file }: { file: UploadFileInfo }) {
  importResult.value = null
  pendingFile.value = file.file ?? null
}

async function handleImportUpload() {
  if (!pendingFile.value) return
  importing.value = true
  try {
    importResult.value = await importLedMappings(pendingFile.value)
    if (importResult.value.successCount > 0) {
      await fetchData()
    }
    pendingFile.value = null
  } finally {
    importing.value = false
  }
}

function resetImportState() {
  pendingFile.value = null
  importResult.value = null
}

function openDelete(row: LocationLedBinding) {
  deleteTarget.value = { id: row.id, locationCode: row.locationCode }
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteLedMapping(deleteTarget.value.id)
    showDelete.value = false
    await fetchData()
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  fetchData()
  fetchLedDevices()
  nextTick(() => {
    updateTableHeight()
    if (tableAreaRef.value) {
      tableResizeObserver = new ResizeObserver(updateTableHeight)
      tableResizeObserver.observe(tableAreaRef.value)
    }
  })
})

onBeforeUnmount(() => {
  tableResizeObserver?.disconnect()
})
</script>

<style scoped>
.table-page-card {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.table-page-card :deep(.n-card__content) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.table-page-content {
  flex: 1;
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.table-page-toolbar,
.table-page-pagination {
  flex-shrink: 0;
}

.led-mapping-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
