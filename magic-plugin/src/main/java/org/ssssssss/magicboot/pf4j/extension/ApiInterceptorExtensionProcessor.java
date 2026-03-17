package org.ssssssss.magicboot.pf4j.extension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorContext;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * API 拦截器扩展点处理器
 * 负责执行所有注册的 API 拦截器扩展点
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiInterceptorExtensionProcessor {

    private final ExtensionPointManager extensionPointManager;

    /**
     * 执行前置拦截
     * @param context 拦截器上下文
     * @return 返回非 null 时中断请求，直接响应
     */
    public Object processPreHandle(ApiInterceptorContext context) {
        List<ApiInterceptorExtension> interceptors = extensionPointManager.getSortedApiInterceptors();

        for (ApiInterceptorExtension interceptor : interceptors) {
            try {
                Object result = interceptor.preHandle(context);
                if (result != null) {
                    log.debug("扩展点 [{}] 中断了请求处理: {}",
                            interceptor.getClass().getName(), context.getApiPath());
                    return result;
                }
            } catch (Exception e) {
                log.error("扩展点 [{}] 执行前置处理失败: {}",
                         interceptor.getClass().getName(), context.getApiPath(), e);
            }
        }
        return null;
    }

    /**
     * 执行后置拦截
     * @param context 拦截器上下文
     * @param returnValue 原始返回值
     */
    public void processPostHandle(ApiInterceptorContext context, Object returnValue) {
        List<ApiInterceptorExtension> interceptors = extensionPointManager.getSortedApiInterceptors();

        for (ApiInterceptorExtension interceptor : interceptors) {
            try {
                interceptor.postHandle(context, returnValue);
            } catch (Exception e) {
                log.error("扩展点 [{}] 执行后置处理失败: {}",
                         interceptor.getClass().getName(), context.getApiPath(), e);
            }
        }
    }

    /**
     * 执行异常处理
     * @param context 拦截器上下文
     * @param ex 异常
     */
    public void processError(ApiInterceptorContext context, Exception ex) {
        List<ApiInterceptorExtension> interceptors = extensionPointManager.getSortedApiInterceptors();

        for (ApiInterceptorExtension interceptor : interceptors) {
            try {
                interceptor.onError(context, ex);
            } catch (Exception e) {
                log.error("扩展点 [{}] 执行异常处理失败: {}",
                         interceptor.getClass().getName(), context.getApiPath(), e);
            }
        }
    }
}
