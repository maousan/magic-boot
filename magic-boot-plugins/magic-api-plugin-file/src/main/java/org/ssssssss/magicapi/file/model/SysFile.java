package org.ssssssss.magicapi.file.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件元数据实体类
 * 对应数据库表 sys_file
 */
@Data
@TableName("sys_file")
public class SysFile {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 存储平台标识(local/minio/aliyun/tencent等)
     */
    private String storageKey;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 类型(FILE-文件/DIR-目录)
     */
    private String fileType;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * MIME类型
     */
    private String contentType;

    /**
     * 文件扩展名(不含.)
     */
    private String fileExt;

    /**
     * MD5哈希(用于去重)
     */
    private String md5;

    /**
     * 访问URL
     */
    private String url;

    /**
     * 扩展元数据(JSON格式)
     */
    private String metadata;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识(0-正常 1-已删除)
     */
    @TableLogic
    private Integer isDeleted;

    // ==================== 文件类型常量 ====================

    /**
     * 文件类型：普通文件
     */
    public static final String TYPE_FILE = "FILE";

    /**
     * 文件类型：目录
     */
    public static final String TYPE_DIR = "DIR";
}
