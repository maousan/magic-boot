package org.ssssssss.magicapi.file.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.platform.LocalPlusFileStorage;
import org.dromara.x.file.storage.core.platform.AmazonS3FileStorage;
import org.dromara.x.file.storage.core.platform.MinioFileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态文件存储客户端管理器
 * 管理动态注册的 FileStorageService 实例
 */
@Component("magicDynamicFileClient")
public class MagicDynamicFileClient {

    private static final Logger logger = LoggerFactory.getLogger(MagicDynamicFileClient.class);

    private final Map<String, FileStorageService> clients = new ConcurrentHashMap<>();
    private final Map<String, Boolean> defaultFlags = new ConcurrentHashMap<>();
    private final Map<String, StorageInfo> storageInfoMap = new ConcurrentHashMap<>();
    private String defaultKey;

    /**
     * 注册文件存储客户端
     */
    public void put(String id, String key, String name, FileStorageService client) {
        if (key == null) {
            key = "";
        }
        logger.info("注册文件存储配置: {} - {}", StringUtils.hasText(key) ? key : "default", name);
        this.clients.put(key, client);

        if (defaultKey == null) {
            defaultKey = key;
        }
    }

    /**
     * 获取所有存储配置Key
     */
    public void put(String id, String key, String name, FileStorageService client, Boolean isDefault) {
        String normalizedKey = key == null ? "" : key;
        put(id, normalizedKey, name, client);
        defaultFlags.put(normalizedKey, Boolean.TRUE.equals(isDefault));
        refreshDefaultKey();
    }

    public List<String> listKeys() {
        return new ArrayList<>(clients.keySet());
    }

    /**
     * 获取默认存储配置Key
     */
    public String getDefaultKey() {
        return defaultKey;
    }

    /**
     * 获取指定存储客户端
     */
    public FileStorageService getClient(String key) {
        if (key == null || key.isEmpty()) {
            key = defaultKey;
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("找不到文件存储配置: default");
        }
        FileStorageService client = clients.get(key);
        if (client == null) {
            throw new IllegalArgumentException("找不到文件存储配置: " + key);
        }
        return client;
    }

    /**
     * 获取所有存储配置信息
     * @return
     */
    public List<StorageInfo> getStorageInfoList() {
        return new ArrayList<>(storageInfoMap.values());
    }

    /**
     * 根据配置创建 FileStorageService 实例
     */
    public FileStorageService createFileStorageService(StorageInfo info) {
        String type = info.getType();
        Map<String, Object> properties = info.getProperties();
        String platformKey = info.getKey();  // 从顶层获取 key

        // 确定平台标识符
        String platform = StringUtils.hasText(platformKey) ? platformKey : type;

        try {
            // 确保 bucket 存在（对于 minio 和 s3 类型）
            ensureBucketExists(type, properties);

            FileStorageProperties storageProps = new FileStorageProperties();
            // 设置默认平台为当前配置的平台
            storageProps.setDefaultPlatform(platform);

            FileStorageServiceBuilder builder = new FileStorageServiceBuilder(storageProps);

            FileStorage storage = createFileStorage(type, properties, platformKey);
            if (storage == null) {
                logger.error("不支持的存储类型或配置错误: {}", type);
                return null;
            }

            builder.addFileStorage(storage);
            builder.useDefault();

            storageInfoMap.put(platform, info);

            return builder.build();
        } catch (Exception e) {
            logger.error("创建文件存储配置失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 确保 bucket 存在（对于 MinIO 和 S3）
     */
    private void ensureBucketExists(String type, Map<String, Object> properties) {
        switch (type) {
            case "minio":
                ensureMinioBucketExists(properties);
                break;
            case "s3":
                ensureS3BucketExists(properties);
                break;
            case "local":
                ensureLocalDirectoryExists(properties);
                break;
        }
    }

    /**
     * 确保 MinIO bucket 存在
     */
    private void ensureMinioBucketExists(Map<String, Object> properties) {
        String endpoint = getString(properties, "endpoint");
        String bucket = getString(properties, "bucket");
        String accessKey = getString(properties, "accessKey");
        String secretKey = getString(properties, "secretKey");

        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build());

            if (!exists) {
                logger.info("MinIO bucket [{}] 不存在，正在创建...", bucket);
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build());
                logger.info("MinIO bucket [{}] 创建成功", bucket);
            }
        } catch (Exception e) {
            logger.warn("检查/创建 MinIO bucket 失败: {}", e.getMessage());
            // 不抛出异常，让后续操作来处理错误
        }
    }

