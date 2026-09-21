import type { RouteRecordRaw } from 'vue-router'
import { createRouter, createWebHashHistory } from 'vue-router'
import { isAuthenticated } from '@/auth'

const AdminLayout = () => import('@/layouts/AdminLayout.vue')

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { label: '仪表盘' } },
      { path: 'led-device', name: 'LedDevice', component: () => import('@/views/LedDevice.vue'), meta: { label: '设备管理' } },
      { path: 'led-color', name: 'LedColor', component: () => import('@/views/LedColor.vue'), meta: { label: '颜色管理' } },
      { path: 'led-mapping', name: 'LedMapping', component: () => import('@/views/LedMapping.vue'), meta: { label: '巷道灯绑定' } },
      { path: 'label-mapping', name: 'LabelMapping', component: () => import('@/views/LabelMapping.vue'), meta: { label: '标签绑定' } },
      { path: 'warehouse-location', name: 'WarehouseLocation', component: () => import('@/views/WarehouseLocation.vue'), meta: { label: '库位列表' } },
      { path: 'user-light-color', name: 'UserLightColor', component: () => import('@/views/UserLightColor.vue'), meta: { label: '用户灯色映射' } },
      { path: 'picking-upload', name: 'PickingUpload', component: () => import('@/views/PickingUpload.vue'), meta: { label: '拣货单' } },
      { path: 'batch-epc', name: 'BatchEpc', component: () => import('@/views/BatchEpc.vue'), meta: { label: '批次 EPC 绑定' } },
      { path: 'rfid-server', name: 'RfidServer', component: () => import('@/views/RfidServer.vue'), meta: { label: 'RFID Server' } },
      { path: 'zintis-netty', name: 'ZintisNetty', component: () => import('@/views/ZintisNetty.vue'), meta: { label: 'Netty 管理' } },
      { path: 'inventory-query', name: 'InventoryQuery', component: () => import('@/views/InventoryQuery.vue'), meta: { label: '库存查询' } },
      { path: 'database', name: 'DatabaseManagement', component: () => import('@/views/DatabaseManagement.vue'), meta: { label: '数据库管理' } },
      { path: 'jobs', name: 'JobManagement', component: () => import('@/views/JobManagement.vue'), meta: { label: '定时任务' } },
      { path: 'magic-api', name: 'MagicApiConsole', component: () => import('@/views/MagicApiConsole.vue'), meta: { label: 'Magic-API' } },
      { path: 'aims-config', name: 'AimsConfig', component: () => import('@/views/AimsConfig.vue'), meta: { label: 'AIMS 配置' } },
      { path: 'app-version', name: 'AppVersion', component: () => import('@/views/AppVersion.vue'), meta: { label: 'App 版本管理' } },
      { path: 'app-log', name: 'AppDeviceLog', component: () => import('@/views/AppDeviceLog.vue'), meta: { label: 'App 运行日志' } },
      { path: 'system-license', name: 'SystemLicense', component: () => import('@/views/SystemLicense.vue'), meta: { label: '系统授权' } },
      { path: 'license-issue', name: 'LicenseIssue', component: () => import('@/views/LicenseIssue.vue'), meta: { label: '授权签发' } },
      { path: 'logs', name: 'RealtimeLog', component: () => import('@/views/RealtimeLog.vue'), meta: { label: '实时日志' } },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth !== false && !isAuthenticated()) {
    return { name: 'Login' }
  }
})

export default router
