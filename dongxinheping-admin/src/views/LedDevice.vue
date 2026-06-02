<template>
  <n-card
    title="设备管理"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-space>
        <n-button type="primary" @click="openCreate">新增设备</n-button>
        <n-button type="primary" @click="handleSearch">查询</n-button>
      </n-space>
    </template>

    <div class="table-page-content">
      <n-space :size="12" class="table-page-toolbar">
        <n-input v-model:value="query.macAddress" placeholder="MAC 地址" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <n-input v-model:value="query.ip" placeholder="IP 地址" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-input v-model:value="query.remark" placeholder="备注" clearable style="width: 160px" @keyup.enter="handleSearch" />
      </n-space>

      <div ref="tableAreaRef" class="table-page-table led-device-table-area">
        <n-data-table
          :columns="columns"
          :data="devices.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: LedDevice) => row.macAddress"
          :scroll-x="620"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="devices.total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <!-- 新增弹窗 -->
    <n-modal v-model:show="showCreate" title="新增 LED 设备" preset="dialog">
      <n-spin :show="submitting">
        <n-space vertical>
          <n-form-item label="MAC 地址" required>
            <n-input v-model:value="form.macAddress" placeholder="3A:69:7A:08:D0:A5" />
          </n-form-item>
          <n-form-item label="IP 地址" required>
            <n-input v-model:value="form.ip" placeholder="192.168.2.102" />
          </n-form-item>
          <n-form-item label="备注">
            <n-input v-model:value="form.remark" placeholder="可选" />
          </n-form-item>
          <n-flex justify="end">
            <n-button @click="showCreate = false">取消</n-button>
            <n-button type="primary" @click="handleCreate">确认</n-button>
          </n-flex>
        </n-space>
      </n-spin>
    </n-modal>

    <!-- 编辑弹窗 -->
    <n-modal v-model:show="showEdit" title="编辑 LED 设备" preset="dialog">
      <n-spin :show="submitting">
        <n-space vertical>
          <n-form-item label="MAC 地址">
            <n-input :value="editForm.macAddress" disabled />
          </n-form-item>
          <n-form-item label="IP 地址" required>
            <n-input v-model:value="editForm.ip" />
          </n-form-item>
          <n-form-item label="备注">
            <n-input v-model:value="editForm.remark" />
          </n-form-item>
          <n-flex justify="end">
            <n-button @click="showEdit = false">取消</n-button>
            <n-button type="primary" @click="handleEdit">保存</n-button>
          </n-flex>
        </n-space>
      </n-spin>
    </n-modal>

    <!-- 删除确认 -->
    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <n-spin :show="submitting">
        <n-space vertical>
          <span>确定要删除设备 <b>{{ deleteTarget?.macAddress }}</b>（{{ deleteTarget?.ip }}）吗？</span>
          <n-flex justify="end">
            <n-button @click="showDelete = false">取消</n-button>
            <n-button type="error" @click="handleDelete">删除</n-button>
          </n-flex>
        </n-space>
      </n-spin>
    </n-modal>

    <!-- 控灯弹窗 -->
    <n-modal v-model:show="showLight" :title="'控灯 - ' + (lightDevice?.macAddress || '') + '（' + (lightDevice?.ip || '') + '）'" preset="card" style="width: 420px">
      <n-spin :show="lightLoading">
        <n-grid :cols="4" :x-gap="8" :y-gap="8">
          <n-gi><n-button block type="success" @click="sendLight('ON', 'GREEN')">开绿灯</n-button></n-gi>
          <n-gi><n-button block type="warning" @click="sendLight('ON', 'YELLOW')">开黄灯</n-button></n-gi>
          <n-gi><n-button block type="error" @click="sendLight('ON', 'RED')">开红灯</n-button></n-gi>
          <n-gi><n-button block type="primary" @click="sendLight('ON', 'ALL')">全部开</n-button></n-gi>
          <n-gi><n-button block type="success" @click="sendLight('OFF', 'GREEN')">关绿灯</n-button></n-gi>
          <n-gi><n-button block type="warning" @click="sendLight('OFF', 'YELLOW')">关黄灯</n-button></n-gi>
          <n-gi><n-button block type="error" @click="sendLight('OFF', 'RED')">关红灯</n-button></n-gi>
          <n-gi><n-button block type="primary" @click="sendLight('OFF', 'ALL')">全部关</n-button></n-gi>
        </n-grid>
      </n-spin>
    </n-modal>

    <!-- 高级控制弹窗 -->
    <n-modal
      v-model:show="showControl"
      :title="'高级操作 - ' + (controlDevice?.macAddress || '')"
      preset="card"
      class="advanced-modal"
      style="width: 880px"
    >
      <n-spin :show="ctrlLoading">
      <div class="advanced-layout">
        <aside class="advanced-nav">
          <div class="advanced-device">
            <div class="advanced-device-title">{{ controlDevice?.macAddress || '-' }}</div>
            <div class="advanced-device-meta">{{ controlDevice?.ip || '-' }}</div>
          </div>
          <n-menu v-model:value="activeControlTab" :options="advancedMenuOptions" />
        </aside>

        <section class="advanced-main">
          <div v-if="activeControlTab === 'basic'" class="advanced-section">
            <div class="section-heading">
              <h3>基础参数</h3>
              <p>这些参数会用于右侧所有高级指令。</p>
            </div>
            <n-grid :cols="2" :x-gap="12">
              <n-form-item label="设备 IP">
                <n-input v-model:value="ctrl.deviceIp" />
              </n-form-item>
              <n-form-item label="端口">
                <n-input-number v-model:value="ctrl.devicePort" :min="1" :max="65535" />
              </n-form-item>
              <n-form-item label="主机地址">
                <n-input-number v-model:value="ctrl.hostAddress" :min="0" :max="255" />
              </n-form-item>
              <n-form-item label="超时 (ms)">
                <n-input-number v-model:value="ctrl.timeoutMs" :min="100" :max="60000" :step="1000" />
              </n-form-item>
              <n-form-item label="数据命令">
                <n-input-number v-model:value="ctrl.dataCommand" :min="1" :max="255" />
              </n-form-item>
            </n-grid>
          </div>

          <div v-else-if="activeControlTab === 'control'" class="advanced-section">
            <div class="section-heading">
              <h3>控制命令</h3>
              <p>向当前设备发送开关灯与脉冲控制指令。</p>
            </div>
            <n-grid :cols="3" :x-gap="8" :y-gap="8">
              <n-gi><n-button block type="success" @click="execClientCommand('CONTROL_ON')">开灯</n-button></n-gi>
              <n-gi><n-button block type="error" @click="execClientCommand('CONTROL_OFF')">关灯</n-button></n-gi>
              <n-gi><n-button block type="warning" @click="execClientCommand('CONTROL_PULSE')">脉冲</n-button></n-gi>
            </n-grid>
          </div>

          <div v-else-if="activeControlTab === 'query'" class="advanced-section">
            <div class="section-heading">
              <h3>查询诊断</h3>
              <p>读取设备状态、系统信息、信号强度与网络配置。</p>
            </div>
            <n-grid :cols="4" :x-gap="8" :y-gap="8">
              <n-gi><n-button block @click="execClientCommand('QUERY')">控制状态</n-button></n-gi>
              <n-gi><n-button block @click="execClientCommand('SYSTEM_INFO')">系统信息</n-button></n-gi>
              <n-gi><n-button block @click="execClientCommand('SIGNAL_STRENGTH')">信号强度</n-button></n-gi>
              <n-gi><n-button block @click="execClientCommand('NETWORK')">网络信息</n-button></n-gi>
            </n-grid>
          </div>

          <div v-else-if="activeControlTab === 'tcp'" class="advanced-section">
            <div class="section-heading">
              <h3>TCP 配置</h3>
              <p>切换设备 TCP Server / Client 工作模式。</p>
            </div>
            <n-grid :cols="2" :x-gap="12">
              <n-form-item label="客户端目标 IP">
                <n-input v-model:value="tcpTarget.targetIp" placeholder="192.168.2.100" />
              </n-form-item>
              <n-form-item label="客户端目标端口">
                <n-input-number v-model:value="tcpTarget.targetPort" :min="1" :max="65535" />
              </n-form-item>
            </n-grid>
            <n-grid :cols="4" :x-gap="8" :y-gap="8">
              <n-gi><n-button block @click="execClientCommand('TCP_SERVER_OPEN')">开 Server</n-button></n-gi>
              <n-gi><n-button block @click="execClientCommand('TCP_SERVER_CLOSE')">关 Server</n-button></n-gi>
              <n-gi><n-button block type="primary" @click="openTcpClient">开 Client</n-button></n-gi>
              <n-gi><n-button block @click="execClientCommand('TCP_CLIENT_CLOSE')">关 Client</n-button></n-gi>
            </n-grid>
          </div>

          <div v-else class="advanced-section">
            <div class="section-heading">
              <h3>OTA 升级</h3>
              <p>版本文件名将按 ASCII 字节下发，设备侧负责校验与升级。</p>
            </div>
            <n-form-item label="版本文件名">
              <n-input v-model:value="ota.version" placeholder="1.0.7.bin" />
            </n-form-item>
            <n-button type="primary" @click="execOta">发送 OTA 升级</n-button>
          </div>

          <div class="advanced-result">
            <div class="result-title">执行结果</div>
            <template v-if="ctrlResult">
              <n-flex align="center" justify="space-between" :wrap="false">
                <n-alert
                  :title="ctrlResult.success ? '成功' : '失败'"
                  class="result-alert"
                  :type="ctrlResult.success ? 'success' : 'error'"
                  show-icon
                >
                  <n-ellipsis>{{ (ctrlResult as any).payloadAscii || '-' }}</n-ellipsis>
                </n-alert>
                <n-popover trigger="click" placement="top-end" style="max-width: 640px">
                  <template #trigger>
                    <n-button size="small">详细信息</n-button>
                  </template>
                  <n-descriptions :column="2" bordered size="small">
                    <n-descriptions-item label="成功">
                      <n-text :type="ctrlResult.success ? 'success' : 'error'">{{ ctrlResult.success ? '是' : '否' }}</n-text>
                    </n-descriptions-item>
                    <n-descriptions-item label="消息">{{ (ctrlResult as any).message || '' }}</n-descriptions-item>
                    <n-descriptions-item v-if="(ctrlResult as any).errorCode" label="错误码">
                      <n-text type="error">{{ (ctrlResult as any).errorCode }}</n-text>
                    </n-descriptions-item>
                    <n-descriptions-item v-if="(ctrlResult as any).signalStrengthDbm != null" label="信号 (dBm)">
                      {{ (ctrlResult as any).signalStrengthDbm }}
                    </n-descriptions-item>
                    <n-descriptions-item v-if="(ctrlResult as any).systemIp" label="设备 IP">
                      {{ (ctrlResult as any).systemIp }}
                    </n-descriptions-item>
                    <n-descriptions-item v-if="(ctrlResult as any).systemMac" label="设备 MAC">
                      {{ (ctrlResult as any).systemMac }}
                    </n-descriptions-item>
                    <n-descriptions-item v-if="(ctrlResult as any).payloadHex" label="Payload HEX" :span="2">
                      <n-ellipsis style="max-width: 560px">{{ (ctrlResult as any).payloadHex }}</n-ellipsis>
                    </n-descriptions-item>
                    <n-descriptions-item label="请求 HEX" :span="2">
                      <n-ellipsis style="max-width: 560px">{{ (ctrlResult as any).rawRequestHex || '' }}</n-ellipsis>
                    </n-descriptions-item>
                    <n-descriptions-item label="响应 HEX" :span="2">
                      <n-ellipsis style="max-width: 560px">{{ (ctrlResult as any).rawResponseHex || '' }}</n-ellipsis>
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
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NInput, NInputNumber,
  NFlex, NPagination, NDescriptions, NDescriptionsItem, NText, NEllipsis, NGrid, NGi, NMenu, NSpin, NPopover, NAlert,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import {
  getLedDeviceList, createLedDevice, updateLedDevice, deleteLedDevice, controlLedDevice,
} from '@/api/led-device'
import type { LedDevice, LedControlCommand, ZintisLedControlRequest } from '@/types'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const loading = ref(false)
const submitting = ref(false)
const ctrlLoading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const showCreate = ref(false)
const showEdit = ref(false)
const showDelete = ref(false)
const showControl = ref(false)
const showLight = ref(false)
const activeControlTab = ref('basic')
const deleteTarget = ref<LedDevice | null>(null)
const controlDevice = ref<LedDevice | null>(null)
const lightDevice = ref<LedDevice | null>(null)
const ctrlResult = ref<Record<string, any> | null>(null)
const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()

