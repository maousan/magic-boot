package org.ssssssss.magicapi.file.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件元数据实体，对应表 sys_file。
 */
@Data
@TableName("sys_file")
public class SysFile {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 存储平台标识，例如 local/minio/s3。 */
    private String storageKey;

    /** 逻辑全路径。 */
    private String filePath;

    /** 父节点ID，根目录为 null。 */
    private String parentId;

    /** 当前节点名（文件名或目录名）。 */
    private String fileName;

    /** 类型：FILE / DIR。 */
    private String fileType;

    private Long fileSize;

    private String contentType;

    private String fileExt;

    private String md5;

    private String url;

    /** JSON 扩展字段。 */
    private String metadata;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

    public static final String TYPE_FILE = "FILE";

    public static final String TYPE_DIR = "DIR";
}
