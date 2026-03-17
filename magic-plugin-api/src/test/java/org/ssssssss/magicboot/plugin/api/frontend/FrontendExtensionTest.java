package org.ssssssss.magicboot.plugin.api.frontend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pf4j.ExtensionPoint;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FrontendExtension 接口测试
 * 验证接口契约和默认方法行为
 */
class FrontendExtensionTest {

    /**
     * 最小化实现 - 用于测试接口契约
     */
    private static class MinimalFrontendExtension implements FrontendExtension {
        private final String pluginId;
        private final String displayName;
        private final String entryScript;
        private final PluginRoute[] routes;

        MinimalFrontendExtension(String pluginId, String displayName, String entryScript, PluginRoute[] routes) {
            this.pluginId = pluginId;
            this.displayName = displayName;
            this.entryScript = entryScript;
            this.routes = routes;
        }

        @Override
        public String getPluginId() {
            return pluginId;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String getEntryScript() {
            return entryScript;
        }

        @Override
        public PluginRoute[] getRoutes() {
            return routes;
        }
    }

    /**
     * 完整实现 - 用于测试所有方法
     */
    private static class FullFrontendExtension implements FrontendExtension {
        private final String pluginId;
        private final String displayName;
        private final String entryScript;
        private final PluginRoute[] routes;
        private final PluginMenuItem[] menuItems;
        private final String[] requiredPermissions;

        FullFrontendExtension(String pluginId, String displayName, String entryScript,
                              PluginRoute[] routes, PluginMenuItem[] menuItems, String[] requiredPermissions) {
            this.pluginId = pluginId;
            this.displayName = displayName;
            this.entryScript = entryScript;
            this.routes = routes;
            this.menuItems = menuItems;
            this.requiredPermissions = requiredPermissions;
        }

        @Override
        public String getPluginId() {
            return pluginId;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String getEntryScript() {
            return entryScript;
        }

        @Override
        public PluginRoute[] getRoutes() {
            return routes;
        }

        @Override
        public PluginMenuItem[] getMenuItems() {
            return menuItems;
        }

        @Override
        public String[] getRequiredPermissions() {
            return requiredPermissions;
        }
    }

    @Test
    @DisplayName("FrontendExtension 应继承 ExtensionPoint")
    void frontendExtension_shouldExtendExtensionPoint() {
        assertTrue(ExtensionPoint.class.isAssignableFrom(FrontendExtension.class));
    }

    @Test
    @DisplayName("最小实现应正确返回基本属性")
    void minimalImplementation_shouldReturnBasicProperties() {
        PluginRoute[] routes = {
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true)
        };
        FrontendExtension extension = new MinimalFrontendExtension(
            "demo-plugin",
            "演示插件",
            "console.js",
            routes
        );

        assertEquals("demo-plugin", extension.getPluginId());
        assertEquals("演示插件", extension.getDisplayName());
        assertEquals("console.js", extension.getEntryScript());
        assertArrayEquals(routes, extension.getRoutes());
    }

    @Test
    @DisplayName("默认 getMenuItems 应返回空数组")
    void defaultGetMenuItems_shouldReturnEmptyArray() {
        FrontendExtension extension = new MinimalFrontendExtension(
            "test-plugin", "测试", "app.js", new PluginRoute[0]
        );

        PluginMenuItem[] menuItems = extension.getMenuItems();

        assertNotNull(menuItems);
        assertEquals(0, menuItems.length);
    }

    @Test
    @DisplayName("默认 getRequiredPermissions 应返回空数组")
    void defaultGetRequiredPermissions_shouldReturnEmptyArray() {
        FrontendExtension extension = new MinimalFrontendExtension(
            "test-plugin", "测试", "app.js", new PluginRoute[0]
        );

        String[] permissions = extension.getRequiredPermissions();

        assertNotNull(permissions);
        assertEquals(0, permissions.length);
    }

    @Test
    @DisplayName("完整实现应正确返回所有属性")
    void fullImplementation_shouldReturnAllProperties() {
        PluginRoute[] routes = {
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true),
            new PluginRoute("/settings", "Settings", "设置", "icon", true)
        };
        PluginMenuItem[] menuItems = {
            new PluginMenuItem("menu-1", null, "主菜单", "icon", "/dashboard", 100)
        };
        String[] permissions = {"plugin:view", "plugin:edit"};

        FrontendExtension extension = new FullFrontendExtension(
            "full-plugin",
            "完整插件",
            "console.js",
            routes,
            menuItems,
            permissions
        );

        assertEquals("full-plugin", extension.getPluginId());
        assertEquals("完整插件", extension.getDisplayName());
        assertEquals("console.js", extension.getEntryScript());
        assertArrayEquals(routes, extension.getRoutes());
        assertArrayEquals(menuItems, extension.getMenuItems());
        assertArrayEquals(permissions, extension.getRequiredPermissions());
    }

