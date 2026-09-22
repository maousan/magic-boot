<template>
  <n-layout class="admin-shell" has-sider content-style="height: 100%">
      <n-layout-sider
        class="admin-sider"
        bordered
        :width="220"
        :native-scrollbar="false"
        collapse-mode="width"
        :collapsed-width="64"
        :show-trigger="false"
      >
        <div class="admin-brand">
          东信和平管理后台
        </div>
        <n-menu class="admin-menu" :options="visibleMenuOptions" :value="activeKey" @update:value="onMenuSelect" />
      </n-layout-sider>
      <n-layout class="admin-main" content-style="height: 100%; min-height: 0; display: flex; flex-direction: column">
        <n-layout-header bordered class="admin-header">
          <span class="admin-user-info" v-if="currentUser">
            {{ currentUser.name || currentUser.username }}
            <n-tag size="small" :type="currentUser.role === 'admin' ? 'success' : 'info'" :bordered="false">
              {{ currentUser.role === 'admin' ? '管理员' : '运营' }}
            </n-tag>
          </span>
          <n-button text @click="showChangePwd = true">修改密码</n-button>
          <n-button text @click="handleLogout">退出登录</n-button>
        </n-layout-header>
        <n-alert
          v-if="licenseBanner"
          :type="licenseBanner.type"
          class="admin-license-banner"
          :bordered="false"
        >
          {{ licenseBanner.text }}
        </n-alert>
        <div ref="tabsRef" class="admin-tabs" @wheel.prevent="onTabsWheel">
          <div
            v-for="tab in storeTabs"
            :ref="(el) => setTabRef(tab.name, el)"
            :key="tab.name"
            :class="['admin-tab', { 'admin-tab--active': tab.name === route.name }]"
            @click="handleTabClick(tab.name)"
          >
            <span>{{ tab.label }}</span>
            <span v-if="tab.name !== 'Dashboard'" class="admin-tab__close" @click.stop="handleTabClose(tab.name)">✕</span>
          </div>
        </div>
        <n-layout-content
          class="admin-content"
          content-style="height: 100%; min-height: 0; padding: 24px; box-sizing: border-box; overflow: hidden"
        >
          <div class="admin-page">
            <router-view v-slot="{ Component }">
              <keep-alive :include="cachedNames">
                <component :is="Component" class="admin-page-content" />
              </keep-alive>
            </router-view>
          </div>
        </n-layout-content>
      </n-layout>

      <!-- 修改密码（自助） -->
      <n-modal
        v-model:show="showChangePwd"
        title="修改密码"
        preset="dialog"
        positive-text="确认修改"
        negative-text="取消"
        @positive-click="handleChangePwd"
      >
        <n-space vertical>
          <n-form-item label="原密码" required>
            <n-input v-model:value="pwdForm.oldPwd" type="password" show-password-on="click" placeholder="请输入原密码" />
          </n-form-item>
          <n-form-item label="新密码" required>
            <n-input v-model:value="pwdForm.newPwd" type="password" show-password-on="click" placeholder="至少6位" />
          </n-form-item>
          <n-form-item label="确认新密码" required>
            <n-input v-model:value="pwdForm.confirmPwd" type="password" show-password-on="click" placeholder="再次输入新密码" />
          </n-form-item>
        </n-space>
      </n-modal>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, ref, reactive, watch, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NMenu, NButton, NAlert, NTag, NModal, NFormItem, NInput, useDialog, useMessage } from 'naive-ui'
import type { MenuOption } from 'naive-ui'
import { clearSession, getCurrentUser, getRole, canAccess } from '@/auth'
import { apiLogout, apiChangeMyPassword } from '@/api/auth'
import { useTabStore } from '@/composables/useTabStore'
import { getLicenseStatus } from '@/api/license'
import type { LicenseStatusView } from '@/types'

const router = useRouter()
const route = useRoute()
const dialog = useDialog()
const message = useMessage()
const { tabs: storeTabs, cachedNames, addTab, removeTab, clearTabs } = useTabStore()

