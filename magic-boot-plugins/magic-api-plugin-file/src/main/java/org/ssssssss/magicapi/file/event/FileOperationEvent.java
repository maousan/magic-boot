package org.ssssssss.magicapi.file.event;

import org.springframework.context.ApplicationEvent;

/**
 * 文件操作事件。
 */
public class FileOperationEvent extends ApplicationEvent {

    public enum OperationType {
        UPLOAD,
        MKDIR,
        DELETE,
        COPY,
        MOVE
    }

    private final OperationType operationType;
    private final String storageKey;
    private final String filePath;
    private final String fileName;
    private final Long fileSize;
    private final String contentType;
    private final String url;
    private final String md5;
    private final String operator;

    public FileOperationEvent(Object source, OperationType operationType, String storageKey,
                              String filePath, String fileName, Long fileSize,
                              String contentType, String url, String md5, String operator) {
        super(source);
        this.operationType = operationType;
        this.storageKey = storageKey;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.url = url;
        this.md5 = md5;
        this.operator = operator;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }

    public String getOperator() {
        return operator;
    }

    public static FileOperationEvent upload(Object source, String storageKey, String filePath,
                                            String fileName, Long fileSize, String contentType,
                                            String url, String md5, String operator) {
        return new FileOperationEvent(source, OperationType.UPLOAD, storageKey, filePath,
                fileName, fileSize, contentType, url, md5, operator);
    }

    public static FileOperationEvent mkdir(Object source, String storageKey, String filePath,
                                           String dirName, String operator) {
        return new FileOperationEvent(source, OperationType.MKDIR, storageKey, filePath,
                dirName, 0L, null, null, null, operator);
    }

    public static FileOperationEvent delete(Object source, String storageKey, String filePath, String operator) {
        return new FileOperationEvent(source, OperationType.DELETE, storageKey, filePath,
                null, null, null, null, null, operator);
    }

    public static FileOperationEvent delete(Object source, String filePath, String operator) {
        return delete(source, null, filePath, operator);
    }
}
