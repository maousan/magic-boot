<template>
  <div class="log-viewer">
    <!-- 标题栏 -->
    <div class="log-titlebar">
      <div class="log-titlebar-dots">
        <span class="dot dot-red" />
        <span class="dot dot-yellow" />
        <span class="dot dot-green" />
      </div>
      <span class="log-titlebar-title">logs — ws</span>
      <div class="log-titlebar-status">
        <span class="log-status-dot" :class="statusClass" />
        <span class="log-status-text">{{ statusText }}</span>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="log-toolbar">
      <n-config-provider :theme="darkTheme" class="log-toolbar-controls">
        <n-select
          v-model:value="selectedLevels"
          :options="levelOptions"
          multiple
          placeholder="LEVEL"
          size="tiny"
          class="log-toolbar-select"
          :teleport-disabled="true"
          @update:value="handleLevelChange"
        />
        <n-input
          v-model:value="keyword"
          placeholder="grep ..."
          size="tiny"
          clearable
          class="log-toolbar-input"
          @keyup.enter="handleKeywordChange"
          @clear="handleKeywordChange"
        />
      </n-config-provider>
      <span class="log-count">{{ filteredLogs.length }}<template v-if="filteredLogs.length !== logs.length">/{{ logs.length }}</template></span>
      <span class="log-toolbar-btn" @click="toggleAutoScroll">{{ autoScroll ? '▾' : '▴' }}</span>
      <span class="log-toolbar-btn" @click="clearLogs">⌧</span>
      <span class="log-toolbar-btn" @click="reconnect">↻</span>
    </div>

    <!-- Forest 日志开关 -->
    <div class="log-forest-bar">
      <n-config-provider :theme="darkTheme" class="log-forest-controls">
        <span class="log-forest-label">Forest</span>
        <label class="log-forest-item">
          <NSwitch v-model:value="forestLog.logEnabled" size="small" @update:value="v => onForestToggle('logEnabled', v)" />
          <span>总开关</span>
        </label>
        <label class="log-forest-item" :style="{ opacity: forestLog.logEnabled ? 1 : 0.35 }">
          <NSwitch v-model:value="forestLog.logRequest" size="small" :disabled="!forestLog.logEnabled" @update:value="v => onForestToggle('logRequest', v)" />
          <span>请求详情</span>
        </label>
        <label class="log-forest-item" :style="{ opacity: forestLog.logEnabled ? 1 : 0.35 }">
          <NSwitch v-model:value="forestLog.logResponseStatus" size="small" :disabled="!forestLog.logEnabled" @update:value="v => onForestToggle('logResponseStatus', v)" />
          <span>响应状态</span>
        </label>
        <label class="log-forest-item" :style="{ opacity: forestLog.logEnabled ? 1 : 0.35 }">
          <NSwitch v-model:value="forestLog.logResponseHeaders" size="small" :disabled="!forestLog.logEnabled" @update:value="v => onForestToggle('logResponseHeaders', v)" />
          <span>响应头</span>
        </label>
        <label class="log-forest-item" :style="{ opacity: forestLog.logEnabled ? 1 : 0.35 }">
          <NSwitch v-model:value="forestLog.logResponseContent" size="small" :disabled="!forestLog.logEnabled" @update:value="v => onForestToggle('logResponseContent', v)" />
          <span>响应内容</span>
        </label>
      </n-config-provider>
    </div>

    <!-- 日志显示区域 -->
    <div ref="logContainerRef" class="log-container" @scroll="onScroll">
      <div
        v-for="(log, i) in filteredLogs"
        :key="i"
        class="log-line"
        :class="'log-level-' + log.level"
      ><span v-if="log.level === 'ERROR'" class="log-level-badge">✖</span><span v-else-if="log.level === 'WARN'" class="log-level-badge">▲</span>{{ log.text }}</div>
      <div v-if="filteredLogs.length === 0" class="log-empty">~</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { NInput, NSelect, NConfigProvider, NSwitch, darkTheme } from 'naive-ui'
import { getForestLogConfig, updateForestLogConfig, type ForestLogConfig } from '@/api/forest-log'

interface LogEntry {
  text: string
  level: string
}

const MAX_LOGS = 5000
const RECONNECT_DELAY = 3000

const logs = ref<LogEntry[]>([])
const wsStatus = ref<'disconnected' | 'connecting' | 'connected'>('disconnected')
const autoScroll = ref(true)
const selectedLevels = ref<string[]>([])
const keyword = ref('')
const logContainerRef = ref<HTMLElement | null>(null)

