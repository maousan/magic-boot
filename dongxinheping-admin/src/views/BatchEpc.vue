<template>
  <n-card
    title="批次 EPC 管理"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-button @click="showBind = true">绑定</n-button>
    </template>

    <div class="table-page-content">
      <n-space :size="12" class="table-page-toolbar">
        <n-input v-model:value="searchKeyword" placeholder="搜索批次ID或EPC" clearable @keyup.enter="fetchData" />
        <n-select
          v-model:value="searchStatus"
          :options="statusOptions"
          placeholder="状态"
          clearable
          style="width: 120px"
        />
        <n-button type="primary" @click="fetchData">搜索</n-button>
      </n-space>

      <div class="table-page-table">
        <n-data-table
          :columns="columns"
          :data="bindings.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: BatchEpcBinding) => row.id"
          :scroll-x="900"
          flex-height
          style="height: 100%"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :item-count="pagination.itemCount"
          :page-sizes="[10, 20, 50, 100]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <n-modal v-model:show="showBind" title="绑定批次 EPC" preset="dialog">
      <n-space vertical>
        <n-form-item label="批次ID">
          <n-input v-model:value="bindForm.batchId" placeholder="请输入批次ID" />
        </n-form-item>
        <n-form-item label="EPC">
          <n-input v-model:value="bindForm.epc" placeholder="请输入 EPC" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showBind = false">取消</n-button>
        <n-button type="primary" :loading="binding" @click="handleBind">确认</n-button>
      </template>
    </n-modal>

    <n-modal v-model:show="showUnbind" title="确认解绑" preset="dialog">
      <span>确定要解绑该 EPC 绑定吗？</span>
      <template #action>
        <n-button @click="showUnbind = false">取消</n-button>
        <n-button type="error" :loading="unbinding" @click="handleUnbind">解绑</n-button>
      </template>
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NInput, NSelect, NTag, NFlex, NPagination,
  NPopconfirm, useMessage,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getBatchEpcList, bindBatchEpc, unbindBatchEpc, deleteBatchEpc } from '@/api/batch-epc'
import type { BatchEpcBinding } from '@/types'

const message = useMessage()
const loading = ref(false)
const binding = ref(false)
const unbinding = ref(false)
const deletingIds = ref<Set<string>>(new Set())
const showBind = ref(false)
const showUnbind = ref(false)
const searchKeyword = ref('')
const searchStatus = ref<number | null>(null)
const unbindTarget = ref<BatchEpcBinding | null>(null)

const bindings = reactive<{ list: BatchEpcBinding[]; total: number }>({
  list: [],
  total: 0,
})

const pagination = reactive({ page: 1, pageSize: 10, itemCount: 0 })
const bindForm = reactive({ batchId: '', epc: '' })

const statusOptions = [
  { label: '已绑定', value: 1 },
  { label: '已解绑', value: 0 },
]

const columns: DataTableColumns<BatchEpcBinding> = [
  { title: '批次ID', key: 'batchId', width: 180 },
  { title: 'EPC', key: 'epc', width: 280 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) =>
      h(NTag, { type: row.status === 1 ? 'success' : 'default', size: 'small' }, () =>
        row.status === 1 ? '已绑定' : '已解绑',
      ),
  },
  { title: '绑定时间', key: 'bindTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    fixed: 'right',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        row.status === 1
          ? h(
              NButton,
              { size: 'small', type: 'error', onClick: () => openUnbind(row) },
              () => '解绑',
            )
          : null,
        h(NPopconfirm, { onPositiveClick: () => handleDelete(row) }, {
          trigger: () =>
            h(NButton, {
              size: 'small',
              type: 'error',
              ghost: true,
              loading: deletingIds.value.has(row.id),
            }, () => '删除'),
          default: () => `确认删除该 EPC 绑定记录？`,
        }),
      ]),
  },
]

async function handleDelete(row: BatchEpcBinding) {
  deletingIds.value.add(row.id)
  try {
    await deleteBatchEpc({ id: row.id })
    message.success('删除成功')
    await fetchData()
  } catch {
    // handled by interceptor
  } finally {
    deletingIds.value.delete(row.id)
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getBatchEpcList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value || undefined,
      status: searchStatus.value ?? undefined,
    })
    bindings.list = res.list
    bindings.total = res.total
    pagination.itemCount = res.total
  } finally {
    loading.value = false
  }
}

async function handleBind() {
  if (!bindForm.batchId || !bindForm.epc) return
  binding.value = true
  try {
    await bindBatchEpc({ batchId: bindForm.batchId, epc: bindForm.epc })
    showBind.value = false
    bindForm.batchId = ''
    bindForm.epc = ''
    await fetchData()
  } finally {
    binding.value = false
  }
}

function openUnbind(row: BatchEpcBinding) {
  unbindTarget.value = row
  showUnbind.value = true
}

async function handleUnbind() {
  if (!unbindTarget.value) return
  unbinding.value = true
  try {
    await unbindBatchEpc({ batchId: unbindTarget.value.batchId, epc: unbindTarget.value.epc })
    showUnbind.value = false
    await fetchData()
  } finally {
    unbinding.value = false
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
</style>
