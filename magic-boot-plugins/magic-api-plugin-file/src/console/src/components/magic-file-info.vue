<template>
    <div class="magic-file-info">
        <form>
            <div class="magic-form-row">
                <label>{{ $i("message.name") }}</label>
                <magic-input v-model:value="info.name" :placeholder="$i('file.form.placeholder.name')" />
            </div>
            <div class="magic-form-row">
                <label>key</label>
                <magic-input v-model:value="info.key" :placeholder="$i('file.form.placeholder.key')" />
            </div>
            <div class="magic-form-row">
                <label>{{ $i("file.form.type") }}</label>
                <magic-select
                    inputable
                    @update:value="handleTypeChange"
                    v-model:value="info.type"
                    width="100%"
                    :options="storageTypeOptions"
                    :placeholder="$i('file.form.placeholder.type')"
                />
            </div>

            <!-- 本地存储配置 -->
            <template v-if="info.type === 'local'">
                <div class="magic-form-row">
                    <label>{{ $i("file.form.basePath") }}</label>
                    <magic-input v-model:value="properties.basePath" placeholder="D:/mb/upload/" />
                </div>
                <div class="magic-form-row">
                    <label>{{ $i("file.form.domain") }}</label>
                    <magic-input v-model:value="properties.domain" placeholder="http://localhost:8089" />
                </div>
            </template>

            <!-- S3/MinIO 存储配置 -->
            <template v-if="info.type === 's3' || info.type === 'minio'">
                <div class="magic-form-row">
                    <label>{{ $i("file.form.endpoint") }}</label>
                    <magic-input
                        v-model:value="properties.endpoint"
                        :placeholder="info.type === 's3' ? 'https://s3.amazonaws.com' : 'http://192.168.1.100:9000'"
                    />
                </div>
                <div class="magic-form-row">
                    <label>{{ $i("file.form.bucket") }}</label>
                    <magic-input v-model:value="properties.bucket" placeholder="my-bucket" />
                </div>
                <div class="magic-form-row">
                    <label>{{ $i("file.form.accessKey") }}</label>
                    <magic-input v-model:value="properties.accessKey" placeholder="Access Key" />
                </div>
                <div class="magic-form-row">
                    <label>{{ $i("file.form.secretKey") }}</label>
                    <magic-input type="password" v-model:value="properties.secretKey" placeholder="Secret Key" />
                </div>
                <div class="magic-form-row">
                    <label>{{ $i("file.form.region") }}</label>
                    <magic-input v-model:value="properties.region" placeholder="us-east-1" />
                </div>
            </template>

            <!-- 通用配置 -->
            <div class="magic-form-row">
                <label>{{ $i("file.form.isDefault") }}</label>
                <magic-checkbox v-model:value="info.isDefault" style="width: auto" />
            </div>
            <div class="magic-form-row">
                <label>{{ $i("file.form.enabled") }}</label>
                <magic-checkbox v-model:value="info.enabled" style="width: auto" />
            </div>
        </form>
    </div>
</template>
<script setup>
import { ref, watch, inject, defineProps, onMounted } from "vue";
const $i = inject("i18n.format");

const { info } = defineProps({
    info: Object,
});

// 存储类型选项
const storageTypeOptions = ref([
    { text: "本地存储", value: "local" },
    { text: "Amazon S3", value: "s3" },
    { text: "MinIO", value: "minio" },
]);

// 配置属性
const properties = ref({});

// 初始化属性
const initProperties = () => {
    if (__props.info.properties) {
        properties.value = { ...__props.info.properties };
    } else {
        properties.value = {};
    }
};
const handleTypeChange = (type) => {
    if (type === "local") {
        properties.value = {
            basePath: "D:/mb/upload/",
            domain: "http://localhost:8089",
        };
    } else if (type === "s3") {
        properties.value = {
            endpoint: "https://s3.amazonaws.com",
            bucket: "",
            accessKey: "",
            secretKey: "",
            region: "us-east-1",
        };
    } else if (type === "minio") {
        properties.value = {
            endpoint: "http://192.168.1.100:9000",
            bucket: "",
            accessKey: "",
            secretKey: "",
            region: "us-east-1",
        };
    }
    __props.info.properties = properties.value;
};

// 监听属性变化，同步到 info
watch(
    properties,
    (val) => {
        __props.info.properties = { ...val };
    },
    { deep: true },
);

// 初始化
onMounted(() => {
    initProperties();
});
</script>
<style scoped>
.magic-file-info {
    padding: 10px;
}

.magic-file-info .magic-form-row {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}

.magic-file-info .magic-form-row > label {
    display: inline-block;
    width: 100px;
    min-width: 100px;
    text-align: right;
    padding-right: 10px;
    color: var(--magic-color-text, #606266);
}

.magic-file-info .magic-form-row > :not(label) {
    //flex: 1;
}
</style>
