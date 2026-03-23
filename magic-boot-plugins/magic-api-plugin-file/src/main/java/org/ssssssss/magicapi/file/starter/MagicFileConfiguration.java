package org.ssssssss.magicapi.file.starter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.ssssssss.magicapi.core.config.MagicPluginConfiguration;
import org.ssssssss.magicapi.core.model.Plugin;
import org.ssssssss.magicapi.core.web.MagicControllerRegister;
import org.ssssssss.magicapi.file.FileModule;
import org.ssssssss.magicapi.file.event.FileEventPublisher;
import org.ssssssss.magicapi.file.event.FileOperationEventListener;
import org.ssssssss.magicapi.file.mapper.SysFileMapper;
import org.ssssssss.magicapi.file.service.FileMagicDynamicRegistry;
import org.ssssssss.magicapi.file.service.FileMagicResourceStorage;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;
import org.ssssssss.magicapi.file.service.SysFileService;
import org.ssssssss.magicapi.file.service.impl.SysFileServiceImpl;
import org.ssssssss.magicapi.file.web.MagicFileController;

/**
 * 文件存储插件配置
 */
@Configuration
@EnableAsync
public class MagicFileConfiguration implements MagicPluginConfiguration {

    private MagicDynamicFileClient magicDynamicFileClient;

    @Override
    public Plugin plugin() {
        return new Plugin("文件存储", "file", "magic-file.1.0.0.iife.js");
    }

    @Override
    public MagicControllerRegister controllerRegister() {
        return (mapping, configuration) -> {
            if (magicDynamicFileClient != null) {
                mapping.registerController(new MagicFileController(configuration, magicDynamicFileClient));
            }
        };
    }

    @Bean(name = "fileMagicResourceStorage")
    @ConditionalOnMissingBean
    public FileMagicResourceStorage fileMagicResourceStorage() {
        return new FileMagicResourceStorage();
    }

    @Bean(name = "magicDynamicFileClient")
    @ConditionalOnMissingBean
    public MagicDynamicFileClient magicDynamicFileClient() {
        this.magicDynamicFileClient = new MagicDynamicFileClient();
        return this.magicDynamicFileClient;
    }

    @Bean(name = "fileMagicDynamicRegistry")
    @ConditionalOnMissingBean
    public FileMagicDynamicRegistry fileMagicDynamicRegistry(
            @Qualifier("fileMagicResourceStorage") FileMagicResourceStorage fileMagicResourceStorage,
            @Qualifier("magicDynamicFileClient") MagicDynamicFileClient magicDynamicFileClient) {
        return new FileMagicDynamicRegistry(fileMagicResourceStorage, magicDynamicFileClient);
    }

    @Bean(name = "fileEventPublisher")
    @ConditionalOnMissingBean
    public FileEventPublisher fileEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new FileEventPublisher(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public SysFileService sysFileService(SysFileMapper sysFileMapper) {
       return new SysFileServiceImpl(sysFileMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public FileOperationEventListener fileOperationEventListener(SysFileService sysFileService) {
        return new FileOperationEventListener(sysFileService);
    }

    @Bean(name = "magicFileModule")
    @ConditionalOnMissingBean
    public FileModule magicFileModule(
            @Qualifier("magicDynamicFileClient") MagicDynamicFileClient magicDynamicFileClient,
            @Qualifier("fileEventPublisher") FileEventPublisher fileEventPublisher) {
        FileModule fileModule = new FileModule(magicDynamicFileClient);
        fileModule.setEventPublisher(fileEventPublisher);
        return fileModule;
    }
}
