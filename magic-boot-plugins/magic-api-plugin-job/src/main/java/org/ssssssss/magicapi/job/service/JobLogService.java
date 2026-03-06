package org.ssssssss.magicapi.job.service;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.ssssssss.magicapi.job.entity.JobLog;
import org.ssssssss.magicapi.job.repository.JobLogRepository;

import java.time.LocalDateTime;

@Service
public class JobLogService {

    private static final Logger logger = LoggerFactory.getLogger(JobLogService.class);

    private final JobLogRepository jobLogRepository;
    private final Scheduler scheduler;

    public JobLogService(JobLogRepository jobLogRepository, Scheduler scheduler) {
        this.jobLogRepository = jobLogRepository;
        this.scheduler = scheduler;
    }

    /**
     * 记录任务开始
     */
    public void logStart(String jobId, String jobName, String triggerType, String scriptPath) {
        try {
            JobLog log = new JobLog();
            log.setJobId(jobId);
            log.setJobName(jobName);
            log.setJobGroup("MAGIC_API_JOBS");
            log.setTriggerType(triggerType);
            log.setScriptPath(scriptPath);
            LocalDateTime now = LocalDateTime.now();
            log.setStartTime(now);
            log.setCreateTime(now);
            log.setStatus("RUNNING");

            jobLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Error logging job start: {}", jobId, e);
        }
    }

    /**
     * 记录任务成功完成
     */
    public void logSuccess(String jobId, String jobName, Object result) {
        try {
            LocalDateTime now = LocalDateTime.now();
            JobLog log = new JobLog();
            log.setJobId(jobId);
            log.setJobName(jobName);
            log.setJobGroup("MAGIC_API_JOBS");
            log.setStartTime(now);
            log.setEndTime(now);
            log.setDuration(0L);
            log.setStatus("SUCCESS");
            log.setResult(String.valueOf(result));
            log.setCreateTime(now);
            jobLogRepository.save(log);
        } catch (Exception e) {
            logger.error("Error logging job success: {}", jobId, e);
        }
    }

    /**
     * 记录任务失败
     */
    public void logFailure(String jobId, String jobName, Exception e) {
        try {
            LocalDateTime now = LocalDateTime.now();
            JobLog log = new JobLog();
            log.setJobId(jobId);
            log.setJobName(jobName);
            log.setJobGroup("MAGIC_API_JOBS");
            log.setStartTime(now);
            log.setEndTime(now);
            log.setDuration(0L);
            log.setStatus("FAILED");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStack(e.getStackTrace().toString());
            log.setCreateTime(now);
            jobLogRepository.save(log);
        } catch (Exception ex) {
            logger.error("Error logging job failure: {}", jobId, ex);
        }
    }
    
    /**
     * 获取任务执行历史
     */
    public java.util.List<JobLog> getJobHistory(String jobId, int page, int size) {
        try {
            return jobLogRepository.findByJobId(jobId, page, size);
        } catch (Exception e) {
            logger.error("Error getting job history: {}", jobId, e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * 清理旧的日志
     */
    public void cleanOldLogs(int days) {
        try {
            jobLogRepository.cleanOldLogs(days);
            logger.info("Cleaned job logs older than {} days", days);
        } catch (Exception e) {
            logger.error("Error cleaning old job logs", e);
        }
    }
}