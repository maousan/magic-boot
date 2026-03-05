<template>
	<div class="magic-plugin-container">
    <div class="magic-api-info">
      <form>
        <label style="width:auto">{{ $i('message.enable') }}</label>
        <magic-checkbox v-model:value="info.enabled" />
        <label>cron</label>
        <magic-input v-model:value="info.cron" :placeholder="$i('job.form.placeholder.cron')" width="250px"/>
        <label>{{ $i('job.form.name') }}</label>
        <magic-input v-model:value="info.name" :placeholder="$i('job.form.placeholder.name')" width="250px"/>
        <label>{{ $i('job.form.path') }}</label>
        <magic-input v-model:value="info.path" :placeholder="$i('job.form.placeholder.path')" width="auto" style="flex:1"/>
      </form>
      <div style="position:relative;flex:1;padding-top:5px;">
        <magic-monaco-editor ref="editor" @change="handleEditorContentChange" :value="info.description" language="text"></magic-monaco-editor>
        <!--      <magic-textarea v-model:value="info.description" :placeholder="$i('job.form.placeholder.description')"/>-->
      </div>
    </div>
	</div>
</template>

<script setup>
import { inject, ref, onMounted } from 'vue'
const $i = inject('i18n.format')
const info = inject('info')
const editor = ref();

const handleEditorContentChange = (e) => {
  const value = editor.value.getInstance().getValue()
  info.value.description = value
}

</script>


<style scoped>
.magic-plugin-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 5px;
}

.magic-api-info {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 5px;
}

.magic-api-info form {
  display: flex;
  padding: 5px;
}

.magic-api-info form :deep(.magic-checkbox){
  width: var(--magic-input-height);
  height: var(--magic-input-height);
}

.magic-api-info form label {
  display: inline-block;
  width: 75px;
  height: var(--magic-input-height);
  line-height: var(--magic-input-height);
  font-weight: 400;
  text-align: right;
  padding: 0 5px;
}

.magic-navbar .magic-navbar-body,
.magic-navbar.magic-navbar-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.magic-api-info+.magic-navbar {
  flex-direction: column;
  overflow: hidden;
}

.magic-monaco-editor {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}

</style>
