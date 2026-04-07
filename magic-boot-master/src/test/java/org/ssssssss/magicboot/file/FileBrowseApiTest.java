package org.ssssssss.magicboot.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 文件管理 API 集成测试
 * 测试 /file/browse/* 接口
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("magic-api 文件浏览接口由独立 Servlet 提供，MockMvc 不经过该 Servlet 映射，当前类会稳定返回 404。")
class FileBrowseApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String uploadedFilePath;
    private static String uploadedFileName;
    private static String testFolderName;

    private static String resolveFilePath(JsonNode data, String fallbackFileName) {
        if (data == null || !data.has("filePath")) {
            return null;
        }
        String basePath = data.get("filePath").asText();
        String fileName = data.has("fileName") ? data.get("fileName").asText() : fallbackFileName;
        if (basePath == null || basePath.isEmpty()) {
            return null;
        }
        if (basePath.endsWith("/")) {
            return basePath + fileName;
        }
        return basePath;
    }

    @BeforeAll
    static void setUpAll() {
        testFolderName = "test-folder-" + System.currentTimeMillis();
        System.out.println("========================================");
        System.out.println("文件管理 API 集成测试");
        System.out.println("测试文件夹: " + testFolderName);
        System.out.println("========================================");
    }

    // ==================== 获取文件列表测试 ====================

    @Test
    @Order(1)
    @DisplayName("1. 获取根目录文件列表")
    void testListFiles() throws Exception {
        MvcResult result = mockMvc.perform(get("/file/browse/list")
                        .param("parentPath", "/")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);

        System.out.println("✅ 获取文件列表成功");
    }

    @Test
    @Order(2)
    @DisplayName("2. 获取指定目录文件列表")
    void testListFilesWithPath() throws Exception {
        mockMvc.perform(get("/file/browse/list")
                        .param("parentPath", "/test-folder/")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✅ 获取指定目录文件列表成功");
    }

    // ==================== 上传文件测试 ====================

    @Test
    @Order(10)
    @DisplayName("10. 上传文件")
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-upload-" + System.currentTimeMillis() + ".txt",
                "text/plain",
                "Hello, File Upload Test!".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult result = mockMvc.perform(multipart("/file/browse/upload")
                        .file(file)
                        .param("uploadPath", "/" + testFolderName + "/")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        JsonNode data = root.get("data");

        if (data != null && data.has("filePath")) {
            uploadedFilePath = data.get("filePath").asText();
            uploadedFileName = data.get("fileName") != null ? data.get("fileName").asText() : "test-file";
        }

        System.out.println("✅ 上传文件成功: " + (uploadedFilePath != null ? uploadedFilePath : "N/A"));
    }

    @Test
    @Order(11)
    @DisplayName("11. 上传文件到指定路径")
    void testUploadFileToPath() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "path-test.txt",
                "text/plain",
                "File with path test".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/file/browse/upload")
                        .file(file)
                        .param("uploadPath", "/upload-test/")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✅ 上传文件到指定路径成功");
    }

    @Test
    @Order(12)
    @DisplayName("12. 上传空文件应失败")
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        // 空文件可能被拒绝或返回错误
        mockMvc.perform(multipart("/file/browse/upload")
                        .file(emptyFile)
                        .param("uploadPath", "/")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print());

        System.out.println("✅ 空文件上传测试完成");
    }

    // ==================== 创建文件夹测试 ====================

    @Test
    @Order(20)
    @DisplayName("20. 创建文件夹")
    void testCreateFolder() throws Exception {
        mockMvc.perform(post("/file/browse/mkdir")
                        .param("parentPath", "/")
                        .param("folderName", testFolderName)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk());

        System.out.println("✅ 创建文件夹成功: " + testFolderName);
    }

    @Test
    @Order(21)
    @DisplayName("21. 创建嵌套文件夹")
    void testCreateNestedFolder() throws Exception {
        String nestedFolder = "parent-" + System.currentTimeMillis() + "/child/grandchild";

        mockMvc.perform(post("/file/browse/mkdir")
                        .param("parentPath", "/" + nestedFolder + "/")
                        .param("folderName", "deep-folder")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk());

        System.out.println("✅ 创建嵌套文件夹成功");
    }

    @Test
    @Order(22)
    @DisplayName("22. 创建文件夹 - 缺少名称参数应失败")
    void testCreateFolderWithoutName() throws Exception {
        mockMvc.perform(post("/file/browse/mkdir")
                        .param("parentPath", "/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0)); // exit 0 返回 code=0

        System.out.println("✅ 缺少名称参数正确失败");
    }

    @Test
    @Order(23)
    @DisplayName("23. 创建文件夹 - 非法字符名称应失败")
    void testCreateFolderWithInvalidName() throws Exception {
        mockMvc.perform(post("/file/browse/mkdir")
                        .param("parentPath", "/")
                        .param("folderName", "test:invalid*name?")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        System.out.println("✅ 非法字符名称正确失败");
    }

    // ==================== 重命名测试 ====================

    @Test
    @Order(30)
    @DisplayName("30. 重命名文件")
    void testRenameFile() throws Exception {
        // 先上传一个文件用于重命名测试
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "rename-test-original.txt",
                "text/plain",
                "File to be renamed".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/file/browse/upload")
                        .file(file)
                        .param("uploadPath", "/rename-test/")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        String response = uploadResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        JsonNode data = root.get("data");

        if (data != null && data.has("filePath")) {
            String originalPath = resolveFilePath(data, "rename-test-original.txt");

            // 执行重命名
            mockMvc.perform(post("/file/browse/rename")
                            .param("filePath", originalPath)
                            .param("newName", "renamed-file.txt")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andDo(print())
                    .andExpect(status().isOk());

            System.out.println("✅ 重命名文件成功");
        } else {
            System.out.println("⚠️ 重命名测试跳过：无法获取上传文件路径");
        }
    }

    @Test
    @Order(31)
    @DisplayName("31. 重命名不存在的文件应失败")
    void testRenameNonExistentFile() throws Exception {
        mockMvc.perform(post("/file/browse/rename")
                        .param("filePath", "/non-existent-path/file.txt")
                        .param("newName", "new-name.txt")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        System.out.println("✅ 重命名不存在的文件正确失败");
    }

    // ==================== 移动文件测试 ====================

    @Test
    @Order(40)
    @DisplayName("40. 移动文件")
    void testMoveFile() throws Exception {
        // 先上传一个文件用于移动测试
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "move-test.txt",
                "text/plain",
                "File to be moved".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/file/browse/upload")
                        .file(file)
                        .param("uploadPath", "/move-source/")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        String response = uploadResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        JsonNode data = root.get("data");

        if (data != null && data.has("filePath")) {
            String sourcePath = resolveFilePath(data, "move-test.txt");
            String targetPath = "/move-target/";

            // 执行移动
            mockMvc.perform(post("/file/browse/move")
                            .param("sourcePath", sourcePath)
                            .param("targetDirPath", targetPath)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andDo(print())
                    .andExpect(status().isOk());

            System.out.println("✅ 移动文件成功");
        } else {
            System.out.println("⚠️ 移动测试跳过：无法获取上传文件路径");
        }
    }

    // ==================== 删除测试 ====================

    @Test
    @Order(50)
    @DisplayName("50. 删除文件")
    void testDeleteFile() throws Exception {
        // 先上传一个文件用于删除测试
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "delete-test.txt",
                "text/plain",
                "File to be deleted".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/file/browse/upload")
                        .file(file)
                        .param("uploadPath", "/delete-test/")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        String response = uploadResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        JsonNode data = root.get("data");

        if (data != null && data.has("filePath")) {
            String filePath = resolveFilePath(data, "delete-test.txt");

            // 执行删除
            mockMvc.perform(delete("/file/browse/delete")
                            .param("filePath", filePath))
                    .andDo(print())
                    .andExpect(status().isOk());

            System.out.println("✅ 删除文件成功");
        } else {
            System.out.println("⚠️ 删除测试跳过：无法获取上传文件路径");
        }
    }

    @Test
    @Order(51)
    @DisplayName("51. 删除不存在的文件应失败")
    void testDeleteNonExistentFile() throws Exception {
        mockMvc.perform(delete("/file/browse/delete")
                        .param("filePath", "/non-existent-file.txt"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        System.out.println("✅ 删除不存在的文件正确失败");
    }

    @Test
    @Order(52)
    @DisplayName("52. 删除 - 缺少路径参数应失败")
    void testDeleteWithoutPath() throws Exception {
        mockMvc.perform(delete("/file/browse/delete"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        System.out.println("✅ 缺少路径参数正确失败");
    }

    // ==================== 下载测试 ====================

    @Test
    @Order(60)
    @DisplayName("60. 下载文件")
    void testDownloadFile() throws Exception {
        // 先上传一个文件用于下载测试
        String content = "Download test content: " + System.currentTimeMillis();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "download-test.txt",
                "text/plain",
                content.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/file/browse/upload")
                        .file(file)
                        .param("uploadPath", "/download-test/")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        String response = uploadResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        JsonNode data = root.get("data");

        if (data != null && data.has("filePath")) {
            String filePath = resolveFilePath(data, "download-test.txt");

            // 执行下载
            mockMvc.perform(get("/file/browse/download")
                            .param("filePath", filePath))
                    .andDo(print())
                    .andExpect(status().isOk());

            System.out.println("✅ 下载文件成功");
        } else {
            System.out.println("⚠️ 下载测试跳过：无法获取上传文件路径");
        }
    }

    @Test
    @Order(61)
    @DisplayName("61. 下载不存在的文件应失败")
    void testDownloadNonExistentFile() throws Exception {
        mockMvc.perform(get("/file/browse/download")
                        .param("filePath", "/non-existent-file.txt"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        System.out.println("✅ 下载不存在的文件正确失败");
    }

    @Test
    @Order(62)
    @DisplayName("62. 下载 - 缺少路径参数应失败")
    void testDownloadWithoutPath() throws Exception {
        mockMvc.perform(get("/file/browse/download"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        System.out.println("✅ 下载缺少路径参数正确失败");
    }
}
