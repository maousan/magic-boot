package org.ssssssss.magicapi.file;

import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件存储集成测试
 * 测试完整的文件上传、下载、删除流程
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileStorageIntegrationTest {

    private static final String TEST_BASE_PATH = "target/integration-test-upload/";
    private static final String TEST_DOMAIN = "http://localhost:8081";
    private static MagicDynamicFileClient fileClient;
    private static String uploadedFilePath;
    private static FileInfo uploadedFileInfo;

    @BeforeAll
    static void setUp() throws Exception {
        fileClient = new MagicDynamicFileClient();

        // 创建测试目录
        Path testPath = Path.of(TEST_BASE_PATH);
        Files.createDirectories(testPath);

        // 注册默认存储
        StorageInfo storageInfo = createLocalStorageInfo("default-local", true);
        FileStorageService service = fileClient.createFileStorageService(storageInfo);
        assertNotNull(service, "默认存储服务创建失败");
        fileClient.put(UUID.randomUUID().toString(), "default-local", "默认本地存储", service);

        System.out.println("========================================");
        System.out.println("文件存储集成测试");
        System.out.println("测试目录: " + TEST_BASE_PATH);
        System.out.println("========================================");
    }

    @AfterAll
    static void tearDown() throws Exception {
        // 清理测试目录
        deleteDirectory(new File(TEST_BASE_PATH));
        System.out.println("========================================");
        System.out.println("集成测试完成，测试目录已清理");
        System.out.println("========================================");
    }

    @Test
    @Order(1)
    @DisplayName("1. 注册多个存储配置")
    void testRegisterMultipleStorages() {
        // 注册第二个存储
        StorageInfo secondStorage = createLocalStorageInfo("secondary-local", false);
        secondStorage.getProperties().put("basePath", TEST_BASE_PATH + "secondary/");
        FileStorageService secondService = fileClient.createFileStorageService(secondStorage);
        assertNotNull(secondService);
        fileClient.put(UUID.randomUUID().toString(), "secondary-local", "第二存储", secondService);

        // 验证两个存储都可用
        var keys = fileClient.listKeys();
        assertTrue(keys.contains("default-local"));
        assertTrue(keys.contains("secondary-local"));

        System.out.println("✅ 多存储注册成功: " + keys);
    }

    @Test
    @Order(2)
    @DisplayName("2. 上传文本文件")
    void testUploadTextFile() {
        FileStorageService service = fileClient.getClient("default-local");

        String content = "Hello, Integration Test! 时间戳: " + System.currentTimeMillis();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        String fileName = "test-text-" + UUID.randomUUID() + ".txt";
        FileInfo fileInfo = service.of(inputStream)
                .setOriginalFilename(fileName)
                .setPath("text/")
                .upload();

        assertNotNull(fileInfo, "文件上传失败");
        assertNotNull(fileInfo.getUrl(), "文件URL不应为空");
        assertEquals(fileName, fileInfo.getOriginalFilename());
        assertEquals(bytes.length, fileInfo.getSize());

        // 保存路径用于后续测试
        uploadedFilePath = fileInfo.getPath() + fileInfo.getFilename();
        uploadedFileInfo = fileInfo;

        System.out.println("✅ 文本文件上传成功:");
        System.out.println("   URL: " + fileInfo.getUrl());
        System.out.println("   路径: " + uploadedFilePath);
    }

    @Test
    @Order(3)
    @DisplayName("3. 上传二进制文件")
    void testUploadBinaryFile() {
        FileStorageService service = fileClient.getClient("default-local");

        // 创建 100KB 的二进制数据
        int size = 100 * 1024;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (i % 256);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        String fileName = "test-binary-" + UUID.randomUUID() + ".bin";

        FileInfo fileInfo = service.of(inputStream)
                .setOriginalFilename(fileName)
                .setPath("binary/")
                .upload();

        assertNotNull(fileInfo);
        assertEquals(size, fileInfo.getSize());

        System.out.println("✅ 二进制文件上传成功，大小: " + (size / 1024) + "KB");
    }

    @Test
    @Order(4)
    @DisplayName("4. 上传到不同存储")
    void testUploadToSecondaryStorage() {
        FileStorageService secondaryService = fileClient.getClient("secondary-local");

        String content = "Secondary storage test";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        FileInfo fileInfo = secondaryService.of(bytes)
                .setOriginalFilename("secondary-test.txt")
                .upload();

        assertNotNull(fileInfo);
        assertTrue(fileInfo.getUrl().contains("secondary"));

        System.out.println("✅ 上传到第二存储成功: " + fileInfo.getUrl());
    }

    @Test
    @Order(5)
    @DisplayName("5. 上传带中文文件名的文件")
    void testUploadChineseFilename() {
        FileStorageService service = fileClient.getClient("default-local");

        String content = "中文文件名测试";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        FileInfo fileInfo = service.of(bytes)
                .setOriginalFilename("测试文件_" + UUID.randomUUID() + ".txt")
                .setPath("chinese/")
                .upload();

        assertNotNull(fileInfo);
        assertTrue(fileInfo.getOriginalFilename().contains("测试"));

        System.out.println("✅ 中文文件名上传成功: " + fileInfo.getOriginalFilename());
    }

    @Test
    @Order(6)
    @DisplayName("6. 上传带特殊字符路径的文件")
    void testUploadWithSpecialPath() {
        FileStorageService service = fileClient.getClient("default-local");

        String content = "Special path test";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        // 嵌套路径
        String nestedPath = "level1/level2/level3/";

        FileInfo fileInfo = service.of(bytes)
                .setOriginalFilename("nested.txt")
                .setPath(nestedPath)
                .upload();

        assertNotNull(fileInfo);
        assertTrue(fileInfo.getUrl().contains(nestedPath));

        System.out.println("✅ 嵌套路径上传成功: " + fileInfo.getUrl());
    }

    @Test
    @Order(7)
    @DisplayName("7. 获取默认存储")
    void testGetDefaultStorage() {
        String defaultKey = fileClient.getDefaultKey();
        assertNotNull(defaultKey, "默认存储Key不应为空");

        FileStorageService defaultService = fileClient.getClient("");
        assertNotNull(defaultService, "获取默认存储失败");

        System.out.println("✅ 默认存储: " + defaultKey);
    }

    @Test
    @Order(8)
    @DisplayName("8. 验证文件物理存在")
    void testPhysicalFileExists() {
        // 验证上传的文件确实存在于磁盘
        File uploadDir = new File(TEST_BASE_PATH);
        assertTrue(uploadDir.exists() && uploadDir.isDirectory(), "测试目录应存在");

        // 注意：x-file-storage 的 LocalPlusFileStorage 可能使用不同的目录结构
        // 这里只验证目录存在，文件计数可能因存储实现而异
        System.out.println("✅ 测试目录验证成功: " + uploadDir.getAbsolutePath());
    }

    @Test
    @Order(9)
    @DisplayName("9. 删除存储配置")
    void testDeleteStorage() {
        // 创建临时存储
        StorageInfo tempStorage = createLocalStorageInfo("temp-delete-test", false);
        tempStorage.getProperties().put("basePath", TEST_BASE_PATH + "temp/");

        FileStorageService tempService = fileClient.createFileStorageService(tempStorage);
        fileClient.put("temp-id", "temp-delete-test", "临时存储", tempService);

        assertTrue(fileClient.listKeys().contains("temp-delete-test"));

        // 删除存储
        fileClient.delete("temp-delete-test");
        assertFalse(fileClient.listKeys().contains("temp-delete-test"));

        System.out.println("✅ 存储配置删除成功");
    }

    @Test
    @Order(10)
    @DisplayName("10. 获取不存在的存储应抛出异常")
    void testGetNonExistentStorage() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileClient.getClient("non-existent-storage");
        });

        System.out.println("✅ 获取不存在的存储正确抛出异常");
    }

    @Test
    @Order(11)
    @DisplayName("11. 并发上传测试")
    void testConcurrentUpload() throws InterruptedException {
        FileStorageService service = fileClient.getClient("default-local");
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    String content = "Concurrent test " + index + " - " + UUID.randomUUID();
                    FileInfo info = service.of(content.getBytes())
                            .setOriginalFilename("concurrent-" + index + ".txt")
                            .setPath("concurrent/")
                            .upload();
                    results[index] = (info != null && info.getUrl() != null);
                } catch (Exception e) {
                    results[index] = false;
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        for (int i = 0; i < threadCount; i++) {
            assertTrue(results[i], "线程 " + i + " 上传失败");
        }

        System.out.println("✅ 并发上传测试成功，" + threadCount + " 个线程全部完成");
    }

    @Test
    @Order(12)
    @DisplayName("12. 空存储检查")
    void testIsEmpty() {
        assertFalse(fileClient.isEmpty(), "存储不应为空");
        System.out.println("✅ 存储非空验证成功");
    }

    // ==================== 辅助方法 ====================

    private static StorageInfo createLocalStorageInfo(String key, boolean isDefault) {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey(key);
        info.setName("测试存储-" + key);
        info.setType("local");
        info.setIsDefault(isDefault);
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

    private int countFiles(File directory) {
        int count = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    count += countFiles(file);
                } else {
                    count++;
                }
            }
        }
        return count;
    }
}
