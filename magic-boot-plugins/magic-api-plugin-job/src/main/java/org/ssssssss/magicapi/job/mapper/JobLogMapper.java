package org.ssssssss.magicapi.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ssssssss.magicapi.job.entity.JobLog;

import java.time.LocalDateTime;

@Mapper
public interface JobLogMapper extends BaseMapper<JobLog> {

    /**
     * 默认分页
     */
    default IPage<JobLog> selectByJobId(@Param("jobId") String jobId) {
        IPage<JobLog> page = new Page<>(1, 10);
        return selectByJobId(page, jobId);
    }

    /**
     * 根据任务ID分页查询日志
     */
    @Select("SELECT * FROM magic_job_log WHERE job_id = #{jobId} ORDER BY start_time DESC")
    IPage<JobLog> selectByJobId(IPage<JobLog> page, @Param("jobId") String jobId);

    /**
     * 清理指定天数之前的日志
     */
    @Delete("DELETE FROM magic_job_log WHERE start_time < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int cleanOldLogs(@Param("days") int days);

    /**
     * 根据时间范围清理日志
     */
    @Delete("DELETE FROM magic_job_log WHERE start_time < #{beforeTime}")
    int cleanLogsBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
}
