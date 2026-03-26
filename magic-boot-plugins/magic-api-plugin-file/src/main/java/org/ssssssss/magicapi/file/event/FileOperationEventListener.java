package org.ssssssss.magicapi.file.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.file.service.SysFileService;

/**
 * 文件操作事件监听器。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileOperationEventListener {

    private final SysFileService sysFileService;

    @Async
    @EventListener
    public void handleUpload(FileOperationEvent event) {
        if (event.getOperationType() != FileOperationEvent.OperationType.UPLOAD) {
            return;
        }

        try {
            sysFileService.saveFileRecord(
                    event.getStorageKey(),
                    event.getFilePath(),
                    event.getFileName(),
                    event.getFileSize(),
                    event.getContentType(),
                    event.getUrl(),
                    event.getMd5(),
                    event.getOperator()
            );
        } catch (Exception e) {
            log.error("保存文件记录失败: storageKey={}, path={}", event.getStorageKey(), event.getFilePath(), e);
        }
    }

    @Async
    @EventListener
    public void handleMkdir(FileOperationEvent event) {
        if (event.getOperationType() != FileOperationEvent.OperationType.MKDIR) {
            return;
        }

        try {
            String fullPath = event.getFilePath();
            String dirName = event.getFileName();
            String parentPath = "/";
            if (fullPath != null && fullPath.endsWith("/")) {
                String trimmed = fullPath.substring(0, fullPath.length() - 1);
                int slash = trimmed.lastIndexOf('/');
                if (slash >= 0) {
                    parentPath = slash == 0 ? "/" : trimmed.substring(0, slash + 1);
                }
            }
            sysFileService.createDirectory(event.getStorageKey(), parentPath, dirName, event.getOperator());
        } catch (Exception e) {
            log.error("保存目录记录失败: storageKey={}, path={}", event.getStorageKey(), event.getFilePath(), e);
        }
    }

    @Async
    @EventListener
    public void handleDelete(FileOperationEvent event) {
        if (event.getOperationType() != FileOperationEvent.OperationType.DELETE) {
            return;
        }

        try {
            sysFileService.deleteByPath(event.getStorageKey(), event.getFilePath(), event.getOperator());
        } catch (Exception e) {
            log.error("删除文件记录失败: storageKey={}, path={}", event.getStorageKey(), event.getFilePath(), e);
        }
    }
}
