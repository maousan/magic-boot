package org.ssssssss.magicboot.plugin.api.frontend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PluginMenuItem 单元测试
 */
class PluginMenuItemTest {

    @Test
    @DisplayName("全参数构造函数应正确设置所有属性")
    void constructor_withAllParams_shouldSetAllProperties() {
        PluginMenuItem item = new PluginMenuItem("menu-1", "parent-1", "菜单项", "icon", "/dashboard", 100);

        assertEquals("menu-1", item.getId());
        assertEquals("parent-1", item.getParentId());
        assertEquals("菜单项", item.getTitle());
        assertEquals("icon", item.getIcon());
        assertEquals("/dashboard", item.getRoute());
        assertEquals(100, item.getOrder());
    }

    @Test
    @DisplayName("无参构造函数应创建默认值")
    void constructor_withNoParams_shouldCreateDefaultValues() {
        PluginMenuItem item = new PluginMenuItem();

        assertNull(item.getId());
        assertNull(item.getParentId());
        assertNull(item.getTitle());
        assertNull(item.getIcon());
        assertNull(item.getRoute());
        assertEquals(0, item.getOrder());
    }

    @Test
    @DisplayName("setter 方法应正确更新属性")
    void setters_shouldUpdateProperties() {
        PluginMenuItem item = new PluginMenuItem();

        item.setId("menu-2");
        item.setParentId("parent-2");
        item.setTitle("设置");
        item.setIcon("settings-icon");
        item.setRoute("/settings");
        item.setOrder(200);

        assertEquals("menu-2", item.getId());
        assertEquals("parent-2", item.getParentId());
        assertEquals("设置", item.getTitle());
        assertEquals("settings-icon", item.getIcon());
        assertEquals("/settings", item.getRoute());
        assertEquals(200, item.getOrder());
    }

