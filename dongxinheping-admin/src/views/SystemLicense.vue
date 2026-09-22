<template>
  <n-card title="系统授权" class="table-page-card" style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column">
    <div class="table-page-content" style="overflow: auto">
      <n-alert v-if="view && view.enabled === false" type="default" title="授权校验未启用">
        当前实例 license.enabled=false，不做授权校验。
      </n-alert>
      <n-alert v-else-if="view" :type="alertType" :title="alertTitle">
        {{ view.message }}
      </n-alert>

      <n-descriptions v-if="view" bordered :column="2" label-placement="left" size="small" style="margin-top: 16px">
        <n-descriptions-item label="授权状态">
          <n-tag :type="alertType" size="small">{{ statusLabel }}</n-tag>
        </n-descriptions-item>
        <n-descriptions-item label="客户 / 项目">{{ view.customer ?? '-' }}</n-descriptions-item>
        <n-descriptions-item label="到期日期">{{ view.expireAt ?? '-' }}</n-descriptions-item>
        <n-descriptions-item label="剩余天数">
          <span v-if="view.remainDays != null" :style="{ color: remainColor }">
            {{ view.remainDays }} 天
          </span>
          <span v-else>-</span>
        </n-descriptions-item>
        <n-descriptions-item label="宽限天数">{{ view.graceDays }} 天</n-descriptions-item>
        <n-descriptions-item label="服务器时间">{{ view.serverTime }}</n-descriptions-item>
      </n-descriptions>

      <n-card title="机器指纹码（签发授权时需提供）" size="small" style="margin-top: 16px">
        <n-text depth="3" style="display: block; margin-bottom: 8px">
          将此机器指纹码完整复制给软件供应商用于签发；一个码对应本机三项硬件特征，授权校验为 3 项命中 2 项。
        </n-text>
        <pre class="fingerprint-block">{{ view?.fingerprintCode ?? '' }}</pre>
        <n-button size="small" style="margin-top: 8px" @click="copyFingerprints">复制机器指纹码</n-button>
      </n-card>

      <n-card title="导入授权文件" size="small" style="margin-top: 16px">
        <n-space vertical>
          <n-upload :max="1" accept=".lic" :default-upload="false" @change="onFileChange">
            <n-button>选择 .lic 授权文件</n-button>
          </n-upload>
          <n-button type="primary" :loading="importing" :disabled="!importFile" @click="handleImport">
            导入并生效
          </n-button>
          <n-text depth="3">
            导入会校验签名与机器指纹；允许导入已过期文件（覆盖旧授权后按新文件重新判定状态）。
          </n-text>
        </n-space>
      </n-card>
    </div>
  </n-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  NCard, NAlert, NDescriptions, NDescriptionsItem, NTag, NText,
  NUpload, NButton, NSpace, useMessage,
} from 'naive-ui'
import type { UploadFileInfo } from 'naive-ui'
import { getLicenseStatus, importLicense } from '@/api/license'
import type { LicenseStatusView } from '@/types'

const message = useMessage()
const view = ref<LicenseStatusView | null>(null)
const importing = ref(false)
const importFile = ref<File | null>(null)

const alertType = computed<'success' | 'warning' | 'error' | 'default' | 'info'>(() => {
  switch (view.value?.status) {
    case 'ok': return 'success'
    case 'warning': return 'warning'
    case 'grace': return 'warning'
    case 'expired': return 'error'
    case 'abnormal': return 'error'
    case 'missing': return 'error'
    default: return 'default'
  }
})

const remainColor = computed(() => ((view.value?.remainDays ?? 999) <= 30 ? '#d03050' : undefined))

const alertTitle = computed(() => {
  switch (view.value?.status) {
    case 'ok': return '授权有效'
    case 'warning': return '授权即将到期'
    case 'grace': return '授权已过期（宽限期）'
    case 'expired': return '授权已过期'
    case 'abnormal': return '系统时间异常'
    case 'missing': return '系统未授权'
    default: return '授权校验未启用'
  }
})

const statusLabel = computed(() => {
  switch (view.value?.status) {
    case 'ok': return '有效'
    case 'warning': return '即将到期'
    case 'grace': return '宽限期'
    case 'expired': return '已过期'
    case 'abnormal': return '时间异常'
    case 'missing': return '未授权'
    default: return '未启用'
  }
})

async function fetchStatus() {
  view.value = await getLicenseStatus()
}

function onFileChange({ file }: { file: UploadFileInfo }) {
  importFile.value = file.file ?? null
}

async function handleImport() {
  if (!importFile.value) return
  importing.value = true
  try {
    const formData = new FormData()
    formData.append('file', importFile.value)
    const result = await importLicense(formData)
    message.success(`授权已生效：${result.customer ?? ''}（${result.expireAt ?? ''}）`)
    importFile.value = null
    await fetchStatus()
  } finally {
    importing.value = false
  }
}

async function copyFingerprints() {
  await navigator.clipboard.writeText(view.value?.fingerprintCode ?? '')
  message.success('机器指纹码已复制')
}

onMounted(fetchStatus)
</script>

<style scoped>
.fingerprint-block {
  margin: 0;
  padding: 12px;
  background: #f6f6f8;
  border-radius: 4px;
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-all;
  white-space: pre-wrap;
}
</style>
