<template>
  <n-card
    title="库存查询"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-space>
        <n-button type="primary" @click="showCreate = true">手动录入</n-button>
        <n-button type="error" @click="showClear = true">清空库存</n-button>
        <n-button type="primary" @click="handleSearch">查询</n-button>
      </n-space>
    </template>

    <div class="table-page-content">
      <n-grid :cols="4" :x-gap="12" :y-gap="8" class="table-page-toolbar">
        <n-gi>
          <n-input v-model:value="filters.warehouseId" placeholder="仓库ID" clearable />
        </n-gi>
        <n-gi>
          <n-input v-model:value="filters.locationId" placeholder="库位码" clearable />
        </n-gi>
        <n-gi>
          <n-input v-model:value="filters.sku" placeholder="产品代码" clearable />
        </n-gi>
        <n-gi>
          <n-input v-model:value="filters.lotAtt09" placeholder="批次号" clearable />
        </n-gi>
      </n-grid>

      <div class="table-page-table">
        <n-data-table
          :columns="columns"
          :data="tableData.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: InventoryItem) => row.warehouseId + '-' + row.locationId + '-' + row.lotAtt09"
          :scroll-x="1240"
          flex-height
          style="height: 100%"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="tableData.total"
          :page-sizes="[10, 20, 50, 100]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <n-modal v-model:show="showCreate" title="手动录入库存" preset="dialog">
      <div style="max-height: 60vh; overflow-y: auto; padding-right: 4px">
      <n-space vertical>
        <n-form-item label="仓库ID" required>
          <n-input v-model:value="form.warehouseId" placeholder="请输入仓库ID" />
        </n-form-item>
        <n-form-item label="库位码" required>
          <n-input v-model:value="form.locationId" placeholder="请输入库位码" />
        </n-form-item>
        <n-form-item label="产品代码" required>
          <n-input v-model:value="form.sku" placeholder="请输入产品代码" />
        </n-form-item>
        <n-form-item label="批次号" required>
          <n-input v-model:value="form.lotAtt09" placeholder="请输入批次号" />
        </n-form-item>
        <n-form-item label="库存数量">
          <n-input-number v-model:value="form.qty" placeholder="0" :min="0" style="width: 100%" />
        </n-form-item>
        <n-form-item label="待拣货数量">
          <n-input-number v-model:value="form.qtyAllocated" placeholder="0" :min="0" style="width: 100%" />
        </n-form-item>
        <n-form-item label="待上架数量">
          <n-input-number v-model:value="form.qtyPa" placeholder="0" :min="0" style="width: 100%" />
        </n-form-item>
        <n-form-item label="自定义字段1">
          <n-input v-model:value="form.userDefine1" placeholder="可选" />
        </n-form-item>
        <n-form-item label="自定义字段2">
          <n-input v-model:value="form.userDefine2" placeholder="可选" />
        </n-form-item>
        <n-form-item label="自定义字段3">
          <n-input v-model:value="form.userDefine3" placeholder="可选" />
        </n-form-item>
        <n-form-item label="自定义字段4">
          <n-input v-model:value="form.userDefine4" placeholder="可选" />
        </n-form-item>
        <n-form-item label="自定义字段5">
          <n-input v-model:value="form.userDefine5" placeholder="可选" />
        </n-form-item>
      </n-space>
      </div>
      <template #action>
        <n-button @click="showCreate = false">取消</n-button>
        <n-button type="primary" :loading="creating" @click="handleCreate">确认录入</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <span>确定要删除库位 <b>{{ deleteTarget?.locationId }}</b> 批次 <b>{{ deleteTarget?.lotAtt09 }}</b> 的库存记录吗？</span>
      <template #action>
        <n-button @click="showDelete = false">取消</n-button>
        <n-button type="error" :loading="deleting" @click="handleDelete">删除</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showClear" title="清空全部库存" preset="dialog">
      <n-space vertical>
        <span>此操作将清空 <b>全部库存数据</b>，不可恢复！</span>
        <span>请输入 <b>确认清空</b> 以继续：</span>
        <n-input v-model:value="clearConfirm" placeholder="请输入 确认清空" />
      </n-space>
      <template #action>
        <n-button @click="showClear = false">取消</n-button>
        <n-button type="error" :loading="clearing" :disabled="clearConfirm !== '确认清空'" @click="handleClear">确认清空</n-button>
      </template>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { NCard, NDataTable, NButton, NSpace, NGrid, NGi, NInput, NInputNumber, NModal, NFormItem, NFlex, NPagination } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getInventoryList, createInventory, deleteInventory, clearInventory } from '@/api/inventory'
