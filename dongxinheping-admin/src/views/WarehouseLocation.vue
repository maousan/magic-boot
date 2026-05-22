<template>
  <n-card title="库位列表">
    <n-space vertical :size="16">
      <n-space justify="space-between">
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

      <n-data-table
        :columns="columns"
        :data="list"
        :loading="loading"
        :bordered="true"
      />

      <n-flex justify="end">
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
          :columns="errorColumns"
          :data="importResult.errors"
          :bordered="true"
          size="small"
          style="margin-top: 12px"
        />
      </n-card>
    </n-space>

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
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  NCard, NSpace, NFlex, NButton, NInput, NDataTable, NPagination, NText,
  NUpload, NModal, NForm, NFormItem, NDescriptions, NDescriptionsItem,
  NDropdown,
  useMessage,
} from 'naive-ui'
import type { DataTableColumns, FormRules, DropdownOption } from 'naive-ui'
import type { UploadCustomRequestOptions } from 'naive-ui'
import { getWarehouseLocations, addWarehouseLocation, importWarehouseLocations } from '@/api/warehouse'
import type { WarehouseLocation, ImportResult } from '@/types'

const message = useMessage()

const loading = ref(false)
const list = ref<WarehouseLocation[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const query = reactive({ warehouseCode: '', locationId: '' })

const uploading = ref(false)
const importResult = ref<ImportResult | null>(null)

const showAddModal = ref(false)
const addForm = reactive({ warehouseCode: '', locationId: '' })

const addRules: FormRules = {
  warehouseCode: { required: true, message: '请输入仓库编码', trigger: 'blur' },
  locationId: { required: true, message: '请输入库位ID', trigger: 'blur' },
}

const columns: DataTableColumns<WarehouseLocation> = [
  { title: '仓库编码', key: 'warehouseCode', width: 180 },
  { title: '库位ID', key: 'locationId' },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '更新时间', key: 'updateTime', width: 180 },
]

const errorColumns: DataTableColumns<{ rowNo: number; message: string }> = [
  { title: '行号', key: 'rowNo', width: 80 },
  { title: '错误信息', key: 'message' },
]

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

async function handleUpload({ file }: UploadCustomRequestOptions) {
  uploading.value = true
  importResult.value = null
  try {
    importResult.value = await importWarehouseLocations(file.file as File)
    loadData()
  } finally {
    uploading.value = false
  }
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

onMounted(loadData)
</script>
