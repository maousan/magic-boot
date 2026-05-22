<template>
  <n-card title="设备管理">
    <template #header-extra>
      <n-space>
        <n-button type="primary" @click="openCreate">新增设备</n-button>
        <n-button type="primary" @click="handleSearch">查询</n-button>
      </n-space>
    </template>

    <n-space :size="12" style="margin-bottom: 16px">
      <n-input v-model:value="query.macAddress" placeholder="MAC 地址" clearable style="width: 200px" @keyup.enter="handleSearch" />
      <n-input v-model:value="query.ip" placeholder="IP 地址" clearable style="width: 160px" @keyup.enter="handleSearch" />
      <n-input v-model:value="query.remark" placeholder="备注" clearable style="width: 160px" @keyup.enter="handleSearch" />
    </n-space>

    <n-data-table
      :columns="columns"
      :data="devices.list"
      :loading="loading"
      :bordered="true"
      :row-key="(row: LedDevice) => row.macAddress"
    />

    <n-flex justify="end" style="margin-top: 12px">
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

    <!-- 新增弹窗 -->
    <n-modal v-model:show="showCreate" title="新增 LED 设备" preset="dialog">
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
      </n-space>
      <template #action>
        <n-button @click="showCreate = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleCreate">确认</n-button>
      </template>
    </n-modal>

    <!-- 编辑弹窗 -->
    <n-modal v-model:show="showEdit" title="编辑 LED 设备" preset="dialog">
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
      </n-space>
      <template #action>
        <n-button @click="showEdit = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleEdit">保存</n-button>
      </template>
    </n-modal>

    <!-- 删除确认 -->
    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <span>确定要删除设备 <b>{{ deleteTarget?.macAddress }}</b>（{{ deleteTarget?.ip }}）吗？</span>
      <template #action>
        <n-button @click="showDelete = false">取消</n-button>
        <n-button type="error" :loading="submitting" @click="handleDelete">删除</n-button>
      </template>
    </n-modal>

    <!-- 高级控制弹窗 -->
    <n-modal v-model:show="showControl" :title="'设备控制 - ' + (controlDevice?.macAddress || '')" preset="card" style="width: 600px">
      <n-space vertical>
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
        </n-grid>

        <n-divider style="margin: 8px 0">控制命令</n-divider>
        <n-grid :cols="3" :x-gap="8" :y-gap="8">
          <n-gi><n-button block type="success" :loading="ctrlLoading" @click="execControl(zintisControlOn, '开灯')">开灯</n-button></n-gi>
          <n-gi><n-button block type="error" :loading="ctrlLoading" @click="execControl(zintisControlOff, '关灯')">关灯</n-button></n-gi>
          <n-gi><n-button block type="warning" :loading="ctrlLoading" @click="execControl(zintisControlPulse, '脉冲')">脉冲</n-button></n-gi>
        </n-grid>

        <n-divider style="margin: 8px 0">查询命令</n-divider>
        <n-grid :cols="4" :x-gap="8" :y-gap="8">
          <n-gi><n-button block :loading="ctrlLoading" @click="execControl(zintisControlQuery, '控制状态')">控制状态</n-button></n-gi>
          <n-gi><n-button block :loading="ctrlLoading" @click="execControl(zintisSystemInfo, '系统信息')">系统信息</n-button></n-gi>
          <n-gi><n-button block :loading="ctrlLoading" @click="execControl(zintisSignalStrength, '信号强度')">信号强度</n-button></n-gi>
          <n-gi><n-button block :loading="ctrlLoading" @click="execControl(zintisNetwork, '网络信息')">网络信息</n-button></n-gi>
        </n-grid>
      </n-space>

      <n-card title="执行结果" size="small" style="margin-top: 16px; max-height: 200px; overflow-y: auto">
        <template v-if="ctrlResult">
          <n-descriptions :column="2" bordered>
            <n-descriptions-item label="成功">
              <n-text :type="ctrlResult.success ? 'success' : 'error'">{{ ctrlResult.success ? '是' : '否' }}</n-text>
            </n-descriptions-item>
            <n-descriptions-item label="消息">{{ (ctrlResult as any).message || '' }}</n-descriptions-item>
            <n-descriptions-item v-if="(ctrlResult as any).payloadAscii" label="设备名称">
              {{ (ctrlResult as any).payloadAscii }}
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
            <n-descriptions-item label="请求 HEX" :span="2">
              <n-ellipsis style="max-width: 500px">{{ (ctrlResult as any).rawRequestHex || '' }}</n-ellipsis>
            </n-descriptions-item>
            <n-descriptions-item label="响应 HEX" :span="2">
              <n-ellipsis style="max-width: 500px">{{ (ctrlResult as any).rawResponseHex || '' }}</n-ellipsis>
            </n-descriptions-item>
          </n-descriptions>
        </template>
        <n-text v-else depth="3">执行命令后查看结果</n-text>
      </n-card>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NInput, NInputNumber,
  NFlex, NPagination, NDivider, NDescriptions, NDescriptionsItem, NText, NEllipsis, NGrid, NGi, NDropdown,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import {
  getLedDeviceList, createLedDevice, updateLedDevice, deleteLedDevice, controlLedDevice,
} from '@/api/led-device'
import {
  zintisControlOn, zintisControlOff, zintisControlPulse,
  zintisControlQuery, zintisSystemInfo, zintisSignalStrength, zintisNetwork,
} from '@/api/zintis-led'
import type { LedDevice, ZintisLedControlRequest } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const ctrlLoading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const showCreate = ref(false)
const showEdit = ref(false)
const showDelete = ref(false)
const showControl = ref(false)
const deleteTarget = ref<LedDevice | null>(null)
const controlDevice = ref<LedDevice | null>(null)
const ctrlResult = ref<Record<string, any> | null>(null)

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

const devices = reactive<{ list: LedDevice[]; total: number }>({ list: [], total: 0 })

const lightOptions = [
  { label: '开灯（绿灯）', key: 'on-green' },
  { label: '开灯（黄灯）', key: 'on-yellow' },
  { label: '开灯（红灯）', key: 'on-red' },
  { label: '开灯（全部）', key: 'on-all' },
  { label: '关灯（全部）', key: 'off-all' },
]

const columns: DataTableColumns<LedDevice> = [
  { title: 'MAC 地址', key: 'macAddress', width: 180 },
  { title: 'IP 地址', key: 'ip', width: 160 },
  { title: '备注', key: 'remark' },
  {
    title: '操作',
    key: 'actions',
    width: 240,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NDropdown, {
          options: lightOptions,
          onSelect: (key: string) => handleLightCommand(row, key),
        }, () => h(NButton, { size: 'small', type: 'primary' }, () => '控灯')),
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

// 快捷控灯
async function handleLightCommand(row: LedDevice, key: string) {
  const map: Record<string, { command: 'ON' | 'OFF'; port: 'ALL' | 'RED' | 'YELLOW' | 'GREEN' }> = {
    'on-green': { command: 'ON', port: 'GREEN' },
    'on-yellow': { command: 'ON', port: 'YELLOW' },
    'on-red': { command: 'ON', port: 'RED' },
    'on-all': { command: 'ON', port: 'ALL' },
    'off-all': { command: 'OFF', port: 'ALL' },
  }
  const cfg = map[key]
  if (!cfg) return
  try {
    await controlLedDevice({ mode: 'client', ledId: row.macAddress, command: cfg.command, port: cfg.port })
    await fetchData()
  } catch {}
}

// 高级控制
function openControl(row: LedDevice) {
  controlDevice.value = row
  ctrlResult.value = null
  ctrl.deviceIp = row.ip
  ctrl.devicePort = 9527
  const lastDot = row.ip.lastIndexOf('.')
  ctrl.hostAddress = lastDot >= 0 ? parseInt(row.ip.substring(lastDot + 1)) || 0 : 0
  ctrl.dataCommand = 1
  ctrl.timeoutMs = 3000
  showControl.value = true
}

async function execControl(fn: (d: ZintisLedControlRequest) => Promise<any>, _label: string) {
  ctrlLoading.value = true
  ctrlResult.value = null
  try {
    ctrlResult.value = await fn({ ...ctrl })
  } finally {
    ctrlLoading.value = false
  }
}

onMounted(fetchData)
</script>
