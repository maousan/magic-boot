package org.ssssssss.magicapi.job.web;

import cn.hutool.core.date.DateUtil;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.job.entity.JobLog;
import org.ssssssss.magicapi.job.model.JobInfo;
import org.ssssssss.magicapi.job.service.MagicJobLogService;
import org.ssssssss.magicapi.job.service.JobMagicDynamicRegistryForQuartz;

import java.util.*;

/**
 * 扩展的 JOB 控制器，提供暂停/恢复/立即执行/查询历史等功能
 */
@RestController
@RequestMapping("/magic/job")
public class ExtendedMagicJobController {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedMagicJobController.class);

    private final JobMagicDynamicRegistryForQuartz registry;
    private final MagicJobLogService jobLogService;
    private final Scheduler scheduler;

    public ExtendedMagicJobController(JobMagicDynamicRegistryForQuartz registry, MagicJobLogService jobLogService, Scheduler scheduler) {
        this.registry = registry;
        this.jobLogService = jobLogService;
        this.scheduler = scheduler;
    }

    @PostMapping("/{jobId}/pause")
    @ResponseBody
    public JsonBean<Object> pauseJob(@PathVariable String jobId) {
        try {
            registry.pauseJob(jobId);
            logger.info("任务暂停成功: {}", jobId);
            return new JsonBean<>(200, "ok");
        } catch (Exception e) {
            return new JsonBean<>(e.getMessage());
        }
    }

    @PostMapping("/{jobId}/resume")
    @ResponseBody
    public JsonBean<Object> resumeJob(@PathVariable String jobId) {
        try {
            registry.resumeJob(jobId);
            logger.info("任务恢复成功: {}", jobId);
            return new JsonBean<>(200, "ok");
        } catch (Exception e) {
            return new JsonBean<>(500, e.getMessage());
        }
    }

    @PostMapping("/{jobId}/trigger")
    @ResponseBody
    public JsonBean<Object> triggerJobNow(@PathVariable String jobId) {
        try {
            registry.triggerJob(jobId);
            logger.info("任务立即执行成功: {}", jobId);
            return new JsonBean<>(200, "ok");
        } catch (Exception e) {
            return new JsonBean<>(500, e.getMessage());
        }
    }

    @GetMapping("/{jobId}/history")
    @ResponseBody
    public JsonBean<Object> getJobHistory(@PathVariable String jobId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            List<JobLog> history = jobLogService.getJobHistory(jobId, page, size);
            return new JsonBean<>(200, "ok", history);
        } catch (Exception e) {
            return new JsonBean<>(500, e.getMessage());
        }
    }

    @GetMapping("/{jobId}/state")
    @ResponseBody
    public JsonBean<Object> getJobState(@PathVariable String jobId) {
        try {
            org.quartz.JobKey jobKey = new org.quartz.JobKey(jobId, "MAGIC_API_JOBS");
            boolean exists = scheduler.checkExists(jobKey);
            if (!exists) {
                return new JsonBean<>(500, "任务不存在");
            }

            org.quartz.TriggerKey triggerKey = new org.quartz.TriggerKey(jobId + "_trigger", "MAGIC_API_JOBS");
            org.quartz.Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            boolean paused = triggerState == org.quartz.Trigger.TriggerState.PAUSED;

            // 获取下一个执行时间
            org.quartz.Trigger trigger = scheduler.getTrigger(triggerKey);
            Date nextFireTime = trigger != null ? trigger.getNextFireTime() : null;

            Map<String, Object> state = new HashMap<>();
            state.put("exists", exists);
            state.put("paused", paused);
            state.put("nextFireTime", nextFireTime != null ? DateUtil.formatDateTime(trigger.getNextFireTime()) : null);

            return new JsonBean<>(200, "ok", state);
        } catch (Exception e) {
            return new JsonBean<>(500, e.getMessage());
        }
    }

    @GetMapping("/list")
    @ResponseBody
    public JsonBean<Object> listJobs() {
        try {
            String jobGroup = "MAGIC_API_JOBS";
            var jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroup));
            List<Map<String, Object>> items = new ArrayList<>();

            for (var jobKey : jobKeys) {
                String jobId = jobKey.getName();
                var entity = MagicConfiguration.getMagicResourceService().file(jobId);
                if (entity == null) {
                    continue;
                }
                JobInfo jobInfo = (JobInfo) entity;

                TriggerKey triggerKey = new TriggerKey(jobId + "_trigger", jobGroup);
                boolean paused = false;
                String nextFireTime = null;

                try {
                    Trigger.TriggerState state = scheduler.getTriggerState(triggerKey);
                    paused = state == Trigger.TriggerState.PAUSED;
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    if (trigger != null && trigger.getNextFireTime() != null) {
                        nextFireTime = DateUtil.formatDateTime(trigger.getNextFireTime());
                    }
                } catch (Exception e) {
                    logger.warn("Failed to get trigger state for job {}", jobId, e);
                }

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", jobId);
                item.put("name", jobInfo.getName());
                item.put("path", jobInfo.getPath());
                item.put("cron", jobInfo.getCron());
                item.put("enabled", jobInfo.isEnabled());
                item.put("description", jobInfo.getDescription());
                item.put("paused", paused);
                item.put("nextFireTime", nextFireTime);
                items.add(item);
            }

            return new JsonBean<>(200, "success", items);
        } catch (Exception e) {
            return new JsonBean<>(500, e.getMessage());
        }
    }
}
