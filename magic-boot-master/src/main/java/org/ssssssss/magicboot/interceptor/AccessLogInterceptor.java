package org.ssssssss.magicboot.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.context.RequestEntity;
import org.ssssssss.magicapi.core.interceptor.RequestInterceptor;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicboot.config.LogSwitchConfig;
import org.ssssssss.magicboot.entity.MagicAccessLog;
import org.ssssssss.magicboot.service.MagicAccessLogService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 接口访问日志拦截器：写操作（非 GET）与异常请求异步落库到 magic_access_log。
 * GET 正常请求不记录（LED 在线状态等高频轮询会刷量）；日志失败不影响业务请求。
 */
@Component
public class AccessLogInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AccessLogInterceptor.class);

    /** 参数截断上限，防超长内容（如整段外箱二维码）把 text 撑爆 */
    private static final int MAX_PARAMS_LENGTH = 4000;

    private static final int MAX_MSG_LENGTH = 1000;

    @Autowired
    private MagicAccessLogService accessLogService;

    @Autowired
    private LogSwitchConfig logSwitchConfig;

    /** 单线程异步落库，队列打满时丢弃（不阻塞业务请求） */
    private final ThreadPoolExecutor writer = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2000), r -> {
                Thread t = new Thread(r, "magic-access-log-writer");
                t.setDaemon(true);
                return t;
            }, new ThreadPoolExecutor.DiscardPolicy());

    public AccessLogInterceptor() {
        writer.allowCoreThreadTimeOut(true);
    }

    @Override
    public void afterCompletion(RequestEntity entity, Object result, Throwable throwable) {
        try {
            // 系统配置开关：access-log.enable=false 时不记录（配置页可切，保存即时生效）
            if (!logSwitchConfig.isAccessLogEnabled()) {
                return;
            }
            ApiInfo apiInfo = entity.getApiInfo();
            if (apiInfo == null) {
                return;
            }
            boolean isError = throwable != null;
            if ("GET".equalsIgnoreCase(apiInfo.getMethod()) && !isError) {
                return;
            }
            MagicAccessLog accessLog = new MagicAccessLog();
            // 记完整请求 URI（含分组前缀，如 /api/location/batch-epc/bind），比脚本段路径更适合审计定位
            accessLog.setRequestPath(entity.getRequest() == null ? apiInfo.getPath() : entity.getRequest().getRequestURI());
            accessLog.setRequestMethod(apiInfo.getMethod());
            accessLog.setRequestParams(truncate(buildParams(entity), MAX_PARAMS_LENGTH));
            accessLog.setUserId(currentUserId());
            accessLog.setIpAddress(entity.getRequest() == null ? null : entity.getRequest().getRemoteAddr());
            accessLog.setExecuteTime((int) (System.currentTimeMillis() - entity.getRequestTime()));
            accessLog.setState(isError ? "error" : "ok");
            accessLog.setExceptionMessage(isError ? truncate(safeMessage(throwable), MAX_MSG_LENGTH) : null);
            accessLog.setCreateTime(LocalDateTime.now());
            writer.execute(() -> {
                try {
                    accessLogService.save(accessLog);
                } catch (Exception e) {
                    log.warn("保存访问日志失败: {}", e.getMessage());
                }
            });
        } catch (Exception e) {
            log.warn("记录访问日志失败: {}", e.getMessage());
        }
    }

    /** 合并 URL 参数与 POST body，与错误日志的参数记录口径一致 */
    private String buildParams(RequestEntity entity) {
        try {
            Map<String, Object> allParams = new HashMap<>();
            Map<String, Object> parameters = entity.getParameters();
            if (parameters != null && !parameters.isEmpty()) {
                allParams.putAll(parameters);
            }
            Object requestBody = entity.getRequestBody();
            if (requestBody instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> bodyMap = (Map<String, Object>) requestBody;
                allParams.putAll(bodyMap);
            } else if (requestBody != null) {
                allParams.put("_requestBody", requestBody.toString());
            }
            return allParams.isEmpty() ? null : JSONUtil.toJsonStr(allParams);
        } catch (Exception e) {
            return null;
        }
    }

    private String currentUserId() {
        try {
            return StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String safeMessage(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null) {
            message = throwable.getClass().getName();
        }
        return message;
    }

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }
}
