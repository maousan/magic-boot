<template>
  <div class="netty-monitor">
    <!-- Status Hero -->
    <div class="status-hero" :class="statusClass">
      <div>
        <div class="status-row">
          <span class="pulse-dot" :class="{ active: status?.running }" />
          <span class="status-title">Netty TCP 服务</span>
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
        <n-button size="small" secondary @click="refreshAll">刷新</n-button>
        <n-button size="small" type="success" :disabled="status?.running ?? false" :loading="opLoading" @click="startServer">启动</n-button>
        <n-button size="small" type="error" :disabled="!status?.running" :loading="opLoading" @click="stopServer">停止</n-button>
      </n-space>
    </div>

    <!-- Metrics -->
    <n-grid :cols="3" :x-gap="16" :y-gap="16" style="margin-bottom: 16px">
      <n-gi>
        <n-card class="metric-card" size="small" :bordered="false">
          <div class="metric-label">监听端口</div>
          <div class="metric-value">{{ status?.port ?? '-' }}</div>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card class="metric-card" size="small" :bordered="false">
          <div class="metric-label">活跃连接</div>
          <div class="metric-value">{{ status?.activeConnections ?? 0 }}</div>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card class="metric-card" size="small" :bordered="false">
          <div class="metric-label">已连接设备</div>
          <div class="metric-value">{{ clients?.totalClients ?? 0 }}</div>
        </n-card>
      </n-gi>
    </n-grid>

    <!-- Two Column Layout -->
    <n-grid :cols="24" :x-gap="16">
      <!-- Clients -->
      <n-gi :span="14">
        <n-card title="连接设备" size="small">
          <template #header-extra>
            <n-text depth="3" style="font-size: 13px">
              {{ clients?.clientDetails?.length ?? 0 }} 台在线
            </n-text>
          </template>
          <div v-if="!clients?.clientDetails?.length" class="empty-clients">
            <n-text depth="3">暂无已连接设备</n-text>
          </div>
          <n-data-table
            v-else
            :columns="clientColumns"
            :data="clients?.clientDetails ?? []"
            :bordered="false"
            size="small"
          />
        </n-card>
      </n-gi>

      <!-- Message Console -->
      <n-gi :span="10">
        <n-card title="消息控制台" size="small">
          <n-space vertical :size="12">
            <n-form-item label="目标客户端" :show-feedback="false">
              <n-select
                v-model:value="sendForm.remoteAddress"
                :options="clientOptions"
                placeholder="选择客户端"
                clearable
                size="small"
              />
            </n-form-item>
            <n-space :size="16" align="center">
              <n-form-item label="格式" :show-feedback="false">
                <n-radio-group v-model:value="sendForm.payloadFormat" size="small">
                  <n-radio-button value="ascii">ASCII</n-radio-button>
                  <n-radio-button value="hex">HEX</n-radio-button>
                </n-radio-group>
              </n-form-item>
              <n-form-item label="等待响应" :show-feedback="false">
                <n-switch v-model:value="sendForm.waitResponse" size="small" />
              </n-form-item>
            </n-space>
            <n-input
              v-model:value="sendForm.payload"
              type="textarea"
              :rows="4"
              placeholder="输入发送内容"
              size="small"
            />
            <n-space>
              <n-button type="primary" size="small" :loading="sendLoading" :disabled="!sendForm.remoteAddress" @click="send">发送</n-button>
              <n-button type="warning" size="small" :loading="sendLoading" @click="broadcast">广播</n-button>
            </n-space>
          </n-space>

          <template v-if="sendResult">
            <n-divider style="margin: 12px 0" />
            <div class="result-summary">
              <span>成功 <strong class="text-success">{{ sendResult.successCount }}</strong> / {{ sendResult.totalTargets }}</span>
              <span style="margin-left: 12px">失败 <strong class="text-error">{{ sendResult.failedCount }}</strong></span>
            </div>
            <n-data-table
              v-if="sendResult.responses.length > 0"
              :columns="responseColumns"
              :data="sendResult.responses"
              :bordered="false"
              size="small"
              :max-height="200"
            />
          </template>
        </n-card>
      </n-gi>
    </n-grid>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onUnmounted, h } from 'vue'
