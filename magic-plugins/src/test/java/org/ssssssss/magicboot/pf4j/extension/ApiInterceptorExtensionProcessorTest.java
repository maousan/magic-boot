package org.ssssssss.magicboot.pf4j.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorContext;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ApiInterceptorExtensionProcessor 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ApiInterceptorExtensionProcessorTest {

    @Mock
    private ExtensionPointManager extensionPointManager;

    private ApiInterceptorExtensionProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ApiInterceptorExtensionProcessor(extensionPointManager);
    }

    @Test
    @DisplayName("processPreHandle_无拦截器时_应返回 null")
    void processPreHandle_noInterceptors_shouldReturnNull() {
        ApiInterceptorContext context = createContext();
        when(extensionPointManager.getSortedApiInterceptors())
                .thenReturn(Collections.emptyList());

        Object result = processor.processPreHandle(context);

        assertNull(result);
    }

    @Test
    @DisplayName("processPreHandle_拦截器返回非null时_应中断处理")
    void processPreHandle_interceptorReturnsNonNull_shouldInterruptProcessing() {
        ApiInterceptorContext context = createContext();
        Object expectedResult = new Object();

        ApiInterceptorExtension interceptor = mock(ApiInterceptorExtension.class);
        when(interceptor.preHandle(context)).thenReturn(expectedResult);
        when(interceptor.getOrder()).thenReturn(100);
        when(extensionPointManager.getSortedApiInterceptors())
                .thenReturn(List.of(interceptor));

        Object result = processor.processPreHandle(context);

        assertSame(expectedResult, result);
    }

    @Test
    @DisplayName("processPreHandle_所有拦截器返回null时_应继续处理")
    void processPreHandle_allInterceptorsReturnNull_shouldContinueProcessing() {
        ApiInterceptorContext context = createContext();

        ApiInterceptorExtension interceptor1 = mock(ApiInterceptorExtension.class);
        ApiInterceptorExtension interceptor2 = mock(ApiInterceptorExtension.class);
        when(interceptor1.preHandle(context)).thenReturn(null);
        when(interceptor1.getOrder()).thenReturn(100);
        when(interceptor2.preHandle(context)).thenReturn(null);
        when(interceptor2.getOrder()).thenReturn(200);
        when(extensionPointManager.getSortedApiInterceptors())
                .thenReturn(Arrays.asList(interceptor1, interceptor2));

        Object result = processor.processPreHandle(context);

        assertNull(result);
        verify(interceptor1).preHandle(context);
        verify(interceptor2).preHandle(context);
    }

    @Test
    @DisplayName("processPreHandle_拦截器抛出异常时_应捕获并记录")
    void processPreHandle_interceptorThrowsException_shouldCatchAndLog() {
        ApiInterceptorContext context = createContext();

        ApiInterceptorExtension interceptor = mock(ApiInterceptorExtension.class);
        when(interceptor.preHandle(context)).thenThrow(new RuntimeException("Test exception"));
        when(interceptor.getOrder()).thenReturn(100);
        when(extensionPointManager.getSortedApiInterceptors())
                .thenReturn(List.of(interceptor));

        // 不应该抛出异常
        Object result = processor.processPreHandle(context);

        assertNull(result);
    }

    @Test
    @DisplayName("processPostHandle_应按顺序调用所有拦截器")
    void processPostHandle_shouldCallAllInterceptorsInOrder() {
        ApiInterceptorContext context = createContext();
        Object returnValue = new Object();

        ApiInterceptorExtension interceptor1 = mock(ApiInterceptorExtension.class);
        ApiInterceptorExtension interceptor2 = mock(ApiInterceptorExtension.class);
        when(interceptor1.getOrder()).thenReturn(100);
        when(interceptor2.getOrder()).thenReturn(200);
        when(extensionPointManager.getSortedApiInterceptors())
                .thenReturn(Arrays.asList(interceptor1, interceptor2));

        processor.processPostHandle(context, returnValue);

        verify(interceptor1).postHandle(context, returnValue);
        verify(interceptor2).postHandle(context, returnValue);
    }

    @Test
    @DisplayName("processError_应调用所有拦截器的错误处理")
    void processError_shouldCallAllErrorHandlers() {
        ApiInterceptorContext context = createContext();
        Exception ex = new RuntimeException("Test error");

        ApiInterceptorExtension interceptor1 = mock(ApiInterceptorExtension.class);
        ApiInterceptorExtension interceptor2 = mock(ApiInterceptorExtension.class);
        when(interceptor1.getOrder()).thenReturn(100);
        when(interceptor2.getOrder()).thenReturn(200);
        when(extensionPointManager.getSortedApiInterceptors())
                .thenReturn(Arrays.asList(interceptor1, interceptor2));

        processor.processError(context, ex);

        verify(interceptor1).onError(context, ex);
        verify(interceptor2).onError(context, ex);
    }

    @Test
    @DisplayName("processError_拦截器错误处理抛出异常时_不应中断其他拦截器")
    void processError_errorHandlerThrowsException_shouldNotInterruptOthers() {
        ApiInterceptorContext context = createContext();
        Exception ex = new RuntimeException("Test error");

        ApiInterceptorExtension interceptor1 = mock(ApiInterceptorExtension.class);
        ApiInterceptorExtension interceptor2 = mock(ApiInterceptorExtension.class);
        doThrow().when(interceptor1.onError(context, ex)).thenThrow(new RuntimeException("Handler error"));
        when(interceptor1.getOrder()).thenReturn(100);
        when(interceptor2.getOrder()).thenReturn(200);
        when(extensionPointManager.getSortedApiInterceptors())
                .thenReturn(Arrays.asList(interceptor1, interceptor2));

        // 不应该抛出异常
        processor.processError(context, ex);

        verify(interceptor1).onError(context, ex);
        verify(interceptor2).onError(context, ex);
    }

    private ApiInterceptorContext createContext() {
        ApiInterceptorContext context = new ApiInterceptorContext();
        context.setApiPath("/test/api");
        context.setHttpMethod("GET");
        context.setPluginId("test-plugin");
        return context;
    }
}
