package org.ssssssss.magicapi.file.service;

import org.ssssssss.magicapi.file.model.SysFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件元数据 Service。
 */
public interface SysFileService {

    SysFile getByPath(String filePath);

    SysFile findByPath(String storageKey, String filePath);

    SysFile getByMd5(String md5);

    List<SysFile> listByParentPath(String parentPath);

    List<SysFile> listChildren(String storageKey, String parentId);

    boolean existsByPath(String filePath);

    boolean existsByPath(String storageKey, String filePath);

    SysFile createDirectory(String storageKey, String parentPath, String dirName, String createBy);

    SysFile createDirectoryByParentId(String storageKey, String parentId, String dirName, String createBy);

    SysFile saveFileRecord(String storageKey, String filePath, String fileName,
                           Long fileSize, String contentType, String url,
                           String md5, String createBy);

    boolean deleteByPath(String filePath, String updateBy);

    boolean deleteByPath(String storageKey, String filePath, String updateBy);

    boolean softDeleteSubtree(String storageKey, String nodeId, String updateBy);

    SysFile rename(String storageKey, String filePath, String newName, String updateBy);

    SysFile move(String storageKey, String sourcePath, String targetParentId, String updateBy);

    List<SysFile> listPendingPhysicalDelete(LocalDateTime cutoffTime, int limit);

    boolean markPhysicalDeleteDone(String id);

    boolean updateById(SysFile sysFile);

    SysFile getById(String id);

    boolean save(SysFile sysFile);
}
