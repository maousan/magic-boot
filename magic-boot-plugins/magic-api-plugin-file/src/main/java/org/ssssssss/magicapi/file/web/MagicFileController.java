package org.ssssssss.magicapi.file.web;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.core.web.MagicController;
import org.ssssssss.magicapi.core.web.MagicExceptionHandler;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.ssssssss.magicapi.file.model.StorageType;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;

/**
 * 文件存储管理接口
 */
public class MagicFileController extends MagicController implements MagicExceptionHandler {

    private final MagicDynamicFileClient magicDynamicFileClient;
    private static final Logger logger = LoggerFactory.getLogger(MagicFileController.class);

    public MagicFileController(MagicConfiguration configuration, MagicDynamicFileClient magicDynamicFileClient) {
        super(configuration);
        this.magicDynamicFileClient = magicDynamicFileClient;
    }

    /**
     * 获取支持的存储类型
     */
    @GetMapping("/file/storage/types")
    @ResponseBody
    public JsonBean<List<StorageType>> getStorageTypes() {
        List<StorageType> types = Arrays.asList(
            createLocalStorageType(),
            createS3StorageType(),
            createMinioStorageType()
        );
        return new JsonBean<>(types);
    }

    /**
     * 测试存储连接
     */
    @PostMapping("/file/storage/test")
    @ResponseBody
    public JsonBean<Map<String, Object>> testStorage(@RequestBody StorageInfo storageInfo) {
        // 空值检查
        if (storageInfo == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "存储配置信息不能为空");
            return new JsonBean<>(result);
        }

        String type = storageInfo.getType();
        Map<String, Object> properties = storageInfo.getProperties();