    @Test
    @DisplayName("空路由数组应被正确处理")
    void emptyRoutes_shouldBeHandledCorrectly() {
        FrontendExtension extension = new MinimalFrontendExtension(
            "empty-plugin", "空插件", "app.js", new PluginRoute[0]
        );

        PluginRoute[] routes = extension.getRoutes();

        assertNotNull(routes);
        assertEquals(0, routes.length);
    }

    @Test
    @DisplayName("null 路由应被正确处理")
    void nullRoutes_shouldBeHandledCorrectly() {
        FrontendExtension extension = new MinimalFrontendExtension(
            "null-plugin", "空插件", "app.js", null
        );

        PluginRoute[] routes = extension.getRoutes();

        assertNull(routes);
    }

    @Test
    @DisplayName("多路由插件应正确配置")
    void multiRoutePlugin_shouldConfigureCorrectly() {
        PluginRoute[] routes = {
            new PluginRoute("/", "Home", "首页", "home-icon", false),
            new PluginRoute("/dashboard", "Dashboard", "控制台", "dashboard-icon", true),
            new PluginRoute("/settings", "Settings", "设置", "settings-icon", true),
            new PluginRoute("/data", "DataView", "数据", "data-icon", true),
            new PluginRoute("/profile", "Profile", "个人中心", "profile-icon", true)
        };

        FrontendExtension extension = new MinimalFrontendExtension(
            "multi-plugin", "多功能插件", "bundle.js", routes
        );

        assertEquals(5, extension.getRoutes().length);
    }

    @Test
    @DisplayName("带层级菜单的插件应正确配置")
    void hierarchicalMenuPlugin_shouldConfigureCorrectly() {
        PluginMenuItem[] menuItems = {
            new PluginMenuItem("parent", null, "父菜单", "icon", null, 100),
            new PluginMenuItem("child-1", "parent", "子菜单1", "icon", "/child1", 101),
            new PluginMenuItem("child-2", "parent", "子菜单2", "icon", "/child2", 102)
        };

        FrontendExtension extension = new FullFrontendExtension(
            "menu-plugin", "菜单插件", "app.js",
            new PluginRoute[0],
            menuItems,
            new String[0]
        );

        assertEquals(3, extension.getMenuItems().length);
        assertNull(extension.getMenuItems()[0].getRoute()); // 父菜单无路由
        assertNotNull(extension.getMenuItems()[1].getRoute()); // 子菜单有路由
    }

    @Test
    @DisplayName("需要权限的插件应正确配置")
    void permissionRequiredPlugin_shouldConfigureCorrectly() {
        String[] permissions = {"plugin:demo:view", "plugin:demo:edit", "plugin:demo:admin"};

        FrontendExtension extension = new FullFrontendExtension(
            "secure-plugin", "安全插件", "secure.js",
            new PluginRoute[0],
            new PluginMenuItem[0],
            permissions
        );

        assertEquals(3, extension.getRequiredPermissions().length);
        assertTrue(extension.getRequiredPermissions()[0].contains("view"));
    }

    @Test
    @DisplayName("不同入口脚本路径应被正确处理")
    void differentEntryScripts_shouldBeHandledCorrectly() {
        // 标准入口
        FrontendExtension standard = new MinimalFrontendExtension(
            "std-plugin", "标准", "console.js", new PluginRoute[0]
        );
        assertEquals("console.js", standard.getEntryScript());

        // 带子目录
        FrontendExtension subDir = new MinimalFrontendExtension(
            "subdir-plugin", "子目录", "assets/js/app.js", new PluginRoute[0]
        );
        assertEquals("assets/js/app.js", subDir.getEntryScript());

        // 带版本号
        FrontendExtension versioned = new MinimalFrontendExtension(
            "versioned-plugin", "版本化", "console.v2.min.js", new PluginRoute[0]
        );
        assertEquals("console.v2.min.js", versioned.getEntryScript());
    }

    @Test
    @DisplayName("插件 ID 应符合命名规范")
    void pluginId_shouldFollowNamingConvention() {
        // 有效的插件 ID 格式
        String[] validIds = {
            "demo-plugin",
            "my-test-plugin",
            "plugin123",
            "my_plugin",
            "MyPlugin",
            "a1b2c3"
        };

        for (String id : validIds) {
            FrontendExtension extension = new MinimalFrontendExtension(
                id, "测试", "app.js", new PluginRoute[0]
            );
            assertEquals(id, extension.getPluginId());
        }
    }

    @Test
    @DisplayName("显示名称支持多语言")
    void displayName_shouldSupportMultipleLanguages() {
        // 中文
        FrontendExtension chinese = new MinimalFrontendExtension(
            "cn-plugin", "数据管理", "app.js", new PluginRoute[0]
        );
        assertEquals("数据管理", chinese.getDisplayName());

        // 英文
        FrontendExtension english = new MinimalFrontendExtension(
            "en-plugin", "Data Management", "app.js", new PluginRoute[0]
        );
        assertEquals("Data Management", english.getDisplayName());

        // 日文
        FrontendExtension japanese = new MinimalFrontendExtension(
            "jp-plugin", "データ管理", "app.js", new PluginRoute[0]
        );
        assertEquals("データ管理", japanese.getDisplayName());
    }
}
