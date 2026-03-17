<template>
  <div class="dashboard">
    <n-space vertical :size="16">
      <n-alert type="info" title="欢迎使用演示插件">
        这是一个基于 Vue 3 + Naive UI 的插件前端控制台示例
      </n-alert>

      <n-grid :cols="4" :x-gap="16" :y-gap="16" responsive="screen">
        <n-gi>
          <n-card>
            <n-statistic label="已安装插件" :value="stats.plugins" />
          </n-card>
        </n-gi>
        <n-gi>
          <n-card>
            <n-statistic label="API 接口" :value="stats.apis" />
          </n-card>
        </n-gi>
        <n-gi>
          <n-card>
            <n-statistic label="活跃用户" :value="stats.users" />
          </n-card>
        </n-gi>
        <n-gi>
          <n-card>
            <n-statistic label="今日请求" :value="stats.requests" />
          </n-card>
        </n-gi>
      </n-grid>

      <n-card title="快速操作">
        <n-space>
          <n-button type="primary" @click="testApi">
            测试 API
          </n-button>
          <n-button @click="refreshStats">
            刷新统计
          </n-button>
        </n-space>
      </n-card>

      <n-card title="系统信息">
        <n-descriptions label-placement="left" :column="2">
          <n-descriptions-item label="插件版本">
            <n-tag type="success">v1.0.0</n-tag>
          </n-descriptions-item>
          <n-descriptions-item label="Vue 版本">
            <n-tag type="info">{{ vueVersion }}</n-tag>
          </n-descriptions-item>
          <n-descriptions-item label="插件 ID">
            <n-text code>demo-plugin</n-text>
          </n-descriptions-item>
          <n-descriptions-item label="UI 框架">
            <n-tag type="warning">Naive UI</n-tag>
          </n-descriptions-item>
          <n-descriptions-item label="当前时间" :span="2">
            {{ currentTime }}
          </n-descriptions-item>
        </n-descriptions>
      </n-card>
    </n-space>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { version as vueVersion } from 'vue'
import {
  NSpace,
  NAlert,
  NGrid,
  NGi,
  NCard,
  NStatistic,
  NButton,
  NDescriptions,
  NDescriptionsItem,
  NTag,
  NText,
  useMessage
} from 'naive-ui'

const message = useMessage()

// 统计数据
const stats = ref({
  plugins: 5,
  apis: 23,
  users: 128,
  requests: '1,234'
})

// 当前时间
const currentTime = ref('')
let timer = null

// 更新时间
const updateTime = () => {
  currentTime.value = new Date().toLocaleString('zh-CN')
}

// 测试 API
const testApi = async () => {
  try {
    const response = await fetch('/plugin/demo-plugin/api/hello?name=Console')
    const data = await response.json()
    message.success(`API 响应: ${JSON.stringify(data)}`)
  } catch (error) {
    message.error(`API 调用失败: ${error.message}`)
  }
}

// 刷新统计
const refreshStats = () => {
  stats.value.requests = Math.floor(Math.random() * 10000).toLocaleString()
  message.success('统计数据已刷新！')
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.dashboard {
  width: 100%;
}
</style>
