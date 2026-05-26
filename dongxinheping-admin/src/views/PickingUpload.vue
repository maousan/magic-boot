<template>
  <div class="picking-page">
    <n-card
      title="拣货单管理"
      class="picking-card"
      style="min-height: 0; display: flex; flex-direction: column"
      content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
    >
      <template #header-extra>
        <n-space>
          <n-input v-model:value="query.waveNo" placeholder="波次号" clearable style="width: 160px" @keyup.enter="handleSearch" />
          <n-input v-model:value="query.userId" placeholder="用户ID" clearable style="width: 140px" @keyup.enter="handleSearch" />
          <n-input v-model:value="query.userName" placeholder="用户姓名" clearable style="width: 120px" @keyup.enter="handleSearch" />
          <n-button type="primary" @click="handleSearch">查询</n-button>
          <n-button type="primary" @click="openAddMaster">新增单据</n-button>
        </n-space>
      </template>

      <div class="picking-card-content">
        <div ref="masterTableAreaRef" class="table-page-table picking-table-area">
          <n-data-table
            :columns="masterColumns"
            :data="masterList"
            :loading="masterLoading"
            :bordered="true"
            :row-props="masterRowProps"
            :max-height="masterTableBodyMaxHeight"
          />
        </div>

        <n-flex justify="end" class="table-page-pagination">
          <n-pagination
            v-model:page="page"
            v-model:page-size="pageSize"
            :item-count="total"
            :page-sizes="[20, 50, 100]"
            show-size-picker
            @update:page="loadMasters"
            @update:page-size="loadMasters"
          />
        </n-flex>
      </div>
    </n-card>

    <n-card
      v-if="selectedMaster"
      :title="'明细 - ' + selectedMaster.waveNo"
      class="picking-card"
      style="min-height: 0; display: flex; flex-direction: column"
      content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
    >
      <template #header-extra>
        <n-button type="primary" @click="openAddDetail">新增明细</n-button>
      </template>

      <div class="picking-card-content">
        <div ref="detailTableAreaRef" class="table-page-table picking-table-area">
          <n-data-table
            :columns="detailColumns"
            :data="detailList"
            :loading="detailLoading"
            :bordered="true"
            size="small"
            :max-height="detailTableBodyMaxHeight"
          />
        </div>
      </div>
    </n-card>

    <!-- 新增/编辑 主表 -->
    <n-modal v-model:show="showMasterForm" :title="masterFormId ? '编辑单据' : '新增单据'" preset="dialog" positive-text="确认" negative-text="取消" @positive-click="handleMasterSubmit">
      <n-space vertical>
        <n-form-item label="波次号" required>
          <n-input v-model:value="masterForm.waveNo" placeholder="请输入波次号" />
        </n-form-item>
        <n-form-item label="用户ID" required>
          <n-input v-model:value="masterForm.userId" placeholder="请输入用户ID" />
        </n-form-item>
        <n-form-item label="用户姓名">
          <n-input v-model:value="masterForm.userName" placeholder="请输入用户姓名" />
        </n-form-item>
      </n-space>
    </n-modal>

    <!-- 删除主表确认 -->
    <n-modal v-model:show="showMasterDelete" title="确认删除" preset="dialog" type="warning" positive-text="确认删除" negative-text="取消" @positive-click="handleMasterDelete">
      确定要删除波次 <b>{{ deleteMasterTarget?.waveNo }}</b> 的拣货单吗？关联的明细数据将一并删除。
    </n-modal>

    <!-- 新增/编辑 明细 -->
    <n-modal v-model:show="showDetailForm" :title="detailFormId ? '编辑明细' : '新增明细'" preset="dialog" positive-text="确认" negative-text="取消" @positive-click="handleDetailSubmit">
      <n-space vertical>
        <n-form-item label="物料代码" required>
          <n-input v-model:value="detailForm.materialCode" placeholder="请输入物料代码" />
        </n-form-item>
        <n-form-item label="批次号" required>
          <n-input v-model:value="detailForm.batchNo" placeholder="请输入批次号" />
        </n-form-item>
        <n-form-item label="库位号" required>
          <n-input v-model:value="detailForm.locationCode" placeholder="请输入库位号" />
        </n-form-item>
        <n-form-item label="计划数量" required>
          <n-input-number v-model:value="detailForm.planQuantity" :min="0" style="width: 100%" />
        </n-form-item>
        <n-form-item label="实际数量">
          <n-input-number v-model:value="detailForm.actualQuantity" :min="0" style="width: 100%" />
        </n-form-item>
        <n-form-item label="状态">
          <n-select v-model:value="detailForm.status" :options="statusOptions" />
        </n-form-item>
      </n-space>
    </n-modal>

    <!-- 删除明细确认 -->
    <n-modal v-model:show="showDetailDelete" title="确认删除" preset="dialog" type="warning" positive-text="确认删除" negative-text="取消" @positive-click="handleDetailDelete">
      确定要删除该明细记录吗？
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NSpace, NFlex, NInput, NInputNumber, NSelect, NModal,
  NFormItem, NTag, NPagination, useMessage,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import {
  getPickingUploadList, getPickingUploadDetail,
  addPickingUpload, updatePickingUpload, deletePickingUpload,
  addPickingUploadDetail, updatePickingUploadDetail, deletePickingUploadDetail,
} from '@/api/picking-upload'
import type { PickingUpload, PickingUploadDetail } from '@/types'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const message = useMessage()

