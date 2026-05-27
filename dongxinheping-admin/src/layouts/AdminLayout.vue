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
        <n-menu class="admin-menu" :options="menuOptions" :value="activeKey" @update:value="onMenuSelect" />
      </n-layout-sider>
      <n-layout class="admin-main" content-style="height: 100%; min-height: 0; display: flex; flex-direction: column">
        <n-layout-header bordered class="admin-header">
          <n-button text @click="handleLogout">退出登录</n-button>
        </n-layout-header>
        <n-layout-content
          class="admin-content"
          content-style="height: 100%; min-height: 0; padding: 24px; box-sizing: border-box; overflow: hidden"
        >
          <div class="admin-page">
            <router-view v-slot="{ Component }">
              <transition name="fade-slide" mode="out-in">
                <keep-alive>
                  <div class="admin-page-content">
                    <component :is="Component" />
                  </div>
                </keep-alive>
              </transition>
            </router-view>
          </div>
        </n-layout-content>
      </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NMenu, NButton, useDialog } from 'naive-ui'
import type { MenuOption } from 'naive-ui'
import { logout } from '@/auth'

const router = useRouter()
const route = useRoute()
const dialog = useDialog()

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
    { label: '数据库管理', key: 'DatabaseManagement' },
    { label: '定时任务', key: 'JobManagement' },
    { label: 'AIMS 配置', key: 'AimsConfig' },
    { label: 'Magic-API', key: 'MagicApiConsole' },
  ]},
]

const activeKey = computed(() => String(route.name))

function onMenuSelect(key: string) {
  router.push({ name: key })
}

function handleLogout() {
  dialog.warning({
    title: '确认退出',
    content: '确定要退出登录吗？',
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: () => {
      logout()
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
  flex-shrink: 0;
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

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