const forestLog = reactive<ForestLogConfig>({
  logEnabled: true,
  logRequest: true,
  logResponseStatus: true,
  logResponseHeaders: true,
  logResponseContent: true,
})

async function loadForestLogConfig() {
  try {
    const cfg = await getForestLogConfig()
    if (cfg) {
      forestLog.logEnabled = cfg.logEnabled
      forestLog.logRequest = cfg.logRequest
      forestLog.logResponseStatus = cfg.logResponseStatus
      forestLog.logResponseHeaders = cfg.logResponseHeaders
      forestLog.logResponseContent = cfg.logResponseContent
    }
  } catch {
    // ignore
  }
}

function onForestToggle(key: keyof ForestLogConfig, value: boolean) {
  updateForestLogConfig({ [key]: value }).catch(() => {})
}

let ws: WebSocket | null = null
let reconnectTimer: ReturnType<typeof setTimeout> | null = null
let isManualClose = false

const levelOptions = [
  { label: 'DEBUG', value: 'DEBUG' },
  { label: 'INFO', value: 'INFO' },
  { label: 'WARN', value: 'WARN' },
  { label: 'ERROR', value: 'ERROR' },
]

const statusClass = computed(() => {
  if (wsStatus.value === 'connected') return 'log-status-connected'
  if (wsStatus.value === 'connecting') return 'log-status-connecting'
  return 'log-status-disconnected'
})

const statusText = computed(() => {
  if (wsStatus.value === 'connected') return 'connected'
  if (wsStatus.value === 'connecting') return 'connecting...'
  return 'disconnected'
})

const filteredLogs = computed(() => {
  const levels = selectedLevels.value
  const kw = keyword.value.trim().toLowerCase()
  return logs.value.filter(log => {
    if (levels.length > 0 && !levels.includes(log.level)) return false
    if (kw && !log.text.toLowerCase().includes(kw)) return false
    return true
  })
})

watch(filteredLogs, () => {
  if (autoScroll.value) scrollToBottom()
})

function parseLogLevel(text: string): string {
  const match = text.match(/\b(DEBUG|INFO|WARN|ERROR|TRACE)\b/i)
  if (match && match[1]) return match[1].toUpperCase()
  return 'INFO'
}

function addLog(text: string) {
  if (text.startsWith('{')) {
    try {
      const obj = JSON.parse(text)
      if (obj.type === 'history_end') return
    } catch {
      // not JSON
    }
  }

  const level = parseLogLevel(text)
  logs.value.push({ text, level })

  if (logs.value.length > MAX_LOGS) {
    logs.value.splice(0, logs.value.length - MAX_LOGS)
  }

  if (autoScroll.value) {
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    const el = logContainerRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function onScroll() {
  const el = logContainerRef.value
  if (!el) return
  const distFromBottom = el.scrollHeight - el.scrollTop - el.clientHeight
  if (distFromBottom > 50) {
    autoScroll.value = false
  } else {
    autoScroll.value = true
  }
}

function toggleAutoScroll() {
  autoScroll.value = !autoScroll.value
  if (autoScroll.value) scrollToBottom()
}

function clearLogs() {
  logs.value = []
}

function sendFilterUpdate() {
  if (!ws || ws.readyState !== WebSocket.OPEN) return
  const level = selectedLevels.value.length > 0 ? selectedLevels.value.join(',') : ''
  const kw = keyword.value || ''
  ws.send(`FILTER:level=${level}&keyword=${kw}`)
}

function handleLevelChange() {
  sendFilterUpdate()
}

function handleKeywordChange() {
  sendFilterUpdate()
}

function buildWsUrl(): string {
  const loc = window.location
  const protocol = loc.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = loc.host
  let url = `${protocol}//${host}/ws/logs?tail=200`
  const level = selectedLevels.value.length > 0 ? selectedLevels.value.join(',') : ''
  if (level) url += `&level=${encodeURIComponent(level)}`
  if (keyword.value) url += `&keyword=${encodeURIComponent(keyword.value)}`
  return url
}

function connect() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }

  wsStatus.value = 'connecting'
  isManualClose = false

  try {
    ws = new WebSocket(buildWsUrl())
  } catch {
    wsStatus.value = 'disconnected'
    scheduleReconnect()
    return
  }

  ws.onopen = () => {
    wsStatus.value = 'connected'
    logs.value.push({ text: '--- connected to log stream ---', level: 'INFO' })
  }

  ws.onmessage = (event) => {
    const data = event.data
    if (typeof data === 'string') {
      addLog(data)
    }
  }

  ws.onclose = () => {
    wsStatus.value = 'disconnected'
    ws = null
    if (!isManualClose) {
      scheduleReconnect()
    }
  }

  ws.onerror = () => {
    // onclose follows
  }
}

