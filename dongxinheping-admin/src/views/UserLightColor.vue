<template>
  <n-card
    title="用户灯色映射"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-space>
        <n-input v-model:value="searchUserId" placeholder="用户ID" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-button type="primary" @click="handleSearch">查询</n-button>
        <n-button type="primary" @click="openAdd">新增</n-button>
      </n-space>
    </template>

    <div class="table-page-content">
      <div ref="tableAreaRef" class="table-page-table user-light-color-table-area">
        <n-data-table
          :columns="columns"
          :data="list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: UserLightColor) => row.id"
          :scroll-x="980"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="total"
          :page-sizes="[20, 50, 100]"
          show-size-picker
          @update:page="loadData"
          @update:page-size="loadData"
        />
      </n-flex>
    </div>

    <n-modal v-model:show="showForm" :title="editId ? '编辑映射' : '新增映射'" preset="dialog" positive-text="确认" negative-text="取消" @positive-click="handleSubmit">
      <n-space vertical>
        <n-form-item label="用户ID" required>
          <n-input v-model:value="form.userId" placeholder="请输入用户ID" :disabled="!!editId" />
        </n-form-item>
        <n-form-item label="灯光颜色" required>
          <n-select v-model:value="form.color" :options="colorOptions" placeholder="选择颜色" />
        </n-form-item>
        <n-form-item label="是否启用">
          <n-switch v-model:value="form.enabled" :checked-value="1" :unchecked-value="0" />
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="form.remark" type="textarea" placeholder="备注（可选）" :rows="2" />
        </n-form-item>
      </n-space>
    </n-modal>

    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog" type="warning" positive-text="确认删除" negative-text="取消" @positive-click="handleDelete">
      确定要删除用户 <b>{{ deleteTarget?.userId }}</b> 的灯色映射吗？
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NSpace, NFlex, NInput, NSelect, NSwitch,
  NModal, NFormItem, NTag, NPagination, useMessage,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { getUserLightColorList, addUserLightColor, updateUserLightColor, deleteUserLightColor } from '@/api/user-light-color'
import type {UserLightColor} from '@/types'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const message = useMessage()

const loading = ref(false)
const list = ref<UserLightColor[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchUserId = ref('')
const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()

const showForm = ref(false)
const editId = ref<number | null>(null)
const form = reactive({ userId: '', color: '', enabled: 1, remark: '' })

const showDelete = ref(false)
const deleteTarget = ref<UserLightColor | null>(null)

const colorOptions = [
  { label: 'RED', value: 'RED' },
  { label: 'GREEN', value: 'GREEN' },
  { label: 'BLUE', value: 'BLUE' },
  { label: 'YELLOW', value: 'YELLOW' },
  { label: 'CYAN', value: 'CYAN' },
]

const colorTagType: Record<string, string> = {
  RED: 'error',
  GREEN: 'success',
  BLUE: 'info',
  YELLOW: 'warning',
  CYAN: 'success',
}

const columns: DataTableColumns<UserLightColor> = [
  { title: '用户ID', key: 'userId' },
  {
    title: '颜色',
    key: 'color',
    render: (row) =>
      h(NTag, { type: (colorTagType[row.color] || 'default') as any, size: 'small' }, () => row.color),
  },
  {
    title: '状态',
    key: 'enabled',
    render: (row) =>
      h(NTag, { type: row.enabled === 1 ? 'success' : 'default', size: 'small' }, () =>
        row.enabled === 1 ? '启用' : '停用',
      ),
  },
  { title: '备注', key: 'remark' },
  { title: '创建时间', key: 'createTime' },
  { title: '更新时间', key: 'updateTime' },
  {
    title: '操作',
    key: 'actions',
    render: (row) =>
      h(NSpace, { size: 'small' }, () => [
        h(NButton, { size: 'small', onClick: () => openEdit(row) }, () => '编辑'),
        h(NButton, { size: 'small', type: 'error', onClick: () => openDelete(row) }, () => '删除'),
      ]),
  },
]

async function loadData() {
  loading.value = true
  try {
    const res = await getUserLightColorList({
      page: page.value,
      pageSize: pageSize.value,
      userId: searchUserId.value || undefined,
    })
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  loadData()
}

function openAdd() {
  editId.value = null
  form.userId = ''
  form.color = ''
  form.enabled = 1
  form.remark = ''
  showForm.value = true
}

function openEdit(row: UserLightColor) {
  editId.value = row.id
  form.userId = row.userId
  form.color = row.color
  form.enabled = row.enabled
  form.remark = row.remark
  showForm.value = true
}

async function handleSubmit() {
  if (!form.userId || !form.color) {
    message.warning('用户ID和颜色不能为空')
    return false
  }
  try {
    if (editId.value) {
      await updateUserLightColor({ id: editId.value, ...form })
      message.success('修改成功')
    } else {
      await addUserLightColor(form)
      message.success('新增成功')
    }
    showForm.value = false
    loadData()
  } catch {
    return false
  }
}

function openDelete(row: UserLightColor) {
  deleteTarget.value = row
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  try {
    await deleteUserLightColor(deleteTarget.value.id)
    message.success('删除成功')
    deleteTarget.value = null
    loadData()
  } catch {
    return false
  }
}

onMounted(loadData)
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

.table-page-pagination {
  flex-shrink: 0;
}

.user-light-color-table-area {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
