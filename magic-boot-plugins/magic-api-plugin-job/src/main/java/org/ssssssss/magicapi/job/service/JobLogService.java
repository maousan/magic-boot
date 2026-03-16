package org.ssssssss.magicapi.job.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.ssssssss.magicapi.job.entity.JobLog;
import org.ssssssss.magicapi.job.mapper.JobLogMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class JobLogService {

    private static final Logger logger = LoggerFactory.getLogger(JobLogService.class);

    private final JobLogMapper jobLogMapper;
    private final Scheduler scheduler;

    public JobLogService(JobLogMapper jobLogMapper, Scheduler scheduler) {
        this.jobLogMapper = jobLogMapper;
        this.scheduler = scheduler;
    }

    /**
     * 记录任务开始
     */
    public Long logStart(String jobId, String jobName, String triggerType) {
        try {
            JobLog log = new JobLog();
            log.setJobId(jobId);
            log.setJobName(jobName);
            log.setJobGroup("MAGIC_API_JOBS");
            log.setTriggerType(triggerType);
            LocalDateTime now = LocalDateTime.now();
            log.setStartTime(now);
            log.setCreateTime(now);
            log.setStatus("RUNNING");

            jobLogMapper.insert(log);

            return log.getId();

        } catch (Exception e) {
            logger.error("Error logging job start: {}", jobId, e);
        }
        return null;
    }

    /**
     * 记录任务成功完成
     */
    public void logSuccess(Long jobLogId, Object result, long duration, LocalDateTime endTime) {
        try {
            LambdaUpdateWrapper<JobLog> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(JobLog::getId, jobLogId)
                    .set(JobLog::getResult, result != null ? result.toString() : null)
                    .set(JobLog::getDuration, duration)
                    .set(JobLog::getEndTime, endTime)
                    .set(JobLog::getStatus, "SUCCESS");
            jobLogMapper.update(null, updateWrapper);
        } catch (Exception e) {
            logger.error("Error logging job success: {}", jobLogId, e);
        }
    }

    /**
     * 记录任务失败
     */
    public void logFailure(Long jobLogId, long duration, LocalDateTime endTime, Exception e) {
        try {
            LambdaUpdateWrapper<JobLog> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(JobLog::getId, jobLogId)
                    .set(JobLog::getStatus, "FAILURE")
                    .set(JobLog::getDuration, duration)
                    .set(JobLog::getEndTime, endTime)
                    .set(JobLog::getExceptionStack, e.getLocalizedMessage())
                    .set(JobLog::getExceptionMessage, ExceptionUtils.getStackTrace(e));
            jobLogMapper.update(null, updateWrapper);
        } catch (Exception ex) {
            logger.error("Error logging job failure: {}", jobLogId, ex);
        }
    }

    /**
     * 根据ID查询日志
     */
    public JobLog findById(Long id) {
        return jobLogMapper.selectById(id);
    }

    /**
     * 获取任务执行历史
     */
    public List<JobLog> getJobHistory(String jobId, int page, int size) {
        try {
            Page<JobLog> pageParam = new Page<>(page, size);
            IPage<JobLog> result = jobLogMapper.selectByJobId(pageParam, jobId);
            return result.getRecords();
        } catch (Exception e) {
            logger.error("Error getting job history: {}", jobId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 分页获取任务执行历史（带总数）
     */
    public IPage<JobLog> getJobHistoryPage(String jobId, int page, int size) {
        try {
            Page<JobLog> pageParam = new Page<>(page, size);
            return jobLogMapper.selectByJobId(pageParam, jobId);
        } catch (Exception e) {
            logger.error("Error getting job history page: {}", jobId, e);
            return new Page<>(page, size);
        }
    }

    /**
     * 清理旧的日志
     */
    public void cleanOldLogs(int days) {
        try {
            int deleted = jobLogMapper.cleanOldLogs(days);
            logger.info("Cleaned {} job logs older than {} days", deleted, days);
        } catch (Exception e) {
            logger.error("Error cleaning old job logs", e);
        }
    }

    /**
     * 根据状态查询日志列表
     */
    public List<JobLog> findByStatus(String status) {
        LambdaQueryWrapper<JobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobLog::getStatus, status)
                .orderByDesc(JobLog::getStartTime);
        return jobLogMapper.selectList(queryWrapper);
    }

    /**
     * 根据任务ID和状态查询日志列表
     */
    public List<JobLog> findByJobIdAndStatus(String jobId, String status) {
        LambdaQueryWrapper<JobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobLog::getJobId, jobId)
                .eq(JobLog::getStatus, status)
                .orderByDesc(JobLog::getStartTime);
        return jobLogMapper.selectList(queryWrapper);
    }

    /**
     * 统计任务执行次数
     */
    public long countByJobId(String jobId) {
        LambdaQueryWrapper<JobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobLog::getJobId, jobId);
        return jobLogMapper.selectCount(queryWrapper);
    }

    /**
     * 统计任务成功次数
     */
    public long countSuccessByJobId(String jobId) {
        LambdaQueryWrapper<JobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobLog::getJobId, jobId)
                .eq(JobLog::getStatus, "SUCCESS");
        return jobLogMapper.selectCount(queryWrapper);
    }

    /**
     * 统计任务失败次数
     */
    public long countFailureByJobId(String jobId) {
        LambdaQueryWrapper<JobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobLog::getJobId, jobId)
                .eq(JobLog::getStatus, "FAILURE");
        return jobLogMapper.selectCount(queryWrapper);
    }
}
