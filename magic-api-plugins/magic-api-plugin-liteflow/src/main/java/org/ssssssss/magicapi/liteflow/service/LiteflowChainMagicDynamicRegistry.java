package org.ssssssss.magicapi.liteflow.service;

import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.element.Chain;
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
import org.ssssssss.magicapi.liteflow.model.FlowInfo;
import org.ssssssss.magicapi.utils.JsonUtils;

public class LiteflowChainMagicDynamicRegistry extends AbstractMagicDynamicRegistry<FlowInfo> {

    private static final Logger logger = LoggerFactory.getLogger(LiteflowChainMagicDynamicRegistry.class);

    public LiteflowChainMagicDynamicRegistry(MagicResourceStorage<FlowInfo> magicResourceStorage) {
        super(magicResourceStorage);
    }

    @EventListener(condition = "#event.type == 'liteflow-chain'")
    public void onFileEvent(FileEvent event) {
        logger.debug("文件事件: {}", JsonUtils.toJsonString(event));
        FlowInfo flowInfo = (FlowInfo) event.getEntity();
        if (event.getAction().equals(EventAction.SAVE)) {
            var validResult = LiteFlowChainELBuilder.validateWithEx(flowInfo.getScript());
            boolean success = validResult.isSuccess();
            if(success){
                Chain chain = LiteflowMetaOperator.getChain(flowInfo.getChainId());
                if (chain != null) {
                    LiteflowMetaOperator.reloadOneChain(flowInfo.getChainId(), flowInfo.getScript());
                } else {
                    LiteFlowChainELBuilder.createChain()
                            .setChainId(flowInfo.getChainId())
                            .setEL(flowInfo.getScript()).build();
                }
            } else {
                logger.error("[{}]EL表达式不正确: {}", flowInfo.getChainId(), ExceptionUtils.getStackTrace(validResult.getCause()));
            }
        } else if (event.getAction().equals(EventAction.DELETE)) {
            LiteflowMetaOperator.removeChain(flowInfo.getChainId());
        }
        processEvent(event);
    }

    @EventListener(condition = "#event.type == 'liteflow-chain'")
    public void onGroupEvent(GroupEvent event) {
        processEvent(event);
    }
}