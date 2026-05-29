<template>
  <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides">
  <div class="db-term">
    <!-- Title bar -->
    <div class="db-term__bar">
      <div class="db-term__dots"><span /><span /><span /></div>
      <span class="db-term__bar-title">Database Terminal</span>
      <button class="db-term__bar-btn" @click="sidebarCollapsed = !sidebarCollapsed">
        {{ sidebarCollapsed ? '[ + ] schema' : '[ - ] schema' }}
      </button>
    </div>

    <!-- Body -->
    <div class="db-term__body">
      <!-- Sidebar: Schema Tree -->
      <div v-if="!sidebarCollapsed" class="db-term__side">
        <div class="db-term__side-head">SCHEMA · {{ tables.length }} tables</div>
        <div v-if="tablesLoading" class="db-term__side-loading"><span class="db-term__blink">█</span></div>
        <div v-else class="db-term__side-tree">
          <n-tree
            :data="treeData"
            block-line
            selectable
            :default-expand-all="false"
            @update:selected-keys="handleSelectTable"
          />
        </div>
      </div>

      <!-- Main panel -->
      <div class="db-term__main">
        <div class="db-term__prompt">
          <div class="db-term__prompt-row">
            <span class="db-term__chevron">mysql&gt;</span>
            <textarea
              v-model="sql"
              class="db-term__textarea"
              rows="4"
              placeholder="SELECT * FROM table_name LIMIT 100"
              @keydown="handleKeydown"
              spellcheck="false"
            />
          </div>
          <div class="db-term__toolbar">
            <button class="db-term__run" :disabled="queryLoading" @click="handleExecute">
              {{ queryLoading ? '...' : '▶ Run' }}
            </button>
            <span class="db-term__shortcut">Ctrl+Enter</span>
          </div>
        </div>

        <div class="db-term__output">
          <template v-if="queryResult">
            <div class="db-term__result-head">
              {{ queryResult.rowCount }} rows · {{ queryResult.duration_ms }}ms
              <span v-if="queryResult.hasMore" class="db-term__warn">may have more</span>
            </div>
            <div class="db-term__table-wrap">
              <n-data-table
                :columns="resultColumns"
                :data="queryResult.list"
                :bordered="false"
                flex-height
                style="height: 100%"
                size="small"
                :scroll-x="resultColumns.length * 150"
              />
            </div>
          </template>
          <div v-else-if="executeResult && !executeResult.preview" class="db-term__ok">
            Query OK, {{ executeResult.affected }} rows affected ({{ executeResult.duration_ms }}ms)
          </div>
          <div v-else class="db-term__idle"><span class="db-term__blink">█</span></div>
        </div>
      </div>
    </div>
  </div>
  </n-config-provider>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, h } from 'vue'
import { NConfigProvider, NDataTable, NTree, darkTheme, useMessage, useDialog } from 'naive-ui'
import type { TreeOption, DataTableColumns } from 'naive-ui'
import {
  getTables,
  executeQuery,
  executeWrite,
  type TableMeta,
  type QueryResponse,
  type ExecuteResponse,
} from '@/api/database'

const message = useMessage()
const dialog = useDialog()

const themeOverrides = {
  common: {
    primaryColor: '#7dcfff',
    primaryColorHover: '#a3dfff',
    primaryColorPressed: '#5cb8e6',
    bodyColor: '#1a1b26',
    cardColor: '#24283b',
    modalColor: '#24283b',
    popoverColor: '#24283b',
    borderColor: '#3b4261',
    textColor1: '#c0caf5',
    textColor2: '#a9b1d6',
    textColor3: '#565f89',
  },
  DataTable: {
    thColor: '#24283b',
    tdColor: '#1a1b26',
    thTextColor: '#565f89',
    tdTextColor: '#c0caf5',
    borderColor: '#3b4261',
  },
  Tree: {
    nodeTextColor: '#c0caf5',
    nodeColorHover: 'rgba(255, 255, 255, 0.05)',
    nodeColorActive: 'rgba(255, 255, 255, 0.08)',
    nodeColorSelected: 'rgba(157, 206, 106, 0.12)',
  },
}

const tables = ref<TableMeta[]>([])
const tablesLoading = ref(false)
const sql = ref('SELECT 1')
const queryLoading = ref(false)
const queryResult = ref<QueryResponse | null>(null)
const executeResult = ref<ExecuteResponse | null>(null)
const sidebarCollapsed = ref(false)

