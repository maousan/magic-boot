<template>
  <div class="menu-permission-page">
    <n-card title="菜单与按钮权限" style="max-width: 760px">
      <n-spin :show="loading">
        <n-alert type="info" :bordered="false" style="margin-bottom: 16px">
          上方「菜单权限」控制该角色登录后可见的页面；下方「按钮权限」控制各页面内可用的操作按钮。管理员（admin）恒为全量无需配置。「仪表盘」是默认首页，不可关闭。保存后该角色用户刷新页面即生效（多角色用户菜单与按钮均取并集）。
        </n-alert>

        <div class="role-picker">
          <span class="role-picker-label">角色：</span>
          <n-select
            v-model:value="selectedRole"
            :options="roleOptions"
            :loading="rolesLoading"
            style="width: 240px"
            @update:value="loadConfig"
          />
        </div>

        <!-- 分区一：菜单权限（页面可见性） -->
        <n-card embed size="small" class="perm-section">
          <template #header>
            <span class="perm-section-title">① 菜单权限<span class="perm-section-sub">登录后可见的页面</span></span>
          </template>
          <div v-for="node in menuNodes" :key="node.key" class="menu-group">
            <template v-if="node.children && node.children.length > 0">
              <div class="menu-group-title">
                <n-checkbox
                  :checked="isGroupAll(node)"
                  :indeterminate="isGroupIndeterminate(node)"
                  @update:checked="(v: boolean) => toggleGroup(node, v)"
                >{{ node.label }}</n-checkbox>
              </div>
              <div class="menu-group-items">
                <n-checkbox
                  v-for="item in node.children"
                  :key="item.key"
                  :checked="selected.has(item.key)"
                  :disabled="item.key === 'Dashboard'"
                  @update:checked="(v: boolean) => toggleItem(item.key, v)"
                >{{ item.label }}</n-checkbox>
              </div>
            </template>
            <n-checkbox
              v-else
              :checked="selected.has(node.key)"
              :disabled="node.key === 'Dashboard'"
              @update:checked="(v: boolean) => toggleItem(node.key, v)"
            >{{ node.label }}</n-checkbox>
          </div>
        </n-card>

        <!-- 分区二：按钮权限（页面内操作），与菜单分区分开 -->
        <n-card embed size="small" class="perm-section">
          <template #header>
            <span class="perm-section-title">② 按钮权限<span class="perm-section-sub">页面内可用的操作按钮</span></span>
          </template>
          <div v-for="node in menuNodes" :key="node.key">
            <template v-if="node.children && node.children.length > 0">
              <div v-if="node.children.some((c) => routeButtons[c.key])" class="btn-group-title">{{ node.label }}</div>
              <div
                v-for="item in node.children"
                :key="item.key"
                class="btn-page-row"
                :class="{ 'btn-page-disabled': !selected.has(item.key) }"
              >
                <template v-if="routeButtons[item.key]">
                  <span class="btn-page-label">{{ item.label }}</span>
                  <div class="btn-page-actions">
                    <n-checkbox
                      v-for="b in routeButtons[item.key]"
                      :key="b.action"
                      size="small"
                      :checked="selectedButtons.has(`${item.key}:${b.action}`)"
                      :disabled="!selected.has(item.key)"
                      @update:checked="(v: boolean) => toggleButton(`${item.key}:${b.action}`, v)"
                    >{{ b.label }}</n-checkbox>
                  </div>
                </template>
              </div>
            </template>
            <template v-else-if="routeButtons[node.key]">
              <div class="btn-page-row" :class="{ 'btn-page-disabled': !selected.has(node.key) }">
                <span class="btn-page-label">{{ node.label }}</span>
                <div class="btn-page-actions">
                  <n-checkbox
                    v-for="b in routeButtons[node.key]"
                    :key="b.action"
                    size="small"
                    :checked="selectedButtons.has(`${node.key}:${b.action}`)"
                    :disabled="!selected.has(node.key)"
                    @update:checked="(v: boolean) => toggleButton(`${node.key}:${b.action}`, v)"
                  >{{ b.label }}</n-checkbox>
                </div>
              </div>
            </template>
          </div>
        </n-card>

        <div v-if="meta.updateBy" class="menu-meta">
          最后修改：{{ meta.updateBy }}<template v-if="meta.updateTime">　{{ meta.updateTime }}</template>
        </div>
      </n-spin>

      <template #action>
        <n-space>
          <n-button secondary :loading="loading" @click="loadConfig">刷新</n-button>
          <n-button v-perm="'MenuPermission:save'" type="primary" :loading="saving" @click="handleSave">保存</n-button>
        </n-space>
      </template>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NCard, NAlert, NCheckbox, NSpace, NSpin, NButton, NSelect, useMessage } from 'naive-ui'
