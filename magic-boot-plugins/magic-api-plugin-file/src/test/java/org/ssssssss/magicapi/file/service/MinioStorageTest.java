package org.ssssssss.magicapi.file.service;

import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.platform.MinioFileStorage;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.file.model.StorageInfo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MinIO 存储测试用例
 * 注意：需要运行 MinIO 服务器才能通过这些测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MinioStorageTest {

    // 测试用的 MinIO 配置（根据实际环境修改）
    private static final String MINIO_ENDPOINT = "http://127.0.0.1:9000";
    private static final String MINIO_ACCESS_KEY = "minioadmin";
    private static final String MINIO_SECRET_KEY = "minioadmin";
    private static final String MINIO_BUCKET = "test-bucket";

    private static MagicDynamicFileClient fileClient;
    private static boolean minioAvailable = false;
    private static String testStorageId;

    @BeforeAll
    static void setUp() {
        fileClient = new MagicDynamicFileClient();

        // 检测 MinIO 是否可用
        minioAvailable = checkMinioConnection();
        if (minioAvailable) {
            StorageInfo storageInfo = createMinioStorageInfo();
            FileStorageService service = fileClient.createFileStorageService(storageInfo);
            if (service != null) {
                testStorageId = UUID.randomUUID().toString();
                fileClient.put(testStorageId, "test-minio", "测试MinIO存储", service);
                System.out.println("✅ MinIO 连接成功，测试将正常运行");
            } else {
                minioAvailable = false;
            }
        }

        if (!minioAvailable) {
            System.out.println("⚠️ MinIO 服务不可用，部分测试将被跳过");
            System.out.println("   请确保 MinIO 服务运行在: " + MINIO_ENDPOINT);
        }
    }

    @Test
    @Order(1)
    void testCreateMinioStorage() {
        // 即使 MinIO 不可用，也应该能创建存储配置对象
        StorageInfo storageInfo = createMinioStorageInfo();
        assertNotNull(storageInfo, "存储配置创建失败");
        assertEquals("minio", storageInfo.getType(), "存储类型应为 minio");

        System.out.println("✅ MinIO 存储配置创建成功");
    }

    @Test
    @Order(2)
    void testMinioStorageInstance() {
        Assumptions.assumeTrue(minioAvailable, "MinIO 服务不可用，跳过测试");

        FileStorageService service = fileClient.getClient("test-minio");
        assertNotNull(service, "获取存储服务失败");
        assertFalse(service.getFileStorageList().isEmpty(), "存储平台列表不应为空");

        boolean hasMinioStorage = service.getFileStorageList().stream()
                .anyMatch(s -> s instanceof MinioFileStorage);
        assertTrue(hasMinioStorage, "存储平台应包含 MinioFileStorage");

        System.out.println("✅ MinIO 存储实例创建成功");
    }

    @Test
    @Order(3)
    void testUploadToMinio() {
        Assumptions.assumeTrue(minioAvailable, "MinIO 服务不可用，跳过测试");

        FileStorageService service = fileClient.getClient("test-minio");
        assertNotNull(service, "获取存储服务失败");

        String content = "MinIO Upload Test " + UUID.randomUUID();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        String fileName = "test-" + UUID.randomUUID() + ".txt";
        FileInfo fileInfo = service.of(inputStream)
                .setOriginalFilename(fileName)
                .setPath("test/")
                .upload();

        assertNotNull(fileInfo, "文件上传失败");
        assertNotNull(fileInfo.getUrl(), "文件URL不应为空");
        assertEquals(fileName, fileInfo.getOriginalFilename(), "文件名不匹配");

        System.out.println("✅ MinIO 文件上传成功，URL: " + fileInfo.getUrl());
    }

    @Test
    @Order(4)
    void testUploadLargeFile() {
        Assumptions.assumeTrue(minioAvailable, "MinIO 服务不可用，跳过测试");

        FileStorageService service = fileClient.getClient("test-minio");

        // 创建 1MB 的测试文件
        int size = 1024 * 1024; // 1MB
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (i % 256);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        String fileName = "large-test-" + UUID.randomUUID() + ".bin";

        FileInfo fileInfo = service.of(inputStream)
                .setOriginalFilename(fileName)
                .setPath("large/")
                .upload();

        assertNotNull(fileInfo, "大文件上传失败");
        assertEquals(size, fileInfo.getSize(), "文件大小不匹配");

        System.out.println("✅ 大文件上传成功，大小: " + (size / 1024) + "KB");
    }

    @Test
    @Order(5)
    void testInvalidEndpoint() {
        StorageInfo invalidInfo = new StorageInfo();
        invalidInfo.setId(UUID.randomUUID().toString());
        invalidInfo.setKey("invalid-minio");
        invalidInfo.setType("minio");

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", "http://invalid-endpoint:9000");
        properties.put("bucket", "test");
        properties.put("accessKey", "invalid");
        properties.put("secretKey", "invalid");
        invalidInfo.setProperties(properties);

        // 无效端点应该能创建存储配置（延迟连接）
        FileStorageService service = fileClient.createFileStorageService(invalidInfo);
        // 创建应该成功，但实际操作会失败
        // 这里只验证不会抛出异常
        System.out.println("✅ 无效端点配置处理正常");
    }

    @Test
    @Order(6)
    void testMissingCredentials() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("no-creds-minio");
        info.setType("minio");

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", MINIO_ENDPOINT);
        properties.put("bucket", MINIO_BUCKET);
        // 缺少 accessKey 和 secretKey
        info.setProperties(properties);

        // 缺少凭证的配置应该能创建，但功能受限
        assertDoesNotThrow(() -> fileClient.createFileStorageService(info));

        System.out.println("✅ 缺少凭证配置处理正常");
    }

    // ==================== 辅助方法 ====================

    private static boolean checkMinioConnection() {
        try {
            io.minio.MinioClient minioClient = io.minio.MinioClient.builder()
                    .endpoint(MINIO_ENDPOINT)
                    .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                    .build();

            // 尝试列出 buckets 来验证连接
            minioClient.listBuckets();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static StorageInfo createMinioStorageInfo() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-minio");
        info.setName("测试MinIO存储");
        info.setType("minio");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", MINIO_ENDPOINT);
        properties.put("bucket", MINIO_BUCKET);
        properties.put("accessKey", MINIO_ACCESS_KEY);
        properties.put("secretKey", MINIO_SECRET_KEY);
        properties.put("basePath", "");
        info.setProperties(properties);

        return info;
    }
}
