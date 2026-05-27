<template>
  <n-card
    title="巷道灯绑定"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-button type="primary" @click="showCreate = true">新增绑定</n-button>
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
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onBeforeUnmount, onMounted, h, computed } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NFlex, NPagination,
  NInput, NSelect, NTag,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getLedMappingList, createLedMapping, deleteLedMapping } from '@/api/led-mapping'
import { getLedDeviceList } from '@/api/led-device'
import type { LedDevice, LocationLedBinding } from '@/types'

const loading = ref(false)
const creating = ref(false)
const deleting = ref(false)
const deviceLoading = ref(false)
const showCreate = ref(false)
const showDelete = ref(false)
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
