import { ref, computed } from 'vue'

export interface Tab {
  name: string
  label: string
}

const tabs = ref<Tab[]>([{ name: 'Dashboard', label: '仪表盘' }])
const activeTab = ref('Dashboard')

export function useTabStore() {
  const cachedNames = computed(() => tabs.value.map((t) => t.name))

  function addTab(name: string, label: string) {
    if (!tabs.value.find((t) => t.name === name)) {
      tabs.value.push({ name, label })
    }
    activeTab.value = name
  }

	/** 关闭 tab，返回需要导航到的 tab name，不需要导航则返回 null */
	function removeTab(name: string): string | null {
		if (name === 'Dashboard') return null
		const idx = tabs.value.findIndex((t) => t.name === name)
		if (idx === -1) return null

		const isActive = activeTab.value === name
		tabs.value.splice(idx, 1)

		if (isActive && tabs.value.length > 0) {
			const nextIdx = Math.min(idx, tabs.value.length - 1)
			const nextName = tabs.value[nextIdx]!.name
			activeTab.value = nextName
			return nextName
		}
		return null
	}

	/** 清空全部历史 tabs（退出登录/会话失效时调用），避免不同权限账号看到上一账号的页签 */
	function clearTabs() {
		tabs.value = [{ name: 'Dashboard', label: '仪表盘' }]
		activeTab.value = 'Dashboard'
	}

	return { tabs, activeTab, cachedNames, addTab, removeTab, clearTabs }
}
