<template>
  <n-card
    title="定时任务"
    class="table-page-card"
    :class="$attrs.class"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-button type="primary" @click="fetchData">刷新</n-button>
    </template>

    <div class="table-page-content">
      <div ref="tableAreaRef" class="table-page-table job-management-table-area">
        <n-data-table
          :columns="columns"
          :data="jobs"
          :loading="loading"
          :bordered="true"
          :row-key="(row: JobItem) => row.id"
          :scroll-x="1060"
          :max-height="tableBodyMaxHeight"
        />
      </div>
    </div>
  </n-card>

  <n-modal
    v-model:show="logModalVisible"
    preset="card"
    :title="logModalTitle"
    class="job-log-modal"
    style="width: min(920px, calc(100vw - 48px))"
    :bordered="false"
  >
    <n-space vertical size="medium">
      <n-data-table
        :columns="logColumns"
        :data="jobLogs"
        :loading="logLoading"
        :bordered="true"
        :row-key="(row: JobLogItem) => row.id"
        :scroll-x="1180"
        :max-height="420"
      />
      <n-space justify="end">
        <n-button :disabled="logPage <= 1 || logLoading" @click="changeLogPage(logPage - 1)">
          上一页
        </n-button>
        <n-button :disabled="!logHasNext || logLoading" @click="changeLogPage(logPage + 1)">
          下一页
        </n-button>
      </n-space>
    </n-space>
  </n-modal>
</template>

<script setup lang="ts">
import { ref, onMounted, h, computed } from 'vue'
defineOptions({ inheritAttrs: false })
import { NCard, NDataTable, NButton, NSpace, NTag, NPopconfirm, NModal, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { pluginClient } from '@/api/request'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

interface JobItem {
  id: string
  name: string
  path: string
  cron: string
  enabled: boolean
  paused: boolean
  description: string
  nextFireTime: string | null
}

interface JobLogItem {
  id: number | string
  startTime: string | null
  endTime: string | null
  duration: number | null
  status: string
  result: string | null
  exceptionMessage: string | null
  exceptionStack: string | null
  triggerType: string | null
}

const loading = ref(false)
const jobs = ref<JobItem[]>([])
const message = useMessage()
const triggering = ref<Set<string>>(new Set())
const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()
const logModalVisible = ref(false)
const logLoading = ref(false)
const logTarget = ref<JobItem | null>(null)
const jobLogs = ref<JobLogItem[]>([])
const logPage = ref(1)
const logPageSize = 10
const logHasNext = computed(() => jobLogs.value.length >= logPageSize)
const logModalTitle = computed(() => (logTarget.value ? `执行日志 - ${logTarget.value.name}` : '执行日志'))

const renderLogStatus = (status: string) => {
  const normalized = status || '-'
  if (normalized === 'SUCCESS') return h(NTag, { type: 'success', size: 'small' }, () => '成功')
  if (normalized === 'RUNNING') return h(NTag, { type: 'info', size: 'small' }, () => '执行中')
  if (normalized === 'FAILURE' || normalized === 'FAILED') {
    return h(NTag, { type: 'error', size: 'small' }, () => '失败')
  }
  return h(NTag, { size: 'small' }, () => normalized)
}

const logColumns: DataTableColumns<JobLogItem> = [
  {
    title: '状态',
    key: 'status',
    width: 90,
    render: (row) => renderLogStatus(row.status),
  },
  { title: '触发方式', key: 'triggerType', width: 100 },
  { title: '开始时间', key: 'startTime', width: 180 },
  { title: '结束时间', key: 'endTime', width: 180 },
  {
    title: '耗时',
    key: 'duration',
    width: 100,
    render: (row) => (row.duration == null ? '-' : `${row.duration}ms`),
  },
  { title: '执行结果', key: 'result', width: 260, ellipsis: { tooltip: true } },
  { title: '异常信息', key: 'exceptionMessage', ellipsis: { tooltip: true } },
]

const columns: DataTableColumns<JobItem> = [
  { title: '任务名称', key: 'name', width: 200 },
  { title: '路径', key: 'path', width: 200 },
  { title: 'Cron', key: 'cron', width: 160 },
  {
    title: '状态',
    key: 'enabled',
    width: 100,
    render: (row) => {
      if (!row.enabled) return h(NTag, { type: 'default', size: 'small' }, () => '已禁用')
      if (row.paused) return h(NTag, { type: 'warning', size: 'small' }, () => '已暂停')
      return h(NTag, { type: 'success', size: 'small' }, () => '运行中')
    },
  },
  { title: '下次执行', key: 'nextFireTime', width: 200 },
  { title: '说明', key: 'description', ellipsis: { tooltip: true } },
  {
    title: '操作',
    key: 'actions',
    width: 260,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(
          NButton,
          { size: 'small', onClick: () => openLogModal(row) },
          () => '日志',
        ),
        h(
          NPopconfirm,
          { onPositiveClick: () => handleTrigger(row) },
          {
            trigger: () =>
              h(
                NButton,
                {
                  size: 'small',
                  type: 'primary',
                  loading: triggering.value.has(row.id),
                },
                () => '立即执行',
              ),
            default: () => `确认立即执行任务「${row.name}」？`,
          },
        ),
        row.enabled && !row.paused
          ? h(
              NButton,
              { size: 'small', type: 'warning', onClick: () => handlePause(row) },
              () => '暂停',
            )
          : null,
        row.paused
          ? h(
              NButton,
              { size: 'small', type: 'info', onClick: () => handleResume(row) },
              () => '恢复',
            )
          : null,
      ]),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await pluginClient.get('/magic/job/list')
    const list = res.data.data || []
    jobs.value = list.map((item: any) => ({
      id: item.id,
      name: item.name || '',
      path: item.path || '',
      cron: item.cron || '',
      enabled: item.enabled !== false,
      paused: item.paused === true,
      description: item.description || '',
      nextFireTime: item.nextFireTime || null,
    }))
  } finally {
    loading.value = false
  }
}

