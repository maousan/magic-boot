package org.ssssssss.magicapi.job.model;

import org.ssssssss.magicapi.core.model.MagicEntity;
import org.ssssssss.magicapi.core.model.PathMagicEntity;

import java.util.Objects;

public class JobInfo extends PathMagicEntity {

	/**
	 *  cron 表达式
	 */
	private String cron;

	/**
	 * 是否启用
	 */
	private boolean enabled;


	/**
	 * 定时任务描述
	 */
	private String description;

	/**
	 * 定时任务参数
	 */
	private String params;

    // 新增字段用于 Quartz 高级功能
    /**
     * 错过触发策略
     */
    private MisfirePolicy misfirePolicy = MisfirePolicy.SMART;

    /**
     * 是否允许并发执行
     */
    private boolean concurrent = false;

    /**
     * 依赖的任务ID
     */
    private String dependsOn;

    /**
     * 最大重试次数
     */
    private int maxRetry = 0;

    /**
     * 超时时间(秒)
     */
    private long timeout = 0;

	private String jobType;

	private String clazz;

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


    public enum MisfirePolicy {
        SMART("智能处理：立即执行一次"),
        IGNORE("忽略：不做任何处理"),
        FIRE_ONCE_NOW("立即执行一次");

        private final String description;

        MisfirePolicy(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public MisfirePolicy getMisfirePolicy() {
        return misfirePolicy;
    }

    public void setMisfirePolicy(MisfirePolicy misfirePolicy) {
        this.misfirePolicy = misfirePolicy;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public String getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

	public JobInfo copy() {
		JobInfo info = new JobInfo();
		super.copyTo(info);
		info.setCron(this.cron);
		info.setEnabled(this.enabled);
		info.setDescription(this.description);
		info.setMisfirePolicy(this.misfirePolicy);
		info.setConcurrent(this.concurrent);
		info.setDependsOn(this.dependsOn);
		info.setMaxRetry(this.maxRetry);
		info.setTimeout(this.timeout);
		info.setJobType(this.jobType);
		info.setClazz(this.clazz);
		info.setParams(this.getParams());
		return info;
	}

	@Override
	public MagicEntity simple() {
		JobInfo info = new JobInfo();
		super.simple(info);
		return info;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		JobInfo taskInfo = (JobInfo) o;
		return Objects.equals(id, taskInfo.id) &&
				Objects.equals(path, taskInfo.path) &&
				Objects.equals(script, taskInfo.script) &&
				Objects.equals(name, taskInfo.name) &&
				Objects.equals(cron, taskInfo.cron) &&
				Objects.equals(description, taskInfo.description) &&
				Objects.equals(enabled, taskInfo.enabled) &&
				Objects.equals(misfirePolicy, taskInfo.misfirePolicy) &&
				Objects.equals(concurrent, taskInfo.concurrent) &&
				Objects.equals(dependsOn, taskInfo.dependsOn) &&
				Objects.equals(maxRetry, taskInfo.maxRetry) &&
				Objects.equals(timeout, taskInfo.timeout);

	}

	@Override
	public int hashCode() {
		return Objects.hash(id, path, script, name, groupId, cron, enabled, description, misfirePolicy, concurrent, dependsOn, maxRetry, timeout);
	}
}
