<template>
  <div class="system-config-page">
    <n-card title="系统配置">
      <template #header-extra>
        <n-button secondary :loading="loading" @click="load">刷新</n-button>
      </template>
      <n-data-table
        :columns="columns"
        :data="rows"
        :loading="loading"
        :bordered="false"
        :row-key="(row: SystemConfigRow) => row.id"
      />
    </n-card>

    <n-modal
      v-model:show="showEdit"
      preset="dialog"
      title="编辑配置值"
      positive-text="保存"
      negative-text="取消"
      :loading="saving"
      @positive-click="handleSave"
    >
      <n-space vertical size="small">
        <div class="edit-key">{{ editing?.configureKey }}</div>
        <div v-if="editing?.configureName" class="edit-name">{{ editing.configureName }}</div>
        <n-input
          v-model:value="editValue"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 6 }"
          placeholder="配置值"
        />
      </n-space>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, ref } from 'vue'
import {
  NButton,
  NCard,
  NDataTable,
  NInput,
  NModal,
  NSpace,
  NSwitch,
  NTooltip,
  useMessage,
  type DataTableColumns,
} from 'naive-ui'
import { listSystemConfigs, saveSystemConfig, type SystemConfigRow } from '@/api/system-config'

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const rows = ref<SystemConfigRow[]>([])
const showEdit = ref(false)
const editing = ref<SystemConfigRow | null>(null)
const editValue = ref('')

async function load() {
  loading.value = true
  try {
    rows.value = await listSystemConfigs()
  } catch (e: any) {
    message.error(e?.response?.data?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function saveValue(row: SystemConfigRow, value: string) {
  await saveSystemConfig({ id: row.id, configureValue: value })
}

async function handleSwitch(row: SystemConfigRow, val: boolean) {
  const target = val ? 'true' : 'false'
  try {
    await saveValue(row, target)
    row.configureValue = target
    message.success(`${row.configureName || row.configureKey} 已${val ? '开启' : '关闭'}`)
  } catch (e: any) {
    message.error(e?.response?.data?.message || '保存失败')
  }
}

function openEdit(row: SystemConfigRow) {
  editing.value = row
  editValue.value = row.configureValue
  showEdit.value = true
}

async function handleSave() {
  if (!editing.value) return false
  saving.value = true
  try {
    await saveValue(editing.value, editValue.value)
    editing.value.configureValue = editValue.value
    message.success('保存成功')
    return true
  } catch (e: any) {
    message.error(e?.response?.data?.message || '保存失败')
    return false
  } finally {
    saving.value = false
  }
}

const columns: DataTableColumns<SystemConfigRow> = [
  {
    title: '配置项',
    key: 'configureKey',
    width: 260,
    render: (row) =>
      h('span', { class: 'mono' }, row.configureKey),
  },
  { title: '说明', key: 'configureName', ellipsis: { tooltip: true } },
  {
    title: '值',
    key: 'configureValue',
    width: 140,
    render: (row) => {
      if (row.configureValue === 'true' || row.configureValue === 'false') {
        return h(NSwitch, {
          value: row.configureValue === 'true',
          size: 'small',
          'onUpdate:value': (val: boolean) => handleSwitch(row, val),
        })
      }
      return h(
        NTooltip,
        {},
        {
          trigger: () => h('span', { class: 'value-text' }, row.configureValue || '（空）'),
          default: () => row.configureValue,
        },
      )
    },
  },
  { title: '更新时间', key: 'updateDate', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 90,
    render: (row) =>
      h(
        NButton,
        { size: 'small', secondary: true, onClick: () => openEdit(row) },
        { default: () => '编辑' },
      ),
  },
]

onMounted(load)
</script>

<style scoped>
.system-config-page {
  padding: 16px;
}

.mono {
  font-family: monospace;
  font-size: 13px;
}

.value-text {
  max-width: 120px;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}

.edit-key {
  font-family: monospace;
  font-size: 13px;
  color: var(--n-text-color, #333);
}

.edit-name {
  font-size: 12px;
  color: #999;
}
</style>