const masterLoading = ref(false)
const detailLoading = ref(false)
const masterList = ref<PickingUpload[]>([])
const detailList = ref<PickingUploadDetail[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedMaster = ref<PickingUpload | null>(null)
const {
  tableAreaRef: masterTableAreaRef,
  tableBodyMaxHeight: masterTableBodyMaxHeight,
  updateTableHeight: updateMasterTableHeight,
} = useTableBodyHeight()
const {
  tableAreaRef: detailTableAreaRef,
  tableBodyMaxHeight: detailTableBodyMaxHeight,
  updateTableHeight: updateDetailTableHeight,
} = useTableBodyHeight()

const query = reactive({ waveNo: '', userId: '', userName: '' })

const statusOptions = [
  { label: '未拣', value: 0 },
  { label: '已拣', value: 1 },
]

// ---- Master form state ----
const showMasterForm = ref(false)
const masterFormId = ref('')
const masterForm = reactive({ waveNo: '', userId: '', userName: '' })

const showMasterDelete = ref(false)
const deleteMasterTarget = ref<PickingUpload | null>(null)

// ---- Detail form state ----
const showDetailForm = ref(false)
const detailFormId = ref('')
const detailForm = reactive({
  materialCode: '', batchNo: '', locationCode: '',
  planQuantity: 0, actualQuantity: 0, status: 0,
})

const showDetailDelete = ref(false)
const deleteDetailTarget = ref<PickingUploadDetail | null>(null)

// ---- Master columns ----
const masterColumns: DataTableColumns<PickingUpload> = [
  { title: '波次号', key: 'waveNo' },
  { title: '用户ID', key: 'userId' },
  { title: '用户姓名', key: 'userName' },
  { title: '明细数量', key: 'detailCount', align: 'right' },
  {
    title: '创建时间',
    key: 'createTime',
    render: (row) => formatTime(row.createTime),
  },
  {
    title: '操作',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NButton, { size: 'small', onClick: () => openEditMaster(row) }, () => '编辑'),
        h(NButton, { size: 'small', type: 'error', onClick: () => openDeleteMaster(row) }, () => '删除'),
      ]),
  },
]

// ---- Detail columns ----
const detailColumns: DataTableColumns<PickingUploadDetail> = [
  { title: '物料代码', key: 'materialCode' },
  { title: '批次号', key: 'batchNo' },
  { title: '库位号', key: 'locationCode' },
  { title: '计划数量', key: 'planQuantity', align: 'right' },
  { title: '实际数量', key: 'actualQuantity', align: 'right' },
  {
    title: '状态',
    key: 'status',
    render: (row) =>
      h(NTag, { type: row.status === 1 ? 'success' : 'default', size: 'small' }, () =>
        row.status === 1 ? '已拣' : '未拣',
      ),
  },
  {
    title: '操作',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NButton, { size: 'small', onClick: () => openEditDetail(row) }, () => '编辑'),
        h(NButton, { size: 'small', type: 'error', onClick: () => openDeleteDetail(row) }, () => '删除'),
      ]),
  },
]

function formatTime(val: string | number): string {
  if (!val) return ''
  const d = typeof val === 'number' ? new Date(val) : new Date(val)
  if (isNaN(d.getTime())) return String(val)
  return d.toLocaleString('zh-CN', { hour12: false })
}

function masterRowProps(row: PickingUpload) {
  return {
    style: selectedMaster.value?.id === row.id ? 'background: var(--n-td-color-hover); cursor: pointer;' : 'cursor: pointer;',
    onClick: () => selectMaster(row),
  }
}

// ---- Data loading ----
async function loadMasters() {
  masterLoading.value = true
  try {
    const res = await getPickingUploadList({
      page: page.value,
      pageSize: pageSize.value,
      waveNo: query.waveNo || undefined,
      userId: query.userId || undefined,
      userName: query.userName || undefined,
    })
    masterList.value = res.list
    total.value = res.total
  } finally {
    masterLoading.value = false
  }
}