const query = reactive({ macAddress: '', ip: '', remark: '' })
const form = reactive({ macAddress: '', ip: '', remark: '' })
const editForm = reactive({ macAddress: '', ip: '', remark: '' })

const ctrl = reactive<ZintisLedControlRequest>({
  deviceIp: '',
  devicePort: 9527,
  hostAddress: 0,
  dataCommand: 1,
  timeoutMs: 3000,
})

const advancedMenuOptions = [
  { label: '基础参数', key: 'basic' },
  { label: '控制命令', key: 'control' },
  { label: '查询诊断', key: 'query' },
  { label: 'TCP 配置', key: 'tcp' },
  { label: 'OTA 升级', key: 'ota' },
]

const tcpTarget = reactive({ targetIp: '', targetPort: 9834 })
const ota = reactive({ version: '1.0.7.bin' })

const devices = reactive<{ list: LedDevice[]; total: number }>({ list: [], total: 0 })

const columns: DataTableColumns<LedDevice> = [
  { title: 'MAC 地址', key: 'macAddress', width: 180 },
  { title: 'IP 地址', key: 'ip', width: 160 },
  { title: '备注', key: 'remark', width: 240 },
  {
    title: '操作',
    key: 'actions',
    width: 240,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NButton, { size: 'small', type: 'primary', onClick: () => openLight(row) }, () => '控灯'),
        h(NButton, { size: 'small', type: 'warning', onClick: () => openControl(row) }, () => '高级'),
        h(NButton, { size: 'small', onClick: () => openEdit(row) }, () => '编辑'),
        h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '删除'),
      ]),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getLedDeviceList({
      page: page.value,
      pageSize: pageSize.value,
      macAddress: query.macAddress || undefined,
      ip: query.ip || undefined,
      remark: query.remark || undefined,
    })
    devices.list = res.list
    devices.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

