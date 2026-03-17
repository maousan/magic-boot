<template>
  <div class="data-view">
    <div class="card">
      <h2>数据管理</h2>
      <p>查看和管理插件数据。</p>
    </div>

    <div class="card">
      <h2>数据列表</h2>
      <div class="toolbar">
        <input
          v-model="searchQuery"
          type="text"
          class="input search-input"
          placeholder="搜索..."
        />
        <button class="btn btn-primary" @click="addData">添加数据</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredData" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>
              <span :class="['status', item.status]">
                {{ statusMap[item.status] }}
              </span>
            </td>
            <td>{{ item.createdAt }}</td>
            <td>
              <button class="btn-small" @click="editData(item)">编辑</button>
              <button class="btn-small btn-small-danger" @click="deleteData(item)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="filteredData.length === 0" class="empty-state">
        <p>暂无数据</p>
      </div>
    </div>

    <div class="card">
      <h2>数据统计</h2>
      <div class="chart-container">
        <div class="chart-placeholder">
          <div class="bar-chart">
            <div
              v-for="(item, index) in chartData"
              :key="index"
              class="bar"
              :style="{ height: item.value + '%' }"
            >
              <span class="bar-label">{{ item.label }}</span>
              <span class="bar-value">{{ item.value }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 状态映射
const statusMap = {
  active: '活跃',
  inactive: '未激活',
  pending: '待处理'
}

// 搜索关键词
const searchQuery = ref('')

// 模拟数据
const dataList = ref([
  { id: 1, name: '示例数据 1', status: 'active', createdAt: '2024-01-15 10:30' },
  { id: 2, name: '示例数据 2', status: 'inactive', createdAt: '2024-01-16 14:20' },
  { id: 3, name: '示例数据 3', status: 'pending', createdAt: '2024-01-17 09:15' },
  { id: 4, name: '测试数据 A', status: 'active', createdAt: '2024-01-18 16:45' },
  { id: 5, name: '测试数据 B', status: 'active', createdAt: '2024-01-19 11:00' }
])

// 图表数据
const chartData = ref([
  { label: '活跃', value: 60 },
  { label: '未激活', value: 25 },
  { label: '待处理', value: 15 }
])

// 过滤后的数据
const filteredData = computed(() => {
  if (!searchQuery.value) {
    return dataList.value
  }
  const query = searchQuery.value.toLowerCase()
  return dataList.value.filter(
    (item) =>
      item.name.toLowerCase().includes(query) ||
      item.id.toString().includes(query)
  )
})

// 添加数据
const addData = () => {
  const newId = Math.max(...dataList.value.map((d) => d.id)) + 1
  dataList.value.push({
    id: newId,
    name: `新数据 ${newId}`,
    status: 'pending',
    createdAt: new Date().toLocaleString('zh-CN')
  })
  alert('数据已添加！')
}

// 编辑数据
const editData = (item) => {
  const newName = prompt('请输入新名称:', item.name)
  if (newName && newName !== item.name) {
    item.name = newName
    alert('数据已更新！')
  }
}

// 删除数据
const deleteData = (item) => {
  if (confirm(`确定要删除 "${item.name}" 吗？`)) {
    const index = dataList.value.findIndex((d) => d.id === item.id)
    if (index > -1) {
      dataList.value.splice(index, 1)
      alert('数据已删除！')
    }
  }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.status {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status.active {
  background-color: #d4edda;
  color: #155724;
}

.status.inactive {
  background-color: #f8d7da;
  color: #721c24;
}

.status.pending {
  background-color: #fff3cd;
  color: #856404;
}

.btn-small {
  padding: 0.25rem 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  background-color: #e3f2fd;
  color: #1976d2;
  margin-right: 0.5rem;
}

.btn-small:hover {
  background-color: #bbdefb;
}

.btn-small-danger {
  background-color: #ffebee;
  color: #d32f2f;
}

.btn-small-danger:hover {
  background-color: #ffcdd2;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #7f8c8d;
}

.chart-container {
  margin-top: 1rem;
}

.chart-placeholder {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 2rem;
}

.bar-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 200px;
}

.bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  width: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px 4px 0 0;
  min-height: 20px;
  position: relative;
}

.bar-label {
  position: absolute;
  bottom: -1.5rem;
  font-size: 0.75rem;
  color: #7f8c8d;
}

.bar-value {
  color: white;
  font-weight: bold;
  font-size: 0.875rem;
  padding-top: 0.5rem;
}
</style>
