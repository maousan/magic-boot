import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'
import type { ApiResponse } from '@/types'
import { getToken, clearSession } from '@/auth'
import { useTabStore } from '@/composables/useTabStore'

const { message } = createDiscreteApi(['message'])

function showMessage(msg: string, type: 'error' | 'success' | 'info') {
  message[type](msg, { duration: 3000 })
}

function gotoLogin() {
  if (location.hash !== '#/login') {
    location.hash = '#/login'
  }
}

function createClient(baseURL?: string) {
  const instance = axios.create({
    ...(baseURL ? { baseURL } : {}),
    timeout: 15000,
  })
  instance.interceptors.request.use((config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })
  instance.interceptors.response.use(
    (response) => {
      const data = response.data as ApiResponse
      if (data.code !== undefined && data.code === 402) {
        // 凭证过期/未登录：清会话与历史 tabs 回登录页
        clearSession()
        useTabStore().clearTabs()
        showMessage('登录已失效，请重新登录', 'error')
        gotoLogin()
        return Promise.reject(new Error(data.message || '登录已失效'))
      }
      if (data.code !== undefined && data.code !== 200 && data.code !== 0) {
        const msg = data.message || '请求失败'
        showMessage(msg, 'error')
        return Promise.reject(new Error(msg))
      }
      return response
    },
    (error) => {
      const msg = error.response?.statusText || error.message || '网络错误'
      showMessage(msg, 'error')
      return Promise.reject(error)
    },
  )
  return instance
}

const client = createClient(import.meta.env.VITE_API_BASE_URL)
const pluginClient = createClient()

export { client as default, pluginClient }
