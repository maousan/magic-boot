import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { canButton } from './auth'

const app = createApp(App)

// 按钮权限指令：无权限时直接移除元素（权限点清单见 config/buttons.ts）
app.directive('perm', {
  mounted(el: HTMLElement, binding) {
    if (binding.value && !canButton(binding.value)) {
      el.parentNode?.removeChild(el)
    }
  },
})

app.use(router)
app.mount('#app')
