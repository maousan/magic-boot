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
      { path: '', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
      { path: 'led-device', name: 'LedDevice', component: () => import('@/views/LedDevice.vue') },
      { path: 'led-color', name: 'LedColor', component: () => import('@/views/LedColor.vue') },
      { path: 'led-mapping', name: 'LedMapping', component: () => import('@/views/LedMapping.vue') },
      { path: 'label-mapping', name: 'LabelMapping', component: () => import('@/views/LabelMapping.vue') },
      { path: 'warehouse-location', name: 'WarehouseLocation', component: () => import('@/views/WarehouseLocation.vue') },
      { path: 'user-light-color', name: 'UserLightColor', component: () => import('@/views/UserLightColor.vue') },
      { path: 'picking-upload', name: 'PickingUpload', component: () => import('@/views/PickingUpload.vue') },
      { path: 'batch-epc', name: 'BatchEpc', component: () => import('@/views/BatchEpc.vue') },
      { path: 'zintis-netty', name: 'ZintisNetty', component: () => import('@/views/ZintisNetty.vue') },
      { path: 'inventory-query', name: 'InventoryQuery', component: () => import('@/views/InventoryQuery.vue') },
      { path: 'database', name: 'DatabaseManagement', component: () => import('@/views/DatabaseManagement.vue') },
      { path: 'jobs', name: 'JobManagement', component: () => import('@/views/JobManagement.vue') },
      { path: 'magic-api', name: 'MagicApiConsole', component: () => import('@/views/MagicApiConsole.vue') },
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
