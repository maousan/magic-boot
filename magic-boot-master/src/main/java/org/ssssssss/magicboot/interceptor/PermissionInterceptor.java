package org.ssssssss.magicboot.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.ssssssss.magicapi.core.context.RequestEntity;
import org.ssssssss.magicapi.core.interceptor.RequestInterceptor;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicapi.core.model.Options;
import org.ssssssss.magicapi.core.service.MagicAPIService;
import org.ssssssss.magicapi.core.service.MagicResourceService;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;
import org.ssssssss.magicapi.utils.PathUtils;
import org.ssssssss.magicboot.license.LicenseManager;
import org.ssssssss.magicboot.model.StatusCode;
import org.ssssssss.magicboot.pf4j.extension.ApiInterceptorExtensionProcessor;
import org.ssssssss.magicboot.plugin.api.interceptor.ApiInterceptorContext;
import org.ssssssss.script.MagicScriptContext;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
@Order(1)
public class PermissionInterceptor implements RequestInterceptor, HandlerInterceptor {

    @Autowired
    MagicAPIService magicAPIService;

    @Autowired
    MagicResourceService magicResourceService;

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private ApiInterceptorExtensionProcessor extensionProcessor;

    @Autowired(required = false)
    private LicenseManager licenseManager;

    private static Boolean isDev;
    private static Boolean isDemo;

    /**
     * 判断当前是否为开发环境
     */
    private boolean isDevEnvironment() {
        if (isDev == null) {
            String[] activeProfiles = environment.getActiveProfiles();
            isDev = Arrays.stream(activeProfiles)
                    .anyMatch("dev"::equalsIgnoreCase);
        }
        return isDev;
    }

    /**
     * 判断当前是否为演示环境
     */
    private boolean isDemoEnvironment() {
        if (isDemo == null) {
            String[] activeProfiles = environment.getActiveProfiles();
            isDemo = Arrays.stream(activeProfiles)
                    .anyMatch("demo"::equalsIgnoreCase);
        }
        return isDemo;
    }

    /**
     * 检查是否为 禁止演示环境 请求
     */
    private boolean isDemoDeny(ApiInfo info) {
        String demoDeny = Objects.toString(info.getOptionValue("demo_deny"), "");
        return demoDeny.equalsIgnoreCase("true");
    }

    /*
     * 当返回对象时，直接将此对象返回到页面，返回null时，继续执行后续操作
     */
    @Override
    public Object preHandle(ApiInfo info, MagicScriptContext context, MagicHttpServletRequest request, MagicHttpServletResponse response) {
        // License 授权闸门：必须位于 dev 判断与 require_login 判断之前，否则两者都是绕过口
        if (licenseManager != null && licenseManager.shouldBlock()) {
            return licenseManager.blockResponse();
        }
        context.getRootVariables().put("__user__", StpUtil.getLoginIdDefaultNull());
        // dev 环境下跳过登录校验
        if (isDevEnvironment()) {
            return null;
        }

        // demo 环境下判断是否禁止操作
        if (isDemoEnvironment() && !isDemoDeny(info)) {
            return StatusCode.DEMO_FORBIDDEN.json("演示环境禁止操作");
        }

        String requireLogin = Objects.toString(info.getOptionValue(Options.REQUIRE_LOGIN), "");
        if(requireLogin.equals("false")){
            return null;
        }
        if(!StpUtil.isLogin()){
            return StatusCode.CERTIFICATE_EXPIRED.json();
        } else {
            // TODO
            List<String> permissions = magicAPIService.execute("post", "/system/security/permissions", new HashMap<String, Object>());
            String permission = Objects.toString(info.getOptionValue(Options.PERMISSION), "");
            if (StringUtils.isNotBlank(permission) && !permissions.contains(permission)) {
                return StatusCode.FORBIDDEN.json();
            }
        }

        // 执行扩展点前置拦截
        if (extensionProcessor != null) {
            ApiInterceptorContext ctx = new ApiInterceptorContext();
            ctx.setApiPath(request.getRequestURI());
            ctx.setHttpMethod(request.getMethod());
            ctx.setRequest(request.getRequest());
            ctx.setResponse(response.getResponse());
            ctx.setRequestTime(System.currentTimeMillis());
            Object result = extensionProcessor.processPreHandle(ctx);
            return result;
        }

        return null;
    }

    @Override
    public Object postHandle(RequestEntity requestEntity, Object returnValue) throws Exception {
        MagicHttpServletRequest request = requestEntity.getRequest();

        // 执行扩展点后置拦截
        if (extensionProcessor != null) {
            ApiInterceptorContext ctx = new ApiInterceptorContext();
            ctx.setApiPath(request.getRequestURI());
            ctx.setHttpMethod(request.getMethod());
            ctx.setRequest(request.getRequest());
            ctx.setRequestTime(requestEntity.getRequestTime());
            extensionProcessor.processPostHandle(ctx, returnValue);
        }

        // 记录操作日志
        if(StpUtil.isLogin()){
            try {
                ApiInfo info = requestEntity.getApiInfo();
                template.update("insert into sys_oper_log(api_name, api_path, api_method, cost_time, create_by, create_date, user_agent, user_ip) values(?,?,?,?,?,?,?,?)",
//                    PathUtils.replaceSlash(groupServiceProvider.getFullName(info.getGroupId()) + "/" + info.getName()).replace("/","-"),
                        PathUtils.replaceSlash(String.format("/%s/%s", magicResourceService.getGroupName(info.getGroupId()), info.getName())),
                        request.getRequestURI(),
                        request.getMethod(),
                        System.currentTimeMillis() - requestEntity.getRequestTime(),
                        StpUtil.getLoginId(),
                        new Date(requestEntity.getRequestTime()),
                        request.getHeader("User-Agent"),
                        request.getRemoteAddr());
            } catch (Exception ignored){
                ignored.printStackTrace();
            }
        }
        return null;
    }

}
