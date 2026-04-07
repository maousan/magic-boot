package org.ssssssss.magicapi.file.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.ssssssss.magicapi.file.mapper.SysFileMapper;
import org.ssssssss.magicapi.file.model.SysFile;
import org.ssssssss.magicapi.file.service.SysFileService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件元数据 Service 实现。
 */
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {
    @SuppressWarnings("unused")
    private final SysFileMapper sysFileMapper;

    @Override
    public SysFile getByPath(String filePath) {
        return getOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFilePath, filePath)
                .eq(SysFile::getIsDeleted, 0)
                .last("limit 1"));
    }

    @Override
    public SysFile findByPath(String storageKey, String filePath) {
        return getOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getStorageKey, storageKey)
                .eq(SysFile::getFilePath, filePath)
                .eq(SysFile::getIsDeleted, 0)
                .last("limit 1"));
    }

    @Override
    public List<SysFile> listByParentPath(String parentPath) {
        String normalized = normalizeDirPath(parentPath);
        SysFile parent = getByPath(normalized);
        if (!"/".equals(normalized) && parent == null) {
            return new ArrayList<>();
        }
        String parentId = parent == null ? null : parent.getId();
        return listChildren(null, parentId);
    }

    @Override
    public List<SysFile> listChildren(String storageKey, String parentId) {
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getIsDeleted, 0)
                .orderByDesc(SysFile::getFileType)
                .orderByAsc(SysFile::getFileName);
        if (StringUtils.hasText(storageKey)) {
            wrapper.eq(SysFile::getStorageKey, storageKey);
        }
        if (StringUtils.hasText(parentId)) {
            wrapper.eq(SysFile::getParentId, parentId);
        } else {
            wrapper.isNull(SysFile::getParentId);
        }
        return list(wrapper);
    }

    @Override
    public SysFile getByMd5(String md5) {
        if (!StringUtils.hasText(md5)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getMd5, md5)
                .eq(SysFile::getIsDeleted, 0)
                .last("limit 1"));
    }

    @Override
    public boolean existsByPath(String filePath) {
        return count(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFilePath, filePath)
                .eq(SysFile::getIsDeleted, 0)) > 0;
    }

    @Override
    public boolean existsByPath(String storageKey, String filePath) {
        return count(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getStorageKey, storageKey)
                .eq(SysFile::getFilePath, filePath)
                .eq(SysFile::getIsDeleted, 0)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile createDirectory(String storageKey, String parentPath, String dirName, String createBy) {
        String normalizedParentPath = normalizeDirPath(parentPath);
        String normalizedName = normalizeName(dirName);
        String fullPath = normalizedParentPath + normalizedName + "/";

        SysFile parent = "/".equals(normalizedParentPath)
                ? null
                : findByPath(storageKey, normalizedParentPath);

        return createDirectoryRecord(storageKey, parent == null ? null : parent.getId(), fullPath, normalizedName, createBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile createDirectoryByParentId(String storageKey, String parentId, String dirName, String createBy) {
        String normalizedName = normalizeName(dirName);
        String parentPath = "/";
        if (StringUtils.hasText(parentId)) {
            SysFile parent = getById(parentId);
            if (parent == null || !Objects.equals(parent.getIsDeleted(), 0)) {
                throw new IllegalArgumentException("父目录不存在");
            }
            parentPath = normalizeDirPath(parent.getFilePath());
        }
        String fullPath = parentPath + normalizedName + "/";
        return createDirectoryRecord(storageKey, parentId, fullPath, normalizedName, createBy);
    }

    @Override
    public SysFile saveFileRecord(String storageKey, String filePath, String fileName,
                                  Long fileSize, String contentType, String url,
                                  String md5, String createBy) {
        String normalizedPath = normalizeFilePath(filePath);
        String normalizedName = StringUtils.hasText(fileName) ? fileName : extractFileName(normalizedPath);
        String parentPath = extractParentPath(normalizedPath);
        String parentId = null;
        if (!"/".equals(parentPath)) {
            SysFile parent = findByPath(storageKey, parentPath);
            parentId = parent == null ? null : parent.getId();
        }

        SysFile existed = findByPath(storageKey, normalizedPath);
        if (existed != null) {
            existed.setFileName(normalizedName);
            existed.setFileSize(fileSize != null ? fileSize : 0L);
            existed.setContentType(contentType);
            existed.setUrl(url);
            existed.setMd5(md5);
            existed.setParentId(parentId);
            fillFileExt(existed, normalizedName);
            updateById(existed);
            return existed;
        }

        SysFile sysFile = new SysFile();
        sysFile.setId(IdUtil.fastSimpleUUID());
        sysFile.setStorageKey(storageKey);
        sysFile.setFilePath(normalizedPath);
        sysFile.setParentId(parentId);
        sysFile.setFileName(normalizedName);
        sysFile.setFileType(SysFile.TYPE_FILE);
        sysFile.setFileSize(fileSize != null ? fileSize : 0L);
        sysFile.setContentType(contentType);
        sysFile.setUrl(url);
        sysFile.setMd5(md5);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setCreateBy(createBy);
        sysFile.setIsDeleted(0);
        fillFileExt(sysFile, normalizedName);

        save(sysFile);
        return sysFile;
    }

    @Override
    public boolean deleteByPath(String filePath, String updateBy) {
        SysFile sysFile = getByPath(filePath);
        if (sysFile == null) {
            return false;
        }
        return deleteByPath(sysFile.getStorageKey(), filePath, updateBy);
    }

    @Override
    public boolean deleteByPath(String storageKey, String filePath, String updateBy) {
        return update(new LambdaUpdateWrapper<SysFile>()
                .eq(SysFile::getStorageKey, storageKey)
                .eq(SysFile::getFilePath, filePath)
                .eq(SysFile::getIsDeleted, 0)
                .set(SysFile::getIsDeleted, 1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean softDeleteSubtree(String storageKey, String nodeId, String updateBy) {
        SysFile node = getById(nodeId);
        if (node == null || !Objects.equals(node.getIsDeleted(), 0)) {
            return false;
        }
        String prefix = node.getFilePath();
        return update(new LambdaUpdateWrapper<SysFile>()
                .eq(SysFile::getStorageKey, storageKey)
                .eq(SysFile::getIsDeleted, 0)
                .likeRight(SysFile::getFilePath, prefix)
                .set(SysFile::getIsDeleted, 1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile rename(String storageKey, String filePath, String newName, String updateBy) {
        SysFile source = findByPath(storageKey, filePath);
        if (source == null) {
            throw new IllegalArgumentException("源节点不存在");
        }
        String normalizedName = normalizeName(newName);
        String parentPath = extractParentPath(source.getFilePath());
        String newPath = SysFile.TYPE_DIR.equals(source.getFileType())
                ? parentPath + normalizedName + "/"
                : parentPath + normalizedName;

        if (existsByPath(storageKey, newPath)) {
            throw new IllegalArgumentException("同级已存在同名节点");
        }

        String oldPath = source.getFilePath();
        source.setFileName(normalizedName);
        source.setFilePath(newPath);
        fillFileExt(source, normalizedName);
        updateById(source);

        if (SysFile.TYPE_DIR.equals(source.getFileType())) {
            rewriteDescendantPaths(storageKey, oldPath, newPath);
        }
        return source;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFile move(String storageKey, String sourcePath, String targetParentId, String updateBy) {
        SysFile source = findByPath(storageKey, sourcePath);
        if (source == null) {
            throw new IllegalArgumentException("源节点不存在");
        }

        String targetParentPath = "/";
        if (StringUtils.hasText(targetParentId)) {
            SysFile parent = getById(targetParentId);
            if (parent == null || !Objects.equals(parent.getIsDeleted(), 0)) {
                throw new IllegalArgumentException("目标目录不存在");
            }
            targetParentPath = normalizeDirPath(parent.getFilePath());
        }

        if (StringUtils.hasText(targetParentId) && targetParentPath.startsWith(source.getFilePath())) {
            throw new IllegalArgumentException("不能将目录移动到自己的子目录下");
        }

        String newPath = SysFile.TYPE_DIR.equals(source.getFileType())
                ? targetParentPath + source.getFileName() + "/"
                : targetParentPath + source.getFileName();
        if (existsByPath(storageKey, newPath)) {
            throw new IllegalArgumentException("目标目录下已存在同名节点");
        }

        String oldPath = source.getFilePath();
        source.setParentId(StringUtils.hasText(targetParentId) ? targetParentId : null);
        source.setFilePath(newPath);
        updateById(source);

        if (SysFile.TYPE_DIR.equals(source.getFileType())) {
            rewriteDescendantPaths(storageKey, oldPath, newPath);
        }
        return source;
    }

    @Override
    public List<SysFile> listPendingPhysicalDelete(LocalDateTime cutoffTime, int limit) {
        int safeLimit = Math.max(limit, 1);
        return list(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getIsDeleted, 1)
                .eq(SysFile::getFileType, SysFile.TYPE_FILE)
                .le(SysFile::getUpdateTime, cutoffTime)
                .and(wrapper -> wrapper.isNull(SysFile::getMetadata)
                        .or()
                        .notLike(SysFile::getMetadata, "\"gcDone\":true"))
                .orderByAsc(SysFile::getUpdateTime)
                .last("limit " + safeLimit));
    }

    @Override
    public boolean markPhysicalDeleteDone(String id) {
        return update(new LambdaUpdateWrapper<SysFile>()
                .eq(SysFile::getId, id)
                .setSql("metadata = JSON_SET(COALESCE(metadata, JSON_OBJECT()), '$.gcDone', true, '$.gcTime', NOW())"));
    }

    private SysFile createDirectoryRecord(String storageKey, String parentId, String fullPath, String dirName, String createBy) {
        if (existsByPath(storageKey, fullPath)) {
            return findByPath(storageKey, fullPath);
        }
        SysFile sysFile = new SysFile();
        sysFile.setId(IdUtil.fastSimpleUUID());
        sysFile.setStorageKey(storageKey);
        sysFile.setFilePath(fullPath);
        sysFile.setParentId(parentId);
        sysFile.setFileName(dirName);
        sysFile.setFileType(SysFile.TYPE_DIR);
        sysFile.setFileSize(0L);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setCreateBy(createBy);
        sysFile.setIsDeleted(0);
        save(sysFile);
        return sysFile;
    }

    private void rewriteDescendantPaths(String storageKey, String oldPrefix, String newPrefix) {
        List<SysFile> descendants = list(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getStorageKey, storageKey)
                .eq(SysFile::getIsDeleted, 0)
                .likeRight(SysFile::getFilePath, oldPrefix)
                .ne(SysFile::getFilePath, oldPrefix));
        if (descendants.isEmpty()) {
            return;
        }
        List<SysFile> updates = new ArrayList<>(descendants.size());
        for (SysFile child : descendants) {
            String suffix = child.getFilePath().substring(oldPrefix.length());
            child.setFilePath(newPrefix + suffix);
            updates.add(child);
        }
        updateBatchById(updates);
    }

    private String normalizeDirPath(String path) {
        if (!StringUtils.hasText(path) || "/".equals(path.trim())) {
            return "/";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        return normalized;
    }

    private String normalizeFilePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "/";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        return normalized;
    }

    private String normalizeName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("名称不能为空");
        }
        return name.trim();
    }

    private String extractParentPath(String fullPath) {
        if (!StringUtils.hasText(fullPath) || "/".equals(fullPath)) {
            return "/";
        }
        String target = fullPath;
        if (target.endsWith("/")) {
            target = target.substring(0, target.length() - 1);
        }
        int lastSlash = target.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "/";
        }
        return target.substring(0, lastSlash + 1);
    }

    private String extractFileName(String fullPath) {
        if (!StringUtils.hasText(fullPath) || "/".equals(fullPath)) {
            return fullPath;
        }
        String target = fullPath;
        if (target.endsWith("/")) {
            target = target.substring(0, target.length() - 1);
        }
        int lastSlash = target.lastIndexOf('/');
        return lastSlash >= 0 ? target.substring(lastSlash + 1) : target;
    }

    private void fillFileExt(SysFile sysFile, String fileName) {
        if (StringUtils.hasText(fileName) && fileName.contains(".")) {
            sysFile.setFileExt(fileName.substring(fileName.lastIndexOf('.') + 1));
        } else {
            sysFile.setFileExt(null);
        }
    }
}
