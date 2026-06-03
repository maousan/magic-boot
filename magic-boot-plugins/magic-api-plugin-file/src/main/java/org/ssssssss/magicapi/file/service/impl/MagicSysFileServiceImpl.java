package org.ssssssss.magicapi.file.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.ssssssss.magicapi.file.model.SysFile;
import org.ssssssss.magicapi.file.service.SysFileService;
import org.ssssssss.magicapi.file.starter.FilePluginProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件元数据 Service 实现。
 */
@Service
@RequiredArgsConstructor
public class MagicSysFileServiceImpl implements SysFileService {

    private final JdbcTemplate jdbcTemplate;
    private final FilePluginProperties props;

    private String table() {
        return props.getTableName();
    }

    @Override
    public SysFile getByPath(String filePath) {
        String sql = "SELECT * FROM " + table() + " WHERE file_path = ? AND is_deleted = 0 LIMIT 1";
        List<SysFile> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysFile.class), filePath);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public SysFile findByPath(String storageKey, String filePath) {
        String sql = "SELECT * FROM " + table() + " WHERE storage_key = ? AND file_path = ? AND is_deleted = 0 LIMIT 1";
        List<SysFile> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysFile.class), storageKey, filePath);
        return list.isEmpty() ? null : list.get(0);
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
        StringBuilder sql = new StringBuilder("SELECT * FROM " + table() + " WHERE is_deleted = 0");
        List<Object> params = new ArrayList<>();
        if (StringUtils.hasText(storageKey)) {
            sql.append(" AND storage_key = ?");
            params.add(storageKey);
        }
        if (StringUtils.hasText(parentId)) {
            sql.append(" AND parent_id = ?");
            params.add(parentId);
        } else {
            sql.append(" AND parent_id IS NULL");
        }
        sql.append(" ORDER BY file_type DESC, file_name ASC");
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(SysFile.class), params.toArray());
    }

    @Override
    public SysFile getByMd5(String md5) {
        if (!StringUtils.hasText(md5)) {
            return null;
        }
        String sql = "SELECT * FROM " + table() + " WHERE md5 = ? AND is_deleted = 0 LIMIT 1";
        List<SysFile> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysFile.class), md5);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean existsByPath(String filePath) {
        String sql = "SELECT COUNT(*) FROM " + table() + " WHERE file_path = ? AND is_deleted = 0";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filePath);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByPath(String storageKey, String filePath) {
        String sql = "SELECT COUNT(*) FROM " + table() + " WHERE storage_key = ? AND file_path = ? AND is_deleted = 0";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, storageKey, filePath);
        return count != null && count > 0;
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
            updateByIdInternal(existed);
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
        sysFile.setUpdateTime(LocalDateTime.now());
        sysFile.setIsDeleted(0);
        fillFileExt(sysFile, normalizedName);

        insert(sysFile);
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
        String sql = "UPDATE " + table() + " SET is_deleted = 1, update_time = ? WHERE storage_key = ? AND file_path = ? AND is_deleted = 0";
        return jdbcTemplate.update(sql, LocalDateTime.now(), storageKey, filePath) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean softDeleteSubtree(String storageKey, String nodeId, String updateBy) {
        SysFile node = getById(nodeId);
        if (node == null || !Objects.equals(node.getIsDeleted(), 0)) {
            return false;
        }
        String prefix = node.getFilePath();
        String sql = "UPDATE " + table() + " SET is_deleted = 1, update_time = ? WHERE storage_key = ? AND is_deleted = 0 AND file_path LIKE ?";
        return jdbcTemplate.update(sql, LocalDateTime.now(), storageKey, prefix + "%") > 0;
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
        String sql = "SELECT * FROM " + table()
                + " WHERE is_deleted = 1 AND file_type = ? AND update_time <= ?"
                + " AND (metadata IS NULL OR metadata NOT LIKE ?)"
                + " ORDER BY update_time ASC LIMIT ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysFile.class),
                SysFile.TYPE_FILE, cutoffTime, "%\"gcDone\":true%", safeLimit);
    }

    @Override
    public boolean markPhysicalDeleteDone(String id) {
        if ("mysql".equalsIgnoreCase(props.getDialect())) {
            String sql = "UPDATE " + table()
                    + " SET metadata = JSON_SET(COALESCE(metadata, JSON_OBJECT()), '$.gcDone', true, '$.gcTime', NOW())"
                    + " WHERE id = ?";
            return jdbcTemplate.update(sql, id) > 0;
        }
        // Non-MySQL dialect: application-level JSON handling
        SysFile file = getById(id);
        if (file == null) {
            return false;
        }
        String metadataStr = file.getMetadata();
        JSONObject meta;
        if (StringUtils.hasText(metadataStr)) {
            meta = JSON.parseObject(metadataStr);
        } else {
            meta = new JSONObject();
        }
        meta.put("gcDone", true);
        meta.put("gcTime", LocalDateTime.now().toString());
        String sql = "UPDATE " + table() + " SET metadata = ? WHERE id = ?";
        return jdbcTemplate.update(sql, meta.toJSONString(), id) > 0;
    }

    // ---- Basic CRUD helpers ----

    @Override
    public SysFile getById(String id) {
        String sql = "SELECT * FROM " + table() + " WHERE id = ? LIMIT 1";
        List<SysFile> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysFile.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean save(SysFile sysFile) {
        if (sysFile.getId() == null) {
            sysFile.setId(IdUtil.fastSimpleUUID());
        }
        if (sysFile.getCreateTime() == null) {
            sysFile.setCreateTime(LocalDateTime.now());
        }
        if (sysFile.getUpdateTime() == null) {
            sysFile.setUpdateTime(LocalDateTime.now());
        }
        if (sysFile.getIsDeleted() == null) {
            sysFile.setIsDeleted(0);
        }
        insert(sysFile);
        return true;
    }

    @Override
    public boolean updateById(SysFile sysFile) {
        updateByIdInternal(sysFile);
        return true;
    }

    private void insert(SysFile sysFile) {
        String sql = "INSERT INTO " + table()
                + " (id, storage_key, file_path, parent_id, file_name, file_type, file_size,"
                + " content_type, file_ext, md5, url, metadata, create_time, create_by, update_time, is_deleted)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                sysFile.getId(),
                sysFile.getStorageKey(),
                sysFile.getFilePath(),
                sysFile.getParentId(),
                sysFile.getFileName(),
                sysFile.getFileType(),
                sysFile.getFileSize(),
                sysFile.getContentType(),
                sysFile.getFileExt(),
                sysFile.getMd5(),
                sysFile.getUrl(),
                sysFile.getMetadata(),
                sysFile.getCreateTime(),
                sysFile.getCreateBy(),
                sysFile.getUpdateTime(),
                sysFile.getIsDeleted());
    }

    private void updateByIdInternal(SysFile sysFile) {
        String sql = "UPDATE " + table()
                + " SET storage_key = ?, file_path = ?, parent_id = ?, file_name = ?, file_type = ?,"
                + " file_size = ?, content_type = ?, file_ext = ?, md5 = ?, url = ?, metadata = ?,"
                + " update_time = ?, is_deleted = ?"
                + " WHERE id = ?";
        jdbcTemplate.update(sql,
                sysFile.getStorageKey(),
                sysFile.getFilePath(),
                sysFile.getParentId(),
                sysFile.getFileName(),
                sysFile.getFileType(),
                sysFile.getFileSize(),
                sysFile.getContentType(),
                sysFile.getFileExt(),
                sysFile.getMd5(),
                sysFile.getUrl(),
                sysFile.getMetadata(),
                LocalDateTime.now(),
                sysFile.getIsDeleted(),
                sysFile.getId());
    }

    private void updateBatchById(List<SysFile> files) {
        String sql = "UPDATE " + table()
                + " SET storage_key = ?, file_path = ?, parent_id = ?, file_name = ?, file_type = ?,"
                + " file_size = ?, content_type = ?, file_ext = ?, md5 = ?, url = ?, metadata = ?,"
                + " update_time = ?, is_deleted = ?"
                + " WHERE id = ?";
        List<Object[]> batchArgs = new ArrayList<>(files.size());
        for (SysFile f : files) {
            batchArgs.add(new Object[]{
                    f.getStorageKey(),
                    f.getFilePath(),
                    f.getParentId(),
                    f.getFileName(),
                    f.getFileType(),
                    f.getFileSize(),
                    f.getContentType(),
                    f.getFileExt(),
                    f.getMd5(),
                    f.getUrl(),
                    f.getMetadata(),
                    LocalDateTime.now(),
                    f.getIsDeleted(),
                    f.getId()
            });
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    // ---- Private helper methods ----

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
        sysFile.setUpdateTime(LocalDateTime.now());
        sysFile.setIsDeleted(0);
        insert(sysFile);
        return sysFile;
    }

    private void rewriteDescendantPaths(String storageKey, String oldPrefix, String newPrefix) {
        String sql = "SELECT * FROM " + table()
                + " WHERE storage_key = ? AND is_deleted = 0 AND file_path LIKE ? AND file_path <> ?";
        List<SysFile> descendants = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SysFile.class),
                storageKey, oldPrefix + "%", oldPrefix);
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
