package org.ssssssss.magicboot.pf4j.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.ssssssss.magicboot.plugin.api.frontend.PluginMenuItem;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PluginFrontendController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class PluginFrontendControllerTest {

    @Mock
    private FrontendExtensionProcessor processor;

    @Mock
    private PluginManager pluginManager;

    @Mock
    private PluginWrapper pluginWrapper;

    private PluginFrontendController controller;

    @BeforeEach
    void setUp() {
        controller = new PluginFrontendController(processor, pluginManager);
    }

    @Test
    @DisplayName("获取所有元数据应返回正确格式")
    void getAllMetadata_shouldReturnCorrectFormat() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "演示插件");
        when(processor.getAllMetadata()).thenReturn(Collections.singletonList(metadata));

        Map<String, Object> result = controller.getAllMetadata();

        assertEquals(1, result.get("total"));
        Collection<?> plugins = (Collection<?>) result.get("plugins");
        assertEquals(1, plugins.size());
    }

    @Test
    @DisplayName("获取单个插件元数据应返回正确数据")
    void getMetadata_whenPluginExists_shouldReturnMetadata() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "演示插件");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        Object result = controller.getMetadata("demo-plugin");

        assertEquals(metadata, result);
    }

    @Test
    @DisplayName("获取不存在的插件元数据应返回错误信息")
    void getMetadata_whenPluginNotExists_shouldReturnError() {
        when(processor.getMetadata("non-existent")).thenReturn(null);

        Object result = controller.getMetadata("non-existent");

        assertTrue(result instanceof Map);
        Map<?, ?> errorMap = (Map<?, ?>) result;
        assertEquals("Plugin not found or has no frontend", errorMap.get("error"));
        assertEquals("non-existent", errorMap.get("pluginId"));
    }

    @Test
    @DisplayName("获取插件首页应返回 HTML")
    void getPluginHome_whenPluginExists_shouldReturnHtml() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "演示插件");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("demo-plugin");

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("演示插件"));
        assertTrue(html.contains("/plugin/demo-plugin/static/console.js"));
        assertTrue(html.contains("DemoPlugin")); // 全局变量名
    }

    @Test
    @DisplayName("插件不存在时应返回 404 页面")
    void getPluginHome_whenPluginNotExists_shouldReturn404Page() {
        when(processor.getMetadata("non-existent")).thenReturn(null);
        when(pluginManager.getPlugin("non-existent")).thenReturn(null);

        String html = controller.getPluginHome("non-existent");

        assertTrue(html.contains("404"));
        assertTrue(html.contains("插件不存在"));
    }

    @Test
    @DisplayName("插件没有前端扩展时应返回提示页面")
    void getPluginHome_whenPluginHasNoFrontend_shouldReturnNoFrontendPage() {
        when(processor.getMetadata("no-frontend-plugin")).thenReturn(null);
        when(pluginManager.getPlugin("no-frontend-plugin")).thenReturn(pluginWrapper);

        String html = controller.getPluginHome("no-frontend-plugin");

        assertTrue(html.contains("404"));
        assertTrue(html.contains("没有前端页面"));
    }

    @Test
    @DisplayName("子路由请求应返回相同的 HTML 容器")
    void getPluginPage_shouldReturnSameHtmlAsHome() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "演示插件");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        String homeHtml = controller.getPluginHome("demo-plugin");
        String pageHtml = controller.getPluginPage("demo-plugin");

        assertEquals(homeHtml, pageHtml);
    }

    @Test
    @DisplayName("HTML 应包含正确的插件 ID")
    void getPluginHome_shouldContainCorrectPluginId() {
        FrontendMetadata metadata = createTestMetadata("my-test-plugin", "测试插件");
        when(processor.getMetadata("my-test-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("my-test-plugin");

        assertTrue(html.contains("const pluginId = 'my-test-plugin'"));
        assertTrue(html.contains("/plugin/my-test-plugin/static/console.js"));
    }

    @Test
    @DisplayName("HTML 应包含 Vue 初始化代码")
    void getPluginHome_shouldContainVueInitCode() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "演示插件");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("demo-plugin");

        assertTrue(html.contains("Vue.createApp"));
        assertTrue(html.contains("app.mount('#plugin-container')"));
    }

    @Test
    @DisplayName("HTML 应包含错误处理代码")
    void getPluginHome_shouldContainErrorHandling() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "演示插件");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("demo-plugin");

        assertTrue(html.contains("try"));
        assertTrue(html.contains("catch"));
        assertTrue(html.contains("插件加载失败"));
    }

    @Test
    @DisplayName("HTML 应检查 Vue 依赖")
    void getPluginHome_shouldCheckVueDependency() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "演示插件");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("demo-plugin");

        assertTrue(html.contains("typeof Vue === 'undefined'"));
        assertTrue(html.contains("Vue 未加载"));
    }

    @Test
    @DisplayName("全局变量名转换应正确处理各种格式")
    void toGlobalVarName_shouldHandleVariousFormats() {
        // 通过反射测试私有方法，或者通过 HTML 输出验证

        // demo-plugin -> DemoPlugin
        FrontendMetadata metadata1 = createTestMetadata("demo-plugin", "测试");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata1);
        assertTrue(controller.getPluginHome("demo-plugin").contains("DemoPlugin"));

        // my-test-plugin -> MyTestPlugin
        FrontendMetadata metadata2 = createTestMetadata("my-test-plugin", "测试");
        when(processor.getMetadata("my-test-plugin")).thenReturn(metadata2);
        assertTrue(controller.getPluginHome("my-test-plugin").contains("MyTestPlugin"));

        // simple -> Simple
        FrontendMetadata metadata3 = createTestMetadata("simple", "测试");
        when(processor.getMetadata("simple")).thenReturn(metadata3);
        assertTrue(controller.getPluginHome("simple").contains("Simple"));
    }

    @Test
    @DisplayName("HTML 应转义特殊字符")
    void getPluginHome_shouldEscapeSpecialChars() {
        FrontendMetadata metadata = new FrontendMetadata(
            "test-plugin",
            "<script>alert('xss')</script>", // 包含 XSS 内容
            "console.js",
            new PluginRoute[0],
            new PluginMenuItem[0],
            new String[0],
            null, null
        );
        when(processor.getMetadata("test-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("test-plugin");

        // 应该被转义，不应该包含原始脚本标签
        assertFalse(html.contains("<script>alert('xss')</script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    /**
     * 创建测试用的 FrontendMetadata
     */
    private FrontendMetadata createTestMetadata(String pluginId, String displayName) {
        return new FrontendMetadata(
            pluginId,
            displayName,
            "console.js",
            new PluginRoute[]{
                new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true)
            },
            new PluginMenuItem[]{
                new PluginMenuItem("menu-1", null, "菜单", "icon", "/dashboard", 100)
            },
            new String[]{"plugin:view"},
            null, null
        );
    }
}
