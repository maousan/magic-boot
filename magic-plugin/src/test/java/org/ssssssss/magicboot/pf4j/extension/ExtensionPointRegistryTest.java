package org.ssssssss.magicboot.pf4j.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginManager;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorContext;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ExtensionPointRegistry 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ExtensionPointRegistryTest {

    @Mock
    private PluginManager pluginManager;

    private ExtensionPointRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ExtensionPointRegistry(pluginManager);
    }

    @Test
    @DisplayName("获取不存在的扩展点应返回空列表")
    void getExtensions_whenNoExtensions_shouldReturnEmptyList() {
        when(pluginManager.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(Collections.emptyList());

        List<ApiInterceptorExtension> extensions = registry.getExtensions(ApiInterceptorExtension.class);

        assertNotNull(extensions);
        assertTrue(extensions.isEmpty());
    }

    @Test
    @DisplayName("获取扩展点应缓存结果")
    void getExtensions_shouldCacheResults() {
        ApiInterceptorExtension extension = mock(ApiInterceptorExtension.class);
        when(pluginManager.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(List.of(extension));

        // 第一次调用
        List<ApiInterceptorExtension> extensions1 = registry.getExtensions(ApiInterceptorExtension.class);
        // 第二次调用
        List<ApiInterceptorExtension> extensions2 = registry.getExtensions(ApiInterceptorExtension.class);

        // 只调用一次 pluginManager
        verify(pluginManager, times(1)).getExtensions(ApiInterceptorExtension.class);
        assertSame(extensions1, extensions2);
    }

    @Test
    @DisplayName("清除缓存后应重新加载扩展点")
    void clearCache_shouldReloadExtensions() {
        ApiInterceptorExtension extension = mock(ApiInterceptorExtension.class);
        when(pluginManager.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(List.of(extension));

        // 第一次加载
        registry.getExtensions(ApiInterceptorExtension.class);

        // 清除缓存
        registry.clearCache();

        // 再次加载
        registry.getExtensions(ApiInterceptorExtension.class);

        // 应该调用两次 pluginManager
        verify(pluginManager, times(2)).getExtensions(ApiInterceptorExtension.class);
    }

    @Test
    @DisplayName("清除指定类型的缓存")
    void clearCache_forType_shouldOnlyClearThatType() {
        ApiInterceptorExtension apiExtension = mock(ApiInterceptorExtension.class);
        TestExtension testExtension = mock(TestExtension.class);

        when(pluginManager.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(List.of(apiExtension));
        when(pluginManager.getExtensions(TestExtension.class))
                .thenReturn(List.of(testExtension));

        // 加载两种类型的扩展点
        registry.getExtensions(ApiInterceptorExtension.class);
        registry.getExtensions(TestExtension.class);

        // 清除 ApiInterceptorExtension 类型的缓存
        registry.clearCache(ApiInterceptorExtension.class);

        // 再次获取
        registry.getExtensions(ApiInterceptorExtension.class);
        registry.getExtensions(TestExtension.class);

        // ApiInterceptorExtension 应该重新加载，TestExtension 应该使用缓存
        verify(pluginManager, times(2)).getExtensions(ApiInterceptorExtension.class);
        verify(pluginManager, times(1)).getExtensions(TestExtension.class);
    }

    @Test
    @DisplayName("获取单个扩展点应返回优先级最高的")
    void getExtension_shouldReturnFirstExtension() {
        ApiInterceptorExtension extension1 = mock(ApiInterceptorExtension.class);
        ApiInterceptorExtension extension2 = mock(ApiInterceptorExtension.class);

        when(pluginManager.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(Arrays.asList(extension1, extension2));

        var result = registry.getExtension(ApiInterceptorExtension.class);

        assertTrue(result.isPresent());
        assertSame(extension1, result.get());
    }

    @Test
    @DisplayName("获取缓存大小应返回正确的数量")
    void getCacheSize_shouldReturnCorrectCount() {
        when(pluginManager.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(List.of(mock(ApiInterceptorExtension.class)));
        when(pluginManager.getExtensions(TestExtension.class))
                .thenReturn(List.of(mock(TestExtension.class)));

        assertEquals(0, registry.getCacheSize());

        registry.getExtensions(ApiInterceptorExtension.class);
        assertEquals(1, registry.getCacheSize());

        registry.getExtensions(TestExtension.class);
        assertEquals(2, registry.getCacheSize());
    }

    // 测试用的扩展点接口
    interface TestExtension extends org.pf4j.ExtensionPoint {
    }
}
