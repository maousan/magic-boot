<template>
  <n-card title="LAN 设备扫描">
    <n-grid :cols="3" :x-gap="12">
      <n-form-item label="子网前缀">
        <n-input v-model:value="form.subnetPrefix" placeholder="留空自动检测" />
      </n-form-item>
      <n-form-item label="设备端口">
        <n-input-number v-model:value="form.devicePort" :min="1" :max="65535" />
      </n-form-item>
      <n-form-item label="扫描范围">
        <n-space align="center">
          <n-input-number v-model:value="form.startHost" :min="1" :max="254" style="width: 80px" />
          <span>~</span>
          <n-input-number v-model:value="form.endHost" :min="1" :max="254" style="width: 80px" />
        </n-space>
      </n-form-item>
      <n-form-item label="并发数">
        <n-input-number v-model:value="form.threadPoolSize" :min="1" :max="128" />
      </n-form-item>
      <n-form-item label="超时 (ms)">
        <n-input-number v-model:value="form.timeoutMs" :min="50" :max="10000" :step="50" />
      </n-form-item>
      <n-form-item label="探测命令">
        <n-input-number v-model:value="form.queryDataCommand" :min="1" :max="255" />
      </n-form-item>
    </n-grid>

    <n-button type="primary" :loading="scanning" @click="scan" style="margin-bottom: 16px">
      开始扫描
    </n-button>

    <template v-if="scanResult">
      <n-descriptions :column="3" bordered style="margin-bottom: 16px">
        <n-descriptions-item label="子网">{{ scanResult.subnetPrefix }}</n-descriptions-item>
        <n-descriptions-item label="扫描数">{{ scanResult.scannedCount }}</n-descriptions-item>
        <n-descriptions-item label="发现设备">
          <n-text type="success">{{ scanResult.matchedCount }}</n-text>
        </n-descriptions-item>
      </n-descriptions>

      <n-data-table
        :columns="columns"
        :data="scanResult.devices"
        :bordered="true"
        :row-key="(row: ZintisLanDeviceInfo) => row.ipAddress"
      />
    </template>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { NCard, NGrid, NFormItem, NInput, NInputNumber, NButton, NSpace, NDescriptions, NDescriptionsItem, NText, NDataTable } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { zintisLanScan } from '@/api/zintis-lan'
import type { ZintisLanScanRequest, ZintisLanScanResponse, ZintisLanDeviceInfo } from '@/types'

const scanning = ref(false)
const scanResult = ref<ZintisLanScanResponse | null>(null)

const form = reactive<ZintisLanScanRequest>({
  subnetPrefix: '',
  devicePort: 9527,
  startHost: 1,
  endHost: 254,
  threadPoolSize: 32,
  timeoutMs: 300,
  queryDataCommand: 207,
})

const columns: DataTableColumns<ZintisLanDeviceInfo> = [
  { title: 'IP 地址', key: 'ipAddress' },
  { title: '主机地址', key: 'hostAddress', width: 100 },
  { title: '设备名称', key: 'deviceName' },
]

async function scan() {
  scanning.value = true
  scanResult.value = null
  try {
    scanResult.value = await zintisLanScan({ ...form, subnetPrefix: form.subnetPrefix || undefined })
  } finally {
    scanning.value = false
  }
}
</script>
