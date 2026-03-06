package org.ssssssss.magicapi.liteflow.service;

import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import com.yomahub.liteflow.meta.LiteflowMetaOperator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.ssssssss.magicapi.core.event.EventAction;
import org.ssssssss.magicapi.core.event.FileEvent;
import org.ssssssss.magicapi.core.event.GroupEvent;
import org.ssssssss.magicapi.core.logging.MagicLoggerContext;
import org.ssssssss.magicapi.core.service.AbstractMagicDynamicRegistry;
import org.ssssssss.magicapi.core.service.MagicResourceStorage;
import org.ssssssss.magicapi.liteflow.model.FlowComponentInfo;
import org.ssssssss.magicapi.liteflow.model.FlowInfo;
import org.ssssssss.magicapi.utils.JsonUtils;

public class LiteflowComponentMagicDynamicRegistry extends AbstractMagicDynamicRegistry<FlowComponentInfo> {

    private static final Logger logger = LoggerFactory.getLogger(LiteflowComponentMagicDynamicRegistry.class);

    public LiteflowComponentMagicDynamicRegistry(MagicResourceStorage<FlowComponentInfo> magicResourceStorage) {
        super(magicResourceStorage);
    }

    @EventListener(condition = "#event.type == 'liteflow-component'")
    public void onFileEvent(FileEvent event) {
        logger.debug("文件事件: {}", JsonUtils.toJsonString(event));
        FlowComponentInfo componentInfo = (FlowComponentInfo) event.getEntity();
        if (event.getAction().equals(EventAction.LOAD)) {
            logger.info("加载Liteflow组件文件: {}", componentInfo.getNodeId());
        }
        if (event.getAction().equals(EventAction.SAVE) || event.getAction().equals(EventAction.LOAD)) {
            LiteFlowNodeBuilder.createScriptNode().setId(componentInfo.getNodeId())
                    .setName(componentInfo.getName())
                    .setScript(componentInfo.getScript())
                    .setType(NodeTypeEnum.SCRIPT)
                    .build();
            logger.info("创建脚本节点: {}[{}]", componentInfo.getNodeId(), componentInfo.getScriptType());
        } else if (event.getAction().equals(EventAction.DELETE)) {
            logger.info("删除脚本节点: {}", componentInfo.getNodeId());
        }
        processEvent(event);
    }

    @EventListener(condition = "#event.type == 'liteflow-component'")
    public void onGroupEvent(GroupEvent event) {
        processEvent(event);
    }
}