package org.ssssssss.magicapi.job.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.ssssssss.magicapi.core.config.MagicPluginConfiguration;
import org.ssssssss.magicapi.core.model.Plugin;
import org.ssssssss.magicapi.core.web.MagicControllerRegister;
import org.ssssssss.magicapi.job.service.JobInfoMagicResourceStorage;
import org.ssssssss.magicapi.job.service.JobMagicDynamicRegistry;
import org.ssssssss.magicapi.job.web.MagicJobController;

@Configuration
@EnableConfigurationProperties(MagicJobConfig.class)
@ConditionalOnClass(QuartzAutoConfiguration.class)
public class MagicAPIJobConfiguration implements MagicPluginConfiguration {

	private final MagicJobConfig config;

	public MagicAPIJobConfiguration(MagicJobConfig config) {
		this.config = config;
	}

	@Bean
	@ConditionalOnMissingBean
	public JobInfoMagicResourceStorage jobInfoMagicResourceStorage() {
		return new JobInfoMagicResourceStorage();
	}

	@Bean
	@ConditionalOnMissingBean
	public JobMagicDynamicRegistry jobMagicDynamicRegistry(JobInfoMagicResourceStorage jobInfoMagicResourceStorage) {
		MagicJobConfig.Shutdown shutdown = config.getShutdown();
		ThreadPoolTaskScheduler poolTaskScheduler = null;
		if(config.isEnable()){
			poolTaskScheduler = new ThreadPoolTaskScheduler();
			poolTaskScheduler.setPoolSize(config.getPool().getSize());
			poolTaskScheduler.setWaitForTasksToCompleteOnShutdown(shutdown.isAwaitTermination());
			if(shutdown.getAwaitTerminationPeriod() != null){
				poolTaskScheduler.setAwaitTerminationSeconds((int) shutdown.getAwaitTerminationPeriod().getSeconds());
			}
			poolTaskScheduler.setThreadNamePrefix(config.getThreadNamePrefix());
			poolTaskScheduler.initialize();
		}
		return new JobMagicDynamicRegistry(jobInfoMagicResourceStorage, poolTaskScheduler, config.isLog());
	}

	@Override
	public Plugin plugin() {
		return new Plugin("定时任务Quartz", "MagicJob", "magic-job.1.0.0.iife.js");
	}

	@Override
	public MagicControllerRegister controllerRegister() {
		return (mapping, configuration) -> mapping.registerController(new MagicJobController(configuration));
	}
}
