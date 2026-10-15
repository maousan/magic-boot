<template>
  <div class="role-page">
    <n-card title="角色管理">
      <n-space style="margin-bottom: 12px">
        <n-button v-perm="'RoleManagement:create'" type="primary" @click="showCreate = true">新增角色</n-button>
        <n-button secondary :loading="loading" @click="fetchRoles">刷新</n-button>
      </n-space>

      <n-data-table
        :columns="columns"
        :data="roles"
        :loading="loading"
        :row-key="(r: RoleItem) => r.id"
        size="small"
      />
    </n-card>

    <!-- 新增 -->
    <n-modal v-model:show="showCreate" preset="dialog" title="新增角色">
      <n-form label-placement="left" label-width="90">
        <n-form-item label="角色名称">
          <n-input v-model:value="createForm.name" placeholder="例: 仓管" />
        </n-form-item>
        <n-form-item label="角色编码">
          <n-input v-model:value="createForm.code" placeholder="小写字母/数字/下划线，如 warehouse" />
        </n-form-item>
      </n-form>
      <div class="dialog-hint">创建后请在「菜单权限」页为该角色配置可见菜单，并在成员管理中绑定用户。</div>
      <template #action>
        <n-space>
          <n-button @click="showCreate = false">取消</n-button>
          <n-button type="primary" :loading="creating" @click="handleCreate">创建</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 编辑 -->
    <n-modal v-model:show="showRename" preset="dialog" title="编辑角色">
      <n-form label-placement="left" label-width="90">
        <n-form-item label="角色名称">
          <n-input v-model:value="renameForm.name" />
        </n-form-item>
      </n-form>
      <div class="dialog-hint">编码不可修改（菜单权限与用户绑定均按编码关联）。</div>
      <template #action>
        <n-space>
          <n-button @click="showRename = false">取消</n-button>
          <n-button type="primary" :loading="renaming" @click="handleRename">保存</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 成员管理 -->
    <n-modal v-model:show="showMembers" preset="dialog" title="成员管理" style="width: 640px">
      <template v-if="memberRole">
        <div class="dialog-hint" style="margin-bottom: 8px">
          角色「{{ memberRole.name }}（{{ memberRole.code }}）」的成员，勾选后保存为最终清单（整组覆盖）。
        </div>
        <n-spin :show="membersLoading">
          <div v-if="allUsers.length === 0" class="dialog-hint">暂无用户，请先在用户管理中创建。</div>
          <n-space :size="[16, 8]">
            <n-checkbox
              v-for="u in allUsers"
              :key="u.id"
              :checked="memberSelected.has(u.id)"
              @update:checked="(v: boolean) => toggleMember(u.id, v)"
            >{{ u.username }}{{ u.name ? `（${u.name}）` : '' }}</n-checkbox>
          </n-space>
        </n-spin>
      </template>
      <template #action>
        <n-space>
          <n-button @click="showMembers = false">取消</n-button>
          <n-button type="primary" :loading="membersSaving" @click="handleSaveMembers">保存</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { h, ref, reactive, onMounted } from 'vue'
