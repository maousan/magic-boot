package org.ssssssss.magicboot.pf4j.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PluginRouteRegistry 单元测试
 */
class PluginRouteRegistryTest {

    private PluginRouteRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new PluginRouteRegistry();
    }

    @Test
    @DisplayName("注册路由应正确存储")
    void registerRoutes_shouldStoreRoutes() {
        PluginRoute[] routes = {
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true),
            new PluginRoute("/settings", "Settings", "设置", "icon", true)
        };

        registry.registerRoutes("demo-plugin", routes);

        List<PluginRoute> stored = registry.getRoutes("demo-plugin");
        assertEquals(2, stored.size());
        assertEquals("/dashboard", stored.get(0).getPath());
        assertEquals("/settings", stored.get(1).getPath());
    }

    @Test
    @DisplayName("获取不存在的插件路由应返回空列表")
    void getRoutes_whenPluginNotFound_shouldReturnEmptyList() {
        List<PluginRoute> routes = registry.getRoutes("non-existent-plugin");

        assertNotNull(routes);
        assertTrue(routes.isEmpty());
    }

    @Test
    @DisplayName("注销路由应清除对应插件的路由")
    void unregisterRoutes_shouldRemovePluginRoutes() {
        PluginRoute[] routes = {
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true)
        };

        registry.registerRoutes("demo-plugin", routes);
        assertEquals(1, registry.getRoutes("demo-plugin").size());

        registry.unregisterRoutes("demo-plugin");

        assertTrue(registry.getRoutes("demo-plugin").isEmpty());
    }

    @Test
    @DisplayName("注册空路由数组应存储空列表")
    void registerRoutes_withEmptyArray_shouldStoreEmptyList() {
        registry.registerRoutes("empty-plugin", new PluginRoute[0]);

        List<PluginRoute> routes = registry.getRoutes("empty-plugin");

        assertNotNull(routes);
        assertTrue(routes.isEmpty());
    }

    @Test
    @DisplayName("注册 null 路由应存储空列表")
    void registerRoutes_withNull_shouldNotThrow() {
        assertDoesNotThrow(() -> registry.registerRoutes("null-plugin", null));

        List<PluginRoute> routes = registry.getRoutes("null-plugin");
        assertNotNull(routes);
    }

    @Test
    @DisplayName("获取所有路由应返回所有插件的路由")
    void getAllRoutes_shouldReturnAllRoutes() {
        PluginRoute[] routes1 = {
            new PluginRoute("/dashboard", "Dashboard", "控制台", "icon", true)
        };
        PluginRoute[] routes2 = {
            new PluginRoute("/home", "Home", "首页", "icon", false)
        };

        registry.registerRoutes("plugin-1", routes1);
        registry.registerRoutes("plugin-2", routes2);

        Map<String, List<PluginRoute>> allRoutes = registry.getAllRoutes();

        assertEquals(2, allRoutes.size());
        assertTrue(allRoutes.containsKey("plugin-1"));
        assertTrue(allRoutes.containsKey("plugin-2"));
    }

    @Test
    @DisplayName("重复注册应覆盖旧路由")
    void registerRoutes_whenOverwrite_shouldReplaceOldRoutes() {
        PluginRoute[] oldRoutes = {
            new PluginRoute("/old", "Old", "旧路由", "icon", true)
        };
        PluginRoute[] newRoutes = {
            new PluginRoute("/new1", "New1", "新路由1", "icon", true),
            new PluginRoute("/new2", "New2", "新路由2", "icon", true)
        };

        registry.registerRoutes("demo-plugin", oldRoutes);
        registry.registerRoutes("demo-plugin", newRoutes);

        List<PluginRoute> routes = registry.getRoutes("demo-plugin");
        assertEquals(2, routes.size());
        assertEquals("/new1", routes.get(0).getPath());
        assertEquals("/new2", routes.get(1).getPath());
    }

    @Test
    @DisplayName("多线程并发注册应线程安全")
    void registerRoutes_concurrentAccess_shouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                PluginRoute[] routes = {
                    new PluginRoute("/route" + index, "Route" + index, "路由" + index, "icon", true)
                };
                registry.registerRoutes("plugin-" + index, routes);
            });
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        Map<String, List<PluginRoute>> allRoutes = registry.getAllRoutes();
        assertEquals(threadCount, allRoutes.size());
    }
}