import type { InventoryItem } from '@/types'

const loading = ref(false)
const creating = ref(false)
const deleting = ref(false)
const showCreate = ref(false)
const showDelete = ref(false)
const deleteTarget = ref<{ warehouseId: string; locationId: string; lotAtt09: string } | null>(null)
const showClear = ref(false)
const clearing = ref(false)
const clearConfirm = ref('')
const page = ref(1)
const pageSize = ref(20)

const filters = reactive({
  warehouseId: '',
  locationId: '',
  sku: '',
  lotAtt09: '',
})

const form = reactive({
  warehouseId: '',
  locationId: '',
  sku: '',
  lotAtt09: '',
  qty: 0,
  qtyAllocated: 0,
  qtyPa: 0,
  userDefine1: '',
  userDefine2: '',
  userDefine3: '',
  userDefine4: '',
  userDefine5: '',
})

const tableData = reactive<{ list: InventoryItem[]; total: number }>({
  list: [],
  total: 0,
})

const columns: DataTableColumns<InventoryItem> = [
  { title: '仓库ID', key: 'warehouseId', width: 120 },
  { title: '库位码', key: 'locationId', width: 180 },
  { title: '产品代码', key: 'sku', width: 180 },
  { title: '批次号', key: 'lotAtt09', width: 120 },
  { title: '库存数量', key: 'qty', width: 100, align: 'right' },
  { title: '待拣货数量', key: 'qtyAllocated', width: 100, align: 'right' },
  { title: '待上架数量', key: 'qtyPa', width: 100, align: 'right' },
  { title: '库存更新时间', key: 'editTime', width: 180 },
  { title: '本地更新时间', key: 'updateTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 80,
    fixed: 'right',
    render: (row) =>
      h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '删除'),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: page.value,
      pageSize: pageSize.value,
    }
    if (filters.warehouseId) params.warehouseId = filters.warehouseId
    if (filters.locationId) params.locationId = filters.locationId
    if (filters.sku) params.sku = filters.sku
    if (filters.lotAtt09) params.lotAtt09 = filters.lotAtt09

    const res = await getInventoryList(params as any)
    tableData.list = res.list
    tableData.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

async function handleCreate() {
  if (!form.warehouseId || !form.locationId || !form.sku || !form.lotAtt09) return
  creating.value = true
  try {
    await createInventory({
      warehouseId: form.warehouseId,
      locationId: form.locationId,
      sku: form.sku,
      lotAtt09: form.lotAtt09,
      qty: form.qty || 0,
      qtyAllocated: form.qtyAllocated || 0,
      qtyPa: form.qtyPa || 0,
      userDefine1: form.userDefine1 || undefined,
      userDefine2: form.userDefine2 || undefined,
      userDefine3: form.userDefine3 || undefined,
      userDefine4: form.userDefine4 || undefined,
      userDefine5: form.userDefine5 || undefined,
    })
    showCreate.value = false
    form.warehouseId = ''
    form.locationId = ''
    form.sku = ''
    form.lotAtt09 = ''
    form.qty = 0
    form.qtyAllocated = 0
    form.qtyPa = 0
    form.userDefine1 = ''
    form.userDefine2 = ''
    form.userDefine3 = ''
    form.userDefine4 = ''
    form.userDefine5 = ''
    await fetchData()
  } finally {
    creating.value = false
  }
}

function openDelete(row: InventoryItem) {
  deleteTarget.value = { warehouseId: row.warehouseId, locationId: row.locationId, lotAtt09: row.lotAtt09 }
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteInventory(deleteTarget.value)
    showDelete.value = false
    deleteTarget.value = null
    await fetchData()
  } finally {
    deleting.value = false
  }
}

async function handleClear() {
  clearing.value = true
  try {
    await clearInventory()
    showClear.value = false
    clearConfirm.value = ''
    await fetchData()
  } finally {
    clearing.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.table-page-card :deep(.n-card__content) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>
