<template>
  <n-config-provider>
    <n-message-provider>
      <div class="demo-plugin-app">
        <n-layout>
          <n-layout-header bordered style="padding: 0 24px; height: 64px; display: flex; align-items: center">
            <n-space justify="space-between" align="center" style="width: 100%">
              <n-h2 style="margin: 0; color: white">演示插件控制台</n-h2>
              <n-menu
                mode="horizontal"
                :options="menuOptions"
                :value="currentRoute"
                @update:value="navigate"
                :inverted="true"
              />
            </n-space>
          </n-layout-header>

          <n-layout-content>
            <n-card style="margin: 16px">
              <component :is="currentComponent" v-if="currentComponent" />
              <n-empty v-else description="页面未找到" />
            </n-card>
          </n-layout-content>

          <n-layout-footer bordered style="text-align: center; padding: 16px">
            <n-text depth="3">
              Demo Plugin v1.0.0 &copy; 2024 - Built with Naive UI
            </n-text>
          </n-layout-footer>
        </n-layout>
      </div>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  NConfigProvider,
  NMessageProvider,
  NLayout,
  NLayoutHeader,
  NLayoutContent,
  NLayoutFooter,
  NMenu,
  NSpace,
  NH2,
  NCard,
  NEmpty,
  NText
} from 'naive-ui'
import Dashboard from './views/Dashboard.vue'
import Settings from './views/Settings.vue'
import DataView from './views/DataView.vue'

// 组件映射
const componentMap = {
  '/dashboard': Dashboard,
  '/settings': Settings,
  '/data': DataView
}

// 当前路由
const currentRoute = ref('/dashboard')

// 当前组件
const currentComponent = computed(() => componentMap[currentRoute.value])

// 菜单配置
const menuOptions = [
  {
    label: '控制台首页',
    key: '/dashboard'
  },
  {
    label: '插件设置',
    key: '/settings'
  },
  {
    label: '数据管理',
    key: '/data'
  }
]

// 导航方法
const navigate = (key) => {
  currentRoute.value = key
  window.location.hash = key
}

// 初始化时读取 hash
onMounted(() => {
  const hash = window.location.hash.slice(1)
  if (hash && componentMap[hash]) {
    currentRoute.value = hash
  }
})
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
}

.demo-plugin-app {
  height: 100vh;
}

.n-layout-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>
