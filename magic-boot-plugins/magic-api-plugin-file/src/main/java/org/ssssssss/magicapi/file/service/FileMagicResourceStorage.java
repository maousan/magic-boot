package org.ssssssss.magicapi.file.service;

import org.ssssssss.magicapi.core.config.JsonCodeConstants;
import org.ssssssss.magicapi.core.model.JsonCode;
import org.ssssssss.magicapi.core.model.MagicEntity;
import org.ssssssss.magicapi.core.service.MagicResourceService;
import org.ssssssss.magicapi.core.service.MagicResourceStorage;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.ssssssss.magicapi.utils.JsonUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 文件存储配置持久化
 */
public class FileMagicResourceStorage implements MagicResourceStorage<StorageInfo>, JsonCodeConstants {

    private MagicResourceService magicResourceService;

    @Override
    public String folder() {
        return "file";
    }

    @Override
    public String suffix() {
        return ".json";
    }

    @Override
    public Class<StorageInfo> magicClass() {
        return StorageInfo.class;
    }

    @Override
    public boolean requirePath() {
        return false;
    }

    @Override
    public boolean requiredScript() {
        return false;
    }

    @Override
    public boolean allowRoot() {
        return true;
    }

    @Override
    public String buildMappingKey(StorageInfo info) {
        return String.format("%s-%s", info.getKey(), info.getUpdateTime());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validate(StorageInfo entity) {
        notBlank(entity.getKey(), DS_KEY_REQUIRED);
        notBlank(entity.getType(), new JsonCode(1020, "存储类型不能为空"));
        notNull(entity.getProperties(), new JsonCode(1020, "存储配置不能为空"));

        String type = entity.getType();
        Map<String, Object> properties = entity.getProperties();

        // 验证配置参数
        switch (type) {
            case "local":
                validateLocalStorage(properties);
                break;
            case "s3":
            case "minio":
                validateS3OrMinioStorage(properties);
                break;
            default:
                notNull(null, new JsonCode(1020, "不支持的存储类型: " + type));
        }

        // 验证 key 是否重复
        boolean noneMatchKey = magicResourceService.listFiles("file:0").stream()
                .map(it -> (StorageInfo) it)
                .filter(it -> !it.getId().equals(entity.getId()))
                .noneMatch(it -> Objects.equals(it.getKey(), entity.getKey()));
        isTrue(noneMatchKey, DS_KEY_CONFLICT);
    }

    @Override
    public void setMagicResourceService(MagicResourceService magicResourceService) {
        this.magicResourceService = magicResourceService;
    }

    @Override
    public StorageInfo read(byte[] bytes) {
        return JsonUtils.readValue(bytes, StorageInfo.class);
    }

    @Override
    public byte[] write(MagicEntity entity) {
        return JsonUtils.toJsonBytes(entity);
    }

    /**
     * 验证本地存储配置
     */
    private void validateLocalStorage(Map<String, Object> properties) {
        String basePath = getString(properties, "basePath");
        notBlank(basePath, new JsonCode(1020, "basePath 不能为空"));
        String domain = getString(properties, "domain");
        notBlank(domain, new JsonCode(1020, "domain 不能为空"));
    }

    /**
     * 验证 S3/MinIO 存储配置
     */
    private void validateS3OrMinioStorage(Map<String, Object> properties) {
        String endpoint = getString(properties, "endpoint");
        notBlank(endpoint, new JsonCode(1020, "endpoint 不能为空"));
        String bucket = getString(properties, "bucket");
        notBlank(bucket, new JsonCode(1020, "bucket 不能为空"));
        String accessKey = getString(properties, "accessKey");
        notBlank(accessKey, new JsonCode(1020, "accessKey 不能为空"));
        String secretKey = getString(properties, "secretKey");
        notBlank(secretKey, new JsonCode(1020, "secretKey 不能为空"));
    }

    private String getString(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        return value != null ? value.toString() : null;
    }
}
