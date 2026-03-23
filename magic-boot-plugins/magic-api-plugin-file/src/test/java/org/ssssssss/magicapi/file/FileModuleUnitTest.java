package org.ssssssss.magicapi.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.get.RemoteFileInfo;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;

/**
 * 文件管理 API 单元测试
 * 测试 FileModule 的核心功能，不需要完整的 Spring Boot 上下文
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileModuleUnitTest {

    private static final String TEST_BASE_PATH = "target/file-api-test/";
    private static final String TEST_DOMAIN = "http://localhost:8089";
    private static MagicDynamicFileClient fileClient;
    private static FileModule fileModule;

    @BeforeAll
    static void setUp() throws Exception {
        fileClient = new MagicDynamicFileClient();

        // 创建测试目录
        Path testPath = Path.of(TEST_BASE_PATH);
        Files.createDirectories(testPath);

        // 注册测试存储
        StorageInfo storageInfo = createLocalStorageInfo("test-storage", true);
        FileStorageService service = fileClient.createFileStorageService(storageInfo);
        assertNotNull(service, "存储服务创建失败");
        fileClient.put(UUID.randomUUID().toString(), "test-storage", "测试存储", service);

        // 创建 FileModule 实例
        fileModule = new FileModule(fileClient);

        System.out.println("========================================");
        System.out.println("文件管理 API 单元测试");
        System.out.println("测试目录: " + TEST_BASE_PATH);
        System.out.println("========================================");
    }

    @AfterAll
    static void tearDown() throws Exception {
        deleteDirectory(new File(TEST_BASE_PATH));
        System.out.println("========================================");
        System.out.println("测试完成，测试目录已清理");
        System.out.println("========================================");
    }

    // ==================== 上传文件测试 ====================

    @Test
    @Order(1)
    @DisplayName("1. 上传文件 - 基本上传")
    void testUploadBasicFile() {
        String content = "Test file content for API";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        var result = fileModule.upload(bytes);

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getUrl(), "文件URL不应为空");
        assertEquals((long) bytes.length, result.getFileSize(), "文件大小应匹配");

        System.out.println("✅ 基本上传成功: " + result.getUrl());
    }

    @Test
    @Order(2)
    @DisplayName("2. 上传文件到指定路径")
    void testUploadFileToPath() {
        String content = "File with specific path";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String path = "api-test-folder/";

        var result = fileModule.upload(path, bytes);

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getFilePath(), "文件路径不应为空");
        assertTrue(result.getFilePath().contains(path), "路径应包含指定文件夹");

        System.out.println("✅ 上传到指定路径成功: " + result.getUrl());
    }

    @Test
    @Order(3)
    @DisplayName("3. 上传文件并指定文件名")
    void testUploadFileWithName() {
        String content = "File with custom name";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String path = "custom-name/";
        String fileName = "api-test-file.txt";

        var result = fileModule.upload(path, bytes, fileName);

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getUrl(), "URL不应为空");

        System.out.println("✅ 上传并指定文件名成功: " + result.getUrl());
    }

    @Test
    @Order(4)
    @DisplayName("4. 上传 InputStream")
    void testUploadInputStream() {
        String content = "InputStream upload test";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        var result = fileModule.upload("stream-folder/", inputStream, "stream-file.txt");

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getUrl(), "URL不应为空");

        System.out.println("✅ 上传 InputStream 成功: " + result.getUrl());
    }

    // ==================== 创建文件夹测试 ====================

    @Test
    @Order(10)
    @DisplayName("10. 创建文件夹 - 通过上传 .folder 标记")
    void testCreateFolder() {
        String folderPath = "api-test-folder-" + System.currentTimeMillis() + "/";

        // 创建空的字节数组作为目录标记
        byte[] emptyBytes = new byte[0];
        ByteArrayInputStream emptyStream = new ByteArrayInputStream(emptyBytes);

        // 上传 .folder 标记文件
        var result = fileModule.upload(folderPath, emptyStream, ".folder");

        assertNotNull(result, "创建文件夹结果不应为空");
        assertNotNull(result.getUrl(), "文件夹URL不应为空");
        assertTrue(result.getFilePath().contains(folderPath), "路径应包含文件夹路径");

        System.out.println("✅ 创建文件夹成功: " + folderPath);
    }

    @Test
    @Order(11)
    @DisplayName("11. 创建嵌套文件夹")
    void testCreateNestedFolder() {
        String nestedPath = "api-parent/api-child/api-grandchild-" + System.currentTimeMillis() + "/";

        byte[] emptyBytes = new byte[0];
        ByteArrayInputStream emptyStream = new ByteArrayInputStream(emptyBytes);

        var result = fileModule.upload(nestedPath, emptyStream, ".folder");

        assertNotNull(result, "创建嵌套文件夹结果不应为空");
        assertTrue(result.getFilePath().contains("api-parent/api-child/"), "路径应包含嵌套结构");

        System.out.println("✅ 创建嵌套文件夹成功: " + nestedPath);
    }

    // ==================== 列出文件测试 ====================

    @Test
    @Order(20)
    @DisplayName("20. 列出文件")
    void testListFiles() {
        // 先上传几个文件
        String folderPath = "list-api-test/";
        for (int i = 0; i < 3; i++) {
            String content = "List test file " + i;
            fileModule.upload(folderPath, content.getBytes(StandardCharsets.UTF_8), "file-" + i + ".txt");
        }

        List<RemoteFileInfo> files = fileModule.list(folderPath);

        assertNotNull(files, "文件列表不应为null");

        System.out.println("✅ 列出文件成功，找到 " + files.size() + " 个文件");
    }

    // ==================== 切换存储源测试 ====================

    @Test
    @Order(30)
    @DisplayName("30. 切换存储源")
    void testSwitchStorage() {
        // 注册第二个存储
        StorageInfo secondStorage = createLocalStorageInfo("api-secondary-storage", false);
        secondStorage.getProperties().put("basePath", TEST_BASE_PATH + "secondary/");
        FileStorageService secondService = fileClient.createFileStorageService(secondStorage);
        fileClient.put(UUID.randomUUID().toString(), "api-secondary-storage", "第二存储", secondService);

        // 切换到第二存储
        FileModule secondaryModule = fileModule.use("api-secondary-storage");

        assertNotNull(secondaryModule, "切换存储应成功");

        // 在第二存储上传文件
        String content = "Secondary storage file";
        var result = secondaryModule.upload("secondary-test/", content.getBytes(StandardCharsets.UTF_8), "test.txt");

        assertNotNull(result, "在第二存储上传应成功");

        System.out.println("✅ 切换存储源成功: " + result.getUrl());
    }

    // ==================== 存储管理测试 ====================

    @Test
    @Order(40)
    @DisplayName("40. 获取默认存储")
    void testGetDefaultStorage() {
        String defaultKey = fileClient.getDefaultKey();
        assertNotNull(defaultKey, "默认存储Key不应为空");

        FileStorageService defaultService = fileClient.getClient("");
        assertNotNull(defaultService, "获取默认存储失败");

        System.out.println("✅ 默认存储: " + defaultKey);
    }

    @Test
    @Order(41)
    @DisplayName("41. 存储非空检查")
    void testStorageNotEmpty() {
        assertFalse(fileClient.isEmpty(), "存储不应为空");
        System.out.println("✅ 存储非空验证成功");
    }

    @Test
    @Order(42)
    @DisplayName("42. 获取不存在的存储应抛出异常")
    void testGetNonExistentStorage() {
        assertThrows(IllegalArgumentException.class, () -> {
            fileClient.getClient("non-existent-storage");
        });

        System.out.println("✅ 获取不存在的存储正确抛出异常");
    }

    @Test
    @Order(43)
    @DisplayName("43. 列出所有存储 Key")
    void testListStorageKeys() {
        var keys = fileClient.listKeys();

        assertNotNull(keys, "Key列表不应为空");
        assertFalse(keys.isEmpty(), "应至少有一个存储");

        System.out.println("✅ 存储列表: " + keys);
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
}
