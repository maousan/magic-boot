<template>
  <n-card title="巷道灯绑定">
    <template #header-extra>
      <n-button type="primary" @click="showCreate = true">新增绑定</n-button>
    </template>

    <n-space :size="12" style="margin-bottom: 16px">
      <n-input v-model:value="query.locationCode" placeholder="库位编码" clearable style="width: 160px" @keyup.enter="handleSearch" />
      <n-input v-model:value="query.ledId" placeholder="设备ID" clearable style="width: 160px" @keyup.enter="handleSearch" />
      <n-select v-model:value="query.color" :options="[{ label: '全部', value: '' }, ...colorOptions]" placeholder="灯色" clearable style="width: 120px" />
      <n-button type="primary" @click="handleSearch">查询</n-button>
    </n-space>

    <n-data-table
      :columns="columns"
      :data="bindings.list"
      :loading="loading"
      :bordered="true"
      :row-key="(row: LocationLedBinding) => row.id"
    />

    <n-flex justify="end" style="margin-top: 12px">
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

    <n-modal v-model:show="showCreate" title="新增绑定" preset="dialog">
      <n-space vertical>
        <n-form-item label="库位码">
          <n-input v-model:value="form.lotNo" placeholder="请输入库位码" />
        </n-form-item>
        <n-form-item label="设备ID">
          <n-input v-model:value="form.ledId" placeholder="请输入设备ID" />
        </n-form-item>
        <n-form-item label="颜色">
          <n-select v-model:value="form.color" :options="colorOptions" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showCreate = false">取消</n-button>
        <n-button type="primary" :loading="creating" @click="handleCreate">确认</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <span>确定要删除该绑定记录吗？</span>
      <template #action>
        <n-button @click="showDelete = false">取消</n-button>
        <n-button type="error" :loading="deleting" @click="handleDelete">删除</n-button>
      </template>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NFlex, NPagination,
  NInput, NSelect, NTag,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getLedMappingList, createLedMapping, deleteLedMapping } from '@/api/led-mapping'
import type { LocationLedBinding } from '@/types'

const loading = ref(false)
const creating = ref(false)
const deleting = ref(false)
const showCreate = ref(false)
const showDelete = ref(false)
const deleteTarget = ref<{ id: string; locationCode: string } | null>(null)

const bindings = reactive<{ list: LocationLedBinding[]; total: number }>({
  list: [],
  total: 0,
})

const page = ref(1)
const pageSize = ref(20)

const form = reactive({ lotNo: '', ledId: '', color: 'RED' as string })

const query = reactive({ locationCode: '', ledId: '', color: '' as string })

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

const columns: DataTableColumns<LocationLedBinding> = [
  { title: '库位码', key: 'locationCode' },
  { title: '设备ID', key: 'ledId' },
  {
    title: '颜色',
    key: 'color',
    render: (row) => h(NTag, { type: colorTagMap[row.color] || 'default', size: 'small' }, () => row.color),
  },
  {
    title: '操作',
    key: 'actions',
    render: (row) =>
      h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '删除'),
  },
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getLedMappingList({
      page: page.value,
      pageSize: pageSize.value,
      locationCode: query.locationCode || undefined,
      ledId: query.ledId || undefined,
      color: query.color || undefined,
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
  if (!form.lotNo || !form.ledId) return
  creating.value = true
  try {
    await createLedMapping({ lotNo: form.lotNo, ledId: form.ledId, color: form.color })
    showCreate.value = false
    form.lotNo = ''
    form.ledId = ''
    form.color = 'RED'
    await fetchData()
  } finally {
    creating.value = false
  }
}

function openDelete(row: LocationLedBinding) {
  deleteTarget.value = { id: row.id, locationCode: row.locationCode }
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await deleteLedMapping(deleteTarget.value.id)
    showDelete.value = false
    await fetchData()
  } finally {
    deleting.value = false
  }
}

onMounted(fetchData)
</script>
