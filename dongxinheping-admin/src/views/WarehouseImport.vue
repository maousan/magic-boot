<template>
  <n-card title="库位导入">
    <n-space vertical :size="16">
      <n-space>
        <n-button @click="downloadTemplate">下载模板</n-button>
        <n-upload
          :max="1"
          accept=".xlsx,.xls"
          :custom-request="handleUpload"
          :show-file-list="false"
        >
          <n-button type="primary" :loading="uploading">上传文件</n-button>
        </n-upload>
      </n-space>

      <n-card v-if="result" title="导入结果" size="small">
        <n-descriptions :column="3" bordered>
          <n-descriptions-item label="总行数">{{ result.totalRows }}</n-descriptions-item>
          <n-descriptions-item label="成功">
            <n-text type="success">{{ result.successCount }}</n-text>
          </n-descriptions-item>
          <n-descriptions-item label="失败">
            <n-text type="error">{{ result.failCount }}</n-text>
          </n-descriptions-item>
        </n-descriptions>

        <n-data-table
          v-if="result.errors.length > 0"
          :columns="errorColumns"
          :data="result.errors"
          :bordered="true"
          size="small"
          style="margin-top: 12px"
        />
      </n-card>
    </n-space>
  </n-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  NCard, NSpace, NButton, NUpload, NDescriptions, NDescriptionsItem, NText, NDataTable,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import type { UploadCustomRequestOptions } from 'naive-ui'
import { importWarehouseLocations } from '@/api/warehouse'
import type { ImportResult } from '@/types'

const uploading = ref(false)
const result = ref<ImportResult | null>(null)

const errorColumns: DataTableColumns<{ rowNo: number; message: string }> = [
  { title: '行号', key: 'rowNo', width: 80 },
  { title: '错误信息', key: 'message' },
]

function downloadTemplate() {
  const header = '库位码,备注\nA-01,示例库位\n'
  const blob = new Blob([header], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '库位导入模板.csv'
  a.click()
  URL.revokeObjectURL(url)
}

async function handleUpload({ file }: UploadCustomRequestOptions) {
  uploading.value = true
  result.value = null
  try {
    result.value = await importWarehouseLocations(file.file as File)
  } finally {
    uploading.value = false
  }
}
</script>
