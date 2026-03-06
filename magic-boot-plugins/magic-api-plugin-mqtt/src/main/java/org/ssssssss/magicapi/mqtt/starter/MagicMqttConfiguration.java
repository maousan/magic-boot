package org.ssssssss.magicapi.mqtt.starter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ssssssss.magicapi.core.config.MagicPluginConfiguration;
import org.ssssssss.magicapi.core.model.Plugin;
import org.ssssssss.magicapi.core.web.MagicControllerRegister;
import org.ssssssss.magicapi.mqtt.MqttModule;
import org.ssssssss.magicapi.mqtt.model.MagicDynamicMqttClient;
import org.ssssssss.magicapi.mqtt.service.MqttMagicDynamicRegistry;
import org.ssssssss.magicapi.mqtt.service.MqttMagicResourceStorage;
import org.ssssssss.magicapi.mqtt.web.MagicMqttController;

@Configuration
public class MagicMqttConfiguration implements MagicPluginConfiguration {

	@Override
	public Plugin plugin() {
		return new Plugin("mqtt消息中间件", "mqtt", "magic-mqtt.1.0.0.iife.js");
	}

	@Override
	public MagicControllerRegister controllerRegister() {
		return (mapping, configuration) -> mapping.registerController(new MagicMqttController(configuration));
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MagicDynamicMqttClient magicDynamicMqttClient() {
		return new MagicDynamicMqttClient();
	}

	@Bean
	@ConditionalOnMissingBean
	public MqttMagicResourceStorage mqttMagicResourceStorage() {
		return new MqttMagicResourceStorage();
	}

	@Bean
	@ConditionalOnMissingBean
	public MqttMagicDynamicRegistry mqttMagicDynamicRegistry(
			@Qualifier("mqttMagicResourceStorage") MqttMagicResourceStorage mqttMagicResourceStorage,
			@Qualifier("magicDynamicMqttClient") MagicDynamicMqttClient magicDynamicMqttClient) {
		return new MqttMagicDynamicRegistry(mqttMagicResourceStorage, magicDynamicMqttClient);
	}

	/**
	 * 注入mqtt模块
	 */
	@Bean(name = "magicMqttModule")
	@ConditionalOnMissingBean
	public MqttModule magicMqttModule(
			@Qualifier("magicDynamicMqttClient") MagicDynamicMqttClient magicDynamicMqttClient) {
		return new MqttModule(magicDynamicMqttClient);
	}

	
}
