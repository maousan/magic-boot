package org.ssssssss.magicboot.demo.extension;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorExtension;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorContext;

import java.util.Map;

/**
 * Demo API 拦截器扩展点实现
 * 用于演示如何在插件中实现 API 拦截功能
 */
@Slf4j
@Extension
public class DemoApiInterceptor implements ApiInterceptorExtension {

    /**
     * 设置较高的优先级（数字越小优先级越高）
     */
    @Override
    public int getOrder() {
        return 50;
    }

    /**
     * 前置拦截：在请求处理之前执行
     * 返回非 null 值将中断请求处理
     */
    @Override
    public Object preHandle(ApiInterceptorContext context) {
        log.info("[Demo拦截器] 前置处理 - API: {} {}",
                context.getHttpMethod(), context.getApiPath());

        // 如果路径包含 "blocked"，则拦截请求
        if (context.getApiPath() != null && context.getApiPath().contains("/blocked")) {
            log.warn("[Demo拦截器] 拦截了被阻止的请求: {}", context.getApiPath());
            return Map.of(
                "code", 403,
                "message", "请求被 Demo 插件拦截",
                "interceptor", "DemoApiInterceptor"
            );
        }

        return null; // 返回 null 继续处理请求
    }

    /**
     * 后置拦截：在请求处理完成后执行
     */
    @Override
    public void postHandle(ApiInterceptorContext context, Object returnValue) {
        long elapsedTime = context.getElapsedTime();
        log.info("[Demo拦截器] 后置处理 - API: {} - 耗时: {}ms",
                context.getApiPath(), elapsedTime);
    }

    /**
     * 异常处理：请求处理出错时执行
     */
    @Override
    public void onError(ApiInterceptorContext context, Exception ex) {
        log.error("[Demo拦截器] 异常处理 - API: {} - 错误: {}",
                  context.getApiPath(), ex.getMessage());
    }
}