function scheduleReconnect() {
  if (reconnectTimer) return
  reconnectTimer = setTimeout(() => {
    reconnectTimer = null
    connect()
  }, RECONNECT_DELAY)
}

function reconnect() {
  isManualClose = true
  if (ws) {
    ws.close()
    ws = null
  }
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  connect()
}

onMounted(() => {
  connect()
  loadForestLogConfig()
})

onBeforeUnmount(() => {
  isManualClose = true
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  if (ws) {
    ws.close()
    ws = null
  }
})
</script>

<style scoped>
.log-viewer {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  background: #0c0c0c;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #333;
  font-family: 'Cascadia Code', 'Fira Code', 'JetBrains Mono', 'Consolas', 'Menlo', monospace;
}

/* ─── 标题栏 ─── */
.log-titlebar {
  display: flex;
  align-items: center;
  padding: 8px 14px;
  background: #1a1a1a;
  border-bottom: 1px solid #2a2a2a;
  user-select: none;
  flex-shrink: 0;
}

.log-titlebar-dots {
  display: flex;
  gap: 6px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot-red { background: #ff5f57; }
.dot-yellow { background: #febc2e; }
.dot-green { background: #28c840; }

.log-titlebar-title {
  flex: 1;
  text-align: center;
  font-size: 12px;
  color: #666;
}

.log-titlebar-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.log-status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.log-status-connected {
  background: #28c840;
  box-shadow: 0 0 4px #28c840;
}

.log-status-connecting {
  background: #febc2e;
  animation: blink 1s step-end infinite;
}

.log-status-disconnected {
  background: #ff5f57;
}

@keyframes blink {
  50% { opacity: 0; }
}

.log-status-text {
  font-size: 11px;
  color: #555;
}

/* ─── 工具栏 ─── */
.log-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 14px;
  background: #111;
  border-bottom: 1px solid #222;
  flex-shrink: 0;
}

.log-toolbar-controls {
  display: contents;
}

.log-toolbar-select {
  width: 180px;
}

.log-toolbar-input {
  width: 140px;
}

.log-count {
  font-size: 11px;
  color: #555;
  margin-left: auto;
  min-width: 40px;
  text-align: right;
  font-variant-numeric: tabular-nums;
}

.log-toolbar-btn {
  font-size: 13px;
  color: #666;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 3px;
  transition: color 0.15s, background 0.15s;
}

.log-toolbar-btn:hover {
  color: #ccc;
  background: #2a2a2a;
}

/* ─── Forest 日志开关 ─── */
.log-forest-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 6px 14px;
  background: #0f0f0f;
  border-bottom: 1px solid #222;
  flex-shrink: 0;
  user-select: none;
}

.log-forest-label {
  font-size: 11px;
  color: #555;
  margin-right: 4px;
}

.log-forest-controls {
  display: contents;
}

.log-forest-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #888;
  cursor: pointer;
}

/* ─── 日志区域 ─── */
.log-container {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 6px 0;
  font-size: 12.5px;
  line-height: 1.65;
}

.log-container::-webkit-scrollbar {
  width: 6px;
}

.log-container::-webkit-scrollbar-track {
  background: transparent;
}

.log-container::-webkit-scrollbar-thumb {
  background: #333;
  border-radius: 3px;
}

.log-container::-webkit-scrollbar-thumb:hover {
  background: #444;
}

.log-line {
  white-space: pre-wrap;
  word-break: break-all;
  color: #cccccc;
  padding: 0 14px;
}

.log-level-badge {
  display: inline-block;
  width: 14px;
  text-align: center;
  margin-right: 2px;
}

.log-level-DEBUG {
  color: #6a6a6a;
}

.log-level-INFO {
  color: #cccccc;
}

.log-level-WARN {
  color: #e5c07b;
}

.log-level-ERROR {
  color: #e06c75;
}

.log-level-TRACE {
  color: #444;
}

.log-empty {
  color: #333;
  text-align: center;
  padding: 60px 0;
  font-size: 24px;
}
</style>
