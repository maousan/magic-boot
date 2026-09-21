<template>
  <n-card
    title="App 运行日志"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-button type="primary" @click="handleSearch">查询</n-button>
    </template>

    <div class="table-page-content">
      <n-space :size="12" class="table-page-toolbar">
        <n-input
          v-model:value="deviceId"
          placeholder="设备ID（精确，留空查全部）"
          clearable
          style="width: 260px"
          @keyup.enter="handleSearch"
        />
        <n-text depth="3">PDA 设置页上传的运行日志包（按设备保留最近 20 个）</n-text>
      </n-space>

      <div ref="tableAreaRef" class="table-page-table">
        <n-data-table
          :columns="columns"
          :data="logs.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: AppDeviceLog) => row.id"
          :scroll-x="1000"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="pageNo"
          v-model:page-size="pageSize"
          :item-count="logs.total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <!-- 删除确认 -->
    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <span>
        确定删除 <b>{{ deleteTarget?.deviceId }}</b> 的日志包
        <b>{{ deleteTarget?.fileName }}</b> 吗？将同时删除落盘文件。
      </span>
      <template #action>
        <n-button @click="showDelete = false">取消</n-button>
        <n-button type="error" :loading="submitting" @click="handleDelete">删除</n-button>
      </template>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NInput,
  NText, NFlex, NPagination, NTag, useMessage,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getAppLogList, deleteAppLog, appLogDownloadUrl } from '@/api/app-log'
import type { AppDeviceLog } from '@/types'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const message = useMessage()

const loading = ref(false)
const submitting = ref(false)
const pageNo = ref(1)
const pageSize = ref(20)
const deviceId = ref('')
const showDelete = ref(false)
const deleteTarget = ref<AppDeviceLog | null>(null)
const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()

const logs = reactive<{ list: AppDeviceLog[]; total: number }>({ list: [], total: 0 })

function formatSize(bytes: number | null): string {
  if (bytes == null) return '-'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}

const columns: DataTableColumns<AppDeviceLog> = [
  { title: '设备ID', key: 'deviceId', width: 170, ellipsis: { tooltip: true } },
  { title: 'App版本', key: 'appVersion', width: 90, render: (row) => row.appVersion || '-' },
  { title: '文件名', key: 'fileName', ellipsis: { tooltip: true } },
  { title: '大小', key: 'fileSize', width: 90, render: (row) => formatSize(row.fileSize) },
  { title: '上传时间', key: 'uploadedAt', width: 170 },
  {
    title: '文件状态',
    key: 'fileExists',
    width: 90,
    render: (row) =>
      h(NTag, { size: 'small', type: row.fileExists ? 'success' : 'warning' }, () =>
        row.fileExists ? '在盘' : '已清理',
      ),
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(
          NButton,
          {
            size: 'small',
            type: 'primary',
            disabled: !row.fileExists,
            onClick: () => window.open(appLogDownloadUrl(row)),
          },
          () => '下载',
        ),
        h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '删除'),
      ]),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getAppLogList({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      deviceId: deviceId.value || undefined,
    })
    logs.list = res.list
    logs.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNo.value = 1
  fetchData()
}

function openDelete(row: AppDeviceLog) {
  deleteTarget.value = row
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  submitting.value = true
  try {
    await deleteAppLog(deleteTarget.value.id)
    showDelete.value = false
    deleteTarget.value = null
    message.success('已删除')
    await fetchData()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchData)
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

.table-page-table {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
