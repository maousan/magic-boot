package org.ssssssss.magicapi.file.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 文件操作事件发布器。
 */
@Slf4j
@RequiredArgsConstructor
public class FileEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishUploadEvent(String storageKey, String filePath, String fileName,
                                   Long fileSize, String contentType, String url,
                                   String md5, String operator) {
        log.debug("发布文件上传事件: storageKey={}, path={}, fileName={}", storageKey, filePath, fileName);
        eventPublisher.publishEvent(FileOperationEvent.upload(
                this, storageKey, filePath, fileName, fileSize, contentType, url, md5, operator
        ));
    }

    public void publishMkdirEvent(String storageKey, String filePath, String dirName, String operator) {
        log.debug("发布创建目录事件: storageKey={}, path={}, dirName={}", storageKey, filePath, dirName);
        eventPublisher.publishEvent(FileOperationEvent.mkdir(
                this, storageKey, filePath, dirName, operator
        ));
    }

    public void publishDeleteEvent(String storageKey, String filePath, String operator) {
        log.debug("发布删除文件事件: storageKey={}, path={}", storageKey, filePath);
        eventPublisher.publishEvent(FileOperationEvent.delete(
                this, storageKey, filePath, operator
        ));
    }

    public void publishDeleteEvent(String filePath, String operator) {
        publishDeleteEvent(null, filePath, operator);
    }
}
