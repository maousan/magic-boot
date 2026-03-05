package org.ssssssss.magicapi.job.repository;

import org.ssssssss.magicapi.job.entity.JobLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JobLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public JobLogRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(JobLog log) {
        String sql = "INSERT INTO magic_job_log (" +
                   "job_id, job_name, job_group, script_path, " +
                   "start_time, end_time, duration, status, " +
                   "result, exception_message, exception_stack, " +
                   "trigger_type, triggered_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            log.getJobId(), log.getJobName(), log.getJobGroup(), log.getScriptPath(),
            log.getStartTime() != null ? Timestamp.valueOf(log.getStartTime()) : null,
            log.getEndTime() != null ? Timestamp.valueOf(log.getEndTime()) : null,
            log.getDuration(), log.getStatus(), log.getResult(),
            log.getExceptionMessage(), log.getExceptionStack(),
            log.getTriggerType(), log.getTriggeredBy());
    }

    public List<JobLog> findByJobId(String jobId, int page, int size) {
        String sql = "SELECT * FROM magic_job_log WHERE job_id = ? ORDER BY start_time DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return jdbcTemplate.query(sql, jobLogRowMapper(), jobId, size, offset);
    }

    public void cleanOldLogs(int days) {
        String sql = "DELETE FROM magic_job_log WHERE start_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
        jdbcTemplate.update(sql, days);
    }

    private RowMapper<JobLog> jobLogRowMapper() {
        return new RowMapper<JobLog>() {
            @Override
            public JobLog mapRow(ResultSet rs, int rowNum) throws SQLException {
                JobLog log = new JobLog();
                log.setId(rs.getLong("id"));
                log.setJobId(rs.getString("job_id"));
                log.setJobName(rs.getString("job_name"));
                log.setJobGroup(rs.getString("job_group"));
                log.setScriptPath(rs.getString("script_path"));
                
                Timestamp startTime = rs.getTimestamp("start_time");
                if (startTime != null) log.setStartTime(startTime.toLocalDateTime());
                
                Timestamp endTime = rs.getTimestamp("end_time");
                if (endTime != null) log.setEndTime(endTime.toLocalDateTime());
                
                log.setDuration(rs.getLong("duration"));
                log.setStatus(rs.getString("status"));
                log.setResult(rs.getString("result"));
                log.setExceptionMessage(rs.getString("exception_message"));
                log.setExceptionStack(rs.getString("exception_stack"));
                log.setTriggerType(rs.getString("trigger_type"));
                log.setTriggeredBy(rs.getString("triggered_by"));

                Timestamp createTime = rs.getTimestamp("create_time");
                if (createTime != null) log.setCreateTime(createTime.toLocalDateTime());

                return log;
            }
        };
    }
}