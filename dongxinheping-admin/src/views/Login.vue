<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: #f5f5f5">
    <n-card title="东信和平管理后台" style="width: 360px">
      <n-alert v-if="licenseNotice" :type="licenseNotice.type" style="margin-bottom: 16px">
        {{ licenseNotice.text }}
      </n-alert>
      <n-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input v-model:value="form.password" type="password" show-password-on="click" placeholder="请输入密码" />
        </n-form-item>
        <n-button type="primary" block attr-type="submit" :loading="loading">登录</n-button>
      </n-form>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NCard, NForm, NFormItem, NInput, NButton, NAlert, useNotification } from 'naive-ui'
import type { FormRules } from 'naive-ui'
import { setSession } from '@/auth'
import { apiLogin } from '@/api/auth'
import { getLicenseStatus } from '@/api/license'
import type { LicenseStatusView } from '@/types'

const router = useRouter()
const notification = useNotification()
const loading = ref(false)

const licenseNotice = ref<{ type: 'warning' | 'error'; text: string } | null>(null)

onMounted(async () => {
  try {
    const view: LicenseStatusView = await getLicenseStatus()
    if (!view.enabled || view.status === 'ok' || view.status === 'disabled') return
    licenseNotice.value = {
      type: view.status === 'warning' ? 'warning' : 'error',
      text: view.message,
    }
  } catch {
    // 后端不可达时静默
  }
})

const form = reactive({ username: '', password: '' })

const rules: FormRules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
}

async function handleLogin() {
  loading.value = true
  try {
    const result = await apiLogin(form.username, form.password)
    setSession(result.token, { id: result.id, username: result.username, name: result.name, role: result.role })
    notification.success({ title: '登录成功', content: `欢迎回来，${result.name || result.username}`, duration: 3000 })
    router.push({ name: 'Dashboard' })
  } catch {
    // 失败提示由请求拦截器统一弹出
  } finally {
    loading.value = false
  }
}
</script>
