package org.ssssssss.magicapi.liteflow.starter;

import cn.hutool.extra.spring.SpringUtil;
import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.common.entity.ValidationResp;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.enums.ScriptTypeEnum;
import com.yomahub.liteflow.meta.LiteflowMetaOperator;
import com.yomahub.liteflow.script.validator.ScriptValidator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.ssssssss.magicapi.core.config.MagicPluginConfiguration;
import org.ssssssss.magicapi.core.logging.MagicLoggerContext;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.core.model.MagicEntity;
import org.ssssssss.magicapi.core.model.Plugin;
import org.ssssssss.magicapi.core.service.MagicResourceService;
import org.ssssssss.magicapi.core.web.MagicControllerRegister;
import org.ssssssss.magicapi.liteflow.LiteflowModule;
import org.ssssssss.magicapi.liteflow.model.FlowComponentInfo;
import org.ssssssss.magicapi.liteflow.model.FlowInfo;
import org.ssssssss.magicapi.liteflow.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ssssssss.magicapi.liteflow.web.MagicLiteflowController;

import java.util.List;

@Configuration
public class MagicAPILiteflowConfiguration implements MagicPluginConfiguration {

    private final Logger logger = LoggerFactory.getLogger(MagicAPILiteflowConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public LiteflowChainInfoMagicResourceStorage flowInfoMagicResourceStorage() {
        return new LiteflowChainInfoMagicResourceStorage();
    }

    @Bean
    @ConditionalOnMissingBean
    public LiteflowChainMagicDynamicRegistry flowMagicDynamicRegistry(LiteflowChainInfoMagicResourceStorage liteflowInfoMagicResourceStorage) {
        return new LiteflowChainMagicDynamicRegistry(liteflowInfoMagicResourceStorage);
    }

    @Bean
    @ConditionalOnMissingBean
    public LiteflowComponentMagicResourceStorage flowComponentMagicResourceStorage() {
        return new LiteflowComponentMagicResourceStorage();
    }

    @Bean
    @ConditionalOnMissingBean
    public LiteflowComponentMagicDynamicRegistry flowComponentMagicDynamicRegistry(LiteflowComponentMagicResourceStorage liteflowComponentMagicResourceStorage) {
        return new LiteflowComponentMagicDynamicRegistry(liteflowComponentMagicResourceStorage);
    }

    @Override
    public MagicControllerRegister controllerRegister() {
        return (mapping, configuration) -> {
            mapping.registerController(new MagicLiteflowController(configuration));
        };
    }

    @Override
    public Plugin plugin() {
        return new Plugin("Liteflow组件编排", "MagicLiteflow", "magic-liteflow.1.0.0.iife.js");
    }

    @Bean
    @ConditionalOnMissingBean
    public LiteflowModule magicLiteflowModule(FlowExecutor flowExecutor) {
        return new LiteflowModule(flowExecutor);
    }

    @Bean
    public CmpAspect cmpAspect() {
        return new CmpAspect();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        MagicResourceService magicResourceService = SpringUtil.getBean(MagicResourceService.class);
//        List<MagicEntity> componentResources = magicResourceService.files("liteflow-component");
//        componentResources.forEach(entity -> {
//            FlowComponentInfo componentInfo = (FlowComponentInfo) entity;
//            ScriptTypeEnum scriptTypeEnum = ScriptTypeEnum.getEnumByDisplayName(componentInfo.getScriptType());
//            ValidationResp resp = ScriptValidator.validateWithEx(componentInfo.getScript(), scriptTypeEnum);
//            boolean isSuccess = resp.isSuccess();
//            Exception e = resp.getCause();
//            if (!isSuccess) {
//                logger.error("脚本验证失败: {}", ExceptionUtils.getStackTrace(e));
//                return;
//            }
//            logger.info("脚本验证通过: {}[{}]", componentInfo.getNodeId(), componentInfo.getScriptType());
//            LiteFlowNodeBuilder.createScriptNode().setId(componentInfo.getNodeId())
//                    .setName(componentInfo.getName())
//                    .setScript(componentInfo.getScript())
//                    .build();
//            logger.info("创建脚本节点: {}[{}]", componentInfo.getNodeId(), componentInfo.getScriptType());
//        });
        List<MagicEntity> chainResources = magicResourceService.files("liteflow-chain");
        chainResources.forEach(entity -> {
            FlowInfo flowInfo = (FlowInfo) entity;
            var validResult = LiteFlowChainELBuilder.validateWithEx(flowInfo.getScript());
            boolean success = validResult.isSuccess();
            if (success) {
                LiteFlowChainELBuilder.createChain()
                        .setChainId(flowInfo.getChainId())
                        .setNamespace(flowInfo.getNamespace())
                        .setRoute(flowInfo.getRoute())
                        .setEL(flowInfo.getScript())
                        .build();
                logger.info("加载liteflow chain[{}]成功", flowInfo.getChainId());
            } else {
                logger.error("加载liteflow chain[{}]失败: {}", flowInfo.getChainId(), ExceptionUtils.getStackTrace(validResult.getCause()));
            }
        });
    }

}