import { menuNodes, allMenuRouteKeys } from '@/config/menus'
import { routeButtons, allButtonKeys } from '@/config/buttons'
import type { MenuNode } from '@/config/menus'
import { getMenuConfig, saveMenuConfig } from '@/api/menu-permission'
import { listRoles } from '@/api/role'

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const selected = ref<Set<string>>(new Set())
const selectedButtons = ref<Set<string>>(new Set())
const meta = ref<{ updateBy: string | null; updateTime: string | null }>({ updateBy: null, updateTime: null })

const rolesLoading = ref(false)
const selectedRole = ref('ops')
const roleOptions = ref<{ label: string; value: string }[]>([])

async function loadRoleOptions() {
  rolesLoading.value = true
  try {
    const roles = await listRoles()
    roleOptions.value = roles
      .filter((r) => r.code !== 'admin')
      .map((r) => ({ label: `${r.name}（${r.code}）`, value: r.code }))
    if (!roleOptions.value.some((o) => o.value === selectedRole.value) && roleOptions.value.length > 0) {
      selectedRole.value = roleOptions.value[0]?.value ?? 'ops'
    }
  } finally {
    rolesLoading.value = false
  }
}

async function loadConfig() {
  loading.value = true
  try {
    const view = await getMenuConfig(selectedRole.value)
    // 该角色无配置行时按全量勾选展示，保存后落库；buttons 未配置（null）同样按全量展示
    selected.value = new Set(view.menus ?? allMenuRouteKeys)
    selectedButtons.value = new Set(view.buttons ?? allButtonKeys)
    meta.value = { updateBy: view.updateBy, updateTime: view.updateTime }
  } finally {
    loading.value = false
  }
}

function toggleButton(key: string, checked: boolean) {
  const next = new Set(selectedButtons.value)
  if (checked) {
    next.add(key)
  } else {
    next.delete(key)
  }
  selectedButtons.value = next
}

function toggleItem(key: string, checked: boolean) {
  if (key === 'Dashboard') return
  const next = new Set(selected.value)
  if (checked) {
    next.add(key)
  } else {
    next.delete(key)
  }
  selected.value = next
}

function toggleGroup(group: MenuNode, checked: boolean) {
  const keys = (group.children ?? []).map((c) => c.key)
  const next = new Set(selected.value)
  for (const key of keys) {
    if (key === 'Dashboard') continue
    if (checked) {
      next.add(key)
    } else {
      next.delete(key)
    }
  }
  selected.value = next
}

function isGroupAll(group: MenuNode): boolean {
  const keys = (group.children ?? []).map((c) => c.key)
  return keys.length > 0 && keys.every((k) => selected.value.has(k))
}

function isGroupIndeterminate(group: MenuNode): boolean {
  const keys = (group.children ?? []).map((c) => c.key)
  const hit = keys.filter((k) => selected.value.has(k)).length
  return hit > 0 && hit < keys.length
}

async function handleSave() {
  const menus = [...selected.value]
  if (!menus.includes('Dashboard')) {
    menus.unshift('Dashboard')
  }
  // 只保存已勾选菜单下的按钮权限点，未授权页面不可达、其按钮无意义
  const buttons = [...selectedButtons.value].filter((k) => menus.includes(k.split(':')[0] ?? ''))
  saving.value = true
  try {
    await saveMenuConfig(selectedRole.value, menus, buttons)
    message.success('已保存，该角色用户刷新页面后生效')
    await loadConfig()
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadRoleOptions()
  loadConfig()
})
</script>

<style scoped>
.menu-permission-page {
  height: 100%;
  overflow-y: auto;
  padding-bottom: 8px;
}
.role-picker {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.role-picker-label {
  font-weight: 600;
}
.perm-section {
  margin-bottom: 16px;
}
.perm-section-title {
  font-weight: 600;
}
.perm-section-sub {
  margin-left: 8px;
  font-weight: 400;
  font-size: 12px;
  color: #999;
}
.menu-group {
  margin-bottom: 14px;
}
.menu-group-title {
  font-weight: 600;
  margin-bottom: 6px;
}
.menu-group-items {
  margin-left: 26px;
}
.btn-group-title {
  font-weight: 600;
  margin: 10px 0 4px;
}
.btn-page-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 6px;
  margin-left: 26px;
}
.btn-page-disabled {
  opacity: 0.45;
}
.btn-page-label {
  flex: 0 0 128px;
  font-size: 13px;
  color: #666;
}
.btn-page-actions {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 4px 14px;
}
.menu-meta {
  margin-top: 12px;
  color: #999;
  font-size: 12px;
}
</style>
