export default (request, $i, modal, JavaClass) => {
	let findResources
	// 设置代码提示
	JavaClass.setExtensionAttribute('org.ssssssss.magicapi.mqtt.MqttModule', () => {
		return findResources && (findResources('mqtt')[0]?.children || []).filter(it => it.key).map(it => {
			return {
				name: it.key,
				type: 'org.ssssssss.magicapi.mqtt.MqttModule',
				comment: it.name
			}
		}) || []
	})
	return {
		injectResources: fn => findResources = fn,
		requireScript: false,
		doTest: info => {
			request.sendJson('/mqtt/jdbc/test', info).success(res => {
				if (res === 'ok') {
					modal.alert($i('mqtt.connected'), $i('mqtt.test'))
				} else {
					modal.alert($i('mqtt.connectFailed', res), $i('mqtt.test'))
				}
			})
		}
	}
}
