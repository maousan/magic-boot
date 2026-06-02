package org.ssssssss.magicapi.job.entity;

import java.time.LocalDateTime;

public class JobLog {

    private Long id;
    private String jobId;
    private String jobName;
    private String jobGroup;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private String status;
    private String result;
    private String exceptionMessage;
    private String exceptionStack;
    private String triggerType;
    private String triggeredBy;
    private LocalDateTime createTime;

    public JobLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getJobGroup() { return jobGroup; }
    public void setJobGroup(String jobGroup) { this.jobGroup = jobGroup; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getExceptionMessage() { return exceptionMessage; }
    public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }

    public String getExceptionStack() { return exceptionStack; }
    public void setExceptionStack(String exceptionStack) { this.exceptionStack = exceptionStack; }

    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }

    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
