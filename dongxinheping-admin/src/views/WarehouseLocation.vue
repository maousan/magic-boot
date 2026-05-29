<template>
  <n-card
    title="库位列表"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <div class="table-page-content">
      <n-space justify="space-between" class="table-page-toolbar">
        <n-space>
          <n-input v-model:value="query.warehouseCode" placeholder="仓库编码" clearable style="width: 160px" @keyup.enter="loadData" />
          <n-input v-model:value="query.locationId" placeholder="库位ID" clearable style="width: 160px" @keyup.enter="loadData" />
          <n-button type="primary" @click="loadData">查询</n-button>
        </n-space>
        <n-space>
          <n-button @click="showAddModal = true">手动添加</n-button>
          <n-dropdown :options="templateOptions" @select="downloadTemplate">
            <n-button>下载模板</n-button>
          </n-dropdown>
          <n-upload
            :max="1"
            accept=".xlsx,.xls,.csv"
            :custom-request="handleUpload"
            :show-file-list="false"
          >
            <n-button type="primary" :loading="uploading">批量导入</n-button>
          </n-upload>
        </n-space>
      </n-space>

      <div ref="tableAreaRef" class="table-page-table warehouse-table-area">
        <n-data-table
          :columns="columns"
          :data="list"
          :loading="loading"
          :bordered="true"
          :scroll-x="980"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="total"
          :page-sizes="[20, 50, 100]"
          show-size-picker
          @update:page="loadData"
          @update:page-size="loadData"
        />
      </n-flex>

    </div>

    <n-modal v-model:show="showAddModal" title="手动添加库位" preset="dialog" positive-text="确认" negative-text="取消" @positive-click="handleAdd">
      <n-form ref="addFormRef" :model="addForm" :rules="addRules" style="margin-top: 16px">
        <n-form-item label="仓库编码" path="warehouseCode">
          <n-input v-model:value="addForm.warehouseCode" placeholder="请输入仓库编码" />
        </n-form-item>
        <n-form-item label="库位ID" path="locationId">
          <n-input v-model:value="addForm.locationId" placeholder="请输入库位ID" />
        </n-form-item>
      </n-form>
    </n-modal>

    <n-modal v-model:show="showDeleteModal" title="确认删除" preset="dialog" type="warning" positive-text="确认删除" negative-text="取消" @positive-click="handleDelete">
      确定要删除库位 <b>{{ deleteTarget?.locationId }}</b> 吗？
    </n-modal>

    <n-modal v-model:show="showEditModal" title="编辑库位" preset="dialog" positive-text="确认" negative-text="取消" @positive-click="handleEdit">
      <n-form ref="editFormRef" :model="editForm" :rules="editRules" style="margin-top: 16px">
        <n-form-item label="仓库编码" path="warehouseCode">
          <n-input v-model:value="editForm.warehouseCode" placeholder="请输入仓库编码" />
        </n-form-item>
        <n-form-item label="库位ID" path="locationId">
          <n-input v-model:value="editForm.locationId" placeholder="请输入库位ID" />
        </n-form-item>
      </n-form>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, h, nextTick, onBeforeUnmount, onMounted } from 'vue'
import {
  NCard, NSpace, NFlex, NButton, NInput, NDataTable, NPagination, NText,
  NUpload, NModal, NForm, NFormItem, NDescriptions, NDescriptionsItem,
  NDropdown, NPopconfirm,
  useMessage, useDialog,
} from 'naive-ui'
import type { DataTableColumns, FormRules, DropdownOption } from 'naive-ui'
import type { UploadCustomRequestOptions } from 'naive-ui'
import { getWarehouseLocations, addWarehouseLocation, updateWarehouseLocation, deleteWarehouseLocation, importWarehouseLocations, syncLocationInventory, updateLocationArticle } from '@/api/warehouse'
import type { WarehouseLocation, ImportResult } from '@/types'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const list = ref<WarehouseLocation[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const tableAreaRef = ref<HTMLElement | null>(null)
const tableBodyMaxHeight = ref(360)
let tableResizeObserver: ResizeObserver | null = null

const query = reactive({ warehouseCode: '', locationId: '' })

const uploading = ref(false)

const showAddModal = ref(false)
const addForm = reactive({ warehouseCode: '', locationId: '' })
const showDeleteModal = ref(false)
const deleteTarget = ref<WarehouseLocation | null>(null)

const showEditModal = ref(false)
const editForm = reactive({ id: '', warehouseCode: '', locationId: '' })

const addRules: FormRules = {
  warehouseCode: { required: true, message: '请输入仓库编码', trigger: 'blur' },
  locationId: { required: true, message: '请输入库位ID', trigger: 'blur' },
}

const editRules: FormRules = {
  warehouseCode: { required: true, message: '请输入仓库编码', trigger: 'blur' },
  locationId: { required: true, message: '请输入库位ID', trigger: 'blur' },
}

const syncingIds = ref<Set<string>>(new Set())
const updatingIds = ref<Set<string>>(new Set())

const columns: DataTableColumns<WarehouseLocation> = [
  { title: '仓库编码', key: 'warehouseCode', width: 180 },
  { title: '库位ID', key: 'locationId' },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '更新时间', key: 'updateTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 360,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NPopconfirm, { onPositiveClick: () => handleSync(row) }, {
          trigger: () =>
            h(NButton, {
              size: 'small',
              type: 'primary',
              loading: syncingIds.value.has(row.id),
            }, () => '同步库存'),
          default: () => `确认同步库位「${row.locationId}」的库存数据？`,
        }),
        h(NPopconfirm, { onPositiveClick: () => handleUpdateArticle(row) }, {
          trigger: () =>
            h(NButton, {
              size: 'small',
              type: 'info',
              loading: updatingIds.value.has(row.id),
            }, () => '更新标签'),
          default: () => `确认更新库位「${row.locationId}」的标签数据？`,
        }),
        h(NButton, {
          size: 'small',
          type: 'warning',
          onClick: () => openEdit(row),
        }, () => '编辑'),
        h(NButton, {
          size: 'small',
          type: 'error',
          onClick: () => openDelete(row),
        }, () => '删除'),
      ]),
  },
]

const errorColumns: DataTableColumns<{ rowNo: number; message: string }> = [
  { title: '行号', key: 'rowNo', width: 80 },
  { title: '错误信息', key: 'message' },
]

async function handleUpdateArticle(row: WarehouseLocation) {
  updatingIds.value.add(row.id)
  try {
    const res = await updateLocationArticle({ locationId: row.locationId })
    if (res.rowCount > 0) {
      message.success(`库位 ${row.locationId} 已推送 ${res.rowCount} 条标签数据`)
    } else {
      message.info(`库位 ${row.locationId} 无库存数据`)
    }
  } catch {
    return false
  } finally {
    updatingIds.value.delete(row.id)
  }
}

async function handleSync(row: WarehouseLocation) {
  syncingIds.value.add(row.id)
  try {
    const res = await syncLocationInventory({
      warehouseCode: row.warehouseCode,
      locationId: row.locationId,
    })
    if (res.savedCount > 0) {
      message.success(`库位 ${row.locationId} 已同步 ${res.savedCount} 条库存数据`)
    } else {
      message.info(`库位 ${row.locationId} 无新数据`)
    }
  } catch {
    return false
  } finally {
    syncingIds.value.delete(row.id)
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getWarehouseLocations({
      page: page.value,
      pageSize: pageSize.value,
      warehouseCode: query.warehouseCode || undefined,
      locationId: query.locationId || undefined,
    })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function updateTableHeight() {
  const areaHeight = tableAreaRef.value?.clientHeight || 0
  if (areaHeight > 0) {
    tableBodyMaxHeight.value = Math.max(areaHeight - 52, 160)
  }
}

async function handleAdd() {
  if (!addForm.warehouseCode || !addForm.locationId) {
    message.warning('请填写完整信息')
    return false
  }
  try {
    await addWarehouseLocation(addForm)
    message.success('添加成功')
    showAddModal.value = false
    addForm.warehouseCode = ''
    addForm.locationId = ''
    loadData()
  } catch {
    return false
  }
}

function openEdit(row: WarehouseLocation) {
  editForm.id = row.id
  editForm.warehouseCode = row.warehouseCode
  editForm.locationId = row.locationId
  showEditModal.value = true
}

async function handleEdit() {
  if (!editForm.warehouseCode || !editForm.locationId) {
    message.warning('请填写完整信息')
    return false
  }
  try {
    await updateWarehouseLocation(editForm)
    message.success('编辑成功')
    showEditModal.value = false
    loadData()
  } catch {
    return false
  }
}

function openDelete(row: WarehouseLocation) {
  deleteTarget.value = row
  showDeleteModal.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) {
    return false
  }
  try {
    await deleteWarehouseLocation(deleteTarget.value.id)
    message.success('删除成功')
    showDeleteModal.value = false
    deleteTarget.value = null
    loadData()
  } catch {
    return false
  }
}

async function handleUpload({ file }: UploadCustomRequestOptions) {
  uploading.value = true
  try {
    const result = await importWarehouseLocations(file.file as File)
    showImportResultDialog(result)
    loadData()
  } finally {
    uploading.value = false
  }
}

function showImportResultDialog(result: ImportResult) {
  dialog[result.failCount > 0 ? 'warning' : 'success']({
    title: '导入结果',
    positiveText: '知道了',
    style: { width: '640px' },
    content: () =>
      h(NSpace, { vertical: true, size: 12 }, () => [
        h(NDescriptions, { column: 3, bordered: true, size: 'small' }, () => [
          h(NDescriptionsItem, { label: '总行数' }, () => result.totalRows),
          h(NDescriptionsItem, { label: '成功' }, () =>
            h(NText, { type: 'success' }, () => result.successCount),
          ),
          h(NDescriptionsItem, { label: '失败' }, () =>
            h(NText, { type: result.failCount > 0 ? 'error' : 'success' }, () => result.failCount),
          ),
        ]),
        result.errors.length > 0
          ? h(NDataTable, {
              columns: errorColumns,
              data: result.errors,
              bordered: true,
              size: 'small',
              maxHeight: 260,
            })
          : null,
      ]),
  })
}

const templateOptions: DropdownOption[] = [
  { label: 'Excel 模板 (.xlsx)', key: 'excel' },
  { label: 'CSV 模板 (.csv)', key: 'csv' },
]

function downloadTemplate(format: string) {
  if (format === 'csv') {
    const content = '仓库编码,库位ID\nWH01,A-01-01\n'
    downloadBlob(content, '库位导入模板.csv', 'text/csv;charset=utf-8;')
  } else {
    const html = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel"><head><meta charset="UTF-8"></head><body><table><tr><td>仓库编码</td><td>库位ID</td></tr><tr><td>WH01</td><td>A-01-01</td></tr></table></body></html>'
    downloadBlob(html, '库位导入模板.xls', 'application/vnd.ms-excel')
  }
}

function downloadBlob(content: string, filename: string, mimeType: string) {
  const blob = new Blob([content], { type: mimeType })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
}

onMounted(() => {
  loadData()
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

.warehouse-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