const currentUser = getCurrentUser()

// 修改密码（自助）
const showChangePwd = ref(false)
const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })

async function handleChangePwd() {
  if (!pwdForm.oldPwd) {
    message.error('请输入原密码')
    return false
  }
  if (pwdForm.newPwd.length < 6) {
    message.error('新密码至少6位')
    return false
  }
  if (pwdForm.newPwd !== pwdForm.confirmPwd) {
    message.error('两次输入的新密码不一致')
    return false
  }
  if (pwdForm.newPwd === pwdForm.oldPwd) {
    message.error('新密码不能与原密码相同')
    return false
  }
  await apiChangeMyPassword(pwdForm.oldPwd, pwdForm.newPwd)
  message.success('密码已修改，请使用新密码重新登录')
  // 后端已使该账号全部会话失效
  clearSession()
  clearTabs()
  router.push({ name: 'Login' })
}

const tabsRef = ref<HTMLElement | null>(null)
const tabElMap = new Map<string, HTMLElement>()

function setTabRef(name: string, el: unknown) {
  if (el instanceof HTMLElement) tabElMap.set(name, el)
}

function onTabsWheel(e: WheelEvent) {
  tabsRef.value?.scrollBy({ left: e.deltaY, behavior: 'smooth' })
}

function scrollTabIntoView(name: string) {
  nextTick(() => {
    tabElMap.get(name)?.scrollIntoView({ behavior: 'smooth', inline: 'nearest', block: 'nearest' })
  })
}

const menuOptions: MenuOption[] = [
  { label: '仪表盘', key: 'Dashboard' },
  { label: '库位管理', key: 'location', children: [
    { label: '库位列表', key: 'WarehouseLocation' },
    { label: '巷道灯绑定', key: 'LedMapping' },
    { label: '标签绑定', key: 'LabelMapping' },
    { label: '库存查询', key: 'InventoryQuery' },
  ]},
  { label: '巷道灯管理', key: 'led', children: [
    { label: '设备管理', key: 'LedDevice' },
    { label: '颜色管理', key: 'LedColor' },
    { label: 'Netty 管理', key: 'ZintisNetty' },
  ]},
  { label: 'EPC 管理', key: 'epc', children: [
    { label: '批次 EPC 绑定', key: 'BatchEpc' },
    { label: 'RFID Server', key: 'RfidServer' },
  ]},
  { label: '拣货管理', key: 'picking', children: [
    { label: '拣货单', key: 'PickingUpload' },
    { label: '用户灯色映射', key: 'UserLightColor' },
  ]},
  { label: '系统管理', key: 'system', children: [
    { label: '用户管理', key: 'AdminUser' },
    { label: 'App 版本管理', key: 'AppVersion' },
    { label: 'App 运行日志', key: 'AppDeviceLog' },
    { label: '系统授权', key: 'SystemLicense' },
    { label: '授权签发', key: 'LicenseIssue' },
    { label: '数据库管理', key: 'DatabaseManagement' },
    { label: '定时任务', key: 'JobManagement' },
    { label: 'AIMS 配置', key: 'AimsConfig' },
    { label: 'Magic-API', key: 'MagicApiConsole' },
    { label: '实时日志', key: 'RealtimeLog' },
  ]},
]

// 按角色过滤：非 admin 角色只保留有权访问的菜单，过滤后为空的分组整组隐藏
const visibleMenuOptions = computed(() => {
  if (getRole() === 'admin') return menuOptions
  return menuOptions
    .map((group) =>
      group.children
        ? { ...group, children: group.children.filter((child) => canAccess(String(child.key))) }
        : group,
    )
    .filter((group) => !group.children || group.children.length > 0)
})

const activeKey = computed(() => String(route.name))

// License 授权横幅：warning 黄 / grace 红（宽限期）/ expired+abnormal 红；ok 或未启用不显示
const licenseBanner = ref<{ type: 'warning' | 'error'; text: string } | null>(null)

