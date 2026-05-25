<template>
  <n-layout style="height: 100vh">
    <n-layout has-sider>
      <n-layout-sider
        bordered
        :width="220"
        :native-scrollbar="false"
        collapse-mode="width"
        :collapsed-width="64"
        :show-trigger="false"
      >
        <div style="padding: 16px; font-size: 16px; font-weight: 600; text-align: center">
          东信和平管理后台
        </div>
        <n-menu :options="menuOptions" :value="activeKey" @update:value="onMenuSelect" />
      </n-layout-sider>
      <n-layout>
        <n-layout-header bordered style="padding: 12px 24px; display: flex; justify-content: flex-end; align-items: center">
          <n-button text @click="handleLogout">退出登录</n-button>
        </n-layout-header>
        <n-layout-content content-style="padding: 24px">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <keep-alive>
                <component :is="Component" />
              </keep-alive>
            </transition>
          </router-view>
        </n-layout-content>
      </n-layout>
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
    { label: '批次 EPC 管理', key: 'BatchEpc' },
  ]},
  { label: '拣货管理', key: 'PickingUpload' },
  { label: '用户灯色映射', key: 'UserLightColor' },
  { label: '系统管理', key: 'system', children: [
    { label: '数据库管理', key: 'DatabaseManagement' },
    { label: '定时任务', key: 'JobManagement' },
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
