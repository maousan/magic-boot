package org.ssssssss.magicapi.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ssssssss.magicapi.file.model.SysFile;

import java.util.List;

/**
 * 文件元数据 Service
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 根据路径查询文件
     *
     * @param filePath 文件路径
     * @return 文件信息
     */
    SysFile getByPath(String filePath);

    /**
     * 根据MD5查询文件
     *
     * @param md5 MD5 哈希值
     * @return 文件信息
     */
    SysFile getByMd5(String md5);

    /**
     * 根据父路径查询子文件列表
     *
     * @param parentPath 父目录路径
     * @return 子文件列表
     */
    List<SysFile> listByParentPath(String parentPath);

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean existsByPath(String filePath);

    /**
     * 创建目录记录
     *
     * @param storageKey 存储标识
     * @param parentPath 父目录路径
     * @param dirName    目录名称
     * @param createBy   创建人
     * @return 目录记录
     */
    SysFile createDirectory(String storageKey, String parentPath, String dirName, String createBy);

    /**
     * 保存文件记录
     *
     * @param storageKey  存储标识
     * @param filePath    文件路径
     * @param fileName    文件名
     * @param fileSize    文件大小
     * @param contentType 内容类型
     * @param url         访问URL
     * @param md5         MD5哈希
     * @param createBy    创建人
     * @return 文件记录
     */
    SysFile saveFileRecord(String storageKey, String filePath, String fileName,
                           Long fileSize, String contentType, String url,
                           String md5, String createBy);

    /**
     * 逻辑删除文件记录
     *
     * @param filePath 文件路径
     * @param updateBy 更新人
     * @return 是否成功
     */
    boolean deleteByPath(String filePath, String updateBy);
}
