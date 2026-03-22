package org.ssssssss.magicapi.file.model;

import org.ssssssss.magicapi.core.model.MagicEntity;

import java.util.Map;

/**
 * 存储平台配置信息
 */
public class StorageInfo extends MagicEntity {

    /**
     * 存储平台唯一标识（用于代码中引用）
     */
    private String key;

    /**
     * 存储类型：local, s3, minio, oss, cos
     */
    private String type;

    /**
     * 平台特定配置
     */
    private Map<String, Object> properties;

    /**
     * 是否为默认存储
     */
    private Boolean isDefault;

    /**
     * 是否启用
     */
    private Boolean enabled;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public MagicEntity simple() {
        StorageInfo info = new StorageInfo();
        info.setKey(this.key);
        info.setType(this.type);
        info.setIsDefault(this.isDefault);
        info.setEnabled(this.enabled);
        super.simple(info);
        return info;
    }

    @Override
    public MagicEntity copy() {
        StorageInfo info = new StorageInfo();
        super.copyTo(info);
        info.setKey(this.key);
        info.setType(this.type);
        info.setProperties(this.properties);
        info.setIsDefault(this.isDefault);
        info.setEnabled(this.enabled);
        return info;
    }
}
