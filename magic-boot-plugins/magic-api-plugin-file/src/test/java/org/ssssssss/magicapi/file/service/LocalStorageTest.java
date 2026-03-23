package org.ssssssss.magicapi.file.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.platform.LocalPlusFileStorage;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.file.model.StorageInfo;

/**
 * 本地存储测试用例
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LocalStorageTest {

    private static final String TEST_BASE_PATH = "target/test-upload/";
    private static final String TEST_DOMAIN = "http://localhost:8089";
    private static MagicDynamicFileClient fileClient;
    private static String testStorageId;

    @BeforeAll
    static void setUp() throws Exception {
        fileClient = new MagicDynamicFileClient();

        // 创建测试目录
        Path testPath = Path.of(TEST_BASE_PATH);
        Files.createDirectories(testPath);

        // 创建测试存储配置
        StorageInfo storageInfo = createLocalStorageInfo();
        FileStorageService service = fileClient.createFileStorageService(storageInfo);
        assertNotNull(service, "FileStorageService 创建失败");

        testStorageId = UUID.randomUUID().toString();
        fileClient.put(testStorageId, "test-local", "测试本地存储", service);
    }

    @AfterAll
    static void tearDown() throws Exception {
        // 清理测试目录
        deleteDirectory(new File(TEST_BASE_PATH));
    }

    @Test
    @Order(1)
    void testCreateLocalStorage() {
        StorageInfo storageInfo = createLocalStorageInfo();
        FileStorageService service = fileClient.createFileStorageService(storageInfo);

        assertNotNull(service, "本地存储服务创建失败");
        assertFalse(service.getFileStorageList().isEmpty(), "存储平台列表不应为空");
        assertTrue(
            service.getFileStorageList().get(0) instanceof LocalPlusFileStorage,
            "存储平台应为 LocalPlusFileStorage"
        );

        System.out.println("✅ 本地存储服务创建成功，使用 LocalPlusFileStorage");
    }

    @Test
    @Order(2)
    void testUploadFile() {
        FileStorageService service = fileClient.getClient("test-local");
        assertNotNull(service, "获取存储服务失败");

        String content = "Hello, Local Storage Test! " + UUID.randomUUID();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        String fileName = "test-" + UUID.randomUUID() + ".txt";
        FileInfo fileInfo = service.of(inputStream).setOriginalFilename(fileName).upload();

        assertNotNull(fileInfo, "文件上传失败");
        assertNotNull(fileInfo.getUrl(), "文件URL不应为空");
        assertEquals(fileName, fileInfo.getOriginalFilename(), "文件名不匹配");
        assertTrue(fileInfo.getUrl().contains(TEST_DOMAIN), "URL应包含配置的域名");

        System.out.println("✅ 文件上传成功，URL: " + fileInfo.getUrl());
    }

    @Test
    @Order(3)
    void testUploadWithSubPath() {
        FileStorageService service = fileClient.getClient("test-local");

        String content = "Test with path " + UUID.randomUUID();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        String fileName = "subpath-test.txt";
        FileInfo fileInfo = service.of(inputStream).setOriginalFilename(fileName).setPath("subdir/2024/").upload();

        assertNotNull(fileInfo, "文件上传失败");
        assertTrue(fileInfo.getUrl().contains("subdir/2024/"), "文件路径应包含子目录");

        System.out.println("✅ 带子路径上传成功: " + fileInfo.getUrl());
    }

    @Test
    @Order(4)
    @Disabled("需要实现 FileRecorder 接口才能使用删除功能")
    void testDeleteFile() {
        // 删除文件需要 FileRecorder 接口支持
        // 这里仅验证服务可用性
        FileStorageService service = fileClient.getClient("test-local");
        assertNotNull(service, "获取存储服务失败");
        System.out.println("✅ 文件删除测试跳过（需要 FileRecorder）");
    }

    @Test
    @Order(5)
    void testInvalidConfig() {
        StorageInfo invalidInfo = new StorageInfo();
        invalidInfo.setType("local");
        invalidInfo.setProperties(new HashMap<>()); // 空配置

        // 空配置可能导致创建失败或使用默认值，验证不会抛出异常
        assertDoesNotThrow(() -> fileClient.createFileStorageService(invalidInfo));

        System.out.println("✅ 空配置处理正常");
    }

    @Test
    @Order(6)
    void testGetClientWithInvalidKey() {
        // 获取不存在的客户端应该抛出异常
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                fileClient.getClient("non-existent-key");
            },
            "获取不存在的客户端应该抛出异常"
        );

        System.out.println("✅ 无效 Key 异常测试通过");
    }

    @Test
    @Order(7)
    void testListKeys() {
        var keys = fileClient.listKeys();
        assertNotNull(keys, "Key列表不应为空");
        assertTrue(keys.contains("test-local"), "应包含测试存储key");

        System.out.println("✅ 存储 Key 列表: " + keys);
    }

    // ==================== 辅助方法 ====================

    private static StorageInfo createLocalStorageInfo() {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-local");
        info.setName("测试本地存储");
        info.setType("local");
        info.setIsDefault(true);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("basePath", TEST_BASE_PATH);
        properties.put("domain", TEST_DOMAIN);
        info.setProperties(properties);

        return info;
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
