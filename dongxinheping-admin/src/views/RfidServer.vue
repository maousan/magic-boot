<template>
  <div class="rfid-layout">
    <!-- Left column -->
    <div class="rfid-left">
      <!-- Status hero -->
      <div class="status-hero" :class="statusClass">
        <div>
          <div class="status-row">
            <span class="pulse-dot" :class="{ active: status?.running }" />
            <span class="status-title">RFID TCP 服务</span>
            <n-tag v-if="status" :type="status.running ? 'success' : 'error'" size="small" :bordered="false" round>
              {{ status.running ? '运行中' : '已停止' }}
            </n-tag>
            <n-spin v-else size="small" />
          </div>
          <div class="status-meta">
            <template v-if="status?.running">
              端口 {{ status.port }}
              <template v-if="status.activeConnections !== undefined"> · {{ status.activeConnections }} 个活跃连接</template>
            </template>
            <template v-else-if="status">服务未启动</template>
          </div>
        </div>
        <n-space :size="8" align="center">
          <n-text depth="3" style="font-size: 12px">自动刷新</n-text>
          <n-switch v-model:value="autoRefresh" size="small" />
          <n-button size="small" secondary :loading="loading" @click="refreshAll">刷新</n-button>
          <n-button size="small" type="success" :disabled="status?.running ?? false" :loading="opLoading" @click="startServer">启动</n-button>
          <n-button size="small" type="error" :disabled="!status?.running" :loading="opLoading" @click="stopServer">停止</n-button>
        </n-space>
      </div>

      <!-- Metrics -->
      <div class="metrics-row">
        <div class="metric-card">
          <div class="metric-label">监听端口</div>
          <div class="metric-value">{{ status?.running ? status.port : '-' }}</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">活跃连接</div>
          <div class="metric-value">{{ status?.activeConnections ?? 0 }}</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">在线终端</div>
          <div class="metric-value">{{ devices.length }}</div>
        </div>
      </div>

      <!-- Console row: command + log side by side -->
      <div class="console-row">
        <n-card title="指令控制台" size="small" style="flex: 2">
          <n-space vertical :size="12">
            <n-form-item label="目标终端" :show-feedback="false">
              <n-select
                v-model:value="commandForm.deviceId"
                :options="deviceOptions"
                placeholder="选择终端 IP"
                clearable
                size="small"
              />
            </n-form-item>
            <n-form-item label="指令" :show-feedback="false">
              <n-input v-model:value="commandForm.command" placeholder="输入指令名称" size="small" />
            </n-form-item>
            <n-form-item label="参数 JSON" :show-feedback="false">
              <n-input
                v-model:value="commandForm.params"
                type="textarea"
                placeholder='例如: {"key": "value"}'
                :rows="3"
                size="small"
              />
            </n-form-item>
            <n-space>
              <n-button
                type="primary"
                size="small"
                :loading="sending"
                :disabled="!commandForm.deviceId || !commandForm.command"
                @click="handleSendCommand"
              >
                发送
              </n-button>
              <n-button size="small" secondary @click="resetCommand">清空</n-button>
              <n-button size="small" secondary @click="generateRandomParams">随机参数</n-button>
            </n-space>
          </n-space>

          <template v-if="commandResult">
            <n-divider style="margin: 12px 0" />
            <div class="result-summary">
              <n-tag :type="commandResult.success ? 'success' : 'error'" size="small" :bordered="false">
                {{ commandResult.status || (commandResult.success ? 'success' : 'failed') }}
              </n-tag>
              <n-text depth="3" style="font-size: 13px">
                {{ commandResult.message || '-' }}
              </n-text>
            </div>
            <div v-if="commandResult.msgId" class="msg-id">
              msgId: {{ commandResult.msgId }}
            </div>
          </template>
        </n-card>

        <n-card title="接收消息日志" size="small" style="flex: 2">
          <template #header-extra>
            <n-space :size="8" align="center">
              <n-text depth="3" style="font-size: 13px">{{ records.length }} 条</n-text>
              <n-button size="tiny" secondary :loading="recordsLoading" @click="fetchRecords">刷新</n-button>
            </n-space>
          </template>
          <n-data-table
            :columns="recordColumns"
            :data="records"
            :loading="recordsLoading"
            :bordered="false"
            size="small"
            :row-key="(row: RfidRecord) => row.id"
            :max-height="320"
          />
          <n-flex justify="end" style="margin-top: 8px">
            <n-pagination
              v-model:page="recordsPage"
              v-model:page-size="recordsPageSize"
              :item-count="recordsTotal"
              :page-sizes="[20, 50, 100]"
              size="small"
              show-size-picker
              @update:page="fetchRecords"
              @update:page-size="fetchRecords"
            />
          </n-flex>
        </n-card>
      </div>
    </div>

    <!-- Right column: Connected devices (full height) -->
    <div class="rfid-right">
      <n-card title="连接终端" size="small">
        <template #header-extra>
          <n-text depth="3" style="font-size: 13px">{{ devices.length }} 台在线</n-text>
        </template>
        <div class="devices-body">
          <div v-if="!devices.length" class="empty-devices">
            <n-text depth="3">暂无已连接终端</n-text>
          </div>
          <n-data-table
            v-else
            :columns="columns"
            :data="devices"
            :loading="loading"
            :bordered="false"
            size="small"
            flex-height
            style="height: 100%"
            :row-key="(row: RfidDeviceInfo) => row.deviceId"
          />
        </div>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onUnmounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NSpace, NFlex, NFormItem, NInput, NTag,
  NText, NSpin, NSwitch, NSelect, NDivider, NPagination, useMessage,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import {
  getRfidStatus, getRfidDevices, sendRfidCommand,
  startRfidServer, stopRfidServer, getRfidRecords,
} from '@/api/rfid-server'
import type { RfidServerStatus, RfidDeviceInfo, RfidCommandResult, RfidRecord } from '@/api/rfid-server'

const message = useMessage()
const loading = ref(false)
const opLoading = ref(false)
const sending = ref(false)
const status = ref<RfidServerStatus | null>(null)
const devices = ref<RfidDeviceInfo[]>([])
const commandResult = ref<RfidCommandResult | null>(null)
const autoRefresh = ref(false)
let refreshTimer: ReturnType<typeof setInterval> | null = null

const records = ref<RfidRecord[]>([])
const recordsLoading = ref(false)
const recordsPage = ref(1)
const recordsPageSize = ref(20)
const recordsTotal = ref(0)

const commandForm = reactive<{
  deviceId: string | undefined
  command: string
  params: string
}>({
  deviceId: undefined,
  command: '',
  params: '',
})

const statusClass = computed(() => {
  if (!status.value) return 'loading'
  return status.value.running ? 'running' : 'stopped'
})

const deviceOptions = computed(() =>
  devices.value.map((d) => ({
    label: `${d.remoteAddress}`,
    value: d.deviceId,
  })),
)

function formatTime(ts: number): string {
  if (!ts) return '-'
  return new Date(ts).toLocaleString()
}

const columns: DataTableColumns<RfidDeviceInfo> = [
  { title: '设备 ID', key: 'deviceId', width: 180, ellipsis: { tooltip: true } },
  { title: '远程地址', key: 'remoteAddress', minWidth: 180, ellipsis: { tooltip: true } },
  { title: '连接时间', key: 'connectedAt', width: 180, render: (row) => formatTime(row.connectedAt) },
  { title: '最后活跃', key: 'lastActiveAt', width: 180, render: (row) => formatTime(row.lastActiveAt) },
  {
    title: '操作',
    key: 'actions',
    width: 90,
    render: (row) =>
      h(NButton, { size: 'small', secondary: true, onClick: () => selectDevice(row) }, () => '选择'),
  },
]

const recordColumns: DataTableColumns<RfidRecord> = [
  { title: '设备 ID', key: 'device_id', width: 160, ellipsis: { tooltip: true } },
  { title: 'EPC', key: 'epc', ellipsis: { tooltip: true } },
  { title: 'RSSI', key: 'rssi', width: 80 },
  { title: '读取时间', key: 'read_time', width: 180 },
]

async function fetchStatus() {
  status.value = await getRfidStatus()
}

async function fetchDevices() {
  const res = await getRfidDevices()
  devices.value = res.devices || []
}

async function fetchRecords() {
  recordsLoading.value = true
  try {
    const res = await getRfidRecords({ page: recordsPage.value, pageSize: recordsPageSize.value })
    records.value = res.list || []
    recordsTotal.value = res.total || 0
  } finally {
    recordsLoading.value = false
  }
}

async function refreshAll() {
  loading.value = true
  try {
    await Promise.all([fetchStatus(), fetchDevices(), fetchRecords()])
  } finally {
    loading.value = false
  }
}

