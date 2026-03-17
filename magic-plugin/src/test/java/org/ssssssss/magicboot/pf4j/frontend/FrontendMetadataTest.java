package org.ssssssss.magicboot.pf4j.frontend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.plugin.api.frontend.PluginMenuItem;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FrontendMetadata 单元测试
 */
class FrontendMetadataTest {

    @Test
    @DisplayName("创建元数据应正确存储所有属性")
    void createMetadata_shouldStoreAllProperties() {
        PluginRoute[] routes = {
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true)
        };
        PluginMenuItem[] menuItems = {
            new PluginMenuItem("menu-1", null, "菜单", "icon", "/dashboard", 100)
        };
        String[] permissions = {"plugin:view"};

        FrontendMetadata metadata = new FrontendMetadata(
            "demo-plugin",
            "演示插件",
            "console.js",
            routes,
            menuItems,
            permissions,
            null,  // embedType 使用默认值 INTEGRATED
            null   // externalUrl
        );

        assertEquals("demo-plugin", metadata.getPluginId());
        assertEquals("演示插件", metadata.getDisplayName());
        assertEquals("console.js", metadata.getEntryScript());
        assertArrayEquals(routes, metadata.getRoutes());
        assertArrayEquals(menuItems, metadata.getMenuItems());
        assertArrayEquals(permissions, metadata.getRequiredPermissions());
    }

    @Test
    @DisplayName("null 路由应转换为空数组")
    void createMetadata_withNullRoutes_shouldConvertToEmptyArray() {
        FrontendMetadata metadata = new FrontendMetadata(
            "test-plugin", "测试", "console.js",
            null, new PluginMenuItem[0], new String[0],
            null, null
        );

        assertNotNull(metadata.getRoutes());
        assertEquals(0, metadata.getRoutes().length);
    }

    @Test
    @DisplayName("null 菜单项应转换为空数组")
    void createMetadata_withNullMenuItems_shouldConvertToEmptyArray() {
        FrontendMetadata metadata = new FrontendMetadata(
            "test-plugin", "测试", "console.js",
            new PluginRoute[0], null, new String[0],
            null, null
        );

        assertNotNull(metadata.getMenuItems());
        assertEquals(0, metadata.getMenuItems().length);
    }

    @Test
    @DisplayName("null 权限应转换为空数组")
    void createMetadata_withNullPermissions_shouldConvertToEmptyArray() {
        FrontendMetadata metadata = new FrontendMetadata(
            "test-plugin", "测试", "console.js",
            new PluginRoute[0], new PluginMenuItem[0], null,
            null, null
        );

        assertNotNull(metadata.getRequiredPermissions());
        assertEquals(0, metadata.getRequiredPermissions().length);
    }

    @Test
    @DisplayName("toString 应包含所有属性信息")
    void toString_shouldContainAllProperties() {
        FrontendMetadata metadata = new FrontendMetadata(
            "demo-plugin",
            "演示插件",
            "console.js",
            new PluginRoute[0],
            new PluginMenuItem[0],
            new String[]{"plugin:view"},
            null, null
        );

        String str = metadata.toString();

        assertTrue(str.contains("demo-plugin"));
        assertTrue(str.contains("演示插件"));
        assertTrue(str.contains("console.js"));
        assertTrue(str.contains("plugin:view"));
    }

    @Test
    @DisplayName("空属性值应正确处理")
    void createMetadata_withEmptyValues_shouldHandleCorrectly() {
        FrontendMetadata metadata = new FrontendMetadata(
            "", "", "",
            new PluginRoute[0],
            new PluginMenuItem[0],
            new String[0],
            null, null
        );

        assertEquals("", metadata.getPluginId());
        assertEquals("", metadata.getDisplayName());
        assertEquals("", metadata.getEntryScript());
    }

    @Test
    @DisplayName("多路由和多菜单项应正确存储")
    void createMetadata_withMultipleRoutesAndMenus_shouldStoreCorrectly() {
        PluginRoute[] routes = {
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true),
            new PluginRoute("/settings", "Settings", "设置", "icon", true),
            new PluginRoute("/data", "DataView", "数据", "icon", false)
        };
        PluginMenuItem[] menuItems = {
            new PluginMenuItem("menu-1", null, "菜单1", "icon", "/dashboard", 100),
            new PluginMenuItem("menu-2", "menu-1", "子菜单", "icon", "/settings", 101)
        };

        FrontendMetadata metadata = new FrontendMetadata(
            "complex-plugin", "复杂插件", "console.js",
            routes, menuItems, new String[]{"perm1", "perm2"},
            null, null
        );

        assertEquals(3, metadata.getRoutes().length);
        assertEquals(2, metadata.getMenuItems().length);
        assertEquals(2, metadata.getRequiredPermissions().length);
    }
}
