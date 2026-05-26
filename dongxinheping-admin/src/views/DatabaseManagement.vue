<template>
  <div style="display: flex; flex-direction: column; height: 100%; min-height: 0">
    <!-- Header -->
    <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px">
      <h3 style="margin: 0">数据库管理</h3>
      <n-tag v-if="tables.length" size="small" :bordered="false" type="info">{{ tables.length }} 张表</n-tag>
      <div style="margin-left: auto">
        <n-button quaternary size="small" @click="sidebarCollapsed = !sidebarCollapsed">
          {{ sidebarCollapsed ? '显示 Schema' : '隐藏 Schema' }}
        </n-button>
      </div>
    </div>

    <!-- Main -->
    <div style="display: flex; flex: 1; min-height: 0; gap: 16px">
      <!-- Left sidebar: Schema Tree -->
      <div
        v-if="!sidebarCollapsed"
        style="width: 300px; flex-shrink: 0; border: 1px solid var(--n-border-color); border-radius: var(--n-border-radius); display: flex; flex-direction: column; overflow: hidden"
      >
        <div style="padding: 8px 12px; font-size: 12px; font-weight: 500; color: #999; border-bottom: 1px solid var(--n-border-color)">
          Schema
        </div>
        <div v-if="tablesLoading" style="display: flex; align-items: center; justify-content: center; padding: 32px">
          <n-spin size="small" />
        </div>
        <div v-else style="flex: 1; overflow: auto; padding: 4px">
          <n-tree :data="treeData" block-line selectable :default-expand-all="false" @update:selected-keys="handleSelectTable" />
        </div>
      </div>

      <!-- Right area -->
      <div style="flex: 1; display: flex; flex-direction: column; min-width: 0; gap: 12px">
        <!-- SQL Editor -->
        <div style="flex-shrink: 0">
          <n-input
            v-model:value="sql"
            type="textarea"
            :rows="6"
            placeholder="输入 SQL 语句，如: SELECT * FROM t_inventory LIMIT 10"
            @keydown="handleKeydown"
          />
          <div style="margin-top: 8px">
            <n-button type="primary" size="small" :loading="queryLoading" @click="handleExecute">执行</n-button>
            <span style="margin-left: 8px; font-size: 12px; color: #999">Ctrl+Enter 快捷执行</span>
          </div>
        </div>

        <!-- Result -->
        <div style="flex: 1; min-height: 0; display: flex; flex-direction: column">
          <div v-if="queryResult" style="flex: 1; min-height: 0; display: flex; flex-direction: column">
            <div style="margin-bottom: 4px; font-size: 12px; color: #999">
              {{ queryResult.rowCount }} 行 · {{ queryResult.duration_ms }}ms
              <n-tag v-if="queryResult.hasMore" size="tiny" type="warning" :bordered="false" style="margin-left: 4px">可能还有更多</n-tag>
            </div>
            <n-data-table
              :columns="resultColumns"
              :data="queryResult.list"
              :bordered="true"
              flex-height
              style="height: 100%"
              size="small"
              :scroll-x="resultColumns.length * 150"
            />
          </div>
          <div v-else-if="executeResult && !executeResult.preview" style="padding: 12px; background: #f6ffed; border-radius: 4px; font-size: 13px">
            执行成功，影响 {{ executeResult.affected }} 行 ({{ executeResult.duration_ms }}ms)
          </div>
          <div
            v-else
            style="flex: 1; display: flex; align-items: center; justify-content: center; color: #999; font-size: 13px"
          >
            输入 SQL 语句并执行
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, h } from 'vue'
import { NTag, NButton, NInput, NDataTable, NTree, NSpin, useMessage, useDialog } from 'naive-ui'
import type { TreeOption, DataTableColumns } from 'naive-ui'
import { getTables, executeQuery, executeWrite, type TableMeta, type QueryResponse, type ExecuteResponse } from '@/api/database'

const message = useMessage()
const dialog = useDialog()

const tables = ref<TableMeta[]>([])
const tablesLoading = ref(false)
const sql = ref('SELECT 1')
const queryLoading = ref(false)
const queryResult = ref<QueryResponse | null>(null)
const executeResult = ref<ExecuteResponse | null>(null)
const sidebarCollapsed = ref(false)

const treeData = computed<TreeOption[]>(() =>
  tables.value.map((table) => ({
    key: table.name,
    label: table.name,
    suffix: () =>
      h(NTag, { size: 'small', bordered: false, type: 'info' }, () => `${table.rowCount ?? 0}`),
    children: (table.columns || []).map((col) => ({
      key: `${table.name}.${col.name}`,
      label: `${col.name}  (${col.type})`,
      suffix: () => {
        const tags: string[] = []
        if (col.pk) tags.push('PK')
        if (col.notnull) tags.push('NN')
        if (!tags.length) return null
        return h(
          'span',
          { style: 'font-size: 11px; color: #999; margin-left: 4px' },
          tags.join(' '),
        )
      },
    })),
  })),
)

const resultColumns = computed<DataTableColumns>(() => {
  if (!queryResult.value?.list?.length) return []
  const keys = Object.keys(queryResult.value?.list?.[0] ?? {})
  return keys.map((key) => ({
    title: key,
    key: key,
    width: 150,
    ellipsis: true,
    render: (row: Record<string, unknown>) => {
      const val = row[key]
      if (val === null || val === undefined) return h('span', { style: 'color: #bbb' }, 'NULL')
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

async function handleExecute() {
  const trimmed = sql.value.trim()
  if (!trimmed) return

  const firstWord = trimmed.split(/\s+/)[0]?.toUpperCase() || ''
  const isWrite = ['INSERT', 'UPDATE', 'DELETE', 'CREATE', 'ALTER', 'DROP', 'TRUNCATE'].includes(firstWord)

  queryLoading.value = true
  executeResult.value = null

  try {
    if (isWrite) {
      await handleWriteExecute(trimmed)
    } else {
      const res = await executeQuery(trimmed)
      queryResult.value = res
    }
  } catch (err) {
    message.error((err as Error).message || '执行失败')
  } finally {
    queryLoading.value = false
  }
}

async function handleWriteExecute(sqlStr: string) {
  const preview = await executeWrite(sqlStr, false)

  dialog.warning({
    title: '确认执行写操作',
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
