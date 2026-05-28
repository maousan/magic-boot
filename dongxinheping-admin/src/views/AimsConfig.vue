<template>
  <div class="aims-config-page">
    <n-card title="AIMS 服务配置" style="max-width: 640px">
      <n-spin :show="loading">
        <n-form label-placement="left" label-width="140">
          <n-form-item label="服务地址 (aimsHost)">
            <n-input v-model:value="form.aimsHost" placeholder="例: 119.29.52.245:9002" />
          </n-form-item>
          <n-form-item label="工位编码 (stationCode)">
            <n-input v-model:value="form.stationCode" placeholder="例: 10001" />
          </n-form-item>
          <n-form-item label="混合灯色 (mixColor)">
            <n-select v-model:value="form.mixColor" :options="colorOptions" />
          </n-form-item>
          <n-form-item label="异步派发灯控">
            <n-switch v-model:value="form.controlDispatchAsync" />
          </n-form-item>
          <n-form-item label="标签默认亮灯时长">
            <n-input v-model:value="form.lightOnDuration" placeholder="60m"/>
          </n-form-item>
        </n-form>
      </n-spin>

      <template #action>
        <n-space>
          <n-button secondary :loading="loading" @click="loadConfig">刷新</n-button>
          <n-button type="primary" :loading="saving" @click="handleSave">保存</n-button>
          <n-button :loading="testing" @click="handleTest">测试连接</n-button>
        </n-space>
      </template>
    </n-card>

    <n-card
      v-if="testResult"
      :type="testResult.success ? 'success' : 'error'"
      size="small"
      style="max-width: 640px; margin-top: 16px"
    >
      {{ testResult.message }}
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { NCard, NForm, NFormItem, NInput, NSelect, NSwitch, NButton, NSpace, NSpin, useMessage } from 'naive-ui'
import { getAimsConfig, updateAimsConfig, testAimsConnection } from '@/api/aims-config'

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const testing = ref(false)
const testResult = ref<{ success: boolean; message: string } | null>(null)

const form = reactive({
  aimsHost: '',
  stationCode: '',
  mixColor: 'CYAN',
  controlDispatchAsync: false,
  lightOnDuration: '60m'
})

const colorOptions = [
  { label: 'CYAN', value: 'CYAN' },
  { label: 'RED', value: 'RED' },
  { label: 'GREEN', value: 'GREEN' },
  { label: 'BLUE', value: 'BLUE' },
  { label: 'YELLOW', value: 'YELLOW' },
  { label: 'WHITE', value: 'WHITE' },
  { label: 'MAGENTA', value: 'MAGENTA' },
  { label: 'ORANGE', value: 'ORANGE' },
]

async function loadConfig() {
  loading.value = true
  try {
    const cfg = await getAimsConfig()
    form.aimsHost = cfg.aimsHost ?? ''
    form.stationCode = cfg.stationCode ?? ''
    form.mixColor = cfg.mixColor || 'CYAN'
    form.controlDispatchAsync = cfg.controlDispatchAsync === 'true'
    form.lightOnDuration = cfg.lightOnDuration || '60m'
    message.success('配置已加载')
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  if (!form.aimsHost) {
    message.warning('服务地址不能为空')
    return
  }
  saving.value = true
  try {
    await updateAimsConfig({
      aimsHost: form.aimsHost,
      stationCode: form.stationCode,
      mixColor: form.mixColor,
      controlDispatchAsync: String(form.controlDispatchAsync),
      lightOnDuration: form.lightOnDuration
    })
    message.success('配置已保存（运行时生效，重启后恢复）')
  } finally {
    saving.value = false
  }
}

async function handleTest() {
  testing.value = true
  testResult.value = null
  try {
    const msg = await testAimsConnection()
    testResult.value = { success: true, message: msg }
  } catch (e: any) {
    testResult.value = { success: false, message: e?.response?.data?.message || e?.message || '连接失败' }
  } finally {
    testing.value = false
  }
}

onMounted(loadConfig)
</script>

<style scoped>
.aims-config-page {
  max-width: 640px;
}
</style>