const treeData = computed(() =>
  tables.value.map((table) => ({
    key: table.name,
    label: table.name,
    suffix: () =>
      h('span', { style: 'color: #565f89; font-size: 11px; margin-left: 6px' }, `${table.rowCount ?? 0}`),
    children: (table.columns || []).map((col) => ({
      key: `${table.name}.${col.name}`,
      label: `${col.name}  (${col.type})`,
      suffix: () => {
        const parts = [col.type]
        if (col.pk) parts.push('PK')
        if (col.notnull) parts.push('NN')
        return h('span', { style: 'color: #565f89; font-size: 11px; margin-left: 6px' }, parts.join(' '))
      },
    })),
  })),
)

const resultColumns = computed<DataTableColumns>(() => {
  if (!queryResult.value?.list?.length) return []
  const keys = Object.keys(queryResult.value.list[0] ?? {})
  return keys.map((key) => ({
    title: key,
    key: key,
    width: 150,
    ellipsis: true,
    render: (row: Record<string, unknown>) => {
      const val = row[key]
      if (val === null || val === undefined) return h('span', { style: 'color: #565f89' }, 'NULL')
      const str = String(val)
      return str.length > 200 ? str.slice(0, 200) + '...' : str
    },
  }))
})

async function fetchTables() {
  tablesLoading.value = true
  try {
    tables.value = await getTables()
  } catch (err) {
    message.error((err as Error).message || '加载表列表失败')
  } finally {
    tablesLoading.value = false
  }
}

function handleSelectTable(_keys: Array<string | number>, option: (TreeOption | null)[]) {
  if (!option.length) return
  const key = String(option[0]?.key)
  if (!key.includes('.')) {
    sql.value = `SELECT * FROM ${key} LIMIT 100`
  }
}

function handleKeydown(e: KeyboardEvent) {
  if (e.ctrlKey && e.key === 'Enter') {
    e.preventDefault()
    handleExecute()
  }
}

const WRITE_KEYWORDS = ['INSERT', 'UPDATE', 'DELETE', 'CREATE', 'ALTER', 'DROP', 'TRUNCATE']
const SENSITIVE_KEYWORDS = ['DELETE', 'TRUNCATE', 'DROP']

async function handleExecute() {
  const trimmed = sql.value.trim()
  if (!trimmed) return

  const firstWord = trimmed.split(/\s+/)[0]?.toUpperCase() || ''
  const isWrite = WRITE_KEYWORDS.includes(firstWord)
  const isSensitive = SENSITIVE_KEYWORDS.includes(firstWord)

  queryLoading.value = true
  executeResult.value = null

  try {
    if (!isWrite) {
      const res = await executeQuery(trimmed)
      queryResult.value = res
    } else if (isSensitive) {
      await confirmAndExecute(trimmed)
    } else {
      const res = await executeWrite(trimmed, true)
      executeResult.value = res
      queryResult.value = null
      fetchTables()
    }
  } catch (err) {
    message.error((err as Error).message || '执行失败')
  } finally {
    queryLoading.value = false
  }
}

async function confirmAndExecute(sqlStr: string) {
  const preview = await executeWrite(sqlStr, false)
  dialog.warning({
    title: '⚠ 确认执行危险操作',
    content: `${preview.message}\n\nSQL:\n${sqlStr}`,
    positiveText: '确认执行',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const res = await executeWrite(sqlStr, true)
        executeResult.value = res
        queryResult.value = null
        message.success(`执行成功，影响 ${res.affected} 行`)
        fetchTables()
      } catch (err) {
        message.error((err as Error).message || '执行失败')
      }
    },
  })
}

onMounted(fetchTables)
</script>

<style scoped>
.db-term {
  --t-bg: #1a1b26;
  --t-surface: #24283b;
  --t-border: #3b4261;
  --t-text: #c0caf5;
  --t-text-dim: #565f89;
  --t-green: #9ece6a;
  --t-cyan: #7dcfff;
  --t-red: #f7768e;
  --t-yellow: #e0af68;

  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  background: var(--t-bg);
  color: var(--t-text);
  font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', Consolas, 'Courier New', monospace;
  font-size: 13px;
  border-radius: 8px;
  overflow: hidden;
}

