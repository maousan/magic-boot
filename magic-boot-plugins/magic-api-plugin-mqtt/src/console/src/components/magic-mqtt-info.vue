<template>
	<div class="magic-mqtt-info">
		<form>
			<div class="magic-form-row">
				<label>{{ $i('message.name') }}</label>
				<magic-input v-model:value="info.name" :placeholder="$i('mqtt.form.placeholder.name')" />
			</div>
			<div class="magic-form-row">
				<label>key</label>
				<magic-input v-model:value="info.key" :placeholder="$i('mqtt.form.placeholder.name')" />
			</div>

			<div class="magic-form-row">
				<label>{{ $i('datasource.form.other') }}</label>
				<magic-monaco-editor language="json" v-model:value="properties" style="height:150px" />
			</div>
		</form>
	</div>
</template>
<script setup>
import { ref, watch, inject,defineProps } from 'vue'
const $i = inject('i18n.format')
const constants = inject('constants')
const { info } = defineProps({
    info: Object
})

const properties = ref(JSON.stringify(__props.info.properties || {
	broker: "mqtt 集群的 broker 地址列表",
	username: "认证用户名称",
	password: "认证密码",
	queueSize: "用于存放等待执行的任务的队列，建议：500~1000",
	"connection-timeout": "连接超时(秒)，不宜过长,建议：30",
	waitForCompletion: "客户端连接broker超时(秒),局域网(3~5)、公共Wi-Fi（10）、跨国（15）",
	qos: "服务质量等级, 建议：高并发建议1，关键业务用2",
	"keep-alive-interval": "心跳间隔 (秒)，保持连接活跃的心跳包发送间隔，建议小于代理服务器的超时设置，建议：120",
	"automatic-reconnect": "必须开启自动重连，true",
	"clean-session": "true: 每次连接创建新会话(不保留订阅和未接收消息); false: 恢复已有会话(保留订阅和QoS1/2的未接收消息)，建议 true"
}, null, 2))

watch(properties, (val) => {
	try {
		__props.info.properties = JSON.parse(val)
	} catch (e) {
		__props.info.properties = {}
	}
})
</script>