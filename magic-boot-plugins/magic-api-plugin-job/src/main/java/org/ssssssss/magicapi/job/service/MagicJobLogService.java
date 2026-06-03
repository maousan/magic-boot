package org.ssssssss.magicapi.job.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import org.ssssssss.magicapi.job.entity.JobLog;
import org.ssssssss.magicapi.job.starter.MagicJobConfig;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class MagicJobLogService {

    private static final Logger logger = LoggerFactory.getLogger(MagicJobLogService.class);

    private final JdbcTemplate jdbcTemplate;
    private final Scheduler scheduler;
    private final MagicJobConfig config;

    private final RowMapper<JobLog> rowMapper = (rs, rowNum) -> {
        JobLog log = new JobLog();
        log.setId(rs.getLong("id"));
        log.setJobId(rs.getString("job_id"));
        log.setJobName(rs.getString("job_name"));
        log.setJobGroup(rs.getString("job_group"));
        log.setStartTime(rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null);
        log.setEndTime(rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null);
        log.setDuration(rs.getObject("duration") != null ? rs.getLong("duration") : null);
        log.setStatus(rs.getString("status"));
        log.setResult(rs.getString("result"));
        log.setExceptionMessage(rs.getString("exception_message"));
        log.setExceptionStack(rs.getString("exception_stack"));
        log.setTriggerType(rs.getString("trigger_type"));
        log.setTriggeredBy(rs.getString("triggered_by"));
        log.setCreateTime(rs.getTimestamp("create_time") != null ? rs.getTimestamp("create_time").toLocalDateTime() : null);
        return log;
    };

    public MagicJobLogService(JdbcTemplate jdbcTemplate, Scheduler scheduler, MagicJobConfig config) {
        this.jdbcTemplate = jdbcTemplate;
        this.scheduler = scheduler;
        this.config = config;
    }

    private String table() {
        return config.getTableName();
    }

    public Long logStart(String jobId, String jobName, String triggerType) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String sql = "INSERT INTO " + table() + " (job_id, job_name, job_group, trigger_type, start_time, create_time, status) VALUES (?, ?, 'MAGIC_API_JOBS', ?, ?, ?, 'RUNNING')";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, jobId);
                ps.setString(2, jobName);
                ps.setString(3, triggerType);
                ps.setObject(4, now);
                ps.setObject(5, now);
                return ps;
            }, keyHolder);
            return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        } catch (Exception e) {
            logger.error("Error logging job start: {}", jobId, e);
        }
        return null;
    }

    public void logSuccess(Long jobLogId, Object result, long duration, LocalDateTime endTime) {
        try {
            String sql = "UPDATE " + table() + " SET result = ?, duration = ?, end_time = ?, status = 'SUCCESS' WHERE id = ?";
            jdbcTemplate.update(sql, result != null ? result.toString() : null, duration, endTime, jobLogId);
        } catch (Exception e) {
            logger.error("Error logging job success: {}", jobLogId, e);
        }
    }

    public void logFailure(Long jobLogId, long duration, LocalDateTime endTime, Exception e) {
        try {
            String sql = "UPDATE " + table() + " SET status = 'FAILURE', duration = ?, end_time = ?, exception_stack = ?, exception_message = ? WHERE id = ?";
            jdbcTemplate.update(sql, duration, endTime, ExceptionUtils.getStackTrace(e), e.getLocalizedMessage(), jobLogId);
        } catch (Exception ex) {
            logger.error("Error logging job failure: {}", jobLogId, ex);
        }
    }

    public JobLog findById(Long id) {
        try {
            String sql = "SELECT * FROM " + table() + " WHERE id = ?";
            List<JobLog> list = jdbcTemplate.query(sql, rowMapper, id);
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            logger.error("Error finding job log by id: {}", id, e);
            return null;
        }
    }

    public List<JobLog> getJobHistory(String jobId, int page, int size) {
        try {
            long offset = (long) (page - 1) * size;
            String sql = "SELECT * FROM " + table() + " WHERE job_id = ? ORDER BY start_time DESC LIMIT ? OFFSET ?";
            return jdbcTemplate.query(sql, rowMapper, jobId, size, offset);
        } catch (Exception e) {
            logger.error("Error getting job history: {}", jobId, e);
            return Collections.emptyList();
        }
    }

    public void cleanOldLogs(int days) {
        try {
            String sql = "DELETE FROM " + table() + " WHERE start_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
            int deleted = jdbcTemplate.update(sql, days);
            logger.info("Cleaned {} job logs older than {} days", deleted, days);
        } catch (Exception e) {
            logger.error("Error cleaning old job logs", e);
        }
    }

    public List<JobLog> findByStatus(String status) {
        try {
            String sql = "SELECT * FROM " + table() + " WHERE status = ? ORDER BY start_time DESC";
            return jdbcTemplate.query(sql, rowMapper, status);
        } catch (Exception e) {
            logger.error("Error finding job logs by status: {}", status, e);
            return Collections.emptyList();
        }
    }

    public List<JobLog> findByJobIdAndStatus(String jobId, String status) {
        try {
            String sql = "SELECT * FROM " + table() + " WHERE job_id = ? AND status = ? ORDER BY start_time DESC";
            return jdbcTemplate.query(sql, rowMapper, jobId, status);
        } catch (Exception e) {
            logger.error("Error finding job logs by jobId and status: {}/{}", jobId, status, e);
            return Collections.emptyList();
        }
    }

    public long countByJobId(String jobId) {
        try {
            String sql = "SELECT COUNT(*) FROM " + table() + " WHERE job_id = ?";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, jobId);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error counting job logs by jobId: {}", jobId, e);
            return 0;
        }
    }

    public long countSuccessByJobId(String jobId) {
        try {
            String sql = "SELECT COUNT(*) FROM " + table() + " WHERE job_id = ? AND status = 'SUCCESS'";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, jobId);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error counting success job logs by jobId: {}", jobId, e);
            return 0;
        }
    }

    public long countFailureByJobId(String jobId) {
        try {
            String sql = "SELECT COUNT(*) FROM " + table() + " WHERE job_id = ? AND status = 'FAILURE'";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, jobId);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error counting failure job logs by jobId: {}", jobId, e);
            return 0;
        }
    }
}
