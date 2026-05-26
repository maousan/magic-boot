<template>
  <n-config-provider class="app-root" :locale="zhCN" :date-locale="dateZhCN">
    <n-dialog-provider>
      <n-notification-provider>
        <n-message-provider>
          <router-view />
        </n-message-provider>
      </n-notification-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { NConfigProvider, NDialogProvider, NNotificationProvider, NMessageProvider, zhCN, dateZhCN } from 'naive-ui'
</script>

<style>
html,
body,
#app {
  width: 100%;
  height: 100%;
  margin: 0;
}

body {
  overflow: hidden;
}

.app-root {
  height: 100%;
}

.table-page-card {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.table-page-card .n-card__content {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.table-page-content {
  flex: 1;
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.table-page-toolbar,
.table-page-pagination {
  flex-shrink: 0;
}

.table-page-table {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
}
</style>
