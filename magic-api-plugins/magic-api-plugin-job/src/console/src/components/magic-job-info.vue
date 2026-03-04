<template>
	<div class="magic-job-info">
		<form>
			<label>{{ $i('message.enable') }}</label>
			<magic-checkbox v-model:value="info.enabled" />
			<label>cron</label>
			<magic-input v-model:value="info.cron" :placeholder="$i('job.form.placeholder.cron')" width="250px"/>
			<label>{{ $i('job.form.name') }}</label>
			<magic-input v-model:value="info.name" :placeholder="$i('job.form.placeholder.name')" width="250px"/>
			<label>{{ $i('job.form.path') }}</label>
			<magic-input v-model:value="info.path" :placeholder="$i('job.form.placeholder.path')" width="auto" style="flex:1"/>
		</form>
		<div style="flex:1;padding-top:5px;">
			<magic-textarea v-model:value="info.description" :placeholder="$i('job.form.placeholder.description')"/>
		</div>
	</div>
</template>
<script setup>
import { inject } from 'vue'
const $i = inject('i18n.format')
const info = inject('info')
</script>
<style scoped>
.magic-job-info{
	display: flex;
	flex-direction: column;
	flex: 1;
	padding: 5px;
}
.magic-job-info form{
	display: flex;
}
.magic-job-info form label{
	display: inline-block;
	width: 75px;
	height: var(--magic-input-height);
	//line-height: var(--magic-input-height);
	font-weight: 400;
	text-align: right;
	padding: 0 5px;
}
.magic-job-info form :deep(.magic-checkbox){
	width: 22px;
	height: 22px;
}
.magic-job-info form :deep(.magic-textarea){
	margin: 5px;
}
</style>