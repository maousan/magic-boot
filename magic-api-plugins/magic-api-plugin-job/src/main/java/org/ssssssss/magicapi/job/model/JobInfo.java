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

	public JobInfo copy() {
		JobInfo info = new JobInfo();
		super.copyTo(info);
		info.setCron(this.cron);
		info.setEnabled(this.enabled);
		info.setDescription(this.description);
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
				Objects.equals(enabled, taskInfo.enabled);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, path, script, name, groupId, cron, enabled, description);
	}
}
