<template>
  <div class="magic-plugin-container">
    <div class="magic-api-info">
      <form>
        <!--      <label>{{ $i('message.enable') }}</label>-->
        <!--      <magic-checkbox v-model:value="info.enabled" />-->
        <label>{{ $i('liteflow.component.form.nodeType') }}</label>
        <magic-select v-model:value="info.nodeType" defaultValue="common" :options="nodeTypeOptions"
                      :placeholder="$i('liteflow.component.form.placeholder.nodeType')" width="150px" style="flex:1"/>
        <label>{{ $i('liteflow.component.form.nodeId') }}</label>
        <magic-input v-model:value="info.nodeId" :placeholder="$i('liteflow.component.form.placeholder.nodeId')"
                     width="150px"/>
        <label>{{ $i('liteflow.component.form.name') }}</label>
        <magic-input v-model:value="info.name" :placeholder="$i('liteflow.component.form.placeholder.name')"
                     width="200px"/>
        <label>{{ $i('liteflow.component.form.scriptType') }}</label>
        <magic-select v-model:value="info.scriptType" defaultValue="groovy" :options="scriptOptions"
                      :placeholder="$i('liteflow.component.form.placeholder.scriptType')" width="auto" style="flex:1">
        </magic-select>
        <label>{{ $i('liteflow.component.form.path') }}</label>
        <magic-input v-model:value="info.path" :placeholder="$i('liteflow.component.form.placeholder.path')"
                     width="auto" style="flex:1"/>
      </form>
    </div>
    <div style="position:relative;flex:1;padding-top:5px;">
      <magic-monaco-editor ref="editor" :value="info.description" language="text"
                           @change="handleEditorContentChange"
                      :placeholder="$i('liteflow.component.form.placeholder.description')"/>
    </div>
  </div>
</template>
<script setup>
import {inject, onMounted, onUnmounted, ref} from 'vue'

const $i = inject('i18n.format')
const info = inject('info')
const editor = ref();
const nodeTypeOptions = ref([
  {
    value: 'common',
    text: '普通组件'
  },
  {
    value: 'script',
    text: '脚本组件'
  }
])
const scriptOptions = ref([
  {
    value: 'groovy',
    text: 'groovy'
  },
  {
    value: 'javascript',
    text: 'javascript'
  },
  {
    value: 'qlexpress',
    text: 'qlexpress'
  }
])

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