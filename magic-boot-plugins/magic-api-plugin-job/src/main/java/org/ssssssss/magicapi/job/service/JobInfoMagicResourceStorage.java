package org.ssssssss.magicapi.job.service;

import org.ssssssss.magicapi.core.service.AbstractPathMagicResourceStorage;
import org.ssssssss.magicapi.job.model.JobInfo;

public class JobInfoMagicResourceStorage extends AbstractPathMagicResourceStorage<JobInfo> {

	@Override
	public String folder() {
		return "job";
	}

	@Override
	public Class<JobInfo> magicClass() {
		return JobInfo.class;
	}

	@Override
	public void validate(JobInfo entity) {
		notBlank(entity.getCron(), CRON_ID_REQUIRED);
	}

	@Override
	public String buildMappingKey(JobInfo info) {
		return buildMappingKey(info, magicResourceService.getGroupPath(info.getGroupId()));
	}
}
