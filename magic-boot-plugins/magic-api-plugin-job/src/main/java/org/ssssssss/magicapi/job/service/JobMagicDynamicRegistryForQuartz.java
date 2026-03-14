package org.ssssssss.magicapi.job.service;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.event.FileEvent;
import org.ssssssss.magicapi.core.event.GroupEvent;
import org.ssssssss.magicapi.core.service.AbstractMagicDynamicRegistry;
import org.ssssssss.magicapi.core.service.MagicResourceStorage;
import org.ssssssss.magicapi.job.model.JobInfo;
import org.ssssssss.magicapi.job.quartz.MagicScriptJob;
import org.ssssssss.magicapi.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class JobMagicDynamicRegistryForQuartz extends AbstractMagicDynamicRegistry<JobInfo> {

    private final Scheduler scheduler;
    private final boolean showLog;

    private static final Logger logger = LoggerFactory.getLogger(JobMagicDynamicRegistryForQuartz.class);

    public JobMagicDynamicRegistryForQuartz(MagicResourceStorage<JobInfo> magicResourceStorage, Scheduler scheduler, boolean showLog) {
        super(magicResourceStorage);
        this.scheduler = scheduler;
        this.showLog = showLog;
    }

    @EventListener(condition = "#event.type == 'job'")
    public void onFileEvent(FileEvent event) throws SchedulerException {
        logger.debug("文件事件: {}", JsonUtils.toJsonString(event));
        JobInfo jobInfo = (JobInfo) event.getEntity();
        JobKey jobKey = new JobKey(jobInfo.getId(), "MAGIC_API_JOBS");
        if (scheduler.checkExists(jobKey)) {
            if (!jobInfo.isEnabled()) {
                logger.info("任务已禁用，已取消注册：[{}]", MagicConfiguration.getMagicResourceService().getScriptName(jobInfo));
                unregister(jobInfo);
            }
        } else if(jobInfo.isEnabled()) {
            logger.info("任务已启用，已注册：[{}]", MagicConfiguration.getMagicResourceService().getScriptName(jobInfo));
            register(jobInfo);
        }
        processEvent(event);
    }

    @EventListener(condition = "#event.type == 'job'")
    public void onGroupEvent(GroupEvent event) {
        processEvent(event);
    }

    @Override
    public boolean register(JobInfo entity) {
        unregister(entity);
        return super.register(entity);
    }

    @Override
    protected boolean register(MappingNode<JobInfo> mappingNode) {
        JobInfo entity = mappingNode.getEntity();

        // 如果任务未启用，则取消注册并返回
        if (!entity.isEnabled()) {
            unregister(mappingNode);
            if (showLog) {
                logger.info("任务未启用，已取消注册：[{}]", MagicConfiguration.getMagicResourceService().getScriptName(entity));
            }
            return false;
        }

        String groupName = "MAGIC_API_JOBS";

        try {
            String jobName = entity.getId();
            if (jobName == null) {
                jobName = entity.getPath().replaceAll("/", "_");
            }

            // 创建 JobDetail
            JobDetail jobDetail = JobBuilder.newJob(MagicScriptJob.class)
                    .withIdentity(jobName, groupName)
                    .usingJobData("script", entity.getScript())
                    .usingJobData("scriptName", MagicConfiguration.getMagicResourceService().getScriptName(entity))
                    .usingJobData("showLog", showLog)
                    .usingJobData("jobType", entity.getJobType() != null ? entity.getJobType() : "script")
                    .usingJobData("params", entity.getParams())
                    .usingJobData("clazz", entity.getClazz())
                    .storeDurably()
                    .build();

            // 根据任务配置构建触发器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(entity.getCron());

            // 根据错失策略设置错失处理
            switch (entity.getMisfirePolicy()) {
                case SMART:
                    scheduleBuilder = scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                    break;
                case IGNORE:
                    scheduleBuilder = scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                    break;
                case FIRE_ONCE_NOW:
                    scheduleBuilder = scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                    break;
            }

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName + "_trigger", groupName)
                    .withSchedule(scheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            mappingNode.setMappingData(jobName);

            if (showLog) {
                logger.info("定时任务注册成功: [{}, {}]", MagicConfiguration.getMagicResourceService().getScriptName(entity), entity.getCron());
            }
        } catch (Exception e) {
            String scriptName = MagicConfiguration.getMagicResourceService().getScriptName(entity);
            logger.error("定时任务:[{}]注册失败", scriptName, e);
        }

        return true;
    }

    @Override
    protected void unregister(MappingNode<JobInfo> mappingNode) {
        if (scheduler == null) {
            return;
        }
        JobInfo info = mappingNode.getEntity();
        String groupName = "MAGIC_API_JOBS";
        String jobName = (String) mappingNode.getMappingData();

        if (jobName == null) {
            jobName = info.getPath().replaceAll("/", "_");
        }

        try {
            JobKey jobKey = new JobKey(jobName, groupName);
            scheduler.deleteJob(jobKey);

            if (showLog) {
                String scriptName = MagicConfiguration.getMagicResourceService().getScriptName(info);
                logger.info("定时任务:[{}]取消注册", scriptName);
            }
        } catch (Exception e) {
            String scriptName = MagicConfiguration.getMagicResourceService().getScriptName(info);
            logger.warn("定时任务:[{}]取消失败", scriptName, e);
        }
    }

    /**
     * 暂停任务
     */
    public void pauseJob(String jobId) {
        try {
            JobKey jobKey = new JobKey(jobId, "MAGIC_API_JOBS");
            if (scheduler.checkExists(jobKey)) {
                scheduler.pauseJob(jobKey);
                logger.info("任务暂停成功: {}", jobId);
            } else {
                logger.warn("尝试暂停不存在的任务: {}", jobId);
            }
        } catch (Exception e) {
            logger.error("暂停任务失败: {}", jobId, e);
        }
    }

    /**
     * 恢复任务
     */
    public void resumeJob(String jobId) {
        try {
            JobKey jobKey = new JobKey(jobId, "MAGIC_API_JOBS");
            if (scheduler.checkExists(jobKey)) {
                scheduler.resumeJob(jobKey);
                logger.info("任务恢复成功: {}", jobId);
            } else {
                logger.warn("尝试恢复不存在的任务: {}", jobId);
            }
        } catch (Exception e) {
            logger.error("恢复任务失败: {}", jobId, e);
        }
    }

    /**
     * 立即执行一次任务
     */
    public void triggerJob(String jobId) {
        try {
            JobKey jobKey = new JobKey(jobId, "MAGIC_API_JOBS");
            if (scheduler.checkExists(jobKey)) {
                scheduler.triggerJob(jobKey);
                logger.info("立即执行任务: {}", jobId);
            } else {
                logger.warn("尝试执行不存在的任务: {}", jobId);
            }
        } catch (Exception e) {
            logger.error("立即执行任务失败: {}", jobId, e);
        }
    }
}