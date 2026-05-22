<template>
  <n-space vertical :size="16">
    <!-- 设备直控 -->
    <n-card title="Zintis LED 设备控制">
      <n-grid :cols="2" :x-gap="16">
        <n-form-item label="设备 IP">
          <n-input v-model:value="form.deviceIp" placeholder="192.168.2.102" />
        </n-form-item>
        <n-form-item label="端口">
          <n-input-number v-model:value="form.devicePort" :min="1" :max="65535" />
        </n-form-item>
        <n-form-item label="主机地址 (0-255)">
          <n-input-number v-model:value="form.hostAddress" :min="0" :max="255" />
        </n-form-item>
        <n-form-item label="数据命令 (1-255)">
          <n-input-number v-model:value="form.dataCommand" :min="1" :max="255" />
        </n-form-item>
        <n-form-item label="超时 (ms)">
          <n-input-number v-model:value="form.timeoutMs" :min="100" :max="60000" :step="1000" />
        </n-form-item>
      </n-grid>

      <n-divider>控制命令</n-divider>
      <n-space>
        <n-button type="success" :loading="loading" @click="exec(zintisControlOn, '开灯')">开灯</n-button>
        <n-button type="error" :loading="loading" @click="exec(zintisControlOff, '关灯')">关灯</n-button>
        <n-button type="warning" :loading="loading" @click="exec(zintisControlPulse, '脉冲')">脉冲</n-button>
      </n-space>

      <n-divider>查询命令</n-divider>
      <n-space>
        <n-button :loading="loading" @click="execQuery(zintisControlQuery, '控制状态查询')">控制状态</n-button>
        <n-button :loading="loading" @click="execQuery(zintisSystemInfo, '系统信息')">系统信息</n-button>
        <n-button :loading="loading" @click="execQuery(zintisSignalStrength, '信号强度')">信号强度</n-button>
        <n-button :loading="loading" @click="execQuery(zintisNetwork, '网络信息')">网络信息</n-button>
      </n-space>
    </n-card>

    <!-- TCP 配置 -->
    <n-card title="设备 TCP 模式配置">
      <n-space vertical>
        <n-space>
          <n-button @click="execTcp(zintisTcpServerOpen, '开启设备TCP服务')">开启设备 TCP 服务</n-button>
          <n-button @click="execTcp(zintisTcpServerClose, '关闭设备TCP服务')">关闭设备 TCP 服务</n-button>
          <n-button @click="showTcpClient = true">开启设备 TCP 客户端</n-button>
          <n-button @click="execTcp(zintisTcpClientClose, '关闭设备TCP客户端')">关闭设备 TCP 客户端</n-button>
        </n-space>
      </n-space>
    </n-card>

    <!-- 结果展示 -->
    <n-card v-if="result" title="执行结果" size="small">
      <n-descriptions :column="2" bordered>
        <n-descriptions-item label="成功">
          <n-text :type="result.success ? 'success' : 'error'">{{ result.success ? '是' : '否' }}</n-text>
        </n-descriptions-item>
        <n-descriptions-item label="消息">{{ (result as any).message }}</n-descriptions-item>
        <n-descriptions-item v-if="(result as any).errorCode" label="错误码">
          <n-text type="error">{{ (result as any).errorCode }}</n-text>
        </n-descriptions-item>
        <n-descriptions-item v-if="(result as any).payloadAscii" label="设备名称">
          {{ (result as any).payloadAscii }}
        </n-descriptions-item>
        <n-descriptions-item v-if="(result as any).signalStrengthDbm != null" label="信号 (dBm)">
          {{ (result as any).signalStrengthDbm }}
        </n-descriptions-item>
        <n-descriptions-item v-if="(result as any).systemIp" label="设备 IP">
          {{ (result as any).systemIp }}
        </n-descriptions-item>
        <n-descriptions-item v-if="(result as any).systemMac" label="设备 MAC">
          {{ (result as any).systemMac }}
        </n-descriptions-item>
        <n-descriptions-item v-if="(result as any).payloadHex" label="Payload HEX">
          <n-ellipsis style="max-width: 400px">{{ (result as any).payloadHex }}</n-ellipsis>
        </n-descriptions-item>
        <n-descriptions-item label="请求 HEX" :span="2">
          <n-ellipsis style="max-width: 600px">{{ (result as any).rawRequestHex }}</n-ellipsis>
        </n-descriptions-item>
        <n-descriptions-item label="响应 HEX" :span="2">
          <n-ellipsis style="max-width: 600px">{{ (result as any).rawResponseHex }}</n-ellipsis>
        </n-descriptions-item>
      </n-descriptions>
    </n-card>

    <!-- TCP 客户端弹窗 -->
    <n-modal v-model:show="showTcpClient" title="开启设备 TCP 客户端" preset="dialog">
      <n-space vertical>
        <n-form-item label="目标服务器 IP">
          <n-input v-model:value="tcpTarget.targetIp" placeholder="192.168.2.100" />
        </n-form-item>
        <n-form-item label="目标端口">
          <n-input-number v-model:value="tcpTarget.targetPort" :min="1" :max="65535" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showTcpClient = false">取消</n-button>
        <n-button type="primary" :loading="loading" @click="openTcpClient">确认</n-button>
      </template>
    </n-modal>
  </n-space>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import {
  NCard, NSpace, NGrid, NFormItem, NInput, NInputNumber, NButton, NDivider,
  NDescriptions, NDescriptionsItem, NText, NEllipsis, NModal,
} from 'naive-ui'
import {
  zintisControlOn, zintisControlOff, zintisControlPulse,
  zintisControlQuery, zintisSystemInfo, zintisSignalStrength, zintisNetwork,
  zintisTcpServerOpen, zintisTcpServerClose, zintisTcpClientOpen, zintisTcpClientClose,
} from '@/api/zintis-led'
import type { ZintisLedControlRequest, ZintisLedTcpConfigRequest } from '@/types'

const loading = ref(false)
const result = ref<Record<string, any> | null>(null)
const showTcpClient = ref(false)

const form = reactive<ZintisLedControlRequest>({
  deviceIp: '',
  devicePort: 9527,
  hostAddress: 0,
  dataCommand: 1,
  timeoutMs: 3000,
})

const tcpTarget = reactive({ targetIp: '', targetPort: 9834 })

async function exec(fn: (d: ZintisLedControlRequest) => Promise<any>, _label: string) {
  loading.value = true
  result.value = null
  try {
    result.value = await fn({ ...form })
  } finally {
    loading.value = false
  }
}

async function execQuery(fn: (d: ZintisLedControlRequest) => Promise<any>, label: string) {
  await exec(fn, label)
}

async function execTcp(fn: (d: ZintisLedTcpConfigRequest) => Promise<any>, label: string) {
  await exec(fn as any, label)
}

async function openTcpClient() {
  showTcpClient.value = false
  loading.value = true
  result.value = null
  try {
    result.value = await zintisTcpClientOpen({ ...form, targetIp: tcpTarget.targetIp, targetPort: tcpTarget.targetPort })
  } finally {
    loading.value = false
  }
}
</script>
