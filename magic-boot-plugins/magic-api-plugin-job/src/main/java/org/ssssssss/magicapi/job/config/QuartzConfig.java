package org.ssssssss.magicapi.job.config;

import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JobFactory jobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        
        // 设置 RAM JobStore 而不是 JDBC（我们只需要使用数据库存储执行历史）
        Properties properties = new Properties();
        properties.put("org.quartz.scheduler.instanceName", "MagicJobScheduler");
        properties.put("org.quartz.threadPool.threadCount", String.valueOf(Runtime.getRuntime().availableProcessors()));
        properties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        factory.setQuartzProperties(properties);

        return factory;
    }

    @Bean
    public Scheduler scheduler() throws Exception {
        SchedulerFactoryBean factory = schedulerFactoryBean();
        factory.afterPropertiesSet();
        Scheduler scheduler = factory.getObject();
        scheduler.start();
        return scheduler;
    }
}