package org.ssssssss.magicapi.liteflow.service;

import org.ssssssss.magicapi.core.service.AbstractPathMagicResourceStorage;
import org.ssssssss.magicapi.liteflow.model.FlowComponentInfo;
import org.ssssssss.magicapi.liteflow.model.FlowInfo;

public class LiteflowComponentMagicResourceStorage extends AbstractPathMagicResourceStorage<FlowComponentInfo> {

    @Override
    public String folder() {
        return "liteflow-component";
    }

    @Override
    public Class<FlowComponentInfo> magicClass() {
		return FlowComponentInfo.class;
    }

    @Override
    public void validate(FlowComponentInfo entity) {
    }

    @Override
    public String buildMappingKey(FlowComponentInfo info) {
        return buildMappingKey(info, magicResourceService.getGroupPath(info.getGroupId()));
    }

}
