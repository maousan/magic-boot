package org.ssssssss.magicboot.pf4j.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginWrapper;
import org.ssssssss.magicboot.pf4j.extension.ExtensionPointManager;
import org.ssssssss.magicboot.plugin.api.frontend.FrontendExtension;
import org.ssssssss.magicboot.plugin.api.frontend.PluginMenuItem;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * FrontendExtensionProcessor 单元测试
 */
@ExtendWith(MockitoExtension.class)
class FrontendExtensionProcessorTest {

    @Mock
    private ExtensionPointManager extensionPointManager;

    @Mock
    private PluginRouteRegistry routeRegistry;

    @Mock
    private PluginWrapper pluginWrapper;

    @Mock
    private FrontendExtension frontendExtension;

    private FrontendExtensionProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new FrontendExtensionProcessor(extensionPointManager, routeRegistry);
    }

    @Test
    @DisplayName("插件启动时应注册前端扩展点")
    void onPluginStarted_shouldRegisterFrontendExtension() {
        // Arrange
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getDisplayName()).thenReturn("演示插件");
        when(frontendExtension.getEntryScript()).thenReturn("console.js");
        when(frontendExtension.getRoutes()).thenReturn(new PluginRoute[]{
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true)
        });
        when(frontendExtension.getMenuItems()).thenReturn(new PluginMenuItem[]{
            new PluginMenuItem("demo-menu", null, "演示菜单", "icon", "/dashboard", 100)
        });
        when(frontendExtension.getRequiredPermissions()).thenReturn(new String[]{"plugin:demo:view"});

        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(List.of(frontendExtension));

        // Act
        processor.onPluginStarted(pluginWrapper);

        // Assert
        verify(routeRegistry).registerRoutes(eq("demo-plugin"), any(PluginRoute[].class));

        FrontendMetadata metadata = processor.getMetadata("demo-plugin");
        assertNotNull(metadata);
        assertEquals("demo-plugin", metadata.getPluginId());
        assertEquals("演示插件", metadata.getDisplayName());
        assertEquals("console.js", metadata.getEntryScript());
    }

    @Test
    @DisplayName("插件停止时应注销前端扩展点")
    void onPluginStopped_shouldUnregisterFrontendExtension() {
        // 先注册
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getDisplayName()).thenReturn("演示插件");
        when(frontendExtension.getEntryScript()).thenReturn("console.js");
        when(frontendExtension.getRoutes()).thenReturn(new PluginRoute[0]);
        when(frontendExtension.getMenuItems()).thenReturn(new PluginMenuItem[0]);
        when(frontendExtension.getRequiredPermissions()).thenReturn(new String[0]);
        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(List.of(frontendExtension));

        processor.onPluginStarted(pluginWrapper);
        assertNotNull(processor.getMetadata("demo-plugin"));

        // 停止
        processor.onPluginStopped(pluginWrapper);

        // 验证已清除
        verify(routeRegistry).unregisterRoutes("demo-plugin");
        assertNull(processor.getMetadata("demo-plugin"));
    }

    @Test
    @DisplayName("获取不存在的插件元数据应返回 null")
    void getMetadata_whenPluginNotFound_shouldReturnNull() {
        FrontendMetadata metadata = processor.getMetadata("non-existent-plugin");
        assertNull(metadata);
    }

    @Test
    @DisplayName("获取所有元数据应返回所有已注册的插件")
    void getAllMetadata_shouldReturnAllRegisteredPlugins() {
        // 注册第一个插件
        when(pluginWrapper.getPluginId()).thenReturn("plugin-1");
        FrontendExtension ext1 = mock(FrontendExtension.class);
        when(ext1.getPluginId()).thenReturn("plugin-1");
        when(ext1.getDisplayName()).thenReturn("插件1");
        when(ext1.getEntryScript()).thenReturn("console1.js");
        when(ext1.getRoutes()).thenReturn(new PluginRoute[0]);
        when(ext1.getMenuItems()).thenReturn(new PluginMenuItem[0]);
        when(ext1.getRequiredPermissions()).thenReturn(new String[0]);

        // 注册第二个插件
        FrontendExtension ext2 = mock(FrontendExtension.class);
        when(ext2.getPluginId()).thenReturn("plugin-2");
        when(ext2.getDisplayName()).thenReturn("插件2");
        when(ext2.getEntryScript()).thenReturn("console2.js");
        when(ext2.getRoutes()).thenReturn(new PluginRoute[0]);
        when(ext2.getMenuItems()).thenReturn(new PluginMenuItem[0]);
        when(ext2.getRequiredPermissions()).thenReturn(new String[0]);

        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(List.of(ext1, ext2));

        processor.onPluginStarted(pluginWrapper);

        // 修改 pluginId 后再次注册
        when(pluginWrapper.getPluginId()).thenReturn("plugin-2");
        processor.onPluginStarted(pluginWrapper);

        Collection<FrontendMetadata> allMetadata = processor.getAllMetadata();
        assertEquals(2, allMetadata.size());
    }

    @Test
    @DisplayName("检查插件是否有前端扩展")
    void hasFrontend_shouldReturnCorrectValue() {
        assertFalse(processor.hasFrontend("demo-plugin"));

        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getDisplayName()).thenReturn("演示插件");
        when(frontendExtension.getEntryScript()).thenReturn("console.js");
        when(frontendExtension.getRoutes()).thenReturn(new PluginRoute[0]);
        when(frontendExtension.getMenuItems()).thenReturn(new PluginMenuItem[0]);
        when(frontendExtension.getRequiredPermissions()).thenReturn(new String[0]);
        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(List.of(frontendExtension));

        processor.onPluginStarted(pluginWrapper);

        assertTrue(processor.hasFrontend("demo-plugin"));
    }

    @Test
    @DisplayName("获取插件菜单项")
    void getMenuItems_shouldReturnCorrectMenuItems() {
        PluginMenuItem[] menuItems = {
            new PluginMenuItem("menu-1", null, "菜单1", "icon", "/route1", 100),
            new PluginMenuItem("menu-2", null, "菜单2", "icon", "/route2", 101)
        };

        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getPluginId()).thenReturn("demo-plugin");
        when(frontendExtension.getDisplayName()).thenReturn("演示插件");
        when(frontendExtension.getEntryScript()).thenReturn("console.js");
        when(frontendExtension.getRoutes()).thenReturn(new PluginRoute[0]);
        when(frontendExtension.getMenuItems()).thenReturn(menuItems);
        when(frontendExtension.getRequiredPermissions()).thenReturn(new String[0]);
        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(List.of(frontendExtension));

        processor.onPluginStarted(pluginWrapper);

        PluginMenuItem[] result = processor.getMenuItems("demo-plugin");
        assertEquals(2, result.length);
        assertEquals("menu-1", result[0].getId());
        assertEquals("menu-2", result[1].getId());
    }

    @Test
    @DisplayName("获取所有插件的菜单项")
    void getAllMenuItems_shouldReturnAllMenuItems() {
        PluginMenuItem[] menuItems1 = {
            new PluginMenuItem("menu-1", null, "菜单1", "icon", "/route1", 100)
        };
        PluginMenuItem[] menuItems2 = {
            new PluginMenuItem("menu-2", null, "菜单2", "icon", "/route2", 101)
        };

        FrontendExtension ext1 = mock(FrontendExtension.class);
        when(ext1.getPluginId()).thenReturn("plugin-1");
        when(ext1.getDisplayName()).thenReturn("插件1");
        when(ext1.getEntryScript()).thenReturn("console.js");
        when(ext1.getRoutes()).thenReturn(new PluginRoute[0]);
        when(ext1.getMenuItems()).thenReturn(menuItems1);
        when(ext1.getRequiredPermissions()).thenReturn(new String[0]);

        FrontendExtension ext2 = mock(FrontendExtension.class);
        when(ext2.getPluginId()).thenReturn("plugin-2");
        when(ext2.getDisplayName()).thenReturn("插件2");
        when(ext2.getEntryScript()).thenReturn("console.js");
        when(ext2.getRoutes()).thenReturn(new PluginRoute[0]);
        when(ext2.getMenuItems()).thenReturn(menuItems2);
        when(ext2.getRequiredPermissions()).thenReturn(new String[0]);

        when(pluginWrapper.getPluginId()).thenReturn("plugin-1");
        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(List.of(ext1));
        processor.onPluginStarted(pluginWrapper);

        when(pluginWrapper.getPluginId()).thenReturn("plugin-2");
        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(List.of(ext2));
        processor.onPluginStarted(pluginWrapper);

        Map<String, PluginMenuItem[]> allMenuItems = processor.getAllMenuItems();
        assertEquals(2, allMenuItems.size());
        assertEquals(1, allMenuItems.get("plugin-1").length);
        assertEquals(1, allMenuItems.get("plugin-2").length);
    }

    @Test
    @DisplayName("插件没有前端扩展点时不应报错")
    void onPluginStarted_whenNoFrontendExtension_shouldNotThrow() {
        when(pluginWrapper.getPluginId()).thenReturn("no-frontend-plugin");
        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> processor.onPluginStarted(pluginWrapper));

        assertFalse(processor.hasFrontend("no-frontend-plugin"));
    }

    @Test
    @DisplayName("扩展点获取异常时应抛出运行时异常")
    void onPluginStarted_whenExtensionThrowsException_shouldThrowRuntimeException() {
        when(pluginWrapper.getPluginId()).thenReturn("error-plugin");
        when(extensionPointManager.getExtensions(FrontendExtension.class))
            .thenThrow(new RuntimeException("Extension error"));

        assertThrows(RuntimeException.class, () -> processor.onPluginStarted(pluginWrapper));
    }
}
