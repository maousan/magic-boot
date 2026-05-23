package org.ssssssss.magicboot.pf4j.frontend;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PluginStaticResourceController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class PluginStaticResourceControllerTest {

    @Mock
    private PluginManager pluginManager;

    @Mock
    private PluginWrapper pluginWrapper;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private HttpServletRequest request;

    private PluginStaticResourceController controller;

    @BeforeEach
    void setUp() {
        controller = new PluginStaticResourceController(pluginManager);
    }

    @Test
    @DisplayName("插件不存在时应返回 404")
    void serveStaticResource_whenPluginNotExists_shouldReturn404() {
        when(pluginManager.getPlugin("non-existent")).thenReturn(null);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "non-existent", request
        );

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("插件没有前端扩展但资源存在时应返回静态资源")
    void serveStaticResource_whenPluginHasNoFrontend_shouldReturnResource() {
        InputStream mockStream = new ByteArrayInputStream("<html></html>".getBytes());

        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/pda/index.html");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/pda/index.html")).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
    }

    @Test
    @DisplayName("请求路径无效时应返回 400")
    void serveStaticResource_whenInvalidPath_shouldReturn400() {
        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/invalid/path");

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("路径穿越攻击应返回 400")
    void serveStaticResource_whenPathTraversalAttack_shouldReturn400() {
        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/../secret.txt");

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("反斜杠路径攻击应返回 400")
    void serveStaticResource_whenBackslashInPath_shouldReturn400() {
        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/..\\secret.txt");

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("资源不存在时应返回 404")
    void serveStaticResource_whenResourceNotExists_shouldReturn404() {
        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/console.js");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/console.js")).thenReturn(null);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("有效 JS 资源应正确返回")
    void serveStaticResource_whenValidJsResource_shouldReturnCorrectly() {
        InputStream mockStream = new ByteArrayInputStream("console.log('test');".getBytes());

        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/console.js");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/console.js")).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.parseMediaType("application/javascript"), response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("应设置缓存控制头")
    void serveStaticResource_shouldSetCacheControlHeader() {
        InputStream mockStream = new ByteArrayInputStream("test".getBytes());

        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/console.js");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/console.js")).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        String cacheControl = response.getHeaders().getCacheControl();
        assertNotNull(cacheControl);
        assertTrue(cacheControl.contains("public"));
        assertTrue(cacheControl.contains("max-age=31536000"));
    }

    @ParameterizedTest
    @CsvSource({
        "console.js, application/javascript",
        "styles.css, text/css",
        "index.html, text/html",
        "data.json, application/json",
        "image.png, image/png",
        "photo.jpg, image/jpeg",
        "photo.jpeg, image/jpeg",
        "icon.svg, image/svg+xml",
        "font.woff, font/woff2",
        "font.woff2, font/woff2",
        "file.bin, application/octet-stream"
    })
    @DisplayName("不同文件类型应返回正确的 Content-Type")
    void serveStaticResource_shouldReturnCorrectContentType(String filename, String expectedContentType) {
        InputStream mockStream = new ByteArrayInputStream("test".getBytes());

        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/" + filename);
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/" + filename)).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(200, response.getStatusCode().value());
        MediaType actualType = response.getHeaders().getContentType();
        assertNotNull(actualType);
        assertTrue(actualType.toString().startsWith(expectedContentType.split(";")[0]),
            "Expected " + expectedContentType + " but got " + actualType);
    }

    @Test
    @DisplayName("子目录资源应正确加载")
    void serveStaticResource_whenResourceInSubdirectory_shouldLoadCorrectly() {
        InputStream mockStream = new ByteArrayInputStream("img data".getBytes());

        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/assets/logo.png");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/assets/logo.png")).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    @DisplayName("深层嵌套路径应正确处理")
    void serveStaticResource_whenDeepNestedPath_shouldHandleCorrectly() {
        InputStream mockStream = new ByteArrayInputStream("nested".getBytes());

        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/demo-plugin/static/a/b/c/d/file.js");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/a/b/c/d/file.js")).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "demo-plugin", request
        );

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("插件 ID 包含特殊字符时应正确处理")
    void serveStaticResource_whenPluginIdHasSpecialChars_shouldHandleCorrectly() {
        InputStream mockStream = new ByteArrayInputStream("test".getBytes());

        when(pluginManager.getPlugin("my-demo-plugin_v2")).thenReturn(pluginWrapper);
        when(request.getRequestURI()).thenReturn("/plugin/my-demo-plugin_v2/static/console.js");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(classLoader);
        when(classLoader.getResourceAsStream("static/console.js")).thenReturn(mockStream);

        ResponseEntity<InputStreamResource> response = controller.serveStaticResource(
            "my-demo-plugin_v2", request
        );

        assertEquals(200, response.getStatusCode().value());
    }

}
