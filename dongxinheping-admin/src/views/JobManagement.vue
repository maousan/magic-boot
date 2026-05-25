<template>
  <n-card title="定时任务">
    <template #header-extra>
      <n-button type="primary" @click="fetchData">刷新</n-button>
    </template>

    <n-data-table
      :columns="columns"
      :data="jobs"
      :loading="loading"
      :bordered="true"
      :row-key="(row: JobItem) => row.id"
    />
  </n-card>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { NCard, NDataTable, NButton, NSpace, NTag, NPopconfirm, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { pluginClient } from '@/api/request'

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

const loading = ref(false)
const jobs = ref<JobItem[]>([])
const message = useMessage()
const triggering = ref<Set<string>>(new Set())

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
    width: 200,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
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
