package org.ssssssss.magicboot.license;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * License 全局闸门：唯一能覆盖静态 ResourceHandler（/userfiles、插件 static、编辑器静态）的层。
 * 白名单请求放行；阻断态其余请求统一 403 JSON + X-License-Status 响应头。
 */
public class LicenseGateFilter extends OncePerRequestFilter {

    /**
     * 内置放行清单：登录族（含验证码/validateToken/logout）、授权状态/导入（恢复闭环，
     * 无授权也能看到指纹并导入，不会死锁）、magic-api 控制台（独立口令，运维恢复通道）、
     * 插件静态资源与页容器（管理页必须能加载）。授权管理面之外的排除项见规格 §6.3。
     */
    private static final List<String> DEFAULT_PERMIT = List.of(
            "/system/security/login",
            "/system/security/verification/code",
            "/system/security/validateToken",
            "/system/security/logout",
            "/system/license/status",
            "/system/license/import",
            "/api/system/license/status",
            "/api/system/license/import",
            "/magic/web/**",
            "/plugin/*/static/**",
            "/plugin/*/static",
            "/favicon.ico",
            "/error"
    );

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private final LicenseManager licenseManager;
    private final List<String> extraPermit;
    private final boolean issueEnabled;

    public LicenseGateFilter(LicenseManager licenseManager, String extraPermitPatterns, boolean issueEnabled) {
        this.licenseManager = licenseManager;
        this.extraPermit = extraPermitPatterns == null || extraPermitPatterns.isBlank()
                ? List.of()
                : Arrays.stream(extraPermitPatterns.split(",")).map(String::trim).toList();
        // 签发模式下放行签发接口（办公实例有私钥才有意义；客户实例未开即不可达）
        this.issueEnabled = issueEnabled;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 授权总开关关闭时整层跳过
        return !licenseManager.isEnabled();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var status = licenseManager.statusView();
        response.setHeader("X-License-Status", status.status);

        String uri = request.getRequestURI();
        if (licenseManager.shouldBlock() && !isPermitted(uri)) {
            response.setStatus(HttpServletResponse.SC_OK); // 响应体 code 语义化，HTTP 层保持 200 供统一拦截
            response.setContentType("application/json;charset=UTF-8");
            String body = "{\"code\":403,\"message\":\"" + escape(status.message)
                    + "\",\"data\":null,\"licenseStatus\":\"" + status.status + "\"}";
            response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isPermitted(String uri) {
        if (issueEnabled && (MATCHER.match("/system/license/issue/**", uri)
                || MATCHER.match("/api/system/license/issue/**", uri))) {
            return true;
        }
        for (String pattern : extraPermit) {
            if (MATCHER.match(pattern, uri)) {
                return true;
            }
        }
        for (String pattern : DEFAULT_PERMIT) {
            if (MATCHER.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
