<template>
  <div class="dashboard-welcome">
    <div class="welcome-content">
      <div class="welcome-emoji">👋</div>
      <h1 class="welcome-title">欢迎回来</h1>
      <p class="welcome-subtitle">东信和平管理后台</p>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.dashboard-welcome {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 120px);
}

.welcome-content {
  text-align: center;
}

.welcome-emoji {
  font-size: 64px;
  margin-bottom: 16px;
}

.welcome-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--n-text-color);
  margin: 0 0 8px;
}

.welcome-subtitle {
  font-size: 16px;
  color: var(--n-text-color-disabled);
  margin: 0;
}
</style>