    @Test
    @DisplayName("相同 ID 的菜单项应相等")
    void equals_whenSameId_shouldBeEqual() {
        PluginMenuItem item1 = new PluginMenuItem("menu-1", "parent-1", "菜单1", "icon1", "/route1", 100);
        PluginMenuItem item2 = new PluginMenuItem("menu-1", "parent-2", "菜单2", "icon2", "/route2", 200);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("不同 ID 的菜单项不应相等")
    void equals_whenDifferentId_shouldNotBeEqual() {
        PluginMenuItem item1 = new PluginMenuItem("menu-1", "parent-1", "菜单", "icon", "/route", 100);
        PluginMenuItem item2 = new PluginMenuItem("menu-2", "parent-1", "菜单", "icon", "/route", 100);

        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("与 null 比较应返回 false")
    void equals_whenComparedToNull_shouldReturnFalse() {
        PluginMenuItem item = new PluginMenuItem("menu-1", null, "菜单", "icon", "/route", 100);

        assertNotEquals(null, item);
    }

    @Test
    @DisplayName("与不同类型比较应返回 false")
    void equals_whenComparedToDifferentType_shouldReturnFalse() {
        PluginMenuItem item = new PluginMenuItem("menu-1", null, "菜单", "icon", "/route", 100);

        assertNotEquals("menu-1", item);
        assertNotEquals(123, item);
    }

    @Test
    @DisplayName("与自身比较应返回 true")
    void equals_whenComparedToSelf_shouldReturnTrue() {
        PluginMenuItem item = new PluginMenuItem("menu-1", null, "菜单", "icon", "/route", 100);

        assertEquals(item, item);
    }

    @Test
    @DisplayName("null ID 的菜单项应能正确比较")
    void equals_whenNullId_shouldHandleCorrectly() {
        PluginMenuItem item1 = new PluginMenuItem(null, "parent-1", "菜单1", "icon1", "/route1", 100);
        PluginMenuItem item2 = new PluginMenuItem(null, "parent-2", "菜单2", "icon2", "/route2", 200);

        assertEquals(item1, item2);
    }

    @Test
    @DisplayName("null ID 与非 null ID 不应相等")
    void equals_whenOneNullId_shouldNotBeEqual() {
        PluginMenuItem item1 = new PluginMenuItem(null, null, "菜单", "icon", "/route", 100);
        PluginMenuItem item2 = new PluginMenuItem("menu-1", null, "菜单", "icon", "/route", 100);

        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("hashCode 对于相等对象应一致")
    void hashCode_shouldBeConsistentForEqualObjects() {
        PluginMenuItem item1 = new PluginMenuItem("menu-1", "parent-1", "菜单1", "icon1", "/route1", 100);
        PluginMenuItem item2 = new PluginMenuItem("menu-1", "parent-2", "菜单2", "icon2", "/route2", 200);

        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("顶级菜单（无父菜单）应正确设置")
    void topLevelMenu_shouldHaveNullParent() {
        PluginMenuItem item = new PluginMenuItem("top-menu", null, "顶级菜单", "icon", "/top", 0);

        assertNull(item.getParentId());
    }

    @Test
    @DisplayName("子菜单应正确设置父菜单 ID")
    void subMenu_shouldHaveCorrectParentId() {
        PluginMenuItem parent = new PluginMenuItem("parent-menu", null, "父菜单", "icon", null, 0);
        PluginMenuItem child = new PluginMenuItem("child-menu", "parent-menu", "子菜单", "icon", "/child", 1);

        assertEquals("parent-menu", child.getParentId());
        assertEquals(parent.getId(), child.getParentId());
    }

    @Test
    @DisplayName("order 值可以为负数")
    void order_canBeNegative() {
        PluginMenuItem item = new PluginMenuItem("menu-1", null, "菜单", "icon", "/route", -100);

        assertEquals(-100, item.getOrder());
    }

    @Test
    @DisplayName("order 值可以为零")
    void order_canBeZero() {
        PluginMenuItem item = new PluginMenuItem("menu-1", null, "菜单", "icon", "/route", 0);

        assertEquals(0, item.getOrder());
    }

    @Test
    @DisplayName("空字符串属性应能正常存储")
    void emptyStrings_shouldBeStoredCorrectly() {
        PluginMenuItem item = new PluginMenuItem("", "", "", "", "", 0);

        assertEquals("", item.getId());
        assertEquals("", item.getParentId());
        assertEquals("", item.getTitle());
        assertEquals("", item.getIcon());
        assertEquals("", item.getRoute());
    }

    @Test
    @DisplayName("中文和特殊字符应正确处理")
    void chineseAndSpecialChars_shouldBeHandledCorrectly() {
        PluginMenuItem item = new PluginMenuItem(
            "菜单-01",
            "父菜单 & 子菜单",
            "数据管理 <设置>",
            "图标-管理",
            "/数据/管理",
            100
        );

        assertEquals("菜单-01", item.getId());
        assertEquals("父菜单 & 子菜单", item.getParentId());
        assertEquals("数据管理 <设置>", item.getTitle());
    }

    @Test
    @DisplayName("多个菜单项应能正确排序")
    void multipleMenuItems_shouldSortCorrectly() {
        PluginMenuItem item1 = new PluginMenuItem("menu-1", null, "菜单1", "icon", "/1", 100);
        PluginMenuItem item2 = new PluginMenuItem("menu-2", null, "菜单2", "icon", "/2", 50);
        PluginMenuItem item3 = new PluginMenuItem("menu-3", null, "菜单3", "icon", "/3", 200);

        PluginMenuItem[] items = {item1, item2, item3};
        java.util.Arrays.sort(items, java.util.Comparator.comparingInt(PluginMenuItem::getOrder));

        assertEquals("menu-2", items[0].getId()); // order: 50
        assertEquals("menu-1", items[1].getId()); // order: 100
        assertEquals("menu-3", items[2].getId()); // order: 200
    }

    @Test
    @DisplayName("路由可以为 null（表示父菜单没有实际页面）")
    void route_canBeNull_forParentMenu() {
        PluginMenuItem item = new PluginMenuItem("parent-menu", null, "父菜单", "icon", null, 0);

        assertNull(item.getRoute());
    }
}
