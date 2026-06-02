<template>
  <div class="netty-layout">
    <!-- Left column -->
    <div class="netty-left">
      <!-- Status hero -->
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
              <template v-if="status.heartbeatEnabled"> · 心跳 {{ heartbeatSummary }}</template>
            </template>
            <template v-else-if="status">服务未启动</template>
          </div>
        </div>
        <n-space :size="8" align="center">
          <n-text depth="3" style="font-size: 12px">自动刷新</n-text>
          <n-switch v-model:value="autoRefresh" size="small" />
          <n-text depth="3" style="font-size: 12px">心跳</n-text>
          <n-switch
            :value="status?.heartbeatEnabled ?? false"
            :loading="heartbeatLoading"
            size="small"
            @update:value="updateHeartbeat"
          />
          <n-text depth="3" style="font-size: 12px">自动入库</n-text>
          <n-switch
            :value="status?.clientReportRegistrationEnabled ?? true"
            :loading="registrationLoading"
            size="small"
            @update:value="updateClientReportRegistration"
          />
          <n-button size="small" secondary @click="refreshAll">刷新</n-button>
          <n-button size="small" type="success" :disabled="status?.running ?? false" :loading="opLoading" @click="startServer">启动</n-button>
          <n-button size="small" type="error" :disabled="!status?.running" :loading="opLoading" @click="stopServer">停止</n-button>
        </n-space>
      </div>

      <!-- Metrics -->
      <div class="metrics-row">
        <div class="metric-card">
          <div class="metric-label">监听端口</div>
          <div class="metric-value">{{ status?.port ?? '-' }}</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">活跃连接</div>
          <div class="metric-value">{{ status?.activeConnections ?? 0 }}</div>
        </div>
        <div class="metric-card">
          <div class="metric-label">已连接设备</div>
          <div class="metric-value">{{ clients?.totalClients ?? 0 }}</div>
        </div>
      </div>

      <!-- Console row: message + result side by side -->
      <div class="console-row">
        <n-card title="消息控制台" size="small" style="flex: 2">
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
              :rows="3"
              placeholder="输入发送内容"
              size="small"
            />
            <n-space>
              <n-button type="primary" size="small" :loading="sendLoading" :disabled="!sendForm.remoteAddress" @click="send">发送</n-button>
              <n-button type="warning" size="small" :loading="sendLoading" @click="broadcast">广播</n-button>
            </n-space>
          </n-space>
        </n-card>

        <n-card title="发送结果" size="small" style="flex: 2">
          <template #header-extra>
            <n-text v-if="sendResult" depth="3" style="font-size: 13px">
              成功 <span class="text-success">{{ sendResult.successCount }}</span> / {{ sendResult.totalTargets }}
            </n-text>
          </template>
          <div v-if="!sendResult" class="empty-clients">
            <n-text depth="3">暂无发送记录</n-text>
          </div>
          <template v-else>
            <n-data-table
              :columns="responseColumns"
              :data="sendResult.responses"
              :bordered="false"
              size="small"
              :max-height="320"
            />
          </template>
        </n-card>
      </div>
    </div>

    <!-- Right column: Connected devices (full height) -->
    <div class="netty-right">
      <n-card title="连接设备" size="small">
        <template #header-extra>
          <n-text depth="3" style="font-size: 13px">{{ clientRows.length }} 台在线</n-text>
        </template>
        <div class="devices-body">
          <div v-if="!clientRows.length" class="empty-clients">
            <n-text depth="3">暂无已连接设备</n-text>
          </div>
          <n-data-table
            v-else
            class="client-table"
            :columns="clientColumns"
            :data="clientRows"
            :bordered="false"
            size="small"
            :row-key="(row: any) => row.remoteAddress"
          />
        </div>
      </n-card>
    </div>

    <!-- 控灯弹窗 -->
    <n-modal v-model:show="showLight" :title="'控灯 - ' + (lightDevice?.macAddress || '')" preset="card" style="width: 420px">
      <n-grid :cols="4" :x-gap="8" :y-gap="8">
        <n-gi><n-button block type="success" :loading="lightLoading" @click="sendLight('ON', 'GREEN')">开绿灯</n-button></n-gi>
        <n-gi><n-button block type="warning" :loading="lightLoading" @click="sendLight('ON', 'YELLOW')">开黄灯</n-button></n-gi>
        <n-gi><n-button block type="error" :loading="lightLoading" @click="sendLight('ON', 'RED')">开红灯</n-button></n-gi>
        <n-gi><n-button block type="primary" :loading="lightLoading" @click="sendLight('ON', 'ALL')">全部开</n-button></n-gi>
        <n-gi><n-button block type="success" :loading="lightLoading" @click="sendLight('OFF', 'GREEN')">关绿灯</n-button></n-gi>
        <n-gi><n-button block type="warning" :loading="lightLoading" @click="sendLight('OFF', 'YELLOW')">关黄灯</n-button></n-gi>
        <n-gi><n-button block type="error" :loading="lightLoading" @click="sendLight('OFF', 'RED')">关红灯</n-button></n-gi>
        <n-gi><n-button block type="primary" :loading="lightLoading" @click="sendLight('OFF', 'ALL')">全部关</n-button></n-gi>
      </n-grid>
    </n-modal>

    <!-- 高级操作弹窗 -->
    <n-modal
      v-model:show="showAdvanced"
      :title="'高级操作 - ' + (advancedDevice?.macAddress || '')"
      preset="card"
      class="advanced-modal"
      style="width: 880px"
    >
      <n-spin :show="advancedLoading">
        <div class="advanced-layout">
          <aside class="advanced-nav">
            <div class="advanced-device">
              <div class="advanced-device-title">{{ advancedDevice?.macAddress || '-' }}</div>
              <div class="advanced-device-meta">{{ advancedDevice?.remoteAddress || '-' }}</div>
            </div>
            <n-menu v-model:value="activeAdvancedTab" :options="advancedMenuOptions" />
          </aside>

          <section class="advanced-main">
            <div v-if="activeAdvancedTab === 'basic'" class="advanced-section">
              <div class="section-heading">
                <h3>基础参数</h3>
                <p>高级指令通过当前 Netty 客户端连接发送。</p>
              </div>
              <n-grid :cols="2" :x-gap="12">
                <n-form-item label="MAC">
                  <n-input :value="advancedDevice?.macAddress || ''" disabled />
                </n-form-item>
                <n-form-item label="客户端地址">
                  <n-input :value="advancedDevice?.remoteAddress || ''" disabled />
                </n-form-item>
                <n-form-item label="数据命令">
                  <n-input-number v-model:value="advancedForm.dataCommand" :min="1" :max="255" />
                </n-form-item>
                <n-form-item label="超时 (ms)">
                  <n-input-number v-model:value="advancedForm.timeoutMs" :min="100" :max="60000" :step="1000" />
                </n-form-item>
              </n-grid>
            </div>

            <div v-else-if="activeAdvancedTab === 'control'" class="advanced-section">
              <div class="section-heading">
                <h3>控制命令</h3>
                <p>向当前客户端发送开关灯与脉冲控制指令。</p>
              </div>
              <n-grid :cols="3" :x-gap="8" :y-gap="8">
                <n-gi><n-button block type="success" @click="execAdvancedCommand('CONTROL_ON')">开灯</n-button></n-gi>
                <n-gi><n-button block type="error" @click="execAdvancedCommand('CONTROL_OFF')">关灯</n-button></n-gi>
                <n-gi><n-button block type="warning" @click="execAdvancedCommand('CONTROL_PULSE')">脉冲</n-button></n-gi>
              </n-grid>
            </div>

            <div v-else-if="activeAdvancedTab === 'query'" class="advanced-section">
              <div class="section-heading">
                <h3>查询诊断</h3>
                <p>读取设备状态、系统信息、信号强度与网络配置。</p>
              </div>
              <n-grid :cols="4" :x-gap="8" :y-gap="8">
                <n-gi><n-button block @click="execAdvancedCommand('QUERY')">控制状态</n-button></n-gi>
                <n-gi><n-button block @click="execAdvancedCommand('SYSTEM_INFO')">系统信息</n-button></n-gi>
                <n-gi><n-button block @click="execAdvancedCommand('SIGNAL_STRENGTH')">信号强度</n-button></n-gi>
                <n-gi><n-button block @click="execAdvancedCommand('NETWORK')">网络信息</n-button></n-gi>
              </n-grid>
            </div>

            <div v-else-if="activeAdvancedTab === 'tcp'" class="advanced-section">
              <div class="section-heading">
                <h3>TCP 配置</h3>
                <p>切换设备 TCP Server / Client 工作模式。</p>
              </div>
              <n-grid :cols="2" :x-gap="12">
                <n-form-item label="客户端目标 IP">
                  <n-input v-model:value="advancedTcp.targetIp" placeholder="192.168.2.100" />
                </n-form-item>
                <n-form-item label="客户端目标端口">
                  <n-input-number v-model:value="advancedTcp.targetPort" :min="1" :max="65535" />
                </n-form-item>
              </n-grid>
              <n-grid :cols="4" :x-gap="8" :y-gap="8">
                <n-gi><n-button block @click="execAdvancedCommand('TCP_SERVER_OPEN')">开 Server</n-button></n-gi>
                <n-gi><n-button block @click="execAdvancedCommand('TCP_SERVER_CLOSE')">关 Server</n-button></n-gi>
                <n-gi><n-button block type="primary" @click="openAdvancedTcpClient">开 Client</n-button></n-gi>
                <n-gi><n-button block @click="execAdvancedCommand('TCP_CLIENT_CLOSE')">关 Client</n-button></n-gi>
              </n-grid>
            </div>

            <div v-else class="advanced-section">
              <div class="section-heading">
                <h3>OTA 升级</h3>
                <p>版本文件名将按 ASCII 字节下发。</p>
              </div>
              <n-form-item label="版本文件名">
                <n-input v-model:value="advancedOta.version" placeholder="1.0.7.bin" />
              </n-form-item>
              <n-button type="primary" @click="execAdvancedCommand('OTA_UPDATE', { version: advancedOta.version })">发送 OTA 升级</n-button>
            </div>

            <div class="advanced-result">
              <div class="result-title">执行结果</div>
              <template v-if="advancedResult">
                <n-flex align="center" justify="space-between" :wrap="false">
                  <n-alert
                    class="result-alert"
                    :type="advancedResult.success ? 'success' : 'error'"
                    :show-icon="false"
                  >
                    <n-ellipsis>{{ (advancedResult as any).payloadAscii || '-' }}</n-ellipsis>
                  </n-alert>
                  <n-popover trigger="click" placement="top-end" style="max-width: 640px">
                    <template #trigger>
                      <n-button size="small">详细信息</n-button>
                    </template>
                    <n-descriptions :column="2" bordered size="small">
                      <n-descriptions-item label="成功">
                        <n-text :type="advancedResult.success ? 'success' : 'error'">{{ advancedResult.success ? '是' : '否' }}</n-text>
                      </n-descriptions-item>
                      <n-descriptions-item label="消息">{{ (advancedResult as any).message || '' }}</n-descriptions-item>
                      <n-descriptions-item v-if="(advancedResult as any).errorCode" label="错误码">
                        <n-text type="error">{{ (advancedResult as any).errorCode }}</n-text>
                      </n-descriptions-item>
                      <n-descriptions-item v-if="(advancedResult as any).payloadHex" label="Payload HEX" :span="2">
                        <n-ellipsis style="max-width: 560px">{{ (advancedResult as any).payloadHex }}</n-ellipsis>
                      </n-descriptions-item>
                      <n-descriptions-item label="请求 HEX" :span="2">
                        <n-ellipsis style="max-width: 560px">{{ (advancedResult as any).rawRequestHex || '' }}</n-ellipsis>
                      </n-descriptions-item>
                      <n-descriptions-item label="响应 HEX" :span="2">
                        <n-ellipsis style="max-width: 560px">{{ (advancedResult as any).rawResponseHex || '' }}</n-ellipsis>
                      </n-descriptions-item>
                    </n-descriptions>
                  </n-popover>
                </n-flex>
              </template>
              <n-text v-else depth="3">执行命令后查看结果</n-text>
            </div>
          </section>
        </div>
      </n-spin>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onUnmounted, h } from 'vue'