async function selectMaster(row: PickingUpload) {
  selectedMaster.value = row
  await nextTick()
  updateMasterTableHeight()
  updateDetailTableHeight()
  detailLoading.value = true
  try {
    const res = await getPickingUploadDetail(row.id)
    detailList.value = res.details || []
  } catch {
    detailList.value = []
  } finally {
    detailLoading.value = false
    await nextTick()
    updateMasterTableHeight()
    updateDetailTableHeight()
  }
}

function handleSearch() {
  page.value = 1
  selectedMaster.value = null
  detailList.value = []
  loadMasters()
  nextTick(updateMasterTableHeight)
}

// ---- Master CRUD ----
function openAddMaster() {
  masterFormId.value = ''
  masterForm.waveNo = ''
  masterForm.userId = ''
  masterForm.userName = ''
  showMasterForm.value = true
}

function openEditMaster(row: PickingUpload) {
  masterFormId.value = row.id
  masterForm.waveNo = row.waveNo
  masterForm.userId = row.userId
  masterForm.userName = row.userName
  showMasterForm.value = true
}

async function handleMasterSubmit() {
  if (!masterForm.waveNo || !masterForm.userId) {
    message.warning('波次号和用户ID不能为空')
    return false
  }
  try {
    if (masterFormId.value) {
      await updatePickingUpload({ id: masterFormId.value, ...masterForm })
      message.success('修改成功')
    } else {
      await addPickingUpload(masterForm)
      message.success('新增成功')
    }
    showMasterForm.value = false
    loadMasters()
    if (selectedMaster.value && masterFormId.value === selectedMaster.value.id) {
      selectMaster({ ...selectedMaster.value, ...masterForm })
    }
  } catch {
    return false
  }
}

function openDeleteMaster(row: PickingUpload) {
  deleteMasterTarget.value = row
  showMasterDelete.value = true
}

async function handleMasterDelete() {
  if (!deleteMasterTarget.value) return
  try {
    await deletePickingUpload(deleteMasterTarget.value.id)
    message.success('删除成功')
    if (selectedMaster.value?.id === deleteMasterTarget.value.id) {
      selectedMaster.value = null
      detailList.value = []
    }
    deleteMasterTarget.value = null
    loadMasters()
  } catch {
    return false
  }
}

// ---- Detail CRUD ----
function openAddDetail() {
  detailFormId.value = ''
  detailForm.materialCode = ''
  detailForm.batchNo = ''
  detailForm.locationCode = ''
  detailForm.planQuantity = 0
  detailForm.actualQuantity = 0
  detailForm.status = 0
  showDetailForm.value = true
}

function openEditDetail(row: PickingUploadDetail) {
  detailFormId.value = row.id
  detailForm.materialCode = row.materialCode
  detailForm.batchNo = row.batchNo
  detailForm.locationCode = row.locationCode
  detailForm.planQuantity = row.planQuantity
  detailForm.actualQuantity = row.actualQuantity
  detailForm.status = row.status
  showDetailForm.value = true
}

async function handleDetailSubmit() {
  if (!detailForm.materialCode || !detailForm.batchNo || !detailForm.locationCode) {
    message.warning('物料代码、批次号、库位号不能为空')
    return false
  }
  try {
    if (detailFormId.value) {
      await updatePickingUploadDetail({ id: detailFormId.value, ...detailForm })
      message.success('修改成功')
    } else if (selectedMaster.value) {
      await addPickingUploadDetail({
        billId: selectedMaster.value.id,
        materialCode: detailForm.materialCode,
        batchNo: detailForm.batchNo,
        locationCode: detailForm.locationCode,
        planQuantity: detailForm.planQuantity,
        actualQuantity: detailForm.actualQuantity,
        status: detailForm.status,
      })
      message.success('新增成功')
    }
    showDetailForm.value = false
    if (selectedMaster.value) {
      selectMaster(selectedMaster.value)
    }
  } catch {
    return false
  }
}

function openDeleteDetail(row: PickingUploadDetail) {
  deleteDetailTarget.value = row
  showDetailDelete.value = true
}

async function handleDetailDelete() {
  if (!deleteDetailTarget.value) return
  try {
    await deletePickingUploadDetail(deleteDetailTarget.value.id)
    message.success('删除成功')
    deleteDetailTarget.value = null
    if (selectedMaster.value) {
      selectMaster(selectedMaster.value)
    }
  } catch {
    return false
  }
}

onMounted(loadMasters)
</script>

<style scoped>
.picking-page {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.picking-card {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.picking-card :deep(.n-card__content) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.picking-card-content {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.picking-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
