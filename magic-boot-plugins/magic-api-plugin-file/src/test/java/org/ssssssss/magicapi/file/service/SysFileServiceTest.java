package org.ssssssss.magicapi.file.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.ssssssss.magicapi.file.MagicApiFileTestApplication;
import org.ssssssss.magicapi.file.model.SysFile;

/**
 * SysFileService 测试类
 * 测试文件元数据的 CRUD 功能
 */
@SpringBootTest(classes = MagicApiFileTestApplication.class)
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysFileServiceTest {

    @Autowired
    private SysFileService sysFileService;

    private static final String TEST_STORAGE_KEY = "test-storage";
    private static String testFileId;
    private static String testDirId;

    @BeforeAll
    static void setUp() {
        System.out.println("========================================");
        System.out.println("SysFileService 测试开始");
        System.out.println("========================================");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("========================================");
        System.out.println("SysFileService 测试结束");
        System.out.println("========================================");
    }

    // ==================== 创建测试 ====================

    @Test
    @Order(1)
    @DisplayName("1. 保存文件记录")
    void testSaveFileRecord() {
        String filePath = "/test/unit-test-" + System.currentTimeMillis() + ".txt";

        SysFile sysFile = sysFileService.saveFileRecord(
                TEST_STORAGE_KEY,
                filePath,
                "unit-test.txt",
                1024L,
                "text/plain",
                "http://localhost:8089/files" + filePath,
                "d41d8cd98f00b204e9800998ecf8427e",
                "test-user"
        );

        assertNotNull(sysFile, "文件记录不应为空");
        assertNotNull(sysFile.getId(), "ID不应为空");
        assertEquals(filePath, sysFile.getFilePath(), "文件路径应匹配");
        assertEquals("unit-test.txt", sysFile.getFileName(), "文件名应匹配");
        assertEquals(SysFile.TYPE_FILE, sysFile.getFileType(), "类型应为FILE");
        assertEquals(1024L, sysFile.getFileSize(), "文件大小应匹配");
        assertEquals("text/plain", sysFile.getContentType(), "内容类型应匹配");
        assertEquals("txt", sysFile.getFileExt(), "扩展名应为txt");

        testFileId = sysFile.getId();
        System.out.println("✅ 保存文件记录成功: id=" + testFileId);
    }

    @Test
    @Order(2)
    @DisplayName("2. 创建目录记录")
    void testCreateDirectory() {
        String parentPath = "/test/";
        String dirName = "unit-dir-" + System.currentTimeMillis();

        SysFile sysFile = sysFileService.createDirectory(
                TEST_STORAGE_KEY,
                parentPath,
                dirName,
                "test-user"
        );

        assertNotNull(sysFile, "目录记录不应为空");
        assertNotNull(sysFile.getId(), "ID不应为空");
        assertTrue(sysFile.getFilePath().endsWith("/"), "目录路径应以/结尾");
        assertEquals(dirName, sysFile.getFileName(), "目录名应匹配");
        assertEquals(SysFile.TYPE_DIR, sysFile.getFileType(), "类型应为DIR");
        assertEquals(0L, sysFile.getFileSize(), "目录大小应为0");

        testDirId = sysFile.getId();
        System.out.println("✅ 创建目录记录成功: id=" + testDirId + ", path=" + sysFile.getFilePath());
    }

    // ==================== 查询测试 ====================

    @Test
    @Order(10)
    @DisplayName("10. 根据路径查询文件")
    void testGetByPath() {
        // 先创建一个测试文件
        String filePath = "/test/query-test-" + System.currentTimeMillis() + ".txt";
        sysFileService.saveFileRecord(
                TEST_STORAGE_KEY, filePath, "query-test.txt",
                512L, "text/plain", "http://localhost" + filePath, null, "test-user"
        );

        SysFile found = sysFileService.getByPath(filePath);

        assertNotNull(found, "应能查询到文件");
        assertEquals(filePath, found.getFilePath(), "路径应匹配");
        System.out.println("✅ 根据路径查询成功: " + filePath);
    }

    @Test
    @Order(11)
    @DisplayName("11. 根据父路径查询子文件列表")
    void testListByParentPath() {
        // 先创建几个测试文件
        String parentPath = "/test-list-" + System.currentTimeMillis() + "/";
        for (int i = 0; i < 3; i++) {
            sysFileService.saveFileRecord(
                    TEST_STORAGE_KEY, parentPath + "file" + i + ".txt",
                    "file" + i + ".txt", 100L, "text/plain",
                    "http://localhost" + parentPath + "file" + i + ".txt",
                    null, "test-user"
            );
        }

        List<SysFile> files = sysFileService.listByParentPath(parentPath);

        assertNotNull(files, "文件列表不应为空");
        assertTrue(files.size() >= 3, "应至少有3个文件");
        System.out.println("✅ 查询子文件列表成功: count=" + files.size());
    }

    @Test
    @Order(12)
    @DisplayName("12. 根据MD5查询文件")
    void testGetByMd5() {
        String md5 = "md5-test-" + UUID.randomUUID().toString().substring(0, 8);
        String filePath = "/test/md5-test-" + System.currentTimeMillis() + ".txt";

        sysFileService.saveFileRecord(
                TEST_STORAGE_KEY, filePath, "md5-test.txt",
                256L, "text/plain", "http://localhost" + filePath,
                md5, "test-user"
        );

        SysFile found = sysFileService.getByMd5(md5);

        assertNotNull(found, "应能通过MD5查询到文件");
        assertEquals(md5, found.getMd5(), "MD5应匹配");
        System.out.println("✅ 根据MD5查询成功: md5=" + md5);
    }

    @Test
    @Order(13)
    @DisplayName("13. 检查文件是否存在")
    void testExistsByPath() {
        String filePath = "/test/exists-test-" + System.currentTimeMillis() + ".txt";

        // 不存在时
        boolean existsBefore = sysFileService.existsByPath(filePath);
        assertFalse(existsBefore, "文件不应存在");

        // 创建后
        sysFileService.saveFileRecord(
                TEST_STORAGE_KEY, filePath, "exists-test.txt",
                128L, "text/plain", "http://localhost" + filePath,
                null, "test-user"
        );

        boolean existsAfter = sysFileService.existsByPath(filePath);
        assertTrue(existsAfter, "文件应存在");

        System.out.println("✅ 文件存在检查成功");
    }

    @Test
    @Order(14)
    @DisplayName("14. 查询不存在的路径")
    void testGetByPathNotFound() {
        SysFile found = sysFileService.getByPath("/non/existent/path/" + System.currentTimeMillis());
        assertNull(found, "不存在的路径应返回null");
        System.out.println("✅ 查询不存在的路径返回null");
    }

    // ==================== 删除测试 ====================

    @Test
    @Order(20)
    @DisplayName("20. 逻辑删除文件记录")
    void testDeleteByPath() {
        String filePath = "/test/delete-test-" + System.currentTimeMillis() + ".txt";

        // 先创建
        sysFileService.saveFileRecord(
                TEST_STORAGE_KEY, filePath, "delete-test.txt",
                64L, "text/plain", "http://localhost" + filePath,
                null, "test-user"
        );

        // 确认存在
        assertTrue(sysFileService.existsByPath(filePath), "文件应存在");

        // 删除
        boolean deleted = sysFileService.deleteByPath(filePath, "test-user");
        assertTrue(deleted, "删除应成功");

        // 确认已删除（逻辑删除后不应能查到）
        assertFalse(sysFileService.existsByPath(filePath), "文件不应存在");

        System.out.println("✅ 逻辑删除成功: " + filePath);
    }

    @Test
    @Order(21)
    @DisplayName("21. 删除不存在的文件")
    void testDeleteNonExistent() {
        boolean deleted = sysFileService.deleteByPath(
                "/non/existent/" + System.currentTimeMillis() + ".txt", "test-user"
        );
        assertFalse(deleted, "删除不存在的文件应返回false");
        System.out.println("✅ 删除不存在的文件返回false");
    }

    // ==================== 更新测试 ====================

    @Test
    @Order(30)
    @DisplayName("30. 更新文件记录")
    void testUpdateFileRecord() {
        String filePath = "/test/update-test-" + System.currentTimeMillis() + ".txt";

        // 先创建
        SysFile created = sysFileService.saveFileRecord(
                TEST_STORAGE_KEY, filePath, "update-test.txt",
                100L, "text/plain", "http://localhost" + filePath,
                null, "test-user"
        );

        // 更新
        created.setFileSize(200L);
        created.setUrl("http://localhost:8080/updated" + filePath);
        boolean updated = sysFileService.updateById(created);

        assertTrue(updated, "更新应成功");

        // 验证
        SysFile found = sysFileService.getByPath(filePath);
        assertNotNull(found, "应能查到更新的记录");
        assertEquals(200L, found.getFileSize(), "文件大小应已更新");

        System.out.println("✅ 更新文件记录成功");
    }

    // ==================== 边界测试 ====================

    @Test
    @Order(40)
    @DisplayName("40. 创建同名目录（幂等性测试）")
    void testCreateDuplicateDirectory() {
        String parentPath = "/test/";
        String dirName = "dup-dir-" + System.currentTimeMillis();

        // 第一次创建
        SysFile first = sysFileService.createDirectory(
                TEST_STORAGE_KEY, parentPath, dirName, "test-user"
        );
        assertNotNull(first, "第一次创建应成功");

        // 第二次创建同名目录
        SysFile second = sysFileService.createDirectory(
                TEST_STORAGE_KEY, parentPath, dirName, "test-user"
        );
        assertNotNull(second, "第二次创建应返回已存在的目录");
        assertEquals(first.getId(), second.getId(), "应返回同一条记录");

        System.out.println("✅ 同名目录幂等性测试通过");
    }

    @Test
    @Order(41)
    @DisplayName("41. 空MD5查询")
    void testGetByEmptyMd5() {
        SysFile found = sysFileService.getByMd5(null);
        assertNull(found, "null MD5应返回null");

        found = sysFileService.getByMd5("");
        assertNull(found, "空MD5应返回null");

        System.out.println("✅ 空MD5查询测试通过");
    }
}
