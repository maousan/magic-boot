package org.ssssssss.magicboot.plugin.api.scheduler;

import java.util.Map;

/**
 * 定时任务执行上下文
 */
public class JobContext {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private Map<String, Object> jobData;
    private long fireTime;
    private long scheduledFireTime;
    private String pluginId;

    public JobContext() {
        this.fireTime = System.currentTimeMillis();
    }

    // Getters and Setters

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public Map<String, Object> getJobData() {
        return jobData;
    }

    public void setJobData(Map<String, Object> jobData) {
        this.jobData = jobData;
    }

    public long getFireTime() {
        return fireTime;
    }

    public void setFireTime(long fireTime) {
        this.fireTime = fireTime;
    }

    public long getScheduledFireTime() {
        return scheduledFireTime;
    }

    public void setScheduledFireTime(long scheduledFireTime) {
        this.scheduledFireTime = scheduledFireTime;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
}
