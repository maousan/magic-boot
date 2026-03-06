<template>
	<div class="container">
    <magic-monaco-editor :value="value" language="json" readonly></magic-monaco-editor>
	</div>
</template>
<script setup>
import { inject, ref } from 'vue'
const $i = inject('i18n.format')
const info = inject('info')
const value = ref('{}')
</script>
<style scoped>
.container{
	display: flex;
	flex-direction: column;
	flex: 1;
	padding: 5px;
}
</style>
