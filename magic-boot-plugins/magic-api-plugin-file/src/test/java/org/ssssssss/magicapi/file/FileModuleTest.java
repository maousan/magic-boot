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
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.get.RemoteFileInfo;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;

/**
 * FileModule 测试用例
 * 测试 file 模块的各个方法
 *
 * 注意：部分功能需要实现 FileRecorder 接口才能使用
 * 参考：https://x-file-storage.xuyanwu.cn/2.2.0/#/基础功能?id=保存上传记录
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileModuleTest {

    private static final String TEST_BASE_PATH = "target/file-module-test/";
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
        System.out.println("FileModule 测试");
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

    @Test
    @Order(1)
    @DisplayName("1. 上传文件 - upload(file)")
    void testUploadFile() {
        String content = "Hello, FileModule Test! " + System.currentTimeMillis();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        var result = fileModule.upload(bytes);

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getUrl(), "文件URL不应为空");
        assertEquals((long) bytes.length, result.getFileSize(), "文件大小应匹配");

        System.out.println("✅ upload(file) 成功: " + result.getUrl());
    }

    @Test
    @Order(2)
    @DisplayName("2. 上传文件到指定路径 - upload(path, file)")
    void testUploadFileWithPath() {
        String content = "File with path test";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String path = "test-folder/";

        var result = fileModule.upload(path, bytes);

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getFilePath(), "文件路径不应为空");
        assertNotNull(result.getUrl(), "URL不应为空");

        System.out.println("✅ upload(path, file) 成功: " + result.getUrl());
    }

    @Test
    @Order(3)
    @DisplayName("3. 上传文件并指定文件名 - upload(path, file, fileName)")
    void testUploadFileWithName() {
        String content = "File with custom name";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String path = "custom-name/";
        String fileName = "custom-test-file.txt";

        var result = fileModule.upload(path, bytes, fileName);

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getUrl(), "URL不应为空");
        // 注意：byte[] 上传时 getFileName() 可能为 null

        System.out.println("✅ upload(path, file, fileName) 成功: " + result.getUrl());
    }

    @Test
    @Order(4)
    @DisplayName("4. 上传 InputStream")
    void testUploadInputStream() {
        String content = "InputStream test content";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        var result = fileModule.upload("stream-test/", inputStream, "stream-test.txt");

        assertNotNull(result, "上传结果不应为空");
        assertNotNull(result.getUrl(), "URL不应为空");

        System.out.println("✅ 上传 InputStream 成功: " + result.getUrl());
    }

    @Test
    @Order(5)
    @DisplayName("5. 创建文件夹 - 通过上传目录标记")
    void testCreateFolder() {
        // 创建文件夹的方式：上传空的 .folder 标记文件
        String folderPath = "test-folder-" + System.currentTimeMillis() + "/";

        // 创建空的字节数组作为目录标记
        byte[] emptyBytes = new byte[0];
        ByteArrayInputStream emptyStream = new ByteArrayInputStream(emptyBytes);

        // 上传 .folder 标记文件
        var result = fileModule.upload(folderPath, emptyStream, ".folder");

        assertNotNull(result, "创建文件夹结果不应为空");
        assertNotNull(result.getUrl(), "文件夹URL不应为空");
        assertTrue(result.getFilePath().contains(folderPath), "路径应包含文件夹路径");

        System.out.println("✅ 创建文件夹成功: " + folderPath + " -> " + result.getUrl());
    }

    @Test
    @Order(6)
    @DisplayName("6. 创建嵌套文件夹")
    void testCreateNestedFolder() {
        // 创建嵌套文件夹
        String nestedPath = "parent/child/grandchild-" + System.currentTimeMillis() + "/";

        byte[] emptyBytes = new byte[0];
        ByteArrayInputStream emptyStream = new ByteArrayInputStream(emptyBytes);

        var result = fileModule.upload(nestedPath, emptyStream, ".folder");

        assertNotNull(result, "创建嵌套文件夹结果不应为空");
        assertTrue(result.getFilePath().contains("parent/child/"), "路径应包含嵌套结构");

        System.out.println("✅ 创建嵌套文件夹成功: " + nestedPath);
    }

    @Test
    @Order(7)
    @DisplayName("7. 切换存储源 - use()")
    void testUseStorage() {
        // 注册第二个存储
        StorageInfo secondStorage = createLocalStorageInfo("secondary-storage", false);
        secondStorage.getProperties().put("basePath", TEST_BASE_PATH + "secondary/");
        FileStorageService secondService = fileClient.createFileStorageService(secondStorage);
        fileClient.put(UUID.randomUUID().toString(), "secondary-storage", "第二存储", secondService);

        // 使用 use() 切换存储
        FileModule secondaryModule = fileModule.use("secondary-storage");

        assertNotNull(secondaryModule, "切换存储应成功");

        // 在第二存储上传文件
        String content = "Secondary storage content";
        var result = secondaryModule.upload("secondary-test/", content.getBytes(StandardCharsets.UTF_8), "test.txt");

        assertNotNull(result, "在第二存储上传应成功");

        System.out.println("✅ use() 切换存储成功: " + result.getUrl());
    }

    @Test
    @Order(8)
    @DisplayName("8. 获取默认存储")
    void testGetDefaultStorage() {
        String defaultKey = fileClient.getDefaultKey();
        assertNotNull(defaultKey, "默认存储Key不应为空");

        FileStorageService defaultService = fileClient.getClient("");
        assertNotNull(defaultService, "获取默认存储失败");

        System.out.println("✅ 默认存储: " + defaultKey);
    }

    @Test
    @Order(9)
    @DisplayName("9. 存储非空检查")
    void testIsNotEmpty() {
        assertFalse(fileClient.isEmpty(), "存储不应为空");
        System.out.println("✅ 存储非空验证成功");
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

    // ==================== 需要 FileRecorder 的测试 ====================
    // 以下测试需要实现 FileRecorder 接口才能使用

    @Test
    @Order(10)
    @DisplayName("10. 判断文件是否存在 - exists()")
    @Disabled("需要实现 FileRecorder 接口")
    void testFileExists() {
        // 需要 FileRecorder 支持
    }

    @Test
    @Order(11)
    @DisplayName("11. 获取文件信息 - info()")
    @Disabled("需要实现 FileRecorder 接口")
    void testGetFileInfo() {
        // 需要 FileRecorder 支持
    }

    @Test
    @Order(12)
    @DisplayName("12. 列出文件 - list()")
    void testListFiles() {
        // list() 在某些存储实现中可能不需要 FileRecorder
        String folderPath = "list-test/";

        List<RemoteFileInfo> files = fileModule.list(folderPath);

        // 如果不支持 list，返回空列表而不是 null
        assertNotNull(files, "文件列表不应为null");

        System.out.println("✅ list() 调用成功，返回 " + files.size() + " 个文件");
    }

    @Test
    @Order(13)
    @DisplayName("13. 下载文件 - download()")
    @Disabled("需要实现 FileRecorder 接口")
    void testDownloadFile() {
        // 需要 FileRecorder 支持
    }

    @Test
    @Order(14)
    @DisplayName("14. 下载文件为 InputStream - downloadAsStream()")
    @Disabled("需要实现 FileRecorder 接口")
    void testDownloadAsStream() {
        // 需要 FileRecorder 支持
    }

    @Test
    @Order(15)
    @DisplayName("15. 移动文件 - move()")
    @Disabled("需要实现 FileRecorder 接口")
    void testMoveFile() {
        // 需要 FileRecorder 支持
    }

    @Test
    @Order(16)
    @DisplayName("16. 复制文件 - copy()")
    @Disabled("需要实现 FileRecorder 接口")
    void testCopyFile() {
        // 需要 FileRecorder 支持
    }

    @Test
    @Order(17)
    @DisplayName("17. 获取文件URL - getUrl()")
    @Disabled("需要实现 FileRecorder 接口")
    void testGetFileUrl() {
        // 需要 FileRecorder 支持
    }

    @Test
    @Order(18)
    @DisplayName("18. 删除文件 - delete()")
    @Disabled("需要实现 FileRecorder 接口")
    void testDeleteFile() {
        // 需要 FileRecorder 支持
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
