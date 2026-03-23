package org.ssssssss.magicapi.file.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 文件操作事件发布器
 */
@Slf4j
@RequiredArgsConstructor
public class FileEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 发布文件上传事件
     */
    public void publishUploadEvent(String storageKey, String filePath, String fileName,
                                    Long fileSize, String contentType, String url,
                                    String md5, String operator) {
        log.debug("发布文件上传事件: path={}, fileName={}", filePath, fileName);
        FileOperationEvent event = FileOperationEvent.upload(
                this, storageKey, filePath, fileName, fileSize, contentType, url, md5, operator
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * 发布创建目录事件
     */
    public void publishMkdirEvent(String storageKey, String filePath, String dirName, String operator) {
        log.debug("发布创建目录事件: path={}, dirName={}", filePath, dirName);
        FileOperationEvent event = FileOperationEvent.mkdir(
                this, storageKey, filePath, dirName, operator
        );
        eventPublisher.publishEvent(event);
    }

    /**
     * 发布删除文件事件
     */
    public void publishDeleteEvent(String filePath, String operator) {
        log.debug("发布删除文件事件: path={}", filePath);
        FileOperationEvent event = FileOperationEvent.delete(
                this, filePath, operator
        );
        eventPublisher.publishEvent(event);
    }
}
