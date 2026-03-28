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
import static org.mockito.Mockito.when;

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
    @DisplayName("getAllMetadata should return expected format")
    void getAllMetadata_shouldReturnCorrectFormat() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "Demo Plugin");
        when(processor.getAllMetadata()).thenReturn(Collections.singletonList(metadata));

        Map<String, Object> result = controller.getAllMetadata();

        assertEquals(1, result.get("total"));
        Collection<?> plugins = (Collection<?>) result.get("plugins");
        assertEquals(1, plugins.size());
    }

    @Test
    @DisplayName("getMetadata should return metadata when plugin exists")
    void getMetadata_whenPluginExists_shouldReturnMetadata() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "Demo Plugin");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        Object result = controller.getMetadata("demo-plugin");

        assertEquals(metadata, result);
    }

    @Test
    @DisplayName("getMetadata should return error map when plugin not found")
    void getMetadata_whenPluginNotExists_shouldReturnError() {
        when(processor.getMetadata("non-existent")).thenReturn(null);

        Object result = controller.getMetadata("non-existent");

        assertTrue(result instanceof Map);
        Map<?, ?> errorMap = (Map<?, ?>) result;
        assertEquals("Plugin not found or has no frontend", errorMap.get("error"));
        assertEquals("non-existent", errorMap.get("pluginId"));
    }

    @Test
    @DisplayName("getPluginHome should return html shell")
    void getPluginHome_whenPluginExists_shouldReturnHtml() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "Demo Plugin");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("demo-plugin");

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<title>Demo Plugin</title>"));
        assertTrue(html.contains("<div id=\"app\">"));
        assertTrue(html.contains("/plugin/demo-plugin/static/console.js"));
    }

    @Test
    @DisplayName("getPluginHome should return 404 page when plugin not exists")
    void getPluginHome_whenPluginNotExists_shouldReturn404Page() {
        when(processor.getMetadata("non-existent")).thenReturn(null);
        when(pluginManager.getPlugin("non-existent")).thenReturn(null);

        String html = controller.getPluginHome("non-existent");

        assertTrue(html.contains("404"));
    }

    @Test
    @DisplayName("getPluginHome should return no-frontend page when plugin exists but has no frontend")
    void getPluginHome_whenPluginHasNoFrontend_shouldReturnNoFrontendPage() {
        when(processor.getMetadata("no-frontend-plugin")).thenReturn(null);
        when(pluginManager.getPlugin("no-frontend-plugin")).thenReturn(pluginWrapper);

        String html = controller.getPluginHome("no-frontend-plugin");

        assertTrue(html.contains("404"));
    }

    @Test
    @DisplayName("getPluginPage should return same shell as home")
    void getPluginPage_shouldReturnSameHtmlAsHome() {
        FrontendMetadata metadata = createTestMetadata("demo-plugin", "Demo Plugin");
        when(processor.getMetadata("demo-plugin")).thenReturn(metadata);

        String homeHtml = controller.getPluginHome("demo-plugin");
        String pageHtml = controller.getPluginPage("demo-plugin");

        assertEquals(homeHtml, pageHtml);
    }

    @Test
    @DisplayName("display name should be html escaped")
    void getPluginHome_shouldEscapeSpecialChars() {
        FrontendMetadata metadata = new FrontendMetadata(
            "test-plugin",
            "<script>alert('xss')</script>",
            "console.js",
            new PluginRoute[0],
            new PluginMenuItem[0],
            new String[0],
            null, null
        );
        when(processor.getMetadata("test-plugin")).thenReturn(metadata);

        String html = controller.getPluginHome("test-plugin");

        assertFalse(html.contains("<script>alert('xss')</script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    private FrontendMetadata createTestMetadata(String pluginId, String displayName) {
        return new FrontendMetadata(
            pluginId,
            displayName,
            "console.js",
            new PluginRoute[]{
                new PluginRoute("/dashboard", "Dashboard", "desc", "icon", true)
            },
            new PluginMenuItem[]{
                new PluginMenuItem("menu-1", null, "Menu", "icon", "/dashboard", 100)
            },
            new String[]{"plugin:view"},
            null, null
        );
    }
}
