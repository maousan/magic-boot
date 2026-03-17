package org.ssssssss.magicboot.pf4j.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ExtensionPointManager 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ExtensionPointManagerTest {

    @Mock
    private ExtensionPointRegistry registry;

    @Mock
    private org.pf4j.PluginManager pluginManager;

    @Mock
    private ApiInterceptorExtension interceptor1;

    @Mock
    private ApiInterceptorExtension interceptor2;

    private ExtensionPointManager manager;

    @BeforeEach
    void setUp() {
        manager = new ExtensionPointManager(registry, pluginManager);
    }

    @Test
    @DisplayName("插件启动时应清除缓存")
    void onPluginStarted_shouldClearCache() {
        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginWrapper.getPluginId()).thenReturn("test-plugin");

        PluginStateEvent event = new PluginStateEvent(pluginManager, pluginWrapper, PluginState.STARTED);

        manager.pluginStateChanged(event);

        verify(registry).clearCache();
    }

    @Test
    @DisplayName("插件停止时应清除缓存")
    void onPluginStopped_shouldClearCache() {
        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginWrapper.getPluginId()).thenReturn("test-plugin");

        PluginStateEvent event = new PluginStateEvent(pluginManager, pluginWrapper, PluginState.STOPPED);

        manager.pluginStateChanged(event);

        verify(registry).clearCache();
    }

    @Test
    @DisplayName("getSortedApiInterceptors_应返回排序后的拦截器")
    void getSortedApiInterceptors_shouldReturnSortedInterceptors() {
        when(interceptor1.getOrder()).thenReturn(1);
        when(interceptor2.getOrder()).thenReturn(2);
        when(registry.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(List.of(interceptor2, interceptor1));

        List<ApiInterceptorExtension> result = manager.getSortedApiInterceptors();

        assertEquals(2, result.size());
        assertEquals(interceptor1, result.get(0));
        assertEquals(interceptor2, result.get(1));
    }

    @Test
    @DisplayName("getSortedApiInterceptors_空列表应返回空")
    void getSortedApiInterceptors_emptyList_shouldReturnEmpty() {
        when(registry.getExtensions(ApiInterceptorExtension.class))
                .thenReturn(Collections.emptyList());

        List<ApiInterceptorExtension> result = manager.getSortedApiInterceptors();

        assertTrue(result.isEmpty());
    }
}
