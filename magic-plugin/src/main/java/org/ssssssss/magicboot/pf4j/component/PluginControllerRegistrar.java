package org.ssssssss.magicboot.pf4j.component;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件 Controller 注册器
 * 在插件启动时将 Controller 动态注册到 Spring MVC
 */
@Slf4j
public class PluginControllerRegistrar implements PluginStateListener {

    private final SpringPluginManager pluginManager;
    private final RequestMappingHandlerMapping handlerMapping;
    private final ApplicationContext mainApplicationContext;
    private final PluginProperties pluginProperties;
    private final Map<String, Set<String>> pluginControllerBeanNames = new ConcurrentHashMap<>();

    public PluginControllerRegistrar(SpringPluginManager pluginManager,
                                      RequestMappingHandlerMapping handlerMapping,
                                      ApplicationContext mainApplicationContext,
                                      PluginProperties pluginProperties) {
        this.pluginManager = pluginManager;
        this.handlerMapping = handlerMapping;
        this.mainApplicationContext = mainApplicationContext;
        this.pluginProperties = pluginProperties;
    }

    @Override
    public void pluginStateChanged(PluginStateEvent event) {
        if (event.getPluginState() == PluginState.STARTED) {
            registerControllers(event.getPlugin());
        } else if (event.getPluginState() == PluginState.STOPPED) {
            unregisterControllers(event.getPlugin());
        }
    }

    /**
     * 注册插件的 Controller
     */
    private void registerControllers(PluginWrapper pluginWrapper) {
        try {
            if (!(pluginWrapper.getPlugin() instanceof SpringPlugin springPlugin)) {
                return;
            }

            ApplicationContext pluginContext = springPlugin.getApplicationContext();
            Map<String, Object> controllers = pluginContext.getBeansWithAnnotation(RestController.class);

            // 也扫描 @Controller 注解的 Bean
            controllers.putAll(pluginContext.getBeansWithAnnotation(Controller.class));

            for (Map.Entry<String, Object> entry : controllers.entrySet()) {
                Object controller = entry.getValue();
                registerController(controller, pluginWrapper.getPluginId());
            }

            log.info("插件 [{}] 的 Controller 注册完成，共 {} 个", pluginWrapper.getPluginId(), controllers.size());
        } catch (Exception e) {
            log.error("注册插件 Controller 失败: {}", pluginWrapper.getPluginId(), e);
        }
    }

    /**
     * 注册单个 Controller
     */
    private void registerController(Object controller, String pluginId) {
        Class<?> controllerClass = AopUtils.getTargetClass(controller);
        Object handlerRef = prepareHandlerReference(controller, pluginId, controllerClass);
        RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(controllerClass, RequestMapping.class);
        String[] classPaths = extractPaths(classMapping);
        String pluginPrefix = resolvePluginApiPrefix(pluginId);

        // 遍历所有方法，注册 mapping
        for (Method method : controllerClass.getDeclaredMethods()) {
            RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (methodMapping == null) {
                continue;
            }
            String[] methodPaths = extractPaths(methodMapping);
            for (String classPath : classPaths) {
                for (String methodPath : methodPaths) {
                    String fullPath = mergePath(pluginPrefix, classPath, methodPath);
                    registerHandlerMethod(handlerRef, method, fullPath, methodMapping);
                }
            }
        }

        log.debug("Controller [{}] 注册成功", controllerClass.getName());
    }

    /**
     * 注册 HandlerMethod
     */
    private void registerHandlerMethod(Object handler, Method method, String path, RequestMapping methodMapping) {
        try {
            RequestMethodsRequestCondition methodsCondition = new RequestMethodsRequestCondition(methodMapping.method());
            RequestMappingInfo.Builder builder = RequestMappingInfo
                    .paths(path)
                    .methods(methodsCondition.getMethods().toArray(new org.springframework.web.bind.annotation.RequestMethod[0]))
                    .params(methodMapping.params())
                    .headers(methodMapping.headers())
                    .consumes(methodMapping.consumes())
                    .produces(methodMapping.produces())
                    .options(handlerMapping.getBuilderConfiguration());

            RequestMappingInfo mappingInfo = builder.build();

            handlerMapping.registerMapping(mappingInfo, handler, method);
            log.debug("注册映射: {} {} -> {}.{}",
                    methodsCondition.getMethods().isEmpty() ? "[ANY]" : methodsCondition.getMethods(),
                    path,
                    resolveHandlerName(handler),
                    method.getName());
        } catch (Exception e) {
            log.error("注册映射失败: {} -> {}.{}", path, resolveHandlerName(handler), method.getName(), e);
        }
    }

