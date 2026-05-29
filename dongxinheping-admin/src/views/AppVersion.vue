<template>
  <n-card
    title="App 版本管理"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-space>
        <n-button type="primary" @click="openCreate">新增版本</n-button>
        <n-button type="primary" @click="handleSearch">查询</n-button>
      </n-space>
    </template>

    <div class="table-page-content">
      <n-space :size="12" class="table-page-toolbar">
        <n-input v-model:value="keyword" placeholder="版本名称 / 更新说明" clearable style="width: 260px" @keyup.enter="handleSearch" />
      </n-space>

      <div ref="tableAreaRef" class="table-page-table">
        <n-data-table
          :columns="columns"
          :data="versions.list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: AppVersion) => row.id"
          :scroll-x="860"
          :max-height="tableBodyMaxHeight"
        />
      </div>

      <n-flex justify="end" class="table-page-pagination">
        <n-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :item-count="versions.total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          @update:page="fetchData"
          @update:page-size="fetchData"
        />
      </n-flex>
    </div>

    <!-- 新增弹窗 -->
    <n-modal v-model:show="showCreate" title="新增版本" preset="dialog" style="width: 520px">
      <n-space vertical>
        <n-form-item label="APK 文件" required>
          <n-upload :max="1" accept=".apk" :default-upload="false" @change="onFileChange">
            <n-button>选择文件</n-button>
          </n-upload>
          <n-text v-if="createForm.file" type="success" style="margin-left: 8px">{{ createForm.file?.name }}</n-text>
        </n-form-item>
        <n-form-item label="版本号" required>
          <n-input-number v-model:value="createForm.versionCode" :min="1" placeholder="整数，如 2" style="width: 100%" />
        </n-form-item>
        <n-form-item label="版本名称" required>
          <n-input v-model:value="createForm.versionName" placeholder="如 1.1.0" />
        </n-form-item>
        <n-form-item label="强制更新">
          <n-switch v-model:value="createForm.forceUpdate" />
        </n-form-item>
        <n-form-item label="更新说明">
          <n-input v-model:value="createForm.description" type="textarea" :rows="3" placeholder="1. 修复 xxx&#10;2. 新增 xxx" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showCreate = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleCreate">确认</n-button>
      </template>
    </n-modal>

    <!-- 编辑弹窗 -->
    <n-modal v-model:show="showEdit" title="修改版本" preset="dialog" style="width: 520px">
      <n-space vertical>
        <n-form-item label="替换 APK（可选）">
          <n-upload :max="1" accept=".apk" :default-upload="false" @change="onEditFileChange">
            <n-button>选择文件</n-button>
          </n-upload>
          <n-text v-if="editForm.file" type="success" style="margin-left: 8px">{{ editForm.file?.name }}</n-text>
        </n-form-item>
        <n-form-item label="版本号">
          <n-input-number v-model:value="editForm.versionCode" :min="1" style="width: 100%" />
        </n-form-item>
        <n-form-item label="版本名称">
          <n-input v-model:value="editForm.versionName" />
        </n-form-item>
        <n-form-item label="强制更新">
          <n-switch v-model:value="editForm.forceUpdate" />
        </n-form-item>
        <n-form-item label="更新说明">
          <n-input v-model:value="editForm.description" type="textarea" :rows="3" />
        </n-form-item>
      </n-space>
      <template #action>
        <n-button @click="showEdit = false">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleEdit">保存</n-button>
      </template>
    </n-modal>

    <!-- 删除确认 -->
    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog">
      <span>确定要删除版本 <b>{{ deleteTarget?.version_name }}</b>（v{{ deleteTarget?.version_code }}）吗？</span>
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
  NCard, NDataTable, NButton, NModal, NSpace, NFormItem, NInput, NInputNumber,
  NSwitch, NUpload, NText, NFlex, NPagination,
} from 'naive-ui'
import type { DataTableColumns, UploadFileInfo } from 'naive-ui'
import { getAppVersionList, createAppVersion, updateAppVersion, deleteAppVersion } from '@/api/app-version'
import type { AppVersion } from '@/types'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const loading = ref(false)
const submitting = ref(false)
const page = ref(1)
const pageSize = ref(20)
const keyword = ref('')
const showCreate = ref(false)
const showEdit = ref(false)
const showDelete = ref(false)
const deleteTarget = ref<AppVersion | null>(null)
const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()

const versions = reactive<{ list: AppVersion[]; total: number }>({ list: [], total: 0 })

const createForm = reactive({
  file: null as File | null,
  versionCode: null as number | null,
  versionName: '',
  forceUpdate: false,
  description: '',
})

const editForm = reactive({
  id: '',
  file: null as File | null,
  versionCode: null as number | null,
  versionName: '',
  forceUpdate: false,
  description: '',
})

const columns: DataTableColumns<AppVersion> = [
  { title: '版本号', key: 'version_code', width: 80 },
  { title: '版本名称', key: 'version_name', width: 100 },
  { title: '文件大小', key: 'apk_size', width: 90 },
  {
    title: '强制更新',
    key: 'force_update',
    width: 90,
    render: (row) => h(NText, { type: row.force_update ? 'error' : 'success' }, () => row.force_update ? '是' : '否'),
  },
  {
    title: '更新说明',
    key: 'description',
    ellipsis: { tooltip: true },
  },
  { title: '创建时间', key: 'create_date', width: 170 },
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
    const res = await getAppVersionList({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
    })
    versions.list = res.list
    versions.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function onFileChange({ file }: { file: UploadFileInfo }) {
  createForm.file = file.file ?? null
}

function onEditFileChange({ file }: { file: UploadFileInfo }) {
  editForm.file = file.file ?? null
}

function openCreate() {
  createForm.file = null
  createForm.versionCode = null
  createForm.versionName = ''
  createForm.forceUpdate = false
  createForm.description = ''
  showCreate.value = true
}

async function handleCreate() {
  if (!createForm.file) return
  if (!createForm.versionCode || !createForm.versionName) return

  const formData = new FormData()
  formData.append('file', createForm.file)
  formData.append('versionCode', String(createForm.versionCode))
  formData.append('versionName', createForm.versionName)
  if (createForm.forceUpdate) formData.append('forceUpdate', 'true')
  if (createForm.description) formData.append('description', createForm.description)

  submitting.value = true
  try {
    await createAppVersion(formData)
    showCreate.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

function openEdit(row: AppVersion) {
  editForm.id = row.id
  editForm.file = null
  editForm.versionCode = row.version_code
  editForm.versionName = row.version_name
  editForm.forceUpdate = row.force_update === 1
  editForm.description = row.description
  showEdit.value = true
}

async function handleEdit() {
  const formData = new FormData()
  formData.append('id', editForm.id)
  if (editForm.versionCode != null) formData.append('versionCode', String(editForm.versionCode))
  if (editForm.versionName) formData.append('versionName', editForm.versionName)
  formData.append('forceUpdate', editForm.forceUpdate ? 'true' : 'false')
  if (editForm.description != null) formData.append('description', editForm.description)
  if (editForm.file) formData.append('file', editForm.file)

  submitting.value = true
  try {
    await updateAppVersion(formData)
    showEdit.value = false
    await fetchData()
  } finally {
    submitting.value = false
  }
}

function openDelete(row: AppVersion) {
  deleteTarget.value = row
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return
  submitting.value = true
  try {
    await deleteAppVersion(deleteTarget.value.id)
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

.table-page-table {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
</style>
