package org.ssssssss.magicboot.plugin.api.interceptor;

import org.pf4j.ExtensionPoint;

/**
 * API 拦截器扩展点
 * 允许插件在 API 请求前后执行自定义逻辑
 */
public interface ApiInterceptorExtension extends ExtensionPoint {

    /**
     * 获取拦截器顺序，值越小优先级越高
     */
    default int getOrder() {
        return 100;
    }

    /**
     * 请求前置处理
     * @param context 拦截器上下文
     * @return 返回非 null 时直接响应，中断后续处理
     */
    default Object preHandle(ApiInterceptorContext context) {
        return null;
    }

    /**
     * 请求后置处理
     * @param context 拦截器上下文
     * @param returnValue 原始返回值
     */
    default void postHandle(ApiInterceptorContext context, Object returnValue) {
    }

    /**
     * 异常处理
     * @param context 拦截器上下文
     * @param ex 异常
     */
    default void onError(ApiInterceptorContext context, Exception ex) {
    }
}
