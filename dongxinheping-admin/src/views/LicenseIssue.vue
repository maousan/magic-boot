<template>
  <n-card title="授权签发（内部）" class="table-page-card" style="height: 100%; min-height: 0; display: flex; flex-direction: column"
    content-style="flex: 1; min-height: 0; display: flex; flex-direction: column">
    <div class="table-page-content" style="overflow: auto; max-width: 720px">
      <n-alert type="info" style="margin-bottom: 16px">
        将客户管理页「系统授权」中的指纹块整块粘贴到下方，填写客户名与有效期后签发。
        签发依赖私钥（license.issue.enabled），客户现场此功能不可用。
      </n-alert>

      <n-form label-placement="left" label-width="110">
        <n-form-item label="客户 / 项目" required>
          <n-input v-model:value="form.customer" placeholder="如：东信和平" />
        </n-form-item>
        <n-form-item label="有效期至" required>
          <n-input v-model:value="form.expireAt" placeholder="yyyy-MM-dd，如 2027-09-21" />
        </n-form-item>
        <n-form-item label="机器指纹码" required>
          <n-input v-model:value="form.fingerprintCode" type="textarea" :rows="4" placeholder="粘贴客户「系统授权」页复制的机器指纹码" />
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="form.notes" placeholder="如合同号" />
        </n-form-item>
        <n-form-item>
          <n-button type="primary" :loading="issuing" @click="handleIssue">签发并下载 .lic</n-button>
        </n-form-item>
      </n-form>
    </div>
  </n-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { NCard, NForm, NFormItem, NInput, NButton, NAlert, useMessage } from 'naive-ui'
import { issueLicense } from '@/api/license'

const message = useMessage()
const issuing = ref(false)

const form = reactive({
  customer: '',
  expireAt: '',
  fingerprintCode: '',
  notes: '',
})

async function handleIssue() {
  if (!form.customer || !form.expireAt || !form.fingerprintCode) {
    message.error('请填写客户名、有效期与机器指纹码')
    return
  }
  issuing.value = true
  try {
    const formData = new FormData()
    formData.append('customer', form.customer)
    formData.append('expireAt', form.expireAt)
    formData.append('fingerprintCode', form.fingerprintCode)
    if (form.notes) formData.append('notes', form.notes)

    const { blob, filename } = await issueLicense(formData)
    if (blob.type.includes('json')) {
      // 签发失败时后端返回 JSON 错误
      const text = await blob.text()
      const parsed = JSON.parse(text) as { message?: string }
      message.error(parsed.message ?? '签发失败')
      return
    }
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    link.click()
    URL.revokeObjectURL(url)
    message.success(`已签发：${filename}`)
  } finally {
    issuing.value = false
  }
}
</script>
