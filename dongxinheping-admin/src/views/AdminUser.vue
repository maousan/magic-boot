<template>
  <n-card
    title="用户管理"
    class="table-page-card"
    style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column"
  >
    <template #header-extra>
      <n-space>
        <n-input v-model:value="searchUsername" placeholder="用户名" clearable style="width: 160px" @keyup.enter="handleSearch" />
        <n-button type="primary" @click="handleSearch">查询</n-button>
        <n-button v-perm="'AdminUser:create'" type="primary" @click="openAdd">新增用户</n-button>
      </n-space>
    </template>

    <div class="table-page-content">
      <div ref="tableAreaRef" class="table-page-table admin-user-table-area">
        <n-data-table
          :columns="columns"
          :data="list"
          :loading="loading"
          :bordered="true"
          :row-key="(row: AdminUserItem) => row.id"
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

    <!-- 新增/编辑 -->
    <n-modal v-model:show="showForm" :title="editId ? '编辑用户' : '新增用户'" preset="dialog" positive-text="确认" negative-text="取消" @positive-click="handleSubmit">
      <n-space vertical>
        <n-form-item label="用户名" required>
          <n-input v-model:value="form.username" placeholder="请输入用户名" :disabled="!!editId" :input-props="{ autocomplete: 'off' }" />
        </n-form-item>
        <n-form-item label="姓名">
          <n-input v-model:value="form.name" placeholder="姓名/昵称（可选）" />
        </n-form-item>
        <n-form-item v-if="!editId" label="初始密码" required>
          <n-input v-model:value="form.password" type="password" show-password-on="click" placeholder="至少6位" :input-props="{ autocomplete: 'new-password' }" />
        </n-form-item>
        <n-form-item label="角色" required>
          <n-select v-model:value="form.role" :options="roleOptions" placeholder="选择角色" />
        </n-form-item>
      </n-space>
    </n-modal>

    <!-- 重置密码 -->
    <n-modal v-model:show="showReset" title="重置密码" preset="dialog" positive-text="确认" negative-text="取消" @positive-click="handleReset">
      <n-space vertical>
        <p>为用户 <b>{{ resetTarget?.username }}</b> 设置新密码：</p>
        <n-input v-model:value="resetPassword" type="password" show-password-on="click" placeholder="至少6位" :input-props="{ autocomplete: 'new-password' }" />
      </n-space>
    </n-modal>

    <!-- 删除确认 -->
    <n-modal v-model:show="showDelete" title="确认删除" preset="dialog" type="warning" positive-text="确认删除" negative-text="取消" @positive-click="handleDelete">
      确定要删除用户 <b>{{ deleteTarget?.username }}</b> 吗？删除后该账号无法登录。
    </n-modal>
  </n-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NDataTable, NButton, NSpace, NFlex, NInput, NSelect,
  NModal, NFormItem, NTag, NPagination, useMessage, useDialog,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import {
  listAdminUsers, saveAdminUser, updateAdminUser,
  resetAdminUserPassword, toggleAdminUserLogin, deleteAdminUser,
} from '@/api/admin-user'
import type { AdminUserItem } from '@/api/admin-user'
import { getCurrentUser, canButton } from '@/auth'
import { useTableBodyHeight } from '@/composables/useTableBodyHeight'

const message = useMessage()
const dialog = useDialog()
const currentUser = getCurrentUser()

const { tableAreaRef, tableBodyMaxHeight } = useTableBodyHeight()