    /**
     * 确保 S3 bucket 存在
     */
    private void ensureS3BucketExists(Map<String, Object> properties) {
        String endpoint = getString(properties, "endpoint");
        String region = getString(properties, "region", "us-east-1");
        String bucket = getString(properties, "bucket");
        String accessKey = getString(properties, "accessKey");
        String secretKey = getString(properties, "secretKey");

        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withPathStyleAccessEnabled(true)
                    .build();

            if (!s3Client.doesBucketExistV2(bucket)) {
                logger.info("S3 bucket [{}] 不存在，正在创建...", bucket);
                s3Client.createBucket(bucket);
                logger.info("S3 bucket [{}] 创建成功", bucket);
            }
        } catch (Exception e) {
            logger.warn("检查/创建 S3 bucket 失败: {}", e.getMessage());
            // 不抛出异常，让后续操作来处理错误
        }
    }

    /**
     * 确保本地存储目录存在
     */
    private void ensureLocalDirectoryExists(Map<String, Object> properties) {
        String basePath = getString(properties, "basePath");
        if (StringUtils.hasText(basePath)) {
            File dir = new File(basePath);
            if (!dir.exists()) {
                logger.info("本地存储目录 [{}] 不存在，正在创建...", basePath);
                boolean created = dir.mkdirs();
                if (created) {
                    logger.info("本地存储目录 [{}] 创建成功", basePath);
                } else {
                    logger.warn("本地存储目录 [{}] 创建失败", basePath);
                }
            }
        }
    }

    /**
     * 创建文件存储实例
     */
    private FileStorage createFileStorage(String type, Map<String, Object> properties, String platformKey) {
        switch (type) {
            case "local":
                return createLocalStorage(properties, platformKey);
            case "s3":
                return createS3Storage(properties, platformKey);
            case "minio":
                return createMinioStorage(properties, platformKey);
            default:
                return null;
        }
    }

    /**
     * 创建本地存储（使用 LocalPlusFileStorage）
     */
    private LocalPlusFileStorage createLocalStorage(Map<String, Object> properties, String platformKey) {
        String basePath = getString(properties, "basePath");
        String domain = getString(properties, "domain");
        String platform = StringUtils.hasText(platformKey) ? platformKey : "local";

        FileStorageProperties.LocalPlusConfig config = new FileStorageProperties.LocalPlusConfig();
        config.setBasePath(basePath);
        config.setDomain(domain);
        config.setPlatform(platform);

        List<LocalPlusFileStorage> storageList = FileStorageServiceBuilder.buildLocalPlusFileStorage(Collections.singletonList(config));
        return storageList.isEmpty() ? null : storageList.get(0);
    }

    /**
     * 创建 S3 存储
     */
    private AmazonS3FileStorage createS3Storage(Map<String, Object> properties, String platformKey) {
        String endpoint = getString(properties, "endpoint");
        String region = getString(properties, "region", "us-east-1");
        String bucket = getString(properties, "bucket");
        String accessKey = getString(properties, "accessKey");
        String secretKey = getString(properties, "secretKey");
        String platform = StringUtils.hasText(platformKey) ? platformKey : "s3";

        FileStorageProperties.AmazonS3Config config = new FileStorageProperties.AmazonS3Config();
        config.setEndPoint(endpoint);
        config.setBucketName(bucket);
        config.setAccessKey(accessKey);
        config.setSecretKey(secretKey);
        config.setRegion(region);
        config.setPlatform(platform);

        List<AmazonS3FileStorage> storageList = FileStorageServiceBuilder.buildAmazonS3FileStorage(Collections.singletonList(config), new ArrayList<>());
        return storageList.isEmpty() ? null : storageList.get(0);
    }

    /**
     * 创建 MinIO 存储
     */
    private MinioFileStorage createMinioStorage(Map<String, Object> properties, String platformKey) {
        String endpoint = getString(properties, "endpoint");
        String bucket = getString(properties, "bucket");
        String accessKey = getString(properties, "accessKey");
        String secretKey = getString(properties, "secretKey");
        String basePath = getString(properties, "basePath", "");
        String platform = StringUtils.hasText(platformKey) ? platformKey : "minio";

        FileStorageProperties.MinioConfig config = new FileStorageProperties.MinioConfig();
        config.setEndPoint(endpoint);
        config.setBucketName(bucket);
        config.setAccessKey(accessKey);
        config.setSecretKey(secretKey);
        config.setBasePath(basePath);
        config.setPlatform(platform);

        List<MinioFileStorage> storageList = FileStorageServiceBuilder.buildMinioFileStorage(Collections.singletonList(config), new ArrayList<>());
        return storageList.isEmpty() ? null : storageList.get(0);
    }

    /**
     * 删除存储客户端
     */
    public void delete(String key) {
        if (key == null) {
            key = "";
        }
        FileStorageService client = clients.remove(key);
        defaultFlags.remove(key);
        refreshDefaultKey();
        if (client != null) {
            logger.info("删除文件存储配置: {}", key);
        }
    }

    /**
     * 检查是否有配置
     */
    public boolean isEmpty() {
        return clients.isEmpty();
    }

    /**
     * 获取属性值
     */
    private String getString(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取属性值，带默认值
     */
    private String getString(Map<String, Object> properties, String key, String defaultValue) {
        String value = getString(properties, key);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private void refreshDefaultKey() {
        Optional<String> explicitDefault = defaultFlags.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()) && clients.containsKey(entry.getKey()))
                .map(Map.Entry::getKey)
                .findFirst();
        if (explicitDefault.isPresent()) {
            defaultKey = explicitDefault.get();
            return;
        }
        if (defaultKey != null && clients.containsKey(defaultKey)) {
            return;
        }
        defaultKey = clients.keySet().stream().findFirst().orElse(null);
    }
}