import {
  NCard, NSpace, NButton, NText, NDivider,
  NDataTable, NGrid, NGi, NFormItem, NInput, NSelect,
  NRadioGroup, NRadioButton, NSwitch, NTag, NSpin,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import {
  zintisNettyStart, zintisNettyStop, zintisNettyStatus, zintisNettyClients,
  zintisNettySend, zintisNettyBroadcast,
} from '@/api/zintis-netty'
import type {
  ZintisNettyServerStatus, ZintisNettyClientList,
  ZintisNettySendResponse, ZintisNettySendResult,
} from '@/types'

const opLoading = ref(false)
const sendLoading = ref(false)
const status = ref<ZintisNettyServerStatus | null>(null)
const clients = ref<ZintisNettyClientList | null>(null)
const sendResult = ref<ZintisNettySendResponse | null>(null)
const autoRefresh = ref(false)
let refreshTimer: ReturnType<typeof setInterval> | null = null

const sendForm = reactive({
  remoteAddress: '',
  payload: '',
  payloadFormat: 'ascii' as 'ascii' | 'hex',
  waitResponse: false,
})

const statusClass = computed(() => {
  if (!status.value) return 'loading'
  return status.value.running ? 'running' : 'stopped'
})

const clientOptions = computed(() =>
  (clients.value?.clients || []).map((c) => ({ label: c, value: c })),
)

const clientColumns: DataTableColumns<{ remoteAddress: string; macAddress: string }> = [
  { title: '地址', key: 'remoteAddress' },
  { title: 'MAC', key: 'macAddress' },
]

const responseColumns: DataTableColumns<ZintisNettySendResult> = [
  { title: '客户端', key: 'remoteAddress', ellipsis: { tooltip: true } },
  { title: 'MAC', key: 'macAddress' },
  { title: 'IP', key: 'ipAddress' },
  { title: '收到', key: 'received', width: 60, render: (r) => h(NText, { type: r.received ? 'success' : 'error' }, () => r.received ? '是' : '否') },
  { title: '超时', key: 'timeout', width: 60, render: (r) => r.timeout ? '是' : '-' },
  { title: '响应 HEX', key: 'rawResponseHex', ellipsis: { tooltip: true } },
]

async function fetchStatus() {
  status.value = await zintisNettyStatus()
}

async function fetchClients() {
  clients.value = await zintisNettyClients()
}

function refreshAll() {
  fetchStatus()
  fetchClients()
}

async function startServer() {
  opLoading.value = true
  try { status.value = await zintisNettyStart(); fetchClients() } finally { opLoading.value = false }
}

async function stopServer() {
  opLoading.value = true
  try { status.value = await zintisNettyStop(); fetchClients() } finally { opLoading.value = false }
}

async function send() {
  if (!sendForm.remoteAddress) return
  sendLoading.value = true
  sendResult.value = null
  try {
    sendResult.value = await zintisNettySend({
      remoteAddress: sendForm.remoteAddress,
      payload: sendForm.payload || undefined,
      payloadFormat: sendForm.payloadFormat,
      waitResponse: sendForm.waitResponse,
    })
  } finally { sendLoading.value = false }
}

async function broadcast() {
  sendLoading.value = true
  sendResult.value = null
  try {
    sendResult.value = await zintisNettyBroadcast({
      payload: sendForm.payload || undefined,
      payloadFormat: sendForm.payloadFormat,
    })
  } finally { sendLoading.value = false }
}

watch(autoRefresh, (on) => {
  if (refreshTimer) { clearInterval(refreshTimer); refreshTimer = null }
  if (on) { refreshTimer = setInterval(refreshAll, 5000) }
})

onMounted(refreshAll)
onUnmounted(() => { if (refreshTimer) clearInterval(refreshTimer) })
</script>

<style scoped>
.netty-monitor {
  max-width: 1200px;
}

.status-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-radius: 8px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}
.status-hero.loading {
  background: #f5f5f5;
}
.status-hero.running {
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
}
.status-hero.stopped {
  background: linear-gradient(135deg, #fbe9e7 0%, #fff3e0 100%);
}

.status-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.status-title {
  font-size: 16px;
  font-weight: 600;
}
.status-meta {
  margin-top: 4px;
  padding-left: 18px;
  font-size: 13px;
  color: #666;
}

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

.metric-card {
  background: #fafafa !important;
  text-align: center;
}
.metric-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}
.metric-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  font-variant-numeric: tabular-nums;
}

.empty-clients {
  padding: 32px 0;
  text-align: center;
}

.result-summary {
  margin-bottom: 8px;
  font-size: 13px;
}
.text-success { color: #18a058; }
.text-error   { color: #d03050; }
</style>
