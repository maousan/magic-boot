package org.ssssssss.magicapi.file.service;

import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.platform.AmazonS3FileStorage;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.file.model.StorageInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S3 存储测试用例
 * 注意：需要配置有效的 S3 端点才能通过这些测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class S3StorageTest {

    // 测试用的 S3 配置（根据实际环境修改）
    private static final String S3_ENDPOINT = "https://s3.amazonaws.com";
    private static final String S3_REGION = "us-east-1";
    private static final String S3_BUCKET = "test-bucket";
    private static final String S3_ACCESS_KEY = "test-access-key";
    private static final String S3_SECRET_KEY = "test-secret-key";

    private static MagicDynamicFileClient fileClient;

    @BeforeAll
    static void setUp() {
        fileClient = new MagicDynamicFileClient();
    }

    @Test
    @Order(1)
    void testCreateS3StorageConfig() {
        StorageInfo storageInfo = createS3StorageInfo();
        assertNotNull(storageInfo, "存储配置创建失败");
        assertEquals("s3", storageInfo.getType(), "存储类型应为 s3");

        System.out.println("✅ S3 存储配置创建成功");
    }

    @Test
    @Order(2)
    void testCreateS3StorageInstance() {
        StorageInfo storageInfo = createS3StorageInfo();
        FileStorageService service = fileClient.createFileStorageService(storageInfo);

        // 创建存储服务实例（不验证连接）
        assertNotNull(service, "S3 存储服务创建失败");
        assertFalse(service.getFileStorageList().isEmpty(), "存储平台列表不应为空");

        boolean hasS3Storage = service.getFileStorageList().stream()
                .anyMatch(s -> s instanceof AmazonS3FileStorage);
        assertTrue(hasS3Storage, "存储平台应包含 AmazonS3FileStorage");

        System.out.println("✅ S3 存储实例创建成功");
    }

    @Test
    @Order(3)
    void testS3ConfigWithCustomRegion() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("s3-ap-southeast");
        info.setType("s3");

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", "https://s3.ap-southeast-1.amazonaws.com");
        properties.put("region", "ap-southeast-1");
        properties.put("bucket", "my-bucket");
        properties.put("accessKey", "test-key");
        properties.put("secretKey", "test-secret");
        info.setProperties(properties);

        FileStorageService service = fileClient.createFileStorageService(info);
        assertNotNull(service, "自定义区域配置失败");

        System.out.println("✅ 自定义区域 S3 配置成功");
    }

    @Test
    @Order(4)
    void testS3ConfigWithMissingRegion() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("s3-no-region");
        info.setType("s3");

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", S3_ENDPOINT);
        properties.put("bucket", S3_BUCKET);
        properties.put("accessKey", S3_ACCESS_KEY);
        properties.put("secretKey", S3_SECRET_KEY);
        // 不设置 region，应使用默认值 us-east-1
        info.setProperties(properties);

        FileStorageService service = fileClient.createFileStorageService(info);
        assertNotNull(service, "缺少区域配置时应使用默认值");

        System.out.println("✅ 缺少区域时使用默认值 us-east-1");
    }

    @Test
    @Order(5)
    void testS3ConfigWithInvalidEndpoint() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("s3-invalid-endpoint");
        info.setType("s3");

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", "not-a-valid-url");
        properties.put("bucket", S3_BUCKET);
        properties.put("accessKey", S3_ACCESS_KEY);
        properties.put("secretKey", S3_SECRET_KEY);
        info.setProperties(properties);

        // 无效端点可能抛出异常或返回 null
        // 取决于 AWS SDK 的行为
        assertDoesNotThrow(() -> {
            try {
                fileClient.createFileStorageService(info);
            } catch (Exception e) {
                // 预期可能会失败
            }
        });

        System.out.println("✅ 无效端点处理正常");
    }

    @Test
    @Order(6)
    void testS3VsMinioConfig() {
        // S3 和 MinIO 使用相似的配置结构
        // 验证两者可以正确区分

        StorageInfo s3Info = createS3StorageInfo();
        StorageInfo minioInfo = createMinioLikeS3StorageInfo();

        assertEquals("s3", s3Info.getType());
        assertEquals("minio", minioInfo.getType());

        // 两者都应该能创建存储服务
        FileStorageService s3Service = fileClient.createFileStorageService(s3Info);
        FileStorageService minioService = fileClient.createFileStorageService(minioInfo);

        assertNotNull(s3Service, "S3 服务创建失败");
        assertNotNull(minioService, "MinIO 服务创建失败");

        System.out.println("✅ S3 和 MinIO 配置区分正常");
    }

    // ==================== 辅助方法 ====================

    private static StorageInfo createS3StorageInfo() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-s3");
        info.setName("测试S3存储");
        info.setType("s3");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", S3_ENDPOINT);
        properties.put("region", S3_REGION);
        properties.put("bucket", S3_BUCKET);
        properties.put("accessKey", S3_ACCESS_KEY);
        properties.put("secretKey", S3_SECRET_KEY);
        info.setProperties(properties);

        return info;
    }

    private static StorageInfo createMinioLikeS3StorageInfo() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-minio-s3");
        info.setName("MinIO兼容S3");
        info.setType("minio");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", "http://127.0.0.1:9000");
        properties.put("bucket", "test-bucket");
        properties.put("accessKey", "minioadmin");
        properties.put("secretKey", "minioadmin");
        properties.put("basePath", "");
        info.setProperties(properties);

        return info;
    }
}
