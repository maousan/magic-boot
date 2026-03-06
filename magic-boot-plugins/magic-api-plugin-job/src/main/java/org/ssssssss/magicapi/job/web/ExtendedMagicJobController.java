package org.ssssssss.magicapi.job.web;

import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.job.entity.JobLog;
import org.ssssssss.magicapi.job.service.JobLogService;
import org.ssssssss.magicapi.job.service.JobMagicDynamicRegistryForQuartz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扩展的 JOB 控制器，提供暂停/恢复/立即执行/查询历史等功能
 */
@RestController
@RequestMapping("/magic/job")
public class ExtendedMagicJobController {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedMagicJobController.class);

    private final JobMagicDynamicRegistryForQuartz registry;
    private final JobLogService jobLogService;
    private final Scheduler scheduler;

    public ExtendedMagicJobController(JobMagicDynamicRegistryForQuartz registry,
                                      JobLogService jobLogService,
                                      Scheduler scheduler) {
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
            return new JsonBean<>("ok");
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
            return new JsonBean<>("ok");
        } catch (Exception e) {
            return new JsonBean<>(e.getMessage());
        }
    }

    @PostMapping("/{jobId}/trigger")
    @ResponseBody
    public JsonBean<Object> triggerJobNow(@PathVariable String jobId) {
        try {
            registry.triggerJob(jobId);
            logger.info("任务立即执行成功: {}", jobId);
            return new JsonBean<>("ok");
        } catch (Exception e) {
            return new JsonBean<>(e.getMessage());
        }
    }

    @GetMapping("/{jobId}/history")
    @ResponseBody
    public JsonBean<Object> getJobHistory(@PathVariable String jobId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        try {
            List<JobLog> history = jobLogService.getJobHistory(jobId, page, size);
            return new JsonBean<>(history);
        } catch (Exception e) {
            return new JsonBean<>(e.getMessage());
        }
    }

    @GetMapping("/{jobId}/state")
    @ResponseBody
    public JsonBean<Object> getJobState(@PathVariable String jobId) {
        try {
            org.quartz.JobKey jobKey = new org.quartz.JobKey(jobId, "MAGIC_API_JOBS");
            boolean exists = scheduler.checkExists(jobKey);
            if (!exists) {
                return new JsonBean<>("任务不存在");
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
            state.put("nextFireTime", nextFireTime != null ? nextFireTime.toString() : null);

            return new JsonBean<>(state);
        } catch (Exception e) {
            return new JsonBean<>(e.getMessage());
        }
    }
}