import {
  NCard, NSpace, NButton, NDataTable, NModal, NForm, NFormItem, NInput,
  NSpin, NCheckbox, NTag, NPopconfirm, useMessage,
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { listRoles, createRole, updateRole, deleteRole, listRoleMembers, saveRoleMembers } from '@/api/role'
import type { RoleItem } from '@/api/role'
import { listAdminUsers } from '@/api/admin-user'
import type { AdminUserItem } from '@/api/admin-user'
import { canButton } from '@/auth'

const message = useMessage()
const loading = ref(false)
const roles = ref<RoleItem[]>([])

const showCreate = ref(false)
const creating = ref(false)
const createForm = reactive({ name: '', code: '' })

const showRename = ref(false)
const renaming = ref(false)
const renameForm = reactive({ id: '', name: '' })

const showMembers = ref(false)
const membersLoading = ref(false)
const membersSaving = ref(false)
const memberRole = ref<RoleItem | null>(null)
const memberSelected = ref<Set<string>>(new Set())
const allUsers = ref<AdminUserItem[]>([])

async function fetchRoles() {
  loading.value = true
  try {
    roles.value = await listRoles()
  } finally {
    loading.value = false
  }
}

async function fetchAllUsers() {
  const page = await listAdminUsers({ page: 1, pageSize: 500 })
  allUsers.value = page.list ?? []
}

const columns: DataTableColumns<RoleItem> = [
  { title: '角色名称', key: 'name' },
  { title: '编码', key: 'code', render: (row) => h('code', null, row.code) },
  { title: '成员数', key: 'userCount', width: 80 },
  {
    title: '内置',
    key: 'builtin',
    width: 70,
    render: (row) =>
      row.id === '1'
        ? h(NTag, { size: 'small', type: 'warning', bordered: false }, { default: () => '内置' })
        : null,
  },
  { title: '创建时间', key: 'createDate', width: 170 },
  {
    title: '操作',
    key: 'actions',
    width: 220,
    render: (row) =>
      h(
        NSpace,
        { size: 'small' },
        {
          default: () => [
            canButton('RoleManagement:update')
              ? h(NButton, { size: 'small', secondary: true, onClick: () => openRename(row) }, { default: () => '编辑' })
              : null,
            canButton('RoleManagement:members')
              ? h(NButton, { size: 'small', secondary: true, onClick: () => openMembers(row) }, { default: () => '成员管理' })
              : null,
            row.id === '1'
              ? null
              : canButton('RoleManagement:delete')
                ? h(
                    NPopconfirm,
                    { onPositiveClick: () => handleDelete(row) },
                    {
                      trigger: () =>
                        h(NButton, { size: 'small', type: 'error', secondary: true }, { default: () => '删除' }),
                      default: () => `确定删除角色「${row.name}」？将同时清理其菜单权限配置。`,
                    },
                  )
                : null,
          ].filter(Boolean),
        },
      ),
  },
]

async function handleCreate() {
  if (!createForm.name.trim()) {
    message.error('请输入角色名称')
    return
  }
  if (!createForm.code.trim()) {
    message.error('请输入角色编码')
    return
  }
  creating.value = true
  try {
    await createRole({ name: createForm.name.trim(), code: createForm.code.trim() })
    message.success('角色已创建，请到「菜单权限」配置可见菜单')
    showCreate.value = false
    createForm.name = ''
    createForm.code = ''
    await fetchRoles()
  } finally {
    creating.value = false
  }
}

function openRename(row: RoleItem) {
  renameForm.id = row.id
  renameForm.name = row.name
  showRename.value = true
}

async function handleRename() {
  if (!renameForm.name.trim()) {
    message.error('请输入角色名称')
    return
  }
  renaming.value = true
  try {
    await updateRole({ id: renameForm.id, name: renameForm.name.trim() })
    message.success('已保存')
    showRename.value = false
    await fetchRoles()
  } finally {
    renaming.value = false
  }
}

async function openMembers(row: RoleItem) {
  memberRole.value = row
  showMembers.value = true
  membersLoading.value = true
  try {
    if (allUsers.value.length === 0) {
      await fetchAllUsers()
    }
    const members = await listRoleMembers(row.id)
    memberSelected.value = new Set(members.map((m) => m.id))
  } finally {
    membersLoading.value = false
  }
}

function toggleMember(userId: string, checked: boolean) {
  const next = new Set(memberSelected.value)
  if (checked) {
    next.add(userId)
  } else {
    next.delete(userId)
  }
  memberSelected.value = next
}

async function handleSaveMembers() {
  if (!memberRole.value) return
  membersSaving.value = true
  try {
    await saveRoleMembers(memberRole.value.id, [...memberSelected.value])
    message.success('成员已保存')
    showMembers.value = false
    await fetchRoles()
  } finally {
    membersSaving.value = false
  }
}

async function handleDelete(row: RoleItem) {
  try {
    await deleteRole(row.id)
    message.success('已删除')
    await fetchRoles()
  } catch {
    // 错误提示由请求拦截器统一弹出
  }
}

onMounted(() => {
  fetchRoles()
  fetchAllUsers()
})
</script>

<style scoped>
.dialog-hint {
  color: #999;
  font-size: 12px;
}
</style>