    private Object prepareHandlerReference(Object controller, String pluginId, Class<?> controllerClass) {
        if (!(mainApplicationContext instanceof ConfigurableApplicationContext configurableContext)) {
            return controller;
        }
        ConfigurableListableBeanFactory beanFactory = configurableContext.getBeanFactory();
        String beanName = buildPluginControllerBeanName(pluginId, controllerClass);
        if (!beanFactory.containsSingleton(beanName)) {
            beanFactory.registerSingleton(beanName, controller);
        }
        pluginControllerBeanNames
                .computeIfAbsent(pluginId, key -> ConcurrentHashMap.newKeySet())
                .add(beanName);
        return beanName;
    }

    private String buildPluginControllerBeanName(String pluginId, Class<?> controllerClass) {
        return "pf4jController$" + pluginId + "$" + controllerClass.getName();
    }

    private String resolveHandlerName(Object handler) {
        if (handler instanceof String beanName) {
            return beanName;
        }
        return handler.getClass().getSimpleName();
    }

    private String[] extractPaths(RequestMapping mapping) {
        if (mapping == null) {
            return new String[]{""};
        }
        String[] paths = mapping.path().length > 0 ? mapping.path() : mapping.value();
        if (paths.length == 0) {
            return new String[]{""};
        }
        return paths;
    }

    private String resolvePluginApiPrefix(String pluginId) {
        String template = pluginProperties.getApiPrefixTemplate();
        String normalizedTemplate = (template == null || template.isBlank())
                ? "/plugin/{pluginId}/api"
                : template.trim();
        return mergePath(normalizedTemplate.replace("{pluginId}", pluginId));
    }

    private String mergePath(String... parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            String segment = part.trim();
            if (!segment.startsWith("/")) {
                segment = "/" + segment;
            }
            while (segment.contains("//")) {
                segment = segment.replace("//", "/");
            }
            if (builder.length() > 0 && builder.charAt(builder.length() - 1) == '/' && segment.startsWith("/")) {
                builder.append(segment.substring(1));
            } else {
                builder.append(segment);
            }
        }
        if (builder.length() == 0) {
            return "/";
        }
        return builder.toString();
    }

    /**
     * 注销插件的 Controller
     */
    private void unregisterControllers(PluginWrapper pluginWrapper) {
        try {
            // 获取所有映射并移除属于该插件的
            Map<org.springframework.web.servlet.mvc.method.RequestMappingInfo, org.springframework.web.method.HandlerMethod> handlerMethods =
                handlerMapping.getHandlerMethods();

            for (Map.Entry<org.springframework.web.servlet.mvc.method.RequestMappingInfo,
                org.springframework.web.method.HandlerMethod> entry : handlerMethods.entrySet()) {

                Class<?> beanType = entry.getValue().getBeanType();
                if (beanType == null) {
                    continue;
                }
                ClassLoader beanClassLoader = beanType.getClassLoader();
                ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();

                if (beanClassLoader != null && beanClassLoader.equals(pluginClassLoader)) {
                    handlerMapping.unregisterMapping(entry.getKey());
                    log.debug("注销映射: {}", entry.getKey());
                }
            }
            unregisterPluginControllerBeans(pluginWrapper.getPluginId());

            log.info("插件 [{}] 的 Controller 注销完成", pluginWrapper.getPluginId());
        } catch (Exception e) {
            log.error("注销插件 Controller 失败: {}", pluginWrapper.getPluginId(), e);
        }
    }

    private void unregisterPluginControllerBeans(String pluginId) {
        if (!(mainApplicationContext instanceof ConfigurableApplicationContext configurableContext)) {
            return;
        }
        Set<String> beanNames = pluginControllerBeanNames.remove(pluginId);
        if (beanNames == null || beanNames.isEmpty()) {
            return;
        }
        ConfigurableListableBeanFactory beanFactory = configurableContext.getBeanFactory();
        Set<String> beanNamesCopy = new HashSet<>(beanNames);
        beanNamesCopy.removeIf(beanName -> !beanFactory.containsSingleton(beanName));
    }
}
