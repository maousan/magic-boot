import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

export function useTableBodyHeight(headerOffset = 52, minHeight = 160, defaultHeight = 360) {
  const tableAreaRef = ref<HTMLElement | null>(null)
  const tableBodyMaxHeight = ref(defaultHeight)
  let tableResizeObserver: ResizeObserver | null = null

  function updateTableHeight() {
    const areaHeight = tableAreaRef.value?.clientHeight || 0
    if (areaHeight > 0) {
      tableBodyMaxHeight.value = Math.max(areaHeight - headerOffset, minHeight)
    }
  }

  function observeTableArea(target: HTMLElement | null) {
    tableResizeObserver?.disconnect()
    tableResizeObserver = null
    if (target) {
      updateTableHeight()
      tableResizeObserver = new ResizeObserver(updateTableHeight)
      tableResizeObserver.observe(target)
    }
  }

  onMounted(() => {
    nextTick(() => {
      observeTableArea(tableAreaRef.value)
    })
  })

  watch(tableAreaRef, (target) => {
    observeTableArea(target)
  })

  onBeforeUnmount(() => {
    tableResizeObserver?.disconnect()
  })

  return {
    tableAreaRef,
    tableBodyMaxHeight,
    updateTableHeight,
  }
}
