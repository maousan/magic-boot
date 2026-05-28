<template>
  <n-card
    title="标签绑定"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-button type="primary" @click="showCreate = true">新增绑定</n-button>
    </template>

    <div class="table-page-content">
      <n-space :size="12" class="table-page-toolbar">
        <n-input v-model:value="query.locationCode" placeholder="库位编码" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-input v-model:value="query.labelCode" placeholder="标签编码" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-button type="primary" @click="handleSearch">查询</n-button>
      </n-space>

      <div ref="tableAreaRef" class="table-page-table label-mapping-table-area">
        <n-data-table
          :columns="columns"
          :data="bindings.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: LocationLabelBinding) => row.id"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="bindings.total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <n-modal v-model:show="showCreate" title="新增库位-标签绑定" preset="dialog">
      <n-space vertical>
        <n-form-item label="库位码">
          <n-input v-model:value="form.locationCode" placeholder="请输入库位码" />
        </n-form-item>
        <n-form-item label="标签码">
          <n-input v-model:value="form.labelCode" placeholder="请输入标签码" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showCreate = false">取消</n-button>
        <n-button type="primary" :loading="creating" @click="handleCreate">确认</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showDelete" title="确认解绑" preset="dialog">
      <span>确定要解绑标签码 <b>{{ deleteTarget?.labelCode }}</b> 吗？</span>
      <template #action>
        <n-button @click="showDelete = false">取消</n-button>
        <n-button type="error" :loading="deleting" @click="handleDelete">解绑</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showLightOn" title="标签亮灯" preset="dialog">
      <n-space vertical>
        <span>标签码：<b>{{ lightOnTarget?.labelCode }}</b></span>
        <n-form-item label="灯光颜色">
          <n-select v-model:value="lightOnForm.color" :options="lightColorOptions" />
        </n-form-item>
        <n-form-item label="亮灯时长">
          <n-select v-model:value="lightOnForm.duration" :options="lightDurationOptions" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showLightOn = false">取消</n-button>
        <n-button type="warning" :loading="lighting" @click="handleLightOn">确认亮灯</n-button>
      </template>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NInput, NFlex, NPagination, NSelect, useMessage,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getLabelMappingList, createLabelMapping, deleteLabelMapping, lightOnLabel, lightOffLabel } from '@/api/label-mapping'
import type { LocationLabelBinding } from '@/types'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const loading = ref(false)
const creating = ref(false)
const deleting = ref(false)
const lighting = ref(false)
const showCreate = ref(false)
const showDelete = ref(false)
const showLightOn = ref(false)
const deleteTarget = ref<{ id: string; labelCode: string } | null>(null)
const lightOnTarget = ref<{ labelCode: string } | null>(null)
const lightOnForm = reactive({ color: 'GREEN', duration: 'inf' })
const message = useMessage()
const page = ref(1)
const pageSize = ref(20)
const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()

const bindings = reactive<{ list: LocationLabelBinding[]; total: number }>({
  list: [],
  total: 0,
})

const form = reactive({ locationCode: '', labelCode: '' })

const query = reactive({ locationCode: '', labelCode: '' })

const lightColorOptions = [
  { label: '红色', value: 'RED' },
  { label: '黄色', value: 'YELLOW' },
  { label: '绿色', value: 'GREEN' },
]

const lightDurationOptions = [
  { label: '10秒', value: '10s' },
  { label: '30秒', value: '30s' },
  { label: '1分钟', value: '1m' },
  { label: '2分钟', value: '2m' },
  { label: '5分钟', value: '5m' },
  { label: '10分钟', value: '10m' },
  { label: '15分钟', value: '15m' },
  { label: '20分钟', value: '20m' },
  { label: '30分钟', value: '30m' },
  { label: '60分钟', value: '60m' },
  { label: '持续亮灯', value: 'inf' },
]

const columns: DataTableColumns<LocationLabelBinding> = [
  { title: '库位码', key: 'locationCode', width: 220 },
  { title: '标签码', key: 'labelCode', width: 220 },
  { title: '绑定时间', key: 'bindTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 220,
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NButton, { size: 'small', type: 'warning', onClick: () => openLightOn(row) }, () => '亮灯'),
        h(NButton, { size: 'small', onClick: () => handleLightOff(row) }, () => '灭灯'),
        h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '解绑'),
      ]),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getLabelMappingList({
      page: page.value,
      pageSize: pageSize.value,
      locationCode: query.locationCode || undefined,
      labelCode: query.labelCode || undefined,
    })
    bindings.list = res.list
    bindings.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

async function handleCreate() {
  if (!form.locationCode || !form.labelCode) return
  creating.value = true
  try {
    await createLabelMapping({ locationCode: form.locationCode, labelCode: form.labelCode })
    showCreate.value = false
    form.locationCode = ''
    form.labelCode = ''
    await fetchData()
  } finally {
    creating.value = false
  }
}

function openLightOn(row: LocationLabelBinding) {
  lightOnTarget.value = { labelCode: row.labelCode }
  lightOnForm.color = 'GREEN'
  lightOnForm.duration = 'inf'
  showLightOn.value = true
}

async function handleLightOn() {
  if (!lightOnTarget.value) return
  lighting.value = true
  try {
    const res = await lightOnLabel({
      labelCode: lightOnTarget.value.labelCode,
      color: lightOnForm.color,
      duration: lightOnForm.duration,
    })
    showLightOn.value = false
    if (res.success) {
      message.success(`标签 ${lightOnTarget.value.labelCode} 亮灯成功`)
    } else {
      message.error(`标签 ${lightOnTarget.value.labelCode} 亮灯失败`)
    }
  } finally {
    lighting.value = false
  }
}

async function handleLightOff(row: LocationLabelBinding) {
  try {
    const res = await lightOffLabel({ labelCode: row.labelCode })
    if (res.success) {
      message.success(`标签 ${row.labelCode} 灭灯成功`)
    } else {
      message.error(`标签 ${row.labelCode} 灭灯失败`)
    }
  } catch {
    message.error(`标签 ${row.labelCode} 灭灯失败`)
  }
}

function openDelete(row: LocationLabelBinding) {
  deleteTarget.value = { id: row.id, labelCode: row.labelCode }
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteLabelMapping(deleteTarget.value.id)
    showDelete.value = false
    deleteTarget.value = null
    await fetchData()
  } finally {
    deleting.value = false
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

.label-mapping-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
