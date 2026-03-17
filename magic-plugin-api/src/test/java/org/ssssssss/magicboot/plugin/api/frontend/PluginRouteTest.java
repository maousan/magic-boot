package org.ssssssss.magicboot.plugin.api.frontend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PluginRoute 单元测试
 */
class PluginRouteTest {

    @Test
    @DisplayName("全参数构造函数应正确设置所有属性")
    void constructor_withAllParams_shouldSetAllProperties() {
        PluginRoute route = new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true);

        assertEquals("/dashboard", route.getPath());
        assertEquals("Dashboard", route.getComponent());
        assertEquals("控制台", route.getTitle());
        assertEquals("icon", route.getIcon());
        assertTrue(route.isRequiresAuth());
    }

    @Test
    @DisplayName("无参构造函数应创建默认值")
    void constructor_withNoParams_shouldCreateDefaultValues() {
        PluginRoute route = new PluginRoute();

        assertNull(route.getPath());
        assertNull(route.getComponent());
        assertNull(route.getTitle());
        assertNull(route.getIcon());
        assertTrue(route.isRequiresAuth()); // 默认需要认证
    }

    @Test
    @DisplayName("setter 方法应正确更新属性")
    void setters_shouldUpdateProperties() {
        PluginRoute route = new PluginRoute();

        route.setPath("/settings");
        route.setComponent("Settings");
        route.setTitle("设置");
        route.setIcon("settings-icon");
        route.setRequiresAuth(false);

        assertEquals("/settings", route.getPath());
        assertEquals("Settings", route.getComponent());
        assertEquals("设置", route.getTitle());
        assertEquals("settings-icon", route.getIcon());
        assertFalse(route.isRequiresAuth());
    }

    @Test
    @DisplayName("相同路径的路由应相等")
    void equals_whenSamePath_shouldBeEqual() {
        PluginRoute route1 = new PluginRoute("/dashboard", "Dashboard1", "控制台1", "icon1", true);
        PluginRoute route2 = new PluginRoute("/dashboard", "Dashboard2", "控制台2", "icon2", false);

        assertEquals(route1, route2);
        assertEquals(route1.hashCode(), route2.hashCode());
    }

    @Test
    @DisplayName("不同路径的路由不应相等")
    void equals_whenDifferentPath_shouldNotBeEqual() {
        PluginRoute route1 = new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true);
        PluginRoute route2 = new PluginRoute("/settings", "Dashboard", "控制台", "icon", true);

        assertNotEquals(route1, route2);
    }

    @Test
    @DisplayName("与 null 比较应返回 false")
    void equals_whenComparedToNull_shouldReturnFalse() {
        PluginRoute route = new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true);

        assertNotEquals(null, route);
    }

    @Test
    @DisplayName("与不同类型比较应返回 false")
    void equals_whenComparedToDifferentType_shouldReturnFalse() {
        PluginRoute route = new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true);

        assertNotEquals("/dashboard", route);
        assertNotEquals(123, route);
    }

    @Test
    @DisplayName("与自身比较应返回 true")
    void equals_whenComparedToSelf_shouldReturnTrue() {
        PluginRoute route = new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true);

        assertEquals(route, route);
    }

    @Test
    @DisplayName("null 路径的路由应能正确比较")
    void equals_whenNullPath_shouldHandleCorrectly() {
        PluginRoute route1 = new PluginRoute(null, "Dashboard", "控制台", "icon", true);
        PluginRoute route2 = new PluginRoute(null, "Settings", "设置", "icon2", false);

        assertEquals(route1, route2);
    }

    @Test
    @DisplayName("null 路径与非 null 路径不应相等")
    void equals_whenOneNullPath_shouldNotBeEqual() {
        PluginRoute route1 = new PluginRoute(null, "Dashboard", "控制台", "icon", true);
        PluginRoute route2 = new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true);

        assertNotEquals(route1, route2);
    }

    @Test
    @DisplayName("hashCode 对于相等对象应一致")
    void hashCode_shouldBeConsistentForEqualObjects() {
        PluginRoute route1 = new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true);
        PluginRoute route2 = new PluginRoute("/dashboard", "Different", "Different", "different", false);

        assertEquals(route1.hashCode(), route2.hashCode());
    }

    @Test
    @DisplayName("认证属性为 false 时应正确设置")
    void requiresAuth_whenFalse_shouldBeCorrectlySet() {
        PluginRoute route = new PluginRoute("/public", "Public", "公开页面", null, false);

        assertFalse(route.isRequiresAuth());
    }

    @Test
    @DisplayName("图标为 null 时应能正常工作")
    void icon_whenNull_shouldWorkCorrectly() {
        PluginRoute route = new PluginRoute("/dashboard", "Dashboard", "控制台", null, true);

        assertNull(route.getIcon());
    }

    @Test
    @DisplayName("空字符串属性应能正常存储")
    void emptyStrings_shouldBeStoredCorrectly() {
        PluginRoute route = new PluginRoute("", "", "", "", true);

        assertEquals("", route.getPath());
        assertEquals("", route.getComponent());
        assertEquals("", route.getTitle());
        assertEquals("", route.getIcon());
    }

    @Test
    @DisplayName("复杂路径应正确存储")
    void complexPath_shouldBeStoredCorrectly() {
        String complexPath = "/dashboard/:id/details/:detailId";
        PluginRoute route = new PluginRoute(complexPath, "Details", "详情", "icon", true);

        assertEquals(complexPath, route.getPath());
    }

    @Test
    @DisplayName("中文和特殊字符应正确处理")
    void chineseAndSpecialChars_shouldBeHandledCorrectly() {
        PluginRoute route = new PluginRoute(
            "/数据管理",
            "DataManagement",
            "数据管理 & 设置 <test>",
            "图标-01",
            true
        );

        assertEquals("/数据管理", route.getPath());
        assertEquals("数据管理 & 设置 <test>", route.getTitle());
    }
}
