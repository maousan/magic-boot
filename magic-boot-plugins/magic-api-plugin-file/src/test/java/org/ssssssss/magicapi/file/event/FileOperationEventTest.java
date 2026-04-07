package org.ssssssss.magicapi.file.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.ssssssss.magicapi.file.model.SysFile;
import org.ssssssss.magicapi.file.service.SysFileService;
import org.springframework.context.ApplicationEventPublisher;

/**
 * FileOperationEvent 测试类
 * 测试文件操作事件的发布和监听
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileOperationEventTest {

    @Mock
    private SysFileService sysFileService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private FileOperationEventListener eventListener;

    @BeforeEach
    void setUp() {
        eventListener = new FileOperationEventListener(sysFileService);
    }

    // ==================== 事件创建测试 ====================

    @Test
    @DisplayName("1. 创建上传事件")
    void testCreateUploadEvent() {
        FileOperationEvent event = FileOperationEvent.upload(
                this,
                "minio",
                "/images/test.png",
                "test.png",
                1024L,
                "image/png",
                "http://localhost:9000/bucket/images/test.png",
                "abc123def456",
                "admin"
        );

        assertNotNull(event);
        assertEquals(FileOperationEvent.OperationType.UPLOAD, event.getOperationType());
        assertEquals("minio", event.getStorageKey());
        assertEquals("/images/test.png", event.getFilePath());
        assertEquals("test.png", event.getFileName());
        assertEquals(1024L, event.getFileSize());
        assertEquals("image/png", event.getContentType());
        assertEquals("admin", event.getOperator());

        System.out.println("✅ 创建上传事件成功");
    }

    @Test
    @DisplayName("2. 创建目录事件")
    void testCreateMkdirEvent() {
        FileOperationEvent event = FileOperationEvent.mkdir(
                this,
                "local",
                "/documents/reports/",
                "reports",
                "admin"
        );

        assertNotNull(event);
        assertEquals(FileOperationEvent.OperationType.MKDIR, event.getOperationType());
        assertEquals("local", event.getStorageKey());
        assertEquals("/documents/reports/", event.getFilePath());
        assertEquals("reports", event.getFileName());
        assertEquals(0L, event.getFileSize());  // 目录事件文件大小为 0
        assertNull(event.getContentType());

        System.out.println("✅ 创建目录事件成功");
    }

    @Test
    @DisplayName("3. 创建删除事件")
    void testCreateDeleteEvent() {
        FileOperationEvent event = FileOperationEvent.delete(
                this,
                "/images/old-file.png",
                "admin"
        );

        assertNotNull(event);
        assertEquals(FileOperationEvent.OperationType.DELETE, event.getOperationType());
        assertEquals("/images/old-file.png", event.getFilePath());
        assertEquals("admin", event.getOperator());
        assertNull(event.getStorageKey());
        assertNull(event.getFileName());

        System.out.println("✅ 创建删除事件成功");
    }

    // ==================== 事件监听器测试 ====================

    @Test
    @DisplayName("10. 监听器处理上传事件")
    void testHandleUploadEvent() {
        // 模拟 saveFileRecord 返回
        SysFile mockFile = new SysFile();
        mockFile.setId("test-file-id");
        when(sysFileService.saveFileRecord(anyString(), anyString(), anyString(), anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockFile);

        // 创建上传事件
        FileOperationEvent event = FileOperationEvent.upload(
                this,
                "minio",
                "/test/upload-test.txt",
                "upload-test.txt",
                512L,
                "text/plain",
                "http://localhost/test/upload-test.txt",
                "md5hash123",
                "test-user"
        );

        // 调用监听器
        eventListener.handleUpload(event);

        // 验证 Service 被调用
        verify(sysFileService, times(1)).saveFileRecord(
                eq("minio"),
                eq("/test/upload-test.txt"),
                eq("upload-test.txt"),
                eq(512L),
                eq("text/plain"),
                eq("http://localhost/test/upload-test.txt"),
                eq("md5hash123"),
                eq("test-user")
        );

        System.out.println("✅ 监听器处理上传事件成功");
    }

    @Test
    @DisplayName("11. 监听器处理目录事件")
    void testHandleMkdirEvent() {
        // 模拟 createDirectory 返回
        SysFile mockDir = new SysFile();
        mockDir.setId("test-dir-id");
        when(sysFileService.createDirectory(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockDir);

        // 创建目录事件
        FileOperationEvent event = FileOperationEvent.mkdir(
                this,
                "local",
                "/test/new-dir/",
                "new-dir",
                "test-user"
        );

        // 调用监听器
        eventListener.handleMkdir(event);

        // 验证 Service 被调用
        verify(sysFileService, times(1)).createDirectory(
                eq("local"),
                eq("/test/"),
                eq("new-dir"),
                eq("test-user")
        );

        System.out.println("✅ 监听器处理目录事件成功");
    }

    @Test
    @DisplayName("12. 监听器处理删除事件")
    void testHandleDeleteEvent() {
        // 模拟 deleteByPath 返回
        when(sysFileService.deleteByPath(any(), anyString(), anyString()))
                .thenReturn(true);

        // 创建删除事件
        FileOperationEvent event = FileOperationEvent.delete(
                this,
                "/test/delete-me.txt",
                "test-user"
        );

        // 调用监听器
        eventListener.handleDelete(event);

        // 验证 Service 被调用
        verify(sysFileService, times(1)).deleteByPath(
                isNull(),
                eq("/test/delete-me.txt"),
                eq("test-user")
        );

        System.out.println("✅ 监听器处理删除事件成功");
    }

    @Test
    @DisplayName("13. 监听器处理删除不存在的文件")
    void testHandleDeleteEventNotFound() {
        // 模拟 deleteByPath 返回 false
        when(sysFileService.deleteByPath(any(), anyString(), anyString()))
                .thenReturn(false);

        // 创建删除事件
        FileOperationEvent event = FileOperationEvent.delete(
                this,
                "/test/non-existent.txt",
                "test-user"
        );

        // 调用监听器
        eventListener.handleDelete(event);

        // 验证 Service 被调用
        verify(sysFileService, times(1)).deleteByPath(
                isNull(),
                eq("/test/non-existent.txt"),
                eq("test-user")
        );

        System.out.println("✅ 监听器处理删除不存在的文件成功");
    }

    // ==================== 事件发布器测试 ====================

    @Test
    @DisplayName("20. 发布器发布上传事件")
    void testPublisherUpload() {
        FileEventPublisher publisher = new FileEventPublisher(eventPublisher);

        publisher.publishUploadEvent(
                "minio",
                "/test/publish-test.txt",
                "publish-test.txt",
                256L,
                "text/plain",
                "http://localhost/test/publish-test.txt",
                "hash456",
                "admin"
        );

        verify(eventPublisher, times(1)).publishEvent(any(FileOperationEvent.class));

        System.out.println("✅ 发布器发布上传事件成功");
    }

    @Test
    @DisplayName("21. 发布器发布目录事件")
    void testPublisherMkdir() {
        FileEventPublisher publisher = new FileEventPublisher(eventPublisher);

        publisher.publishMkdirEvent(
                "local",
                "/test/publish-dir/",
                "publish-dir",
                "admin"
        );

        verify(eventPublisher, times(1)).publishEvent(any(FileOperationEvent.class));

        System.out.println("✅ 发布器发布目录事件成功");
    }

    @Test
    @DisplayName("22. 发布器发布删除事件")
    void testPublisherDelete() {
        FileEventPublisher publisher = new FileEventPublisher(eventPublisher);

        publisher.publishDeleteEvent(
                "/test/publish-delete.txt",
                "admin"
        );

        verify(eventPublisher, times(1)).publishEvent(any(FileOperationEvent.class));

        System.out.println("✅ 发布器发布删除事件成功");
    }

    // ==================== 边界测试 ====================

    @Test
    @DisplayName("30. 监听器忽略不相关的事件类型")
    void testListenerIgnoresOtherEventTypes() {
        // 创建一个上传事件
        FileOperationEvent uploadEvent = FileOperationEvent.upload(
                this, "local", "/test.txt", "test.txt",
                100L, "text/plain", "http://localhost/test.txt", null, "user"
        );

        // 调用删除处理器（应该忽略）
        eventListener.handleDelete(uploadEvent);

        // 验证 Service 未被调用
        verify(sysFileService, never()).deleteByPath(anyString(), anyString());

        System.out.println("✅ 监听器正确忽略不相关的事件类型");
    }

    @Test
    @DisplayName("31. 监听器处理异常情况")
    void testListenerHandlesException() {
        // 模拟 Service 抛出异常
        when(sysFileService.saveFileRecord(anyString(), anyString(), anyString(), anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        FileOperationEvent event = FileOperationEvent.upload(
                this, "local", "/test/error.txt", "error.txt",
                100L, "text/plain", "http://localhost/error.txt", null, "user"
        );

        // 调用监听器（不应抛出异常）
        assertDoesNotThrow(() -> eventListener.handleUpload(event),
                "监听器应捕获异常不抛出");

        System.out.println("✅ 监听器正确处理异常");
    }
}
