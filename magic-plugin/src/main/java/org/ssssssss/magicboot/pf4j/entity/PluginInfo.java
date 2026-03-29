package org.ssssssss.magicboot.pf4j.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ssssssss.magicboot.pf4j.model.PluginStatus;

import java.time.LocalDateTime;

/**
 * 鎻掍欢淇℃伅瀹炰綋
 */
@Data
@TableName("magic_plugin")
public class PluginInfo {

    /**
     * 涓婚敭 ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 鎻掍欢 ID
     */
    private String pluginId;

    /**
     * 鎻掍欢鍚嶇О
     */
    private String pluginName;

    /**
     * 鎻掍欢鐗堟湰
     */
    private String version;

    /**
     * 鎻掍欢鎻忚堪
     */
    private String description;

    /**
     * 鎻掍欢浣滆€?
     */
    private String author;

    /**
     * 鎻掍欢绫?
     */
    private String pluginClass;

    /**
     * 鎻掍欢鐘舵€?
     */
    private String status;

    /**
     * JAR 鏂囦欢璺緞
     */
    private String jarPath;

    /**
     * 鍒涘缓鏃堕棿
     */
    private LocalDateTime createTime;

    /**
     * 鏇存柊鏃堕棿
     */
    private LocalDateTime updateTime;

    /**
     * 鎻掍欢渚濊禆
     */
    private String dependencies;

    /**
     * 鎻掍欢鎻愪緵鑰?     */
    private String provider;

    /**
     * 瀹夎鍖呯被鍨嬶細ZIP / LEGACY_JAR
     */
    private String packageType;

    /**
     * 瀹夎鍖呮牎楠屽拰锛堥€氬父涓?entryJar sha256锛?     */
    private String packageChecksum;

    /**
     * Manifest 鐗堟湰
     */
    private String manifestVersion;

    /**
     * Manifest 鍘熷 JSON
     */
    private String manifestJson;

    /**
     * Manifest 瑕佹眰鐨?magic-boot 鐗堟湰
     */
    private String requiresMagicBoot;

    /**
     * Manifest 鏉冮檺澹版槑锛圝SON锛?     */
    private String permissions;

    /**
     * 瀹夎鏉ユ簮锛圲PLOAD_ZIP / UPLOAD_JAR / LOCAL_PATH / REMOTE_URL锛?     */
    private String installSource;

    /**
     * 瀹夎鏃堕棿
     */
    private LocalDateTime installTime;
}
