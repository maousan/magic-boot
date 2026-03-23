package org.ssssssss.magicapi.file.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.dromara.x.file.storage.core.FileStorageService;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.file.model.StorageInfo;

/**
 * 存储类型定义测试
 * 测试 MagicFileController 中的存储类型定义逻辑
 */
class StorageTypeDefinitionTest {

    private MagicDynamicFileClient fileClient;

    @BeforeEach
    void setUp() {
        fileClient = new MagicDynamicFileClient();
    }

    // ==================== 存储类型定义测试 ====================

    @Test
    @Order(1)
    @DisplayName("本地存储配置创建")
    void testCreateLocalStorage() {
        StorageInfo info = createLocalStorageInfo();
        assertNotNull(info.getId());
        assertEquals("local", info.getType());
        assertEquals("/tmp/upload", info.getProperties().get("basePath"));

        System.out.println("✅ 本地存储配置创建成功");
    }

    @Test
    @Order(2)
    @DisplayName("MinIO 存储配置创建")
    void testCreateMinioStorage() {
        StorageInfo info = createMinioStorageInfo();
        assertEquals("minio", info.getType());
        assertNotNull(info.getProperties().get("endpoint"));

        System.out.println("✅ MinIO 存储配置创建成功");
    }

    @Test
    @Order(3)
    @DisplayName("S3 存储配置创建")
    void testCreateS3Storage() {
        StorageInfo info = createS3StorageInfo();
        assertEquals("s3", info.getType());
        assertNotNull(info.getProperties().get("region"));

        System.out.println("✅ S3 存储配置创建成功");
    }

    @Test
    @Order(10)
    @DisplayName("本地存储服务创建")
    void testCreateLocalStorageService() {
        StorageInfo info = createLocalStorageInfo();
        info.getProperties().put("basePath", "target/test-storage/");
        info.getProperties().put("domain", "http://localhost:8089");

        var service = fileClient.createFileStorageService(info);
        assertNotNull(service, "本地存储服务创建失败");
        assertFalse(service.getFileStorageList().isEmpty());

        System.out.println("✅ 本地存储服务创建成功");
    }

    @Test
    @Order(11)
    @DisplayName("MinIO 存储服务创建")
    void testCreateMinioStorageService() {
        StorageInfo info = createMinioStorageInfo();
        var service = fileClient.createFileStorageService(info);
        assertNotNull(service, "MinIO 存储服务创建失败");

        System.out.println("✅ MinIO 存储服务创建成功");
    }

    @Test
    @Order(12)
    @DisplayName("S3 存储服务创建")
    void testCreateS3StorageService() {
        StorageInfo info = createS3StorageInfo();
        var service = fileClient.createFileStorageService(info);
        assertNotNull(service, "S3 存储服务创建失败");

        System.out.println("✅ S3 存储服务创建成功");
    }

    @Test
    @Order(20)
    @DisplayName("多存储注册")
    void testRegisterMultipleStorages() {
        StorageInfo localInfo = createLocalStorageInfo();
        localInfo.setKey("test-local-1");
        localInfo.getProperties().put("basePath", "target/test-multi/");

        var localService = fileClient.createFileStorageService(localInfo);
        fileClient.put(UUID.randomUUID().toString(), "test-local-1", "本地存储1", localService);

        StorageInfo minioInfo = createMinioStorageInfo();
        minioInfo.setKey("test-minio-1");
        var minioService = fileClient.createFileStorageService(minioInfo);
        fileClient.put(UUID.randomUUID().toString(), "test-minio-1", "MinIO存储1", minioService);

        List<String> keys = fileClient.listKeys();
        assertTrue(keys.contains("test-local-1"));
        assertTrue(keys.contains("test-minio-1"));

        System.out.println("✅ 多存储注册成功: " + keys);
    }

    @Test
    @Order(21)
    @DisplayName("获取存储客户端")
    void testGetStorageClient() {
        StorageInfo info = createLocalStorageInfo();
        info.setKey("test-get-client");
        info.getProperties().put("basePath", "target/test-get-client/");

        var service = fileClient.createFileStorageService(info);
        fileClient.put(UUID.randomUUID().toString(), "test-get-client", "测试获取", service);

        var client = fileClient.getClient("test-get-client");
        assertNotNull(client);

        System.out.println("✅ 获取存储客户端成功");
    }

    @Test
    @Order(22)
    @DisplayName("获取不存在的存储客户端应抛出异常")
    void testGetNonExistentClient() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileClient.getClient("non-existent-key");
        });

        System.out.println("✅ 获取不存在的客户端正确抛出异常");
    }

    @Test
    @Order(23)
    @DisplayName("删除存储配置")
    void testDeleteStorage() {
        String key = "test-delete-" + UUID.randomUUID().toString().substring(0, 8);

        StorageInfo info = createLocalStorageInfo();
        info.setKey(key);
        info.getProperties().put("basePath", "target/test-delete/");

        var service = fileClient.createFileStorageService(info);
        fileClient.put(UUID.randomUUID().toString(), key, "待删除存储", service);

        assertTrue(fileClient.listKeys().contains(key));

        fileClient.delete(key);
        assertFalse(fileClient.listKeys().contains(key));

        System.out.println("✅ 删除存储配置成功");
    }

    @Test
    @Order(24)
    @DisplayName("空存储检查")
    void testIsEmpty() {
        MagicDynamicFileClient newClient = new MagicDynamicFileClient();
        assertTrue(newClient.isEmpty(), "新客户端应为空");

        StorageInfo info = createLocalStorageInfo();
        info.setKey("test-empty");
        info.getProperties().put("basePath", "target/test-empty/");

        var service = newClient.createFileStorageService(info);
        newClient.put(UUID.randomUUID().toString(), "test-empty", "测试", service);
        assertFalse(newClient.isEmpty(), "添加存储后不应为空");

        System.out.println("✅ 空存储检查成功");
    }

    // ==================== 辅助方法 ====================

    private StorageInfo createLocalStorageInfo() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-local-" + UUID.randomUUID().toString().substring(0, 8));
        info.setName("测试本地存储");
        info.setType("local");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("basePath", "/tmp/upload");
        properties.put("domain", "http://localhost:8089");
        info.setProperties(properties);

        return info;
    }

    private StorageInfo createMinioStorageInfo() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-minio-" + UUID.randomUUID().toString().substring(0, 8));
        info.setName("测试MinIO存储");
        info.setType("minio");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", "http://localhost:9000");
        properties.put("bucket", "test-bucket");
        properties.put("accessKey", "minioadmin");
        properties.put("secretKey", "minioadmin");
        properties.put("basePath", "");
        info.setProperties(properties);

        return info;
    }

    private StorageInfo createS3StorageInfo() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-s3-" + UUID.randomUUID().toString().substring(0, 8));
        info.setName("测试S3存储");
        info.setType("s3");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", "https://s3.amazonaws.com");
        properties.put("region", "us-east-1");
        properties.put("bucket", "test-bucket");
        properties.put("accessKey", "test-key");
        properties.put("secretKey", "test-secret");
        info.setProperties(properties);

        return info;
    }
}