import {
  NCard, NSpace, NButton, NText,
  NDataTable, NFormItem, NInput, NSelect,
  NRadioGroup, NRadioButton, NSwitch, NTag, NSpin,
  NModal, NGrid, NGi, NInputNumber, NMenu, NAlert, NEllipsis,
  NPopover, NDescriptions, NDescriptionsItem, NFlex,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import {
  zintisNettyStart, zintisNettyStop, zintisNettyStatus, zintisNettyClients,
  zintisNettySend, zintisNettyBroadcast, zintisNettyHeartbeat, zintisNettyClientReportRegistration,
} from '@/api/zintis-netty'
import { controlLedDevice } from '@/api/led-device'
import type {
  ZintisNettyServerStatus, ZintisNettyClientList,
  ZintisNettySendResponse, ZintisNettySendResult,
  LedControlCommand, LedControlResult,
} from '@/types'

const opLoading = ref(false)
const sendLoading = ref(false)
const heartbeatLoading = ref(false)
const registrationLoading = ref(false)
const status = ref<ZintisNettyServerStatus | null>(null)
const clients = ref<ZintisNettyClientList | null>(null)
const sendResult = ref<ZintisNettySendResponse | null>(null)
const autoRefresh = ref(false)
const showLight = ref(false)
const showAdvanced = ref(false)
const lightLoading = ref(false)
const advancedLoading = ref(false)
const lightDevice = ref<{ remoteAddress: string; macAddress: string } | null>(null)
const advancedDevice = ref<NettyClientRow | null>(null)
const advancedResult = ref<LedControlResult | null>(null)
const activeAdvancedTab = ref('basic')
let refreshTimer: ReturnType<typeof setInterval> | null = null

const sendForm = reactive({
  remoteAddress: undefined,
  payload: '',
  payloadFormat: 'ascii' as 'ascii' | 'hex',
  waitResponse: false,
})

const advancedForm = reactive({
  dataCommand: 1,
  timeoutMs: 3000,
})

const advancedTcp = reactive({
  targetIp: '',
  targetPort: 9834,
})

const advancedOta = reactive({
  version: '1.0.7.bin',
})

const advancedMenuOptions = [
  { label: '基础参数', key: 'basic' },
  { label: '控制命令', key: 'control' },
  { label: '查询诊断', key: 'query' },
  { label: 'TCP 配置', key: 'tcp' },
  { label: 'OTA 升级', key: 'ota' },
]

const statusClass = computed(() => {
  if (!status.value) return 'loading'
  return status.value.running ? 'running' : 'stopped'
})

const heartbeatSummary = computed(() => {
  if (!status.value?.lastHeartbeatAt) return '等待发送'
  const time = new Date(status.value.lastHeartbeatAt).toLocaleTimeString()
  return `${time} 成功 ${status.value.lastHeartbeatSuccessCount}/${status.value.lastHeartbeatTargets}`
})

const clientOptions = computed(() =>
  clientRows.value.map((client) => ({ label: client.remoteAddress, value: client.remoteAddress })),
)

const selectedSendClient = computed(() =>
  clientRows.value.find((client) => client.remoteAddress === sendForm.remoteAddress),
)

type NettyClientRow = {
  remoteAddress: string
  macAddress: string
}

const clientRows = computed<NettyClientRow[]>(() => {
  const details = clients.value?.clientDetails ?? []
  if (details.length) {
    return details.map(normalizeClientRow).filter((client) => client.remoteAddress)
  }
  return (clients.value?.clients ?? []).map((remoteAddress) => ({
    remoteAddress,
    macAddress: '',
  }))
})

function normalizeClientRow(client: unknown): NettyClientRow {
  if (typeof client === 'string') {
    return { remoteAddress: client, macAddress: '' }
  }
  if (!client || typeof client !== 'object') {
    return { remoteAddress: '', macAddress: '' }
  }
  const record = client as Record<string, unknown>
  return {
    remoteAddress: String(record.remoteAddress ?? record.address ?? record.remote ?? record.client ?? ''),
    macAddress: String(record.macAddress ?? record.mac ?? ''),
  }
}

const clientColumns: DataTableColumns<NettyClientRow> = [
  { title: '地址', key: 'remoteAddress' },
  { title: 'MAC', key: 'macAddress' },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    render: (row) =>
      h(NSpace, { size: 6 }, () => [
        h(NButton, { size: 'small', type: 'primary', onClick: () => openLight(row) }, () => '控灯'),
        h(NButton, { size: 'small', type: 'warning', onClick: () => openAdvanced(row) }, () => '高级'),
      ]),
  },
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

function openLight(row: { remoteAddress: string; macAddress: string }) {
  lightDevice.value = row
  showLight.value = true
}

function openAdvanced(row: NettyClientRow) {
  advancedDevice.value = row
  advancedResult.value = null
  activeAdvancedTab.value = 'basic'
  advancedForm.dataCommand = 1
  advancedForm.timeoutMs = 3000
  showAdvanced.value = true
}

async function sendLight(command: 'ON' | 'OFF', port: 'ALL' | 'RED' | 'YELLOW' | 'GREEN') {
  if (!lightDevice.value) return
  lightLoading.value = true
  try {
    await controlLedDevice({ mode: 'client', ledId: lightDevice.value.macAddress, command, port })
  } finally {
    lightLoading.value = false
  }
}

async function execAdvancedCommand(command: LedControlCommand['command'], extra: Partial<LedControlCommand> = {}) {
  if (!advancedDevice.value) return
  advancedLoading.value = true
  advancedResult.value = null
  try {
    advancedResult.value = await controlLedDevice({
      mode: 'client',
      ledId: advancedDevice.value.macAddress,
      command,
      dataCommand: advancedForm.dataCommand,
      timeoutMs: advancedForm.timeoutMs,
      ...extra,
    })
  } finally {
    advancedLoading.value = false
  }
}

async function openAdvancedTcpClient() {
  await execAdvancedCommand('TCP_CLIENT_OPEN', {
    targetIp: advancedTcp.targetIp,
    targetPort: advancedTcp.targetPort,
  })
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

async function updateHeartbeat(enabled: boolean) {
  heartbeatLoading.value = true
  try {
    status.value = await zintisNettyHeartbeat(enabled)
  } finally {
    heartbeatLoading.value = false
  }
}

async function updateClientReportRegistration(enabled: boolean) {
  registrationLoading.value = true
  try {
    status.value = await zintisNettyClientReportRegistration(enabled)
  } finally {
    registrationLoading.value = false
  }
}

async function send() {
  if (!sendForm.remoteAddress) return
  sendLoading.value = true
  sendResult.value = null
  try {
    sendResult.value = await zintisNettySend({
      remoteAddress: sendForm.remoteAddress,
      macAddress: selectedSendClient.value?.macAddress || undefined,
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
.netty-layout {
  display: flex;
  height: 100%;
  min-height: 0;
  gap: 16px;
}

.netty-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.netty-right {
  width: 520px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* Make devices card fill the full column height */
.netty-right :deep(.n-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.netty-right :deep(.n-card__content) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.devices-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
}
.client-table {
  min-width: 100%;
}

/* Status hero */
.status-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.empty-clients { padding: 32px 0; text-align: center; }
.text-success { color: #18a058; }
.text-error   { color: #d03050; }

.advanced-modal :deep(.n-card__content) {
  padding: 0;
}

.advanced-layout {
  display: grid;
  grid-template-columns: 190px minmax(0, 1fr);
  min-height: 560px;
}

.advanced-nav {
  padding: 18px 12px;
  border-right: 1px solid #e5e7eb;
  background: #fafafa;
}

.advanced-device {
  padding: 0 10px 14px;
  margin-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
}

.advanced-device-title {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  word-break: break-all;
}

.advanced-device-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.advanced-main {
  min-width: 0;
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.advanced-section {
  min-height: 180px;
}

.section-heading {
  margin-bottom: 18px;
}

.section-heading h3 {
  margin: 0;
  font-size: 18px;
  line-height: 1.35;
}

.section-heading p {
  margin: 4px 0 0;
  color: #6b7280;
  font-size: 13px;
}

.advanced-result {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  max-height: 240px;
  overflow: auto;
}

.result-title {
  margin-bottom: 10px;
  font-weight: 600;
  color: #111827;
}

.result-alert {
  flex: 1;
  min-width: 0;
}

.result-alert :deep(.n-alert-body__content) {
  min-width: 0;
  max-width: 420px;
}

@media (max-width: 900px) {
  .advanced-layout {
    grid-template-columns: 1fr;
  }

  .advanced-nav {
    border-right: 0;
    border-bottom: 1px solid #e5e7eb;
  }
}
</style>
