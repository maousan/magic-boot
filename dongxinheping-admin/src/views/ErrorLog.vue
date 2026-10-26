<template>
  <div class="table-page-content">
    <n-space :size="12" class="table-page-toolbar">
      <n-input
        v-model:value="keyword"
        placeholder="路径 / 异常类关键字"
        clearable
        style="width: 280px"
        @keyup.enter="handleSearch"
      />
      <n-button secondary :loading="loading" @click="load">刷新</n-button>
      <n-button v-perm="'ErrorLog:clean'" secondary type="warning" @click="showClean = true">清理日志</n-button>
    </n-space>

    <div class="table-page-table">
      <n-data-table
        :columns="columns"
        :data="rows.list"
        :loading="loading"
        :bordered="false"
        :row-key="(row: ErrorLogRow) => row.id"
      />
    </div>

    <n-flex justify="end" class="table-page-pagination">
      <n-pagination
        v-model:page="pageNo"
        v-model:page-size="pageSize"
        :item-count="rows.total"
        :page-sizes="[20, 50, 100]"
        show-size-picker
        @update:page="load"
        @update:page-size="handleSearch"
      />
    </n-flex>

    <n-modal
      v-model:show="showClean"
      preset="dialog"
      type="warning"
      title="清理错误日志"
      positive-text="确认清理"
      negative-text="取消"
      :loading="cleaning"
      @positive-click="handleClean"
    >
      删除早于
      <n-input-number v-model:value="cleanDays" :min="0" size="small" style="width: 110px" />
      天的错误日志（0 = 全部清理），操作不可恢复。
    </n-modal>

    <n-modal v-model:show="showDetail" preset="card" title="错误日志详情" class="log-detail-modal" style="width: min(860px, 92vw)">
      <n-descriptions :column="2" size="small" bordered>
        <n-descriptions-item label="时间">{{ detail?.createTime }}</n-descriptions-item>
        <n-descriptions-item label="方法">{{ detail?.requestMethod }}</n-descriptions-item>
        <n-descriptions-item label="用户">{{ detail?.userId || '（未登录）' }}</n-descriptions-item>
        <n-descriptions-item label="IP">{{ detail?.ipAddress || '-' }}</n-descriptions-item>
        <n-descriptions-item label="请求路径" :span="2">
          <span class="mono">{{ detail?.requestPath }}</span>
        </n-descriptions-item>
        <n-descriptions-item label="异常类" :span="2">
          <span class="mono">{{ detail?.exceptionClass || '-' }}</span>
        </n-descriptions-item>
        <n-descriptions-item label="异常消息" :span="2">{{ detail?.exceptionMessage || '-' }}</n-descriptions-item>
      </n-descriptions>
      <div class="detail-label">请求参数</div>
      <pre class="detail-pre">{{ prettyParams }}</pre>
      <div class="detail-label">堆栈跟踪</div>
      <pre class="detail-pre">{{ detail?.stackTrace || '（无）' }}</pre>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, h, onMounted, reactive, ref } from 'vue'
import {
  NButton,
  NDataTable,
  NDescriptions,
  NDescriptionsItem,
  NInputNumber,
  NModal,
  NPagination,
  NInput,
  NSpace,
  useMessage,
  type DataTableColumns,
} from 'naive-ui'
import { listErrorLogs, getErrorLogDetail, cleanErrorLogs, type ErrorLogRow, type ErrorLogDetail } from '@/api/error-log'

const message = useMessage()
const loading = ref(false)
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(20)
const rows = reactive<{ list: ErrorLogRow[]; total: number }>({ list: [], total: 0 })
const showDetail = ref(false)
const detail = ref<ErrorLogDetail | null>(null)
const showClean = ref(false)
const cleaning = ref(false)
const cleanDays = ref(90)

async function handleClean() {
  cleaning.value = true
  try {
    const res = await cleanErrorLogs(cleanDays.value ?? 0)
    message.success(`已清理 ${res.errorDeleted} 条错误日志`)
    showClean.value = false
    pageNo.value = 1
    await load()
    return true
  } catch (e: any) {
    message.error(e?.response?.data?.message || '清理失败')
    return false
  } finally {
    cleaning.value = false
  }
}

const prettyParams = computed(() => {
  if (!detail.value?.requestParams) return '（无）'
  try {
    return JSON.stringify(JSON.parse(detail.value.requestParams), null, 2)
  } catch {
    return detail.value.requestParams
  }
})

async function load() {
  loading.value = true
  try {
    const res = await listErrorLogs({
      pageNo: pageNo.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
    })
    rows.list = res.list ?? []
    rows.total = res.total ?? 0
  } catch (e: any) {
    message.error(e?.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNo.value = 1
  load()
}

async function openDetail(row: ErrorLogRow) {
  try {
    detail.value = await getErrorLogDetail(row.id)
    showDetail.value = true
  } catch (e: any) {
    message.error(e?.response?.data?.message || '加载详情失败')
  }
}

const columns: DataTableColumns<ErrorLogRow> = [
  { title: '时间', key: 'createTime', width: 170 },
  {
    title: '请求路径',
    key: 'requestPath',
    ellipsis: { tooltip: { contentStyle: { maxWidth: '420px', whiteSpace: 'pre-wrap' } } },
    render: (row) => h('span', { class: 'mono' }, row.requestPath),
  },
  { title: '方法', key: 'requestMethod', width: 70 },
  { title: '用户', key: 'userId', width: 130, ellipsis: { tooltip: true }, render: (row) => row.userId || '（未登录）' },
  { title: 'IP', key: 'ipAddress', width: 120 },
  {
    title: '异常类',
    key: 'exceptionClass',
    ellipsis: { tooltip: true },
    render: (row) => h('span', { class: 'mono' }, row.exceptionClass || '-'),
  },
  {
    title: '异常消息',
    key: 'exceptionMessage',
    ellipsis: { tooltip: { contentStyle: { maxWidth: '420px', whiteSpace: 'pre-wrap' } } },
  },
  {
    title: '操作',
    key: 'actions',
    width: 80,
    render: (row) => h(NButton, { size: 'small', secondary: true, onClick: () => openDetail(row) }, () => '详情'),
  },
]

onMounted(load)
</script>

<style scoped>
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

.mono {
  font-family: monospace;
  font-size: 13px;
}

.detail-label {
  margin: 12px 0 4px;
  font-size: 12px;
  color: #999;
}

.detail-pre {
  max-height: 300px;
  overflow: auto;
  margin: 0;
  padding: 8px;
  background: rgba(128, 128, 128, 0.08);
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
