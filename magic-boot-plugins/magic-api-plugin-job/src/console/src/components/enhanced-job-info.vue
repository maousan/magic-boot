<template>
	<div class="magic-plugin-container">
    <div class="magic-api-info">
      <form>
        <label>并发执行</label>
        <magic-checkbox v-model:value="info.concurrent" />
        <label>错失策略</label>
        <magic-select v-model:value="info.misfirePolicy" defaultValue="SMART" :options="misfirePolicyOptions" width="250px" />
        <label>超时(秒)</label>
        <magic-input v-model:value="info.timeout" :min="0" :step="1" width="150px" />
        <label>最大重试</label>
        <magic-input v-model:value="info.maxRetry" :min="0" :step="1" width="150px" />
      </form>
      <div style="position:relative;flex:1;padding-top:5px;">
        <magic-monaco-editor ref="editor" placeholder="运行参数" @change="handleEditorContentChange" :value="info.params" language="json"></magic-monaco-editor>
      </div>
    </div>
  </div>
</template>

<script setup>
import { inject, ref, onMounted, watch } from 'vue'

// 从magic-api注入所需对象
const $i = inject('i18n.format')
const info = inject('info')
const path = inject('path', ref(''))
const onSave = inject('onSave')
const request = inject('request')
const editor = ref();

// 内部状态
const loaded = ref(false)
const jobStateData = ref({ exists: false, paused: false, nextFireTime: null })
const showHistoryPanel = ref(false)
const historyItems = ref([])
const currentPage = ref(1)
const hasNext = ref(false)

// 缺失策略选项
const misfirePolicyOptions = ref([
  { value: 'SMART', text: '智能处理' },
  { value: 'IGNORE', text: '忽略错失' },
  { value: 'FIRE_ONCE_NOW', text: '立即执行一次' }
])

const handleEditorContentChange = (e) => {
  const value = editor.value.getInstance().getValue()
  info.value.params = value
}

// 初始化任务状态和额外的JobInfo字段
onMounted(async () => {
    // 确保info对象有新字段（对于现有任务）
    if (info.value.misfirePolicy === undefined) {
        info.value.misfirePolicy = 'SMART'
    }
    if (info.value.concurrent === undefined) {
        info.value.concurrent = false
    }
    if (info.value.maxRetry === undefined) {
        info.value.maxRetry = 0
    }
    if (info.value.timeout === undefined) {
        info.value.timeout = 0
    }
    if (info.value.params) {
      info.value.params = JSON.parse(info.value.params)
    }
    // if (info.value.dependsOn === undefined) {
    //     info.value.dependsOn = null
    // }

    // 只在有真实路径时（即非new）拉取状态信息
    // if (path && path.value && path.value !== '/new') {
    //     await loadJobState()
    //     if (showHistoryPanel.value) {
    //         await loadHistory(1)
    //     }
    // }
    
    // loaded.value = true
})

// 监听路径变化，用于新任务创建后的状态刷新
watch(() => path?.value, async (newPath) => {
    if (newPath && newPath !== '/new') {
        await loadJobState()
    }
})

// 加载任务状态
async function loadJobState() {
    try {
        const response = await request({
            method: 'GET',
            url: `/magic/job/${encodeURIComponent(info.value.id)}/state`,
        })
        if (response.code === 200) {
            jobStateData.value = response.data
        } else {
            // 重置状态信息
            jobStateData.value = { exists: false, paused: false, nextFireTime: null }
        }
    } catch (error) {
        console.error('Error loading job state:', error)
        jobStateData.value = { exists: false, paused: false, nextFireTime: null }
    }
}

// 加载执行历史
async function loadHistory(page) {
    if (page < 1) return
    
    try {
        const response = await request({
            method: 'GET',
            url: `/magic/job/${encodeURIComponent(info.value.id)}/history`,
            params: { page: page, size: 10 }
        })
        if (response.code === 200) {
            historyItems.value = Array.isArray(response.data) ? response.data : []
            currentPage.value = page
            // 简单判断是否有下一页（实际应根据总数判断）
            hasNext.value = response.data && response.data.length >= 10
        } else {
            historyItems.value = []
            hasNext.value = false
        }
    } catch (error) {
        console.error('Error loading job history:', error)
        historyItems.value = []
        hasNext.value = false
    }
}

// 暂停/恢复任务
async function togglePauseResume() {
    if (!info.value.id) {
        console.error('Job ID not available')
        return
    }
    
    try {
        const isCurrentlyPaused = jobStateData.value.paused
        let response
        
        if (isCurrentlyPaused) {
            // 恢复任务
            response = await request({
                method: 'POST',
                url: `/magic/job/${encodeURIComponent(info.value.id)}/resume`
            })
        } else {
            // 暂停任务
            response = await request({
                method: 'POST',
                url: `/magic/job/${encodeURIComponent(info.value.id)}/pause`
            })
        }
        
        if (response.code === 200) {
            // 刷新状态
            await loadJobState()
        } else {
            console.error('操作失败:', response.msg)
        }
    } catch (error) {
        console.error('操作失败:', error)
    }
}

// 立即执行任务
async function triggerNow() {
    if (!info.value.id) {
        console.error('Job ID not available')
        return
    }
    
    try {
        const response = await request({
            method: 'POST',
            url: `/magic/job/${encodeURIComponent(info.value.id)}/trigger`
        })
        
        if (response.code === 200) {
            console.log('立即执行任务成功')
        } else {
            console.error('立即执行失败:', response.msg)
        }
    } catch (error) {
        console.error('立即执行失败:', error)
    }
}

// 执行当前脚本
async function testExecute() {
    if (!info.value.id) {
        console.error('Job ID not available')
        return
    }
    
    try {
        const response = await request({
            method: 'POST',
            url: '/magic/job/execute',
            params: { id: info.value.id }
        })
        
        if (response.code === 200) {
            console.log('Test execution result:', response.data)
        } else {
            console.error('Test execution failed:', response.msg)
        }
    } catch (error) {
        console.error('Test execution failed:', error)
    }
}

// 保存脚本
function saveScript() {
    onSave && onSave()
}

// 格式化时间的辅助函数
function formatDate(dateStr) {
    if (!dateStr) return 'N/A'
    try {
        // 尝试解析不同的时间格式
        const date = new Date(dateStr)
        if (!isNaN(date.getTime())) {
            return date.toLocaleString()
        } else {
            return dateStr // 如果解析失败，则原样返回
        }
    } catch (e) {
        return dateStr
    }
}

// 显示历史记录面板
function toggleHistory() {
    showHistoryPanel.value = !showHistoryPanel.value
    if (showHistoryPanel.value && currentPage.value === 1) {
        loadHistory(currentPage.value)
    }
}
</script>


<style scoped>
.magic-plugin-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 5px;
}

.magic-api-info {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 5px;
}

.magic-api-info form {
  display: flex;
  padding: 5px;
}

.magic-api-info form :deep(.magic-checkbox){
  width: var(--magic-input-height);
  height: var(--magic-input-height);
}

.magic-api-info form label {
  display: inline-block;
  width: 75px;
  height: var(--magic-input-height);
  line-height: var(--magic-input-height);
  font-weight: 400;
  text-align: right;
  padding: 0 5px;
}

.magic-navbar .magic-navbar-body,
.magic-navbar.magic-navbar-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.magic-api-info+.magic-navbar {
  flex-direction: column;
  overflow: hidden;
}

.magic-monaco-editor {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}

</style>
