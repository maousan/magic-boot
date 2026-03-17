<template>
  <div class="settings">
    <div class="card">
      <h2>插件设置</h2>
      <p>配置演示插件的各项参数。</p>
    </div>

    <div class="card">
      <h2>基本设置</h2>
      <form @submit.prevent="saveSettings">
        <div class="form-group">
          <label>插件名称</label>
          <input
            v-model="settings.name"
            type="text"
            class="input"
            placeholder="请输入插件名称"
          />
        </div>

        <div class="form-group">
          <label>调试模式</label>
          <label class="switch">
            <input v-model="settings.debug" type="checkbox" />
            <span class="slider"></span>
          </label>
          <span class="switch-label">{{ settings.debug ? '已开启' : '已关闭' }}</span>
        </div>

        <div class="form-group">
          <label>日志级别</label>
          <select v-model="settings.logLevel" class="input">
            <option value="debug">Debug</option>
            <option value="info">Info</option>
            <option value="warn">Warn</option>
            <option value="error">Error</option>
          </select>
        </div>

        <div class="form-group">
          <label>缓存时间（秒）</label>
          <input
            v-model.number="settings.cacheTime"
            type="number"
            class="input"
            min="0"
            max="3600"
          />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary">保存设置</button>
          <button type="button" class="btn btn-secondary" @click="resetSettings">
            重置默认
          </button>
        </div>
      </form>
    </div>

    <div class="card">
      <h2>高级设置</h2>
      <div class="form-group">
        <label>API 超时时间（毫秒）</label>
        <input
          v-model.number="settings.apiTimeout"
          type="number"
          class="input"
          min="1000"
          max="60000"
        />
      </div>

      <div class="form-group">
        <label>最大重试次数</label>
        <input
          v-model.number="settings.maxRetries"
          type="number"
          class="input"
          min="0"
          max="10"
        />
      </div>
    </div>

    <div class="card">
      <h2>数据管理</h2>
      <div class="data-actions">
        <button class="btn btn-secondary" @click="exportSettings">导出设置</button>
        <button class="btn btn-secondary" @click="clearCache">清除缓存</button>
        <button class="btn btn-danger" @click="resetAll">重置所有</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 默认设置
const defaultSettings = {
  name: '演示插件',
  debug: false,
  logLevel: 'info',
  cacheTime: 300,
  apiTimeout: 5000,
  maxRetries: 3
}

// 设置数据
const settings = ref({ ...defaultSettings })

// 保存设置
const saveSettings = () => {
  localStorage.setItem('demo-plugin-settings', JSON.stringify(settings.value))
  alert('设置已保存！')
}

// 重置设置
const resetSettings = () => {
  settings.value = { ...defaultSettings }
  alert('已重置为默认设置')
}

// 导出设置
const exportSettings = () => {
  const data = JSON.stringify(settings.value, null, 2)
  const blob = new Blob([data], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'demo-plugin-settings.json'
  a.click()
  URL.revokeObjectURL(url)
}

// 清除缓存
const clearCache = () => {
  localStorage.removeItem('demo-plugin-cache')
  alert('缓存已清除！')
}

// 重置所有
const resetAll = () => {
  if (confirm('确定要重置所有数据吗？此操作不可恢复。')) {
    localStorage.clear()
    settings.value = { ...defaultSettings }
    alert('所有数据已重置！')
  }
}

// 加载保存的设置
onMounted(() => {
  const saved = localStorage.getItem('demo-plugin-settings')
  if (saved) {
    try {
      Object.assign(settings.value, JSON.parse(saved))
    } catch (e) {
      console.error('加载设置失败:', e)
    }
  }
})
</script>

<style scoped>
.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2c3e50;
}

.form-actions,
.data-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background-color: #c0392b;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
  vertical-align: middle;
  margin-right: 0.5rem;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.3s;
  border-radius: 26px;
}

.slider:before {
  position: absolute;
  content: '';
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

input:checked + .slider:before {
  transform: translateX(24px);
}

.switch-label {
  color: #7f8c8d;
  font-size: 0.875rem;
}
</style>