async function openLogModal(row: JobItem) {
  logTarget.value = row
  logPage.value = 1
  logModalVisible.value = true
  await fetchJobLogs()
}

async function changeLogPage(page: number) {
  if (page < 1 || logLoading.value) return
  logPage.value = page
  await fetchJobLogs()
}

async function fetchJobLogs() {
  if (!logTarget.value) return
  logLoading.value = true
  try {
    const res = await pluginClient.get(`/magic/job/${encodeURIComponent(logTarget.value.id)}/history`, {
      params: { page: logPage.value, size: logPageSize },
    })
    const list = Array.isArray(res.data.data) ? res.data.data : []
    jobLogs.value = list.map((item: any) => ({
      id: item.id,
      startTime: item.startTime || null,
      endTime: item.endTime || null,
      duration: item.duration ?? null,
      status: item.status || '',
      result: item.result || null,
      exceptionMessage: item.exceptionMessage || null,
      exceptionStack: item.exceptionStack || null,
      triggerType: item.triggerType || null,
    }))
  } finally {
    logLoading.value = false
  }
}

async function handleTrigger(row: JobItem) {
  triggering.value.add(row.id)
  try {
    await pluginClient.post(`/magic/job/${row.id}/trigger`)
    message.success(`任务 ${row.name} 已触发`)
    // 刷新状态
    try {
      const stateRes = await pluginClient.get(`/magic/job/${row.id}/state`)
      const state = stateRes.data.data
      row.paused = state.paused === true
      row.nextFireTime = state.nextFireTime || null
    } catch {
      // ignore
    }
  } catch {
    // error handled by interceptor
  } finally {
    triggering.value.delete(row.id)
  }
}

async function handlePause(row: JobItem) {
  try {
    await pluginClient.post(`/magic/job/${row.id}/pause`)
    message.success(`任务 ${row.name} 已暂停`)
    row.paused = true
  } catch {
    // error handled by interceptor
  }
}

async function handleResume(row: JobItem) {
  try {
    await pluginClient.post(`/magic/job/${row.id}/resume`)
    message.success(`任务 ${row.name} 已恢复`)
    row.paused = false
    // 刷新下次执行时间
    try {
      const stateRes = await pluginClient.get(`/magic/job/${row.id}/state`)
      const state = stateRes.data.data
      row.nextFireTime = state.nextFireTime || null
    } catch {
      // ignore
    }
  } catch {
    // error handled by interceptor
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
}

.job-management-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}

</style>
