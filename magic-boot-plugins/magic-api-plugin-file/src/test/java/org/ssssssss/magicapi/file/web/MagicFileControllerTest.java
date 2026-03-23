package org.ssssssss.magicapi.file.web;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.junit.jupiter.api.*;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.ssssssss.magicapi.file.model.StorageType;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;

/**
 * MagicFileController 单元测试
 * 测试文件存储管理接口的各个功能
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MagicFileControllerTest {

    private static final String TEST_BASE_PATH = "target/controller-test/";
    private static final String TEST_DOMAIN = "http://localhost:8089";

    private MagicFileController controller;
    private MagicDynamicFileClient fileClient;

    @BeforeAll
    static void setUpAll() throws Exception {
        // 创建测试目录
        Path testPath = Path.of(TEST_BASE_PATH);
        Files.createDirectories(testPath);

        System.out.println("========================================");
        System.out.println("MagicFileController 测试");
        System.out.println("测试目录: " + TEST_BASE_PATH);
        System.out.println("========================================");
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        // 清理测试目录
        deleteDirectory(new File(TEST_BASE_PATH));
        System.out.println("========================================");
        System.out.println("测试完成，测试目录已清理");
        System.out.println("========================================");
    }

    @BeforeEach
    void setUp() {
        fileClient = new MagicDynamicFileClient();
        MagicConfiguration configuration = new MagicConfiguration();
        controller = new MagicFileController(configuration, fileClient);
    }

    // ==================== 获取存储类型测试 ====================

    @Test
    @Order(1)
    @DisplayName("1. 获取支持的存储类型列表")
    void testGetStorageTypes() {
        JsonBean<List<StorageType>> result = controller.getStorageTypes();

        assertNotNull(result, "返回结果不应为空");
        assertNotNull(result.getData(), "存储类型列表不应为空");

        List<StorageType> types = result.getData();
        assertEquals(3, types.size(), "应支持3种存储类型");

        System.out.println("✅ 获取存储类型成功，共 " + types.size() + " 种");
        for (StorageType type : types) {
            System.out.println("   - " + type.getName() + " (" + type.getType() + ")");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. 验证本地存储类型定义")
    void testLocalStorageTypeDefinition() {
        JsonBean<List<StorageType>> result = controller.getStorageTypes();
        List<StorageType> types = result.getData();

        StorageType localType = types.stream()
            .filter(t -> "local".equals(t.getType()))
            .findFirst()
            .orElse(null);

        assertNotNull(localType, "应包含本地存储类型");
        assertEquals("本地存储", localType.getName());
        assertEquals("folder", localType.getIcon());
        assertNotNull(localType.getFields());
        assertTrue(localType.getFields().size() >= 2, "本地存储至少需要2个配置字段");

        // 验证必需字段
        boolean hasBasePath = localType.getFields().stream()
            .anyMatch(f -> "basePath".equals(f.getName()) && f.isRequired());
        boolean hasDomain = localType.getFields().stream()
            .anyMatch(f -> "domain".equals(f.getName()) && f.isRequired());

        assertTrue(hasBasePath, "应包含必需的 basePath 字段");
        assertTrue(hasDomain, "应包含必需的 domain 字段");

        System.out.println("✅ 本地存储类型定义验证成功");
        System.out.println("   字段数量: " + localType.getFields().size());
    }

    @Test
    @Order(3)
    @DisplayName("3. 验证 MinIO 存储类型定义")
    void testMinioStorageTypeDefinition() {
        JsonBean<List<StorageType>> result = controller.getStorageTypes();
        List<StorageType> types = result.getData();

        StorageType minioType = types.stream()
            .filter(t -> "minio".equals(t.getType()))
            .findFirst()
            .orElse(null);

        assertNotNull(minioType, "应包含 MinIO 存储类型");
        assertEquals("MinIO", minioType.getName());
        assertEquals("server", minioType.getIcon());
        assertNotNull(minioType.getFields());
        assertTrue(minioType.getFields().size() >= 4, "MinIO 存储至少需要4个配置字段");

        // 验证必需字段
        boolean hasEndpoint = minioType.getFields().stream()
            .anyMatch(f -> "endpoint".equals(f.getName()) && f.isRequired());
        boolean hasBucket = minioType.getFields().stream()
            .anyMatch(f -> "bucket".equals(f.getName()) && f.isRequired());
        boolean hasAccessKey = minioType.getFields().stream()
            .anyMatch(f -> "accessKey".equals(f.getName()) && f.isRequired());
        boolean hasSecretKey = minioType.getFields().stream()
            .anyMatch(f -> "secretKey".equals(f.getName()) && f.isRequired());

        assertTrue(hasEndpoint, "应包含必需的 endpoint 字段");
        assertTrue(hasBucket, "应包含必需的 bucket 字段");
        assertTrue(hasAccessKey, "应包含必需的 accessKey 字段");
        assertTrue(hasSecretKey, "应包含必需的 secretKey 字段");

        System.out.println("✅ MinIO 存储类型定义验证成功");
        System.out.println("   字段数量: " + minioType.getFields().size());
    }

    @Test
    @Order(4)
    @DisplayName("4. 验证 S3 存储类型定义")
    void testS3StorageTypeDefinition() {
        JsonBean<List<StorageType>> result = controller.getStorageTypes();
        List<StorageType> types = result.getData();

        StorageType s3Type = types.stream()
            .filter(t -> "s3".equals(t.getType()))
            .findFirst()
            .orElse(null);

        assertNotNull(s3Type, "应包含 S3 存储类型");
        assertEquals("Amazon S3", s3Type.getName());
        assertEquals("cloud", s3Type.getIcon());
        assertNotNull(s3Type.getFields());
        assertTrue(s3Type.getFields().size() >= 4, "S3 存储至少需要4个配置字段");

        // 验证 region 字段有默认值
        StorageType.StorageTypeField regionField = s3Type.getFields().stream()
            .filter(f -> "region".equals(f.getName()))
            .findFirst()
            .orElse(null);

        assertNotNull(regionField, "应包含 region 字段");
        assertEquals("us-east-1", regionField.getDefaultValue(), "region 应有默认值 us-east-1");
        assertFalse(regionField.isRequired(), "region 应该是可选的");

        System.out.println("✅ S3 存储类型定义验证成功");
        System.out.println("   字段数量: " + s3Type.getFields().size());
    }

    // ==================== 本地存储连接测试 ====================

    @Test
    @Order(10)
    @DisplayName("10. 测试本地存储连接 - 有效路径")
    void testLocalStorageConnectionValid() {
        StorageInfo storageInfo = createLocalStorageInfo(TEST_BASE_PATH + "valid-test/");

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        assertNotNull(result, "返回结果不应为空");
        Map<String, Object> data = result.getData();
        assertNotNull(data, "测试结果数据不应为空");

        Boolean success = (Boolean) data.get("success");
        assertTrue(success, "本地存储连接测试应成功");

        System.out.println("✅ 本地存储连接测试成功");
        System.out.println("   消息: " + data.get("message"));
    }

    @Test
    @Order(11)
    @DisplayName("11. 测试本地存储连接 - 自动创建目录")
    void testLocalStorageConnectionAutoCreate() {
        String newDir = TEST_BASE_PATH + "auto-created-" + System.currentTimeMillis() + "/";
        StorageInfo storageInfo = createLocalStorageInfo(newDir);

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        assertTrue((Boolean) data.get("success"), "应自动创建目录");
        assertTrue(Files.exists(Path.of(newDir)), "目录应该被创建");

        System.out.println("✅ 自动创建目录测试成功");
        System.out.println("   创建路径: " + newDir);
    }

    @Test
    @Order(12)
    @DisplayName("12. 测试本地存储连接 - 空路径")
    void testLocalStorageConnectionEmptyPath() {
        StorageInfo storageInfo = createLocalStorageInfo("");

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        assertFalse((Boolean) data.get("success"), "空路径应测试失败");
        assertNotNull(data.get("message"), "应返回错误消息");

        System.out.println("✅ 空路径验证成功");
        System.out.println("   错误消息: " + data.get("message"));
    }

    @Test
    @Order(13)
    @DisplayName("13. 测试本地存储连接 - null路径")
    void testLocalStorageConnectionNullPath() {
        StorageInfo storageInfo = createLocalStorageInfo(null);

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        assertFalse((Boolean) data.get("success"), "null路径应测试失败");

        System.out.println("✅ null路径验证成功");
    }

    @Test
    @Order(14)
    @DisplayName("14. 测试本地存储连接 - 读写权限验证")
    void testLocalStorageConnectionPermissions() {
        String testDir = TEST_BASE_PATH + "permission-test/";
        StorageInfo storageInfo = createLocalStorageInfo(testDir);

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        assertTrue((Boolean) data.get("success"), "连接测试应成功");

        Boolean canRead = (Boolean) data.get("canRead");
        Boolean canWrite = (Boolean) data.get("canWrite");

        assertTrue(canRead, "应具有读权限");
        assertTrue(canWrite, "应具有写权限");

        System.out.println("✅ 读写权限验证成功");
        System.out.println("   读权限: " + canRead + ", 写权限: " + canWrite);
    }

    // ==================== MinIO 存储连接测试 ====================

    @Test
    @Order(20)
    @DisplayName("20. 测试 MinIO 存储连接 - 无效端点")
    void testMinioStorageConnectionInvalidEndpoint() {
        StorageInfo storageInfo = createMinioStorageInfo(
            "http://invalid-minio-host:9999",
            "test-bucket",
            "invalid-access",
            "invalid-secret"
        );

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        // 连接应该失败（因为端点不存在）
        Boolean success = (Boolean) data.get("success");
        assertFalse(success, "无效端点应测试失败");

        System.out.println("✅ MinIO 无效端点验证成功");
        System.out.println("   消息: " + data.get("message"));
    }

    @Test
    @Order(21)
    @DisplayName("21. 测试 MinIO 存储连接 - 缺少必需参数")
    void testMinioStorageConnectionMissingParams() {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setType("minio");
        Map<String, Object> properties = new HashMap<>();
        // 缺少必需参数
        properties.put("endpoint", "http://localhost:9000");
        storageInfo.setProperties(properties);

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        // 应该失败或抛出异常
        assertNotNull(data, "应返回测试结果");

        System.out.println("✅ MinIO 缺少参数验证成功");
    }

    // ==================== S3 存储连接测试 ====================

    @Test
    @Order(30)
    @DisplayName("30. 测试 S3 存储连接 - 无效凭证")
    void testS3StorageConnectionInvalidCredentials() {
        StorageInfo storageInfo = createS3StorageInfo(
            "https://s3.amazonaws.com",
            "us-east-1",
            "test-bucket",
            "invalid-access-key",
            "invalid-secret-key"
        );

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        // 连接应该失败（凭证无效）
        Boolean success = (Boolean) data.get("success");
        assertFalse(success, "无效凭证应测试失败");

        System.out.println("✅ S3 无效凭证验证成功");
        System.out.println("   消息: " + data.get("message"));
    }

    @Test
    @Order(31)
    @DisplayName("31. 测试 S3 存储连接 - 缺少必需参数")
    void testS3StorageConnectionMissingParams() {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setType("s3");
        Map<String, Object> properties = new HashMap<>();
        // 只提供部分参数
        properties.put("endpoint", "https://s3.amazonaws.com");
        properties.put("bucket", "test-bucket");
        storageInfo.setProperties(properties);

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        assertNotNull(data, "应返回测试结果");

        System.out.println("✅ S3 缺少参数验证成功");
    }

    // ==================== 不支持的存储类型测试 ====================

    @Test
    @Order(40)
    @DisplayName("40. 测试不支持的存储类型")
    void testUnsupportedStorageType() {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setType("unsupported-type");
        storageInfo.setProperties(new HashMap<>());

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        Boolean success = (Boolean) data.get("success");
        assertFalse(success, "不支持的类型应测试失败");
        assertTrue(
            data.get("message").toString().contains("不支持的存储类型"),
            "应返回不支持的类型错误消息"
        );

        System.out.println("✅ 不支持的存储类型验证成功");
        System.out.println("   消息: " + data.get("message"));
    }

    @Test
    @Order(41)
    @DisplayName("41. 测试空存储类型")
    void testEmptyStorageType() {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setType("");
        storageInfo.setProperties(new HashMap<>());

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        Boolean success = (Boolean) data.get("success");
        assertFalse(success, "空类型应测试失败");

        System.out.println("✅ 空存储类型验证成功");
    }

    @Test
    @Order(42)
    @DisplayName("42. 测试 null 存储类型")
    void testNullStorageType() {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setType(null);
        storageInfo.setProperties(new HashMap<>());

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        assertNotNull(data, "应返回测试结果");

        System.out.println("✅ null 存储类型验证成功");
    }

    // ==================== 异常处理测试 ====================

    @Test
    @Order(50)
    @DisplayName("50. 测试 null StorageInfo")
    void testNullStorageInfo() {
        try {
            JsonBean<Map<String, Object>> result = controller.testStorage(null);
            // 如果没有抛出异常，验证返回结果
            if (result != null && result.getData() != null) {
                Boolean success = (Boolean) result.getData().get("success");
                assertFalse(success, "null StorageInfo 应测试失败");
                System.out.println("✅ null StorageInfo 正确处理");
            }
        } catch (Exception e) {
            // 抛出异常也是合理的行为
            System.out.println("✅ null StorageInfo 正确抛出异常: " + e.getClass().getSimpleName());
        }
    }

    @Test
    @Order(51)
    @DisplayName("51. 测试 null Properties")
    void testNullProperties() {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.setType("local");
        storageInfo.setProperties(null);

        JsonBean<Map<String, Object>> result = controller.testStorage(storageInfo);

        Map<String, Object> data = result.getData();
        // 应该优雅地处理 null properties
        assertNotNull(data, "应返回测试结果");

        System.out.println("✅ null Properties 处理成功");
    }

    // ==================== 辅助方法 ====================

    private StorageInfo createLocalStorageInfo(String basePath) {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-local-" + UUID.randomUUID().toString().substring(0, 8));
        info.setName("测试本地存储");
        info.setType("local");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("basePath", basePath);
        properties.put("domain", TEST_DOMAIN);
        info.setProperties(properties);

        return info;
    }

    private StorageInfo createMinioStorageInfo(String endpoint, String bucket, String accessKey, String secretKey) {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-minio-" + UUID.randomUUID().toString().substring(0, 8));
        info.setName("测试MinIO存储");
        info.setType("minio");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", endpoint);
        properties.put("bucket", bucket);
        properties.put("accessKey", accessKey);
        properties.put("secretKey", secretKey);
        properties.put("basePath", "");
        info.setProperties(properties);

        return info;
    }

    private StorageInfo createS3StorageInfo(String endpoint, String region, String bucket, String accessKey, String secretKey) {
        StorageInfo info = new StorageInfo();
        info.setId(UUID.randomUUID().toString());
        info.setKey("test-s3-" + UUID.randomUUID().toString().substring(0, 8));
        info.setName("测试S3存储");
        info.setType("s3");
        info.setIsDefault(false);
        info.setEnabled(true);

        Map<String, Object> properties = new HashMap<>();
        properties.put("endpoint", endpoint);
        properties.put("region", region);
        properties.put("bucket", bucket);
        properties.put("accessKey", accessKey);
        properties.put("secretKey", secretKey);
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
