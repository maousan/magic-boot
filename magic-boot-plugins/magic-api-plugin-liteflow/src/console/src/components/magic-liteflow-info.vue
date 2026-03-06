<template>
  <div class="magic-plugin-container">
    <div class="magic-api-info">
      <form>
        <label style="width:40px;">{{ $i('message.enable') }}</label>
        <magic-checkbox v-model:value="info.enabled"/>
        <label>{{ $i('liteflow.flow.form.chainId') }}</label>
        <magic-input v-model:value="info.chainId" :placeholder="$i('liteflow.flow.form.placeholder.chainId')"
                     width="140px"/>
        <label>{{ $i('liteflow.flow.form.name') }}</label>
        <magic-input v-model:value="info.name" :placeholder="$i('liteflow.flow.form.placeholder.name')" width="140px"/>
        <label>{{ $i('liteflow.flow.form.namespace') }}</label>
        <magic-input v-model:value="info.namespace" :placeholder="$i('liteflow.flow.form.placeholder.namespace')"
                     width="140px"/>
        <label>{{ $i('liteflow.flow.form.route') }}</label>
        <magic-input v-model:value="info.route" :placeholder="$i('liteflow.flow.form.placeholder.route')" width="140px"/>
        <label>{{ $i('liteflow.flow.form.path') }}</label>
        <magic-input v-model:value="info.path" :placeholder="$i('liteflow.flow.form.placeholder.path')" width="auto"
                     style="flex:1"/>
      </form>
    </div>
    <div class="magic-navbar magic-navbar__horizontal" style="flex:1">
      <ul class="magic-navbar-header none-select">
        <li v-for="(item, key) in navs" :key="'request_item_' + key" :class="{ selected: showIndex === key }"
            @click="showIndex = key;">{{ item }}
        </li>
      </ul>
      <div v-show="showIndex === 0" class="magic-navbar-body">
        <magic-monaco-editor ref="editor1" :value="info.params" language="json" @change="handleParamsChange"></magic-monaco-editor>
      </div>
      <div v-show="showIndex === 1" class="magic-navbar-body">
        <magic-monaco-editor ref="editor2" :value="info.description" language="text" @change="handleDescriptionChange"></magic-monaco-editor>
      </div>
    </div>
  </div>
</template>

<script setup>
import {inject, ref} from 'vue'

const $i = inject('i18n.format')
const info = inject('info')
const bus = inject('bus')
const editor1 = ref()
const editor2 = ref()

const showIndex = ref(0);

const navs = ref(['请求参数', '描述']);

const handleParamsChange = (e) => {
  const value = editor1.value.getInstance().getValue()
  console.log(value);
  info.value.params = value
}

const handleDescriptionChange = (e) => {
  const value = editor2.value.getInstance().getValue()
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
