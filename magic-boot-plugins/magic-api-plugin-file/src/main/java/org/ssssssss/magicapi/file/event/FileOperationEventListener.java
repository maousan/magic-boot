package org.ssssssss.magicapi.file.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.file.model.SysFile;
import org.ssssssss.magicapi.file.service.SysFileService;

/**
 * 文件操作事件监听器
 * 监听文件操作事件，异步处理数据库落库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileOperationEventListener {

    private final SysFileService sysFileService;

    /**
     * 处理文件上传事件
     */
    @Async
    @EventListener
    public void handleUpload(FileOperationEvent event) {
        if (event.getOperationType() != FileOperationEvent.OperationType.UPLOAD) {
            return;
        }

        log.info("处理文件上传事件: path={}, fileName={}", event.getFilePath(), event.getFileName());

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
            log.info("文件记录保存成功: path={}", event.getFilePath());

        } catch (Exception e) {
            log.error("保存文件记录失败: path={}", event.getFilePath(), e);
        }
    }

    /**
     * 处理创建目录事件
     */
    @Async
    @EventListener
    public void handleMkdir(FileOperationEvent event) {
        if (event.getOperationType() != FileOperationEvent.OperationType.MKDIR) {
            return;
        }

        log.info("处理创建目录事件: path={}, dirName={}", event.getFilePath(), event.getFileName());

        try {
            sysFileService.createDirectory(
                    event.getStorageKey(),
                    event.getFilePath(),
                    event.getFileName(),
                    event.getOperator()
            );
            log.info("目录记录保存成功: path={}", event.getFilePath());

        } catch (Exception e) {
            log.error("保存目录记录失败: path={}", event.getFilePath(), e);
        }
    }

    /**
     * 处理删除文件事件
     */
    @Async
    @EventListener
    public void handleDelete(FileOperationEvent event) {
        if (event.getOperationType() != FileOperationEvent.OperationType.DELETE) {
            return;
        }

        log.info("处理删除文件事件: path={}", event.getFilePath());

        try {
            sysFileService.deleteByPath(event.getFilePath(), event.getOperator());
            log.info("文件记录删除成功: path={}", event.getFilePath());

        } catch (Exception e) {
            log.error("删除文件记录失败: path={}", event.getFilePath(), e);
        }
    }
}