        // 类型空值检查
        if (type == null || type.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "存储类型不能为空");
            return new JsonBean<>(result);
        }

        // 属性空值检查
        if (properties == null) {
            properties = new HashMap<>();
        }

        try {
            switch (type) {
                case "minio":
                    return testMinioStorage(properties);
                case "s3":
                    return testS3Storage(properties);
                case "local":
                    return testLocalStorage(properties);
                default:
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "不支持的存储类型: " + type);
                    return new JsonBean<>(result);
            }
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "连接测试失败: " + e.getMessage());
            logger.info(ExceptionUtils.getStackTrace(e));
            return new JsonBean<>(result);
        }
    }

    /**
     * 测试 MinIO 存储连接
     */
    private JsonBean<Map<String, Object>> testMinioStorage(Map<String, Object> properties) {
        String endpoint = getString(properties, "endpoint");
        String bucket = getString(properties, "bucket");
        String accessKey = getString(properties, "accessKey");
        String secretKey = getString(properties, "secretKey");

        Map<String, Object> result = new HashMap<>();

        try {
            // 创建 MinIO 客户端
            MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

            long startTime = System.currentTimeMillis();

            // 检查 bucket 是否存在
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());

            long responseTime = System.currentTimeMillis() - startTime;

            if (bucketExists) {
                result.put("success", true);
                result.put("message", "连接测试成功，Bucket 已存在");
                result.put("responseTime", responseTime);
                result.put("bucketExists", true);
            } else {
                result.put("success", true);
                result.put("message", "连接测试成功，但 Bucket 不存在（保存配置时将自动创建）");
                result.put("responseTime", responseTime);
                result.put("bucketExists", false);
            }

            return new JsonBean<>(result);
        } catch (ErrorResponseException e) {
            // 认证失败等错误
            result.put("success", false);
            result.put("message", "认证失败: " + e.getLocalizedMessage());
            return new JsonBean<>(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "连接失败: " + e.getMessage());
            return new JsonBean<>(result);
        }
    }

    /**
     * 测试 S3 存储连接
     */
    private JsonBean<Map<String, Object>> testS3Storage(Map<String, Object> properties) {
        String endpoint = getString(properties, "endpoint");
        String region = getString(properties, "region", "us-east-1");
        String bucket = getString(properties, "bucket");
        String accessKey = getString(properties, "accessKey");
        String secretKey = getString(properties, "secretKey");

        Map<String, Object> result = new HashMap<>();

        try {
            // 创建 S3 客户端
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .build();

            long startTime = System.currentTimeMillis();

            // 检查 bucket 是否存在
            boolean bucketExists = s3Client.doesBucketExistV2(bucket);

            long responseTime = System.currentTimeMillis() - startTime;

            if (bucketExists) {
                result.put("success", true);
                result.put("message", "连接测试成功，Bucket 已存在");
                result.put("responseTime", responseTime);
                result.put("bucketExists", true);
            } else {
                result.put("success", true);
                result.put("message", "连接测试成功，但 Bucket 不存在（保存配置时将自动创建）");
                result.put("responseTime", responseTime);
                result.put("bucketExists", false);
            }

            return new JsonBean<>(result);
        } catch (AmazonS3Exception e) {
            result.put("success", false);
            result.put("message", "认证失败: " + e.getErrorMessage());
            return new JsonBean<>(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "连接失败: " + e.getMessage());
            return new JsonBean<>(result);
        }
    }

    /**
     * 测试本地存储
     */
    private JsonBean<Map<String, Object>> testLocalStorage(Map<String, Object> properties) {
        String basePath = getString(properties, "basePath");

        Map<String, Object> result = new HashMap<>();

        if (basePath == null || basePath.isEmpty()) {
            result.put("success", false);
            result.put("message", "存储路径不能为空");
            return new JsonBean<>(result);
        }

        try {
            Path path = Path.of(basePath);
            File dir = path.toFile();

            // 检查目录是否存在
            if (!dir.exists()) {
                // 尝试创建目录
                boolean created = dir.mkdirs();
                if (created) {
                    result.put("success", true);
                    result.put("message", "目录不存在，已自动创建");
                    result.put("pathCreated", true);
                } else {
                    result.put("success", false);
                    result.put("message", "目录不存在且无法创建，请检查权限");
                    return new JsonBean<>(result);
                }
            } else if (!dir.isDirectory()) {
                result.put("success", false);
                result.put("message", "指定的路径不是目录");
                return new JsonBean<>(result);
            }

            // 检查读写权限
            boolean canRead = dir.canRead();
            boolean canWrite = dir.canWrite();

            if (canRead && canWrite) {
                result.put("success", true);
                if (!result.containsKey("message")) {
                    result.put("message", "存储路径可正常访问");
                }
                result.put("canRead", true);
                result.put("canWrite", true);
            } else {
                result.put("success", false);
                result.put("message", String.format("权限不足: 读=%b, 写=%b", canRead, canWrite));
            }

            return new JsonBean<>(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查存储路径失败: " + e.getMessage());
            return new JsonBean<>(result);
        }
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
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    /**
     * 创建本地存储类型定义
     */
    private StorageType createLocalStorageType() {
        StorageType type = new StorageType();
        type.setType("local");
        type.setName("本地存储");
        type.setIcon("folder");

        List<StorageType.StorageTypeField> fields = new ArrayList<>();

        StorageType.StorageTypeField basePath = new StorageType.StorageTypeField();
        basePath.setName("basePath");
        basePath.setLabel("存储路径");
        basePath.setType("text");
        basePath.setRequired(true);
        basePath.setPlaceholder("upload/");
        fields.add(basePath);

        StorageType.StorageTypeField domain = new StorageType.StorageTypeField();
        domain.setName("domain");
        domain.setLabel("访问域名");
        domain.setType("text");
        domain.setRequired(true);
        domain.setPlaceholder("例如: http://localhost:8089");
        fields.add(domain);

        type.setFields(fields);
        return type;
    }

    /**
     * 创建 S3 存储类型定义
     */
    private StorageType createS3StorageType() {
        StorageType type = new StorageType();
        type.setType("s3");
        type.setName("Amazon S3");
        type.setIcon("cloud");

        List<StorageType.StorageTypeField> fields = new ArrayList<>();

        StorageType.StorageTypeField endpoint = new StorageType.StorageTypeField();
        endpoint.setName("endpoint");
        endpoint.setLabel("Endpoint URL");
        endpoint.setType("text");
        endpoint.setRequired(true);
        endpoint.setPlaceholder("例如: https://s3.amazonaws.com");
        fields.add(endpoint);

        StorageType.StorageTypeField bucket = new StorageType.StorageTypeField();
        bucket.setName("bucket");
        bucket.setLabel("Bucket 名称");
        bucket.setType("text");
        bucket.setRequired(true);
        bucket.setPlaceholder("例如: my-bucket");
        fields.add(bucket);

        StorageType.StorageTypeField region = new StorageType.StorageTypeField();
        region.setName("region");
        region.setLabel("区域");
        region.setType("text");
        region.setRequired(false);
        region.setDefaultValue("us-east-1");
        fields.add(region);

        StorageType.StorageTypeField accessKey = new StorageType.StorageTypeField();
        accessKey.setName("accessKey");
        accessKey.setLabel("Access Key");
        accessKey.setType("text");
        accessKey.setRequired(true);
        fields.add(accessKey);

        StorageType.StorageTypeField secretKey = new StorageType.StorageTypeField();
        secretKey.setName("secretKey");
        secretKey.setLabel("Secret Key");
        secretKey.setType("password");
        secretKey.setRequired(true);
        fields.add(secretKey);

        type.setFields(fields);
        return type;
    }

    /**
     * 创建 MinIO 存储类型定义
     */
    private StorageType createMinioStorageType() {
        StorageType type = new StorageType();
        type.setType("minio");
        type.setName("MinIO");
        type.setIcon("server");

        List<StorageType.StorageTypeField> fields = new ArrayList<>();

        StorageType.StorageTypeField endpoint = new StorageType.StorageTypeField();
        endpoint.setName("endpoint");
        endpoint.setLabel("Endpoint URL");
        endpoint.setType("text");
        endpoint.setRequired(true);
        endpoint.setPlaceholder("例如: http://192.168.1.100:9000");
        fields.add(endpoint);

        StorageType.StorageTypeField bucket = new StorageType.StorageTypeField();
        bucket.setName("bucket");
        bucket.setLabel("Bucket 名称");
        bucket.setType("text");
        bucket.setRequired(true);
        fields.add(bucket);

        StorageType.StorageTypeField accessKey = new StorageType.StorageTypeField();
        accessKey.setName("accessKey");
        accessKey.setLabel("Access Key");
        accessKey.setType("text");
        accessKey.setRequired(true);
        fields.add(accessKey);

        StorageType.StorageTypeField secretKey = new StorageType.StorageTypeField();
        secretKey.setName("secretKey");
        secretKey.setLabel("Secret Key");
        secretKey.setType("password");
        secretKey.setRequired(true);
        fields.add(secretKey);

        StorageType.StorageTypeField basePath = new StorageType.StorageTypeField();
        basePath.setName("basePath");
        basePath.setLabel("基础路径");
        basePath.setType("text");
        basePath.setRequired(false);
        basePath.setDefaultValue("");
        fields.add(basePath);

        type.setFields(fields);
        return type;
    }
}
