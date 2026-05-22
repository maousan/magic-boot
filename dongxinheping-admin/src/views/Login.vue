<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: #f5f5f5">
    <n-card title="东信和平管理后台" style="width: 360px">
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { NCard, NForm, NFormItem, NInput, NButton, useMessage, useNotification } from 'naive-ui'
import type { FormRules } from 'naive-ui'
import { login } from '@/auth'

const router = useRouter()
const message = useMessage()
const notification = useNotification()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules: FormRules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
}

function handleLogin() {
  loading.value = true
  const ok = login(form.username, form.password)
  loading.value = false
  if (ok) {
    notification.success({ title: '登录成功', content: `欢迎回来，${form.username}`, duration: 3000 })
    router.push({ name: 'Dashboard' })
  } else {
    message.error('用户名或密码错误')
  }
}
</script>