// CRUD
function openCreate() {
  form.macAddress = ''
  form.ip = ''
  form.remark = ''
  showCreate.value = true
}

async function handleCreate() {
  if (!form.macAddress || !form.ip) return
  submitting.value = true
  try {
    await createLedDevice({ macAddress: form.macAddress, ip: form.ip, remark: form.remark || undefined })
    showCreate.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

function openEdit(row: LedDevice) {
  editForm.macAddress = row.macAddress
  editForm.ip = row.ip
  editForm.remark = row.remark
  showEdit.value = true
}

async function handleEdit() {
  submitting.value = true
  try {
    await updateLedDevice({ macAddress: editForm.macAddress, ip: editForm.ip, remark: editForm.remark })
    showEdit.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

function openDelete(row: LedDevice) {
  deleteTarget.value = row
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  submitting.value = true
  try {
    await deleteLedDevice(deleteTarget.value.macAddress)
    showDelete.value = false
    deleteTarget.value = null
    await fetchData()
  } finally {
    submitting.value = false
  }
}

// 控灯弹窗
const lightLoading = ref(false)

function openLight(row: LedDevice) {
  lightDevice.value = row
  showLight.value = true
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

// 高级控制
function openControl(row: LedDevice) {
  controlDevice.value = row
  ctrlResult.value = null
  activeControlTab.value = 'basic'
  ctrl.deviceIp = row.ip
  ctrl.devicePort = 9527
  const lastDot = row.ip.lastIndexOf('.')
  ctrl.hostAddress = lastDot >= 0 ? parseInt(row.ip.substring(lastDot + 1)) || 0 : 0
  ctrl.dataCommand = 1
  ctrl.timeoutMs = 3000
  showControl.value = true
}

async function execClientCommand(command: LedControlCommand['command'], extra: Partial<LedControlCommand> = {}) {
  if (!controlDevice.value) return
  ctrlLoading.value = true
  ctrlResult.value = null
  try {
    ctrlResult.value = await controlLedDevice({
      mode: 'client',
      ledId: controlDevice.value.macAddress,
      command,
      dataCommand: ctrl.dataCommand,
      timeoutMs: ctrl.timeoutMs,
      ...extra,
    })
  } finally {
    ctrlLoading.value = false
  }
}

async function openTcpClient() {
  await execClientCommand('TCP_CLIENT_OPEN', {
    targetIp: tcpTarget.targetIp,
    targetPort: tcpTarget.targetPort,
  })
}

async function execOta() {
  await execClientCommand('OTA_UPDATE', {
    version: ota.version,
  })
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

.led-device-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}

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
