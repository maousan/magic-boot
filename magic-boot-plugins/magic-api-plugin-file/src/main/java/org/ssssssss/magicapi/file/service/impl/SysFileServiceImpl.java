package org.ssssssss.magicapi.file.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ssssssss.magicapi.file.mapper.SysFileMapper;
import org.ssssssss.magicapi.file.model.SysFile;
import org.ssssssss.magicapi.file.service.SysFileService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 文件元数据 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {

    private final SysFileMapper sysFileMapper;

    @Override
    public SysFile getByPath(String filePath) {
        return getOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFilePath, filePath)
                .eq(SysFile::getIsDeleted, 0));
    }

    @Override
    public List<SysFile> listByParentPath(String parentPath) {
        // 确保路径以 / 结尾
        if (parentPath == null || parentPath.isEmpty()) {
            parentPath = "/";
        }
        if (!parentPath.endsWith("/")) {
            parentPath = parentPath + "/";
        }
        final String path = parentPath;
        return list(new LambdaQueryWrapper<SysFile>()
                .likeRight(SysFile::getFilePath, path)
                .eq(SysFile::getIsDeleted, 0)
                .orderByDesc(SysFile::getCreateTime));
    }

    @Override
    public SysFile getByMd5(String md5) {
        if (md5 == null || md5.isEmpty()) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getMd5, md5)
                .eq(SysFile::getIsDeleted, 0));
    }

    @Override
    public boolean existsByPath(String filePath) {
        return count(new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getFilePath, filePath)
                .eq(SysFile::getIsDeleted, 0)) > 0;
    }

    @Override
    public SysFile createDirectory(String storageKey, String parentPath, String dirName, String createBy) {
        // 确保父路径以 / 结尾
        if (parentPath == null || parentPath.isEmpty()) {
            parentPath = "/";
        }
        if (!parentPath.endsWith("/")) {
            parentPath = parentPath + "/";
        }
        // 构建完整目录路径（以 / 结尾表示目录）
        String dirPath = parentPath + dirName + "/";

        // 检查是否已存在
        if (existsByPath(dirPath)) {
            log.warn("目录已存在: {}", dirPath);
            return getByPath(dirPath);
        }

        SysFile sysFile = new SysFile();
        sysFile.setId(IdUtil.fastSimpleUUID());
        sysFile.setStorageKey(storageKey);
        sysFile.setFilePath(dirPath);
        sysFile.setFileName(dirName);
        sysFile.setFileType(SysFile.TYPE_DIR);
        sysFile.setFileSize(0L);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setCreateBy(createBy);
        sysFile.setIsDeleted(0);

        save(sysFile);
        log.info("目录记录创建成功: path={}, id={}", dirPath, sysFile.getId());
        return sysFile;
    }

    @Override
    public SysFile saveFileRecord(String storageKey, String filePath, String fileName,
                                   Long fileSize, String contentType, String url,
                                   String md5, String createBy) {
        SysFile sysFile = new SysFile();
        sysFile.setId(IdUtil.fastSimpleUUID());
        sysFile.setStorageKey(storageKey);
        sysFile.setFilePath(filePath);
        sysFile.setFileName(fileName);
        sysFile.setFileType(SysFile.TYPE_FILE);
        sysFile.setFileSize(fileSize != null ? fileSize : 0L);
        sysFile.setContentType(contentType);
        sysFile.setUrl(url);
        sysFile.setMd5(md5);
        sysFile.setCreateTime(LocalDateTime.now());
        sysFile.setCreateBy(createBy);
        sysFile.setIsDeleted(0);

        // 提取文件扩展名
        if (fileName != null && fileName.contains(".")) {
            sysFile.setFileExt(fileName.substring(fileName.lastIndexOf(".") + 1));
        }

        save(sysFile);
        log.info("文件记录保存成功: path={}, id={}", filePath, sysFile.getId());
        return sysFile;
    }

    @Override
    public boolean deleteByPath(String filePath, String updateBy) {
        SysFile sysFile = getByPath(filePath);
        if (sysFile == null) {
            log.info("文件记录不存在，跳过删除: path={}", filePath);
            return false;
        }

        // 使用 MyBatis-Plus 的逻辑删除
        boolean result = removeById(sysFile.getId());

        log.info("文件记录删除成功: path={}, id={}, result={}", filePath, sysFile.getId(), result);
        return result;
    }
}
