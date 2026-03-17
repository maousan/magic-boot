<template>
  <div class="demo-component">
    <h3>{{ title }}</h3>
    <p>{{ message }}</p>
    <button class="btn btn-primary" @click="handleClick">{{ buttonText }}</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: 'Demo Component'
  },
  message: {
    type: String,
    default: 'This is a reusable component from the demo plugin.'
  },
  buttonText: {
    type: String,
    default: 'Click Me'
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', { timestamp: Date.now() })
}
</script>

<style scoped>
.demo-component {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.demo-component h3 {
  margin-bottom: 0.75rem;
  color: #2c3e50;
}

.demo-component p {
  margin-bottom: 1rem;
  color: #7f8c8d;
}
</style>
