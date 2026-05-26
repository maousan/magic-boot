<template>
  <n-card
    title="颜色管理"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-space>
        <n-button type="primary" @click="openCreate">新增颜色</n-button>
        <n-button type="primary" @click="handleSearch">查询</n-button>
      </n-space>
    </template>

    <div class="table-page-content">
      <n-space :size="12" class="table-page-toolbar">
        <n-input v-model:value="query.code" placeholder="颜色代码" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-select v-model:value="query.color" :options="[{ label: '全部', value: '' }, ...colorOptions]" placeholder="颜色" clearable style="width: 120px" />
      </n-space>

      <div ref="tableAreaRef" class="table-page-table led-color-table-area">
        <n-data-table
          :columns="columns"
          :data="records.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: LedColor) => row.id"
          :scroll-x="620"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="records.total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <!-- 新增弹窗 -->
    <n-modal v-model:show="showCreate" title="新增颜色映射" preset="dialog">
      <n-space vertical>
        <n-form-item label="颜色代码" required>
          <n-input v-model:value="form.code" placeholder="控制指令代码" />
        </n-form-item>
        <n-form-item label="颜色" required>
          <n-select v-model:value="form.color" :options="colorOptions" />
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="form.remark" placeholder="可选" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showCreate = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleCreate">确认</n-button>
      </template>
    </n-modal>

    <!-- 编辑弹窗 -->
    <n-modal v-model:show="showEdit" title="编辑颜色映射" preset="dialog">
      <n-space vertical>
        <n-form-item label="颜色代码" required>
          <n-input v-model:value="editForm.code" />
        </n-form-item>
        <n-form-item label="颜色" required>
          <n-select v-model:value="editForm.color" :options="colorOptions" />
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="editForm.remark" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showEdit = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleEdit">保存</n-button>
      </template>
    </n-modal>

    <!-- 删除确认 -->
    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <span>确定要删除颜色代码 <b>{{ deleteTarget?.code }}</b>（{{ deleteTarget?.color }}）吗？</span>
      <template #action>
        <n-button @click="showDelete = false">取消</n-button>
        <n-button type="error" :loading="submitting" @click="handleDelete">删除</n-button>
      </template>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NInput, NSelect,
  NFlex, NPagination, NTag,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getLedColorList, createLedColor, updateLedColor, deleteLedColor } from '@/api/led-color'
import type { LedColor } from '@/types'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const loading = ref(false)
const submitting = ref(false)
const showCreate = ref(false)
const showEdit = ref(false)
const showDelete = ref(false)
const deleteTarget = ref<LedColor | null>(null)

const page = ref(1)
const pageSize = ref(20)
const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()

const query = reactive({ code: '', color: '' as string })
const form = reactive({ code: '', color: 'RED' as string, remark: '' })
const editForm = reactive({ id: 0, code: '', color: 'RED' as string, remark: '' })

const records = reactive<{ list: LedColor[]; total: number }>({ list: [], total: 0 })

const colorOptions = [
  { label: '红色', value: 'RED' },
  { label: '黄色', value: 'YELLOW' },
  { label: '绿色', value: 'GREEN' },
]

const colorTagMap: Record<string, 'error' | 'warning' | 'success'> = {
  RED: 'error',
  YELLOW: 'warning',
  GREEN: 'success',
}

const columns: DataTableColumns<LedColor> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '颜色代码', key: 'code', width: 140 },
  {
    title: '颜色',
    key: 'color',
    width: 120,
    render: (row) => h(NTag, { type: colorTagMap[row.color] || 'default', size: 'small' }, () => row.color),
  },
  { title: '备注', key: 'remark' },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NButton, { size: 'small', onClick: () => openEdit(row) }, () => '编辑'),
        h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '删除'),
      ]),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getLedColorList({
      page: page.value,
      pageSize: pageSize.value,
      code: query.code || undefined,
      color: query.color || undefined,
    })
    records.list = res.list
    records.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function openCreate() {
  form.code = ''
  form.color = 'RED'
  form.remark = ''
  showCreate.value = true
}

async function handleCreate() {
  if (!form.code || !form.color) return
  submitting.value = true
  try {
    await createLedColor({ code: form.code, color: form.color, remark: form.remark || undefined })
    showCreate.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

function openEdit(row: LedColor) {
  editForm.id = row.id
  editForm.code = row.code
  editForm.color = row.color
  editForm.remark = row.remark
  showEdit.value = true
}

async function handleEdit() {
  submitting.value = true
  try {
    await updateLedColor({
      id: editForm.id,
      code: editForm.code,
      color: editForm.color,
      remark: editForm.remark,
    })
    showEdit.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

function openDelete(row: LedColor) {
  deleteTarget.value = row
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  submitting.value = true
  try {
    await deleteLedColor(deleteTarget.value.id)
    showDelete.value = false
    deleteTarget.value = null
    await fetchData()
  } finally {
    submitting.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.table-page-card {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.table-page-card :deep(.n-card__content) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.table-page-content {
  flex: 1;
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.table-page-toolbar,
.table-page-pagination {
  flex-shrink: 0;
}

.led-color-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
