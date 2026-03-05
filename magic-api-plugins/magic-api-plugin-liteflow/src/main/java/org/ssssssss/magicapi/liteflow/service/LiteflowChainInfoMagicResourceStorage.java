package org.ssssssss.magicapi.liteflow.service;

import org.ssssssss.magicapi.core.service.AbstractPathMagicResourceStorage;
import org.ssssssss.magicapi.liteflow.model.FlowInfo;

public class LiteflowChainInfoMagicResourceStorage extends AbstractPathMagicResourceStorage<FlowInfo> {

	@Override
	public String folder() {
		return "liteflow-chain";
	}

	@Override
	public Class<FlowInfo> magicClass() {
		return FlowInfo.class;
	}

	@Override
	public void validate(FlowInfo entity) {
	}

	@Override
	public String buildMappingKey(FlowInfo info) {
		return buildMappingKey(info, magicResourceService.getGroupPath(info.getGroupId()));
	}

}
