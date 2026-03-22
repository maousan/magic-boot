package org.ssssssss.magicapi.file.service;

import org.dromara.x.file.storage.core.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.core.event.FileEvent;
import org.ssssssss.magicapi.core.service.AbstractMagicDynamicRegistry;
import org.ssssssss.magicapi.core.service.MagicResourceStorage;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 动态注册文件存储配置
 */
@Component("fileMagicDynamicRegistry")
public class FileMagicDynamicRegistry extends AbstractMagicDynamicRegistry<StorageInfo> {

    private static final Logger logger = LoggerFactory.getLogger(FileMagicDynamicRegistry.class);

    private final MagicDynamicFileClient magicDynamicFileClient;

    public FileMagicDynamicRegistry(MagicResourceStorage<StorageInfo> magicResourceStorage,
                                      MagicDynamicFileClient magicDynamicFileClient) {
        super(magicResourceStorage);
        this.magicDynamicFileClient = magicDynamicFileClient;
    }

    @EventListener(condition = "#event.type == 'file'")
    public void onFileEvent(FileEvent event) {
        try {
            processEvent(event);
        } catch (Exception e) {
            logger.error("注册文件存储配置失败", e);
        }
    }

    @Override
    protected boolean register(MappingNode<StorageInfo> mappingNode) {
        StorageInfo info = mappingNode.getEntity();
        String id = info.getId();
        String key = info.getKey() != null ? info.getKey() : "";
        String name = info.getName();

        logger.info("注册文件存储配置: {} - {}", StringUtils.hasText(key) ? key : "default", name);

        FileStorageService fileStorageService = magicDynamicFileClient.createFileStorageService(info);
        if (fileStorageService == null) {
            return false;
        }
        magicDynamicFileClient.put(id, key, name, fileStorageService);
        return true;
    }

    @Override
    protected void unregister(MappingNode<StorageInfo> mappingNode) {
        String key = mappingNode.getEntity().getKey();
        logger.info("注销文件存储配置: {}", key);
        magicDynamicFileClient.delete(key);
    }
}
