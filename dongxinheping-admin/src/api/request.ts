import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'
import type { ApiResponse } from '@/types'

const { message } = createDiscreteApi(['message'])

function showMessage(msg: string, type: 'error' | 'success' | 'info') {
  message[type](msg, { duration: 3000 })
}

function createClient(baseURL?: string) {
  const instance = axios.create({
    ...(baseURL ? { baseURL } : {}),
    timeout: 15000,
  })
  instance.interceptors.response.use(
    (response) => {
      const data = response.data as ApiResponse
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