/* Title bar */
.db-term__bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  background: var(--t-surface);
  border-bottom: 1px solid var(--t-border);
  user-select: none;
}
.db-term__dots { display: flex; gap: 6px; }
.db-term__dots span { width: 12px; height: 12px; border-radius: 50%; }
.db-term__dots span:nth-child(1) { background: #ff5f57; }
.db-term__dots span:nth-child(2) { background: #febc2e; }
.db-term__dots span:nth-child(3) { background: #28c840; }
.db-term__bar-title { flex: 1; color: var(--t-text-dim); font-size: 12px; }
.db-term__bar-btn {
  background: none;
  border: 1px solid var(--t-border);
  color: var(--t-text-dim);
  padding: 2px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-family: inherit;
  font-size: 11px;
}
.db-term__bar-btn:hover { color: var(--t-text); border-color: var(--t-text-dim); }

/* Body layout */
.db-term__body { flex: 1; display: flex; min-height: 0; }

/* Sidebar */
.db-term__side {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid var(--t-border);
  display: flex;
  flex-direction: column;
}
.db-term__side-head {
  padding: 8px 12px;
  font-size: 11px;
  font-weight: 600;
  color: var(--t-text-dim);
  border-bottom: 1px solid var(--t-border);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.db-term__side-loading,
.db-term__side-tree { flex: 1; overflow: auto; padding: 4px 8px; }
.db-term__side-loading { display: flex; align-items: flex-start; padding-top: 16px; padding-left: 12px; }

/* Main panel */
.db-term__main { flex: 1; display: flex; flex-direction: column; min-width: 0; }

/* Prompt area */
.db-term__prompt { flex-shrink: 0; padding: 12px; border-bottom: 1px solid var(--t-border); }
.db-term__prompt-row { display: flex; align-items: flex-start; }
.db-term__chevron {
  flex-shrink: 0;
  color: var(--t-cyan);
  margin-right: 8px;
  padding-top: 2px;
  font-weight: 700;
  user-select: none;
}
.db-term__textarea {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--t-text);
  font-family: inherit;
  font-size: 13px;
  line-height: 1.5;
  resize: none;
  outline: none;
}
.db-term__textarea::placeholder { color: var(--t-text-dim); }
.db-term__toolbar { display: flex; align-items: center; gap: 8px; margin-top: 8px; }
.db-term__run {
  background: var(--t-green);
  color: #1a1b26;
  border: none;
  padding: 3px 14px;
  border-radius: 3px;
  cursor: pointer;
  font-family: inherit;
  font-size: 12px;
  font-weight: 600;
}
.db-term__run:disabled { opacity: 0.5; cursor: not-allowed; }
.db-term__run:hover:not(:disabled) { background: #b9f27c; }
.db-term__shortcut { color: var(--t-text-dim); font-size: 11px; }

/* Output */
.db-term__output { flex: 1; min-height: 0; display: flex; flex-direction: column; }
.db-term__result-head {
  padding: 6px 12px;
  font-size: 11px;
  color: var(--t-text-dim);
  border-bottom: 1px solid var(--t-border);
  display: flex;
  align-items: center;
  gap: 8px;
}
.db-term__warn { color: var(--t-yellow); font-size: 10px; }
.db-term__table-wrap { flex: 1; min-height: 0; }
.db-term__ok { padding: 12px; color: var(--t-green); }
.db-term__idle { flex: 1; display: flex; align-items: center; justify-content: center; color: var(--t-text-dim); }

/* Blink cursor */
.db-term__blink { animation: blink 1s step-end infinite; }
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

/* ── Naive UI font overrides ── */
.db-term :deep(.n-data-table-th) {
  font-family: inherit;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 11px;
  letter-spacing: 0.3px;
}
.db-term :deep(.n-data-table-td) { font-family: inherit; font-size: 12px; }
.db-term :deep(.n-tree) { font-size: 12px; }
.db-term :deep(.n-tree-node-content) { font-family: inherit; }
.db-term :deep(.n-tree--block-line .n-tree-node-content) {
  border-bottom: 1px solid rgba(59, 66, 97, 0.3);
}

/* Scrollbar */
.db-term ::-webkit-scrollbar { width: 6px; height: 6px; }
.db-term ::-webkit-scrollbar-track { background: var(--t-bg); }
.db-term ::-webkit-scrollbar-thumb { background: var(--t-border); border-radius: 3px; }
.db-term ::-webkit-scrollbar-thumb:hover { background: var(--t-text-dim); }
</style>