async function startServer() {
  opLoading.value = true
  try {
    status.value = await startRfidServer()
    await fetchDevices()
  } finally {
    opLoading.value = false
  }
}

async function stopServer() {
  opLoading.value = true
  try {
    status.value = await stopRfidServer()
    await fetchDevices()
  } finally {
    opLoading.value = false
  }
}

function selectDevice(row: RfidDeviceInfo) {
  commandForm.deviceId = row.deviceId
}

function resetCommand() {
  commandForm.command = ''
  commandForm.params = ''
  commandResult.value = null
}

function generateRandomParams() {
  const keys = ['action', 'mode', 'type', 'channel', 'interval', 'threshold', 'power', 'freq']
  const count = 2 + Math.floor(Math.random() * 3)
  const obj: Record<string, unknown> = {}
  const used = new Set<string>()
  for (let i = 0; i < count; i++) {
    let key = keys[Math.floor(Math.random() * keys.length)]!
    while (used.has(key)) key = keys[Math.floor(Math.random() * keys.length)]!
    used.add(key)
    const val = Math.random() > 0.5
      ? Math.floor(Math.random() * 100)
      : `val_${Math.random().toString(36).slice(2, 6)}`
    obj[key] = val
  }
  commandForm.params = JSON.stringify(obj, null, 2)
}

async function handleSendCommand() {
  if (!commandForm.deviceId || !commandForm.command) return
  sending.value = true
  commandResult.value = null
  try {
    let params: Record<string, unknown> | undefined
    if (commandForm.params.trim()) {
      try {
        params = JSON.parse(commandForm.params)
      } catch {
        message.error('参数 JSON 格式错误')
        return
      }
    }
    commandResult.value = await sendRfidCommand({
      deviceId: commandForm.deviceId,
      command: commandForm.command,
      params,
    })
    if (commandResult.value.success) {
      message.success(commandResult.value.message || '指令已发送')
    } else {
      message.error(commandResult.value.message || '发送失败')
    }
  } catch {
    // handled by interceptor
  } finally {
    sending.value = false
  }
}

watch(autoRefresh, (on) => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
  if (on) {
    refreshTimer = setInterval(refreshAll, 5000)
  }
})

onMounted(refreshAll)
onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>

<style scoped>
.rfid-layout {
  display: flex;
  height: 100%;
  min-height: 0;
  gap: 16px;
}

.rfid-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.rfid-right {
  width: 520px;
  flex-shrink: 0;
}

/* Make devices card fill the full column height */
.rfid-right :deep(.n-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.rfid-right :deep(.n-card__content) {
  flex: 1;
  min-height: 0;
}
.devices-body {
  flex: 1;
  min-height: 0;
}

/* Status hero */
.status-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  border-radius: 8px;
  flex-shrink: 0;
  transition: all 0.3s ease;
}
.status-hero.loading { background: #f5f5f5; }
.status-hero.running { background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%); }
.status-hero.stopped { background: linear-gradient(135deg, #fbe9e7 0%, #fff3e0 100%); }

.status-row { display: flex; align-items: center; gap: 8px; }
.status-title { font-size: 16px; font-weight: 600; }
.status-meta { margin-top: 4px; padding-left: 18px; font-size: 13px; color: #666; }

.pulse-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  background-color: #ddd;
}
.pulse-dot.active {
  background-color: #18a058;
  animation: pulse 2s ease-in-out infinite;
}
@keyframes pulse {
  0%   { box-shadow: 0 0 0 0 rgba(24, 160, 88, 0.4); }
  70%  { box-shadow: 0 0 0 8px rgba(24, 160, 88, 0); }
  100% { box-shadow: 0 0 0 0 rgba(24, 160, 88, 0); }
}

/* Metrics */
.metrics-row { display: flex; gap: 12px; flex-shrink: 0; }
.metric-card {
  flex: 1;
  background: #fafafa;
  text-align: center;
  padding: 12px 16px;
  border-radius: 6px;
}
.metric-label { font-size: 13px; color: #999; margin-bottom: 4px; }
.metric-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  font-variant-numeric: tabular-nums;
}

/* Console row */
.console-row { display: flex; gap: 16px; }

.empty-devices { padding: 32px 0; text-align: center; }

.result-summary { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.msg-id { font-size: 12px; color: #666; word-break: break-all; }
</style>
