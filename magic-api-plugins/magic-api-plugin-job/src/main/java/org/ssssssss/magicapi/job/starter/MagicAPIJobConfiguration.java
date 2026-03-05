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
import org.ssssssss.magicapi.job.repository.JobLogRepository;
import org.ssssssss.magicapi.job.service.*;
import org.ssssssss.magicapi.job.web.ExtendedMagicJobController;
import org.ssssssss.magicapi.job.web.MagicJobController;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(MagicJobConfig.class)
@ConditionalOnClass(QuartzAutoConfiguration.class)
public class MagicAPIJobConfiguration implements MagicPluginConfiguration {

	private final MagicJobConfig config;
	
	@Autowired
    private Scheduler scheduler;

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
	public JobMagicDynamicRegistryForQuartz jobMagicDynamicRegistry(JobInfoMagicResourceStorage jobInfoMagicResourceStorage) {
		if (config.isEnable()) {
			return new JobMagicDynamicRegistryForQuartz(jobInfoMagicResourceStorage, scheduler, config.isLog());
		} else {
			// 如果禁止 job 功能，返回一个空实现的调度器
			return new JobMagicDynamicRegistryForQuartz(jobInfoMagicResourceStorage, null, config.isLog());
		}
	}

	@Override
	public Plugin plugin() {
		return new Plugin("定时任务Quartz", "MagicJob", "magic-job.1.0.0.iife.js");
	}

	@Override
	public MagicControllerRegister controllerRegister() {
		return (mapping, configuration) -> {
			mapping.registerController(new MagicJobController(configuration));
		};
	}


	@Bean
    public ExtendedMagicJobController extendedMagicJobController(
            JobMagicDynamicRegistryForQuartz registry,
            JobLogService jobLogService,
            Scheduler scheduler) {
        return new ExtendedMagicJobController(registry, jobLogService, scheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    public JobLogRepository jobLogRepository(DataSource dataSource) {
        return new JobLogRepository(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public JobLogService jobLogService(JobLogRepository jobLogRepository, Scheduler scheduler) {
        return new JobLogService(jobLogRepository, scheduler);
    }
}