const loading = ref(false)
const list = ref<AdminUserItem[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchUsername = ref('')

const roleOptions = [
  { label: '管理员', value: 'admin' },
  { label: '运营', value: 'ops' },
]

const columns: DataTableColumns<AdminUserItem> = [
  { title: '用户名', key: 'username', width: 140 },
  { title: '姓名', key: 'name', width: 120, render: (row) => row.name || '-' },
  {
    title: '角色',
    key: 'role',
    width: 100,
    render: (row) =>
      h(NTag, { type: row.role === 'admin' ? 'success' : 'info', size: 'small', bordered: false }, { default: () => (row.role === 'admin' ? '管理员' : '运营') }),
  },
  {
    title: '状态',
    key: 'isLogin',
    width: 90,
    render: (row) =>
      h(NTag, { type: row.isLogin === 1 ? 'error' : 'success', size: 'small', bordered: false }, { default: () => (row.isLogin === 1 ? '已禁用' : '正常') }),
  },
  { title: '创建时间', key: 'createDate', width: 180, render: (row) => (row.createDate ? String(row.createDate).replace('T', ' ').slice(0, 19) : '-') },
  {
    title: '操作',
    key: 'actions',
    width: 320,
    render: (row) => {
      const actions = [
        canButton('AdminUser:resetPassword')
          ? h(NButton, { size: 'small', onClick: () => openReset(row) }, { default: () => '重置密码' })
          : null,
      ]
      if (row.id !== '1') {
        if (canButton('AdminUser:update')) {
          actions.push(
            h(NButton, { size: 'small', onClick: () => openEdit(row) }, { default: () => '编辑' }),
          )
        }
        if (row.id !== currentUser?.id) {
          if (canButton('AdminUser:toggle')) {
            actions.push(
              h(
                NButton,
                {
                  size: 'small',
                  type: row.isLogin === 1 ? 'success' : 'warning',
                  onClick: () => handleToggle(row),
                },
                { default: () => (row.isLogin === 1 ? '解禁' : '禁用') },
              ),
            )
          }
          if (canButton('AdminUser:delete')) {
            actions.push(
              h(
                NButton,
                { size: 'small', type: 'error', onClick: () => openDelete(row) },
                { default: () => '删除' },
              ),
            )
          }
        }
      }
      return h(NSpace, { size: 'small', wrap: false }, { default: () => actions.filter(Boolean) })
    },
  },
]

async function loadData() {
  loading.value = true
  try {
    const result = await listAdminUsers({
      page: page.value,
      pageSize: pageSize.value,
      username: searchUsername.value || undefined,
    })
    list.value = result.list
    total.value = result.total
  } catch {
    // 错误提示由请求拦截器统一弹出
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  loadData()
}

// 新增/编辑
const showForm = ref(false)
const editId = ref<string | null>(null)
const form = reactive<{ username: string; name: string; password: string; role: 'admin' | 'ops' }>({
  username: '',
  name: '',
  password: '',
  role: 'ops',
})

function openAdd() {
  editId.value = null
  form.username = ''
  form.name = ''
  form.password = ''
  form.role = 'ops'
  showForm.value = true
}

function openEdit(row: AdminUserItem) {
  editId.value = row.id
  form.username = row.username
  form.name = row.name || ''
  form.password = ''
  form.role = row.role
  showForm.value = true
}

async function handleSubmit() {
  if (!editId.value) {
    if (!form.username.trim()) {
      message.error('请输入用户名')
      return false
    }
    if (form.password.length < 6) {
      message.error('初始密码至少6位')
      return false
    }
    await saveAdminUser({ username: form.username.trim(), name: form.name.trim() || undefined, password: form.password, role: form.role })
    message.success('新增成功')
  } else {
    await updateAdminUser({ id: editId.value, name: form.name.trim() || undefined, role: form.role })
    message.success('修改成功')
  }
  loadData()
}

// 重置密码
const showReset = ref(false)
const resetTarget = ref<AdminUserItem | null>(null)
const resetPassword = ref('')

function openReset(row: AdminUserItem) {
  resetTarget.value = row
  resetPassword.value = ''
  showReset.value = true
}

async function handleReset() {
  if (resetPassword.value.length < 6) {
    message.error('新密码至少6位')
    return false
  }
  if (!resetTarget.value) return false
  await resetAdminUserPassword({ id: resetTarget.value.id, password: resetPassword.value })
  message.success('密码已重置')
}

// 禁用/解禁
function handleToggle(row: AdminUserItem) {
  const disable = row.isLogin !== 1
  dialog.warning({
    title: disable ? '禁用账号' : '解禁账号',
    content: disable ? `确定禁用用户 ${row.username} 吗？禁用后将立即踢下线。` : `确定解禁用户 ${row.username} 吗？`,
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: async () => {
      await toggleAdminUserLogin({ id: row.id, isLogin: disable ? 1 : 0 })
      message.success(disable ? '已禁用' : '已解禁')
      loadData()
    },
  })
}

// 删除
const showDelete = ref(false)
const deleteTarget = ref<AdminUserItem | null>(null)

function openDelete(row: AdminUserItem) {
  deleteTarget.value = row
  showDelete.value = true
}

async function handleDelete() {
  if (!deleteTarget.value) return false
  await deleteAdminUser(deleteTarget.value.id)
  message.success('已删除')
  loadData()
}

onMounted(loadData)
</script>