async function refreshLicenseBanner() {
  try {
    const view: LicenseStatusView = await getLicenseStatus()
    if (!view.enabled || view.status === 'ok' || view.status === 'disabled') {
      licenseBanner.value = null
      return
    }
    if (view.status === 'warning') {
      licenseBanner.value = { type: 'warning', text: `⚠ ${view.message}（剩余 ${view.remainDays ?? '-'} 天）` }
    } else if (view.status === 'grace' || view.status === 'expired' || view.status === 'abnormal') {
      licenseBanner.value = { type: 'error', text: `⛔ ${view.message}。可在「系统管理 → 系统授权」导入新授权文件恢复` }
    } else {
      licenseBanner.value = { type: 'error', text: `⛔ ${view.message}` }
    }
  } catch {
    licenseBanner.value = null
  }
}

onMounted(() => {
  refreshLicenseBanner()
  setInterval(refreshLicenseBanner, 60_000)
})

function onMenuSelect(key: string) {
  router.push({ name: key })
}

function syncTab() {
  const name = route.name as string
  if (name) {
    const label = (route.meta?.label as string) || name
    addTab(name, label)
    scrollTabIntoView(name)
  }
}

watch(() => route.name, syncTab, { immediate: true })

function handleTabClick(name: string) {
  if (name !== route.name) {
    router.push({ name })
  }
}

function handleTabClose(name: string) {
  const nextName = removeTab(name)
  if (nextName && nextName !== route.name) {
    router.push({ name: nextName })
  }
}

function handleLogout() {
  dialog.warning({
    title: '确认退出',
    content: '确定要退出登录吗？',
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: () => {
      apiLogout()
      clearSession()
      clearTabs()
      router.push({ name: 'Login' })
    },
  })
}
</script>

<style scoped>
.admin-shell {
  position: fixed;
  inset: 0;
  height: 100vh;
  min-height: 0;
  overflow: hidden;
}

.admin-content .n-layout-scroll-container {
  padding: 0 !important;
}

.admin-sider {
  height: 100%;
}

.admin-sider :deep(.n-layout-sider-scroll-container) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.admin-brand {
  padding: 16px;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  flex-shrink: 0;
}

.admin-menu {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

.admin-main {
  height: 100%;
  min-width: 0;
  min-height: 0;
}

.admin-header {
  height: 52px;
  padding: 12px 24px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.admin-user-info {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.admin-license-banner {
  flex-shrink: 0;
  border-radius: 0;
}

.admin-content {
  flex: 1;
  height: 0;
  min-height: 0;
}

.admin-page {
  height: 100%;
  min-height: 100%;
}

.admin-page-content {
  height: 100%;
  min-height: 100%;
}

.admin-tabs {
  flex-shrink: 0;
  display: flex;
  align-items: flex-end;
  padding: 0 8px;
  background: #f5f7f9;
  border-bottom: 1px solid #efeff5;
  overflow-x: auto;
  overflow-y: hidden;
  gap: 4px;
}

.admin-tabs::-webkit-scrollbar {
  height: 4px;
}

.admin-tabs::-webkit-scrollbar-thumb {
  background: #d4d4d4;
  border-radius: 2px;
}

.admin-tabs::-webkit-scrollbar-thumb:hover {
  background: #bbb;
}

.admin-tab {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  font-size: 13px;
  border: 1px solid transparent;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
  cursor: pointer;
  white-space: nowrap;
  user-select: none;
  color: #666;
  position: relative;
}

.admin-tab:hover {
  background: rgba(0, 0, 0, 0.04);
}

.admin-tab--active {
  background: rgba(24, 160, 88, 0.06);
  border-color: transparent;
  color: #18a058;
  font-weight: 500;
}

.admin-tab--active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: #18a058;
}

.admin-tab__close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  font-size: 10px;
  color: #999;
  border-radius: 50%;
  line-height: 1;
}

.admin-tab__close:hover {
  background: #ccc;
  color: #333;
}

</style>
