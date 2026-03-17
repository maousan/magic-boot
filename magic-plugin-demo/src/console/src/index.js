// 插件入口文件
import { createApp } from 'vue'
import App from './App.vue'

// 插件定义（共享模式使用）
const plugin = {
  name: 'demo-plugin',
  displayName: '演示插件',
  version: '1.0.0',

  /**
   * 插件安装函数
   * @param {import('vue').App} app - Vue 应用实例
   * @param {Object} options - 插件选项
   * @param {string} options.pluginId - 插件 ID
   * @param {string} options.apiBase - API 基础路径
   * @param {string} options.token - 认证 token
   */
  install(app, options = {}) {
    console.log('[DemoPlugin] Installing with options:', options)

    // 保存插件配置
    app.config.globalProperties.$pluginOptions = options

    // 注册全局组件
    const components = import.meta.glob('./components/*.vue', { eager: true })
    Object.entries(components).forEach(([path, module]) => {
      const name = path.split('/').pop().replace('.vue', '')
      app.component(`Demo${name}`, module.default)
      console.log('[DemoPlugin] Registered component:', `Demo${name}`)
    })

    // 返回路由配置（供主应用使用）
    return {
      routes: [
        {
          path: '/dashboard',
          component: () => import('./views/Dashboard.vue'),
          title: '控制台首页',
          icon: 'dashboard'
        },
        {
          path: '/settings',
          component: () => import('./views/Settings.vue'),
          title: '插件设置',
          icon: 'settings'
        },
        {
          path: '/data',
          component: () => import('./views/DataView.vue'),
          title: '数据管理',
          icon: 'database'
        }
      ],
      menuItems: [
        {
          id: 'demo-dashboard',
          title: '控制台首页',
          icon: 'dashboard',
          route: '/dashboard',
          order: 100
        },
        {
          id: 'demo-settings',
          title: '插件设置',
          icon: 'settings',
          route: '/settings',
          order: 101
        },
        {
          id: 'demo-data',
          title: '数据管理',
          icon: 'database',
          route: '/data',
          order: 102
        }
      ]
    }
  }
}

// 独立运行初始化函数
function initStandalone(options) {
  console.log('[DemoPlugin] 初始化独立运行模式:', options)
  const app = createApp(App)
  plugin.install(app, options)
  app.mount('#app')
}

// 检测运行环境，自动决定挂载方式
if (window.self !== window.top) {
  // iframe 模式：等待主应用的初始化消息
  console.log('[DemoPlugin] 检测到 iframe 环境，等待主应用初始化...')
  window.addEventListener('message', (event) => {
    if (event.data?.type === 'INIT') {
      initStandalone(event.data)
    }
  })
} else if (!window.Vue?.__shared_mode__) {
  // 独立模式：自己创建 Vue 实例
  console.log('[DemoPlugin] 检测到独立运行模式')
  initStandalone({
    pluginId: 'demo-plugin',
    apiBase: '/plugin/demo-plugin/api',
    token: localStorage.getItem('token') || ''
  })
} else {
  // 共享模式：导出插件对象供主应用使用
  console.log('[DemoPlugin] 检测到共享模式，导出插件对象')
  window.DemoPlugin = plugin
}

export default plugin
