<template>
	<div class="magic-job-enhanced-info">
		<form>
			<template v-if="loaded">
			  <label>{{ $i('message.enable') }}</label>
			  <magic-switch v-model:value="info.enabled" />
			  <label>cron</label>
			  <magic-input v-model:value="info.cron" :placeholder="$i('job.form.placeholder.cron')" width="250px"/>
			  <label>{{ $i('job.form.name') }}</label>
			  <magic-input v-model:value="info.name" :placeholder="$i('job.form.placeholder.name')" width="250px"/>
			  <label>{{ $i('job.form.path') }}</label>
			  <magic-input v-model:value="info.path" :placeholder="$i('job.form.placeholder.path')" width="auto" style="flex:1"/>
		  
			  <!-- 新增Quartz配置选项 -->
			  <label>并发执行</label>
			  <magic-switch v-model:value="info.concurrent" />
			  <label>错失策略</label>
			  <magic-select v-model:value="info.misfirePolicy" :options="misfirePolicyOptions" width="250px" />
			  <label>超时(秒)</label>
			  <magic-input-number v-model:value="info.timeout" :min="0" :step="1" width="150px" />
			  <label>最大重试</label>
			  <magic-input-number v-model:value="info.maxRetry" :min="0" :step="1" width="150px" />
			</template>
			<template v-else>
				<magic-loading />
			</template>
		</form>
		
		<div style="padding: 5px;" v-if="loaded">
			<!-- 操作按钮 -->
			<div class="operation-buttons">
				<magic-button @click="saveScript" type="primary">{{ $i('api.save') }}</magic-button>
				<magic-button @click="testExecute" v-if="path !== '/new'">{{ $i('job.execute_now') }}</magic-button>
				<magic-button @click="togglePauseResume" :disabled="!jobStateData.exists || jobStateData.paused" v-if="path !== '/new' && jobStateData.exists && !jobStateData.paused">暂停任务</magic-button>
				<magic-button @click="togglePauseResume" :disabled="!jobStateData.exists || !jobStateData.paused" v-else-if="path !== '/new' && jobStateData.exists && jobStateData.paused">恢复任务</magic-button>
				<magic-button @click="triggerNow" :disabled="!jobStateData.exists" v-if="path !== '/new'">立即执行</magic-button>
			</div>
			
			<!-- 显示任务状态 -->
			<div class="job-status" v-if="path !== '/new' && jobStateData.exists">
				状态: {{ jobStateData.paused ? '已暂停' : '运行中' }} | 
				下次执行时间: {{ jobStateData.nextFireTime ? formatDate(jobStateData.nextFireTime) : '未知' }}
			</div>
			
			<!-- 执行历史部分 -->
			<div class="execution-history" v-if="showHistoryPanel">
				<h4>执行历史</h4>
				<div class="controls">
					<magic-button @click="loadHistory(currentPage - 1)" :disabled="currentPage <= 1">上一页</magic-button>
					<span>第 {{ currentPage }} 页</span>
					<magic-button @click="loadHistory(currentPage + 1)" :disabled="hasNext">下一页</magic-button>
				</div>
				<div class="history-list">
					<div class="history-item" v-for="(item, index) in historyItems" :key="index">
						<div class="item-header">执行时间: {{ formatDate(item.startTime) }}</div>
						<div class="item-status">状态: {{ item.status }}</div>
						<div class="item-duration">耗时: {{ item.duration > 0 ? item.duration + ' ms' : 'N/A' }}</div>
						<div v-if="item.exceptionMessage" class="item-error">错误: {{ item.exceptionMessage }}</div>
						<div v-if="item.result" class="item-result">结果: {{ item.result }}</div>
					</div>
				</div>
			</div>
			
			<!-- 显示历史记录按钮 -->
			<div class="history-toggle" v-if="path !== '/new'">
				<magic-button @click="toggleHistory">{{ showHistoryPanel ? '隐藏历史' : '查看执行历史' }}</magic-button>
			</div>
		</div>
		
		<div style="flex:1;padding-top:5px;">
			<magic-textarea v-model:value="info.script" placeholder="在此编辑定时任务脚本 (cron: {{ info.cron || 'Not set' }})"/>
		</div>
	</div>
</template>

<script setup>
import { inject, ref, onMounted, watch } from 'vue'

// 从magic-api注入所需对象
const $i = inject('i18n.format')
const info = inject('info')
const path = inject('path')
const onSave = inject('onSave')
const request = inject('request')

// 内部状态
const loaded = ref(false)
const jobStateData = ref({ exists: false, paused: false, nextFireTime: null })
const showHistoryPanel = ref(false)
const historyItems = ref([])
const currentPage = ref(1)
const hasNext = ref(false)

// 缺失策略选项
const misfirePolicyOptions = ref([
  { value: 'SMART', label: '智能处理' },
  { value: 'IGNORE', label: '忽略错失' },
  { value: 'FIRE_ONCE_NOW', label: '立即执行一次' }
])

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
    if (info.value.dependsOn === undefined) {
        info.value.dependsOn = null
    }

    // 只在有真实路径时（即非new）拉取状态信息
    if (path && path.value && path.value !== '/new') {
        await loadJobState()
        if (showHistoryPanel.value) {
            await loadHistory(1)
        }
    }
    
    loaded.value = true
})

// 监听路径变化，用于新任务创建后的状态刷新
watch(() => path.value, async (newPath) => {
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
.magic-job-enhanced-info {
	display: flex;
	flex-direction: column;
	flex: 1;
	padding: 5px;
}

.magic-job-enhanced-info form {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	align-items: center;
}

.magic-job-enhanced-info form label {
	display: inline-block;
	width: 80px;
	font-weight: 400;
	text-align: right;
	padding: 0 5px;
}

.operation-buttons {
	margin-bottom: 10px;
}

.job-status {
	margin: 10px 0;
	padding: 8px;
	background-color: #f5f5f5;
	border-radius: 4px;
	font-size: 14px;
}

.execution-history {
	margin: 15px 0;
	padding: 10px;
	border: 1px solid #ddd;
	border-radius: 4px;
}

.controls {
	display: flex;
	align-items: center;
	gap: 10px;
	margin-bottom: 10px;
}

.history-list {
	max-height: 300px;
	overflow-y: auto;
}

.history-item {
	border: 1px solid #eee;
	border-radius: 4px;
	padding: 8px;
	margin-bottom: 5px;
	font-size: 13px;
	background-color: #fafafa;
}

.item-header {
	font-weight: bold;
	color: #333;
}

.item-status {
	color: #555;
}

.item-error {
	color: #d32f2f;
	font-style: italic;
}

.item-result {
	color: #2e7d32;
}

.history-toggle {
	margin-top: 10px;
}

:deep(.magic-textarea) {
	margin-top: 5px;
	flex: 1;
}
</style>