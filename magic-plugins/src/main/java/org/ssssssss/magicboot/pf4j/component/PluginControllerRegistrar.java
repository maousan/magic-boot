package org.ssssssss.magicboot.pf4j.component;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 插件 Controller 注册器
 * 在插件启动时将 Controller 动态注册到 Spring MVC
 */
@Slf4j
public class PluginControllerRegistrar implements PluginStateListener {

    private final SpringPluginManager pluginManager;
    private final RequestMappingHandlerMapping handlerMapping;
    private final ApplicationContext mainApplicationContext;

    public PluginControllerRegistrar(SpringPluginManager pluginManager,
                                      RequestMappingHandlerMapping handlerMapping,
                                      ApplicationContext mainApplicationContext) {
        this.pluginManager = pluginManager;
        this.handlerMapping = handlerMapping;
        this.mainApplicationContext = mainApplicationContext;
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
        Class<?> controllerClass = controller.getClass();

        // 获取类级别的 @RequestMapping
        RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
        String classPath = "";
        if (classMapping != null && classMapping.value().length > 0) {
            classPath = classMapping.value()[0];
        }

        // 遍历所有方法，注册 mapping
        for (Method method : controllerClass.getDeclaredMethods()) {
            RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
            if (methodMapping != null) {
                String[] paths = methodMapping.value().length > 0 ? methodMapping.value() : methodMapping.path();
                for (String path : paths) {
                    String fullPath = classPath + path;
                    registerHandlerMethod(controller, method, fullPath, pluginId);
                }
            }

            // 处理 @GetMapping, @PostMapping 等派生注解
            registerDerivedMappings(controller, method, classPath, pluginId);
        }

        log.debug("Controller [{}] 注册成功", controllerClass.getName());
    }

    /**
     * 注册派生注解的映射
     */
    private void registerDerivedMappings(Object controller, Method method, String classPath, String pluginId) {
        // 处理 @GetMapping
        org.springframework.web.bind.annotation.GetMapping getMapping =
            method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
        if (getMapping != null) {
            for (String path : getMapping.value()) {
                registerHandlerMethod(controller, method, classPath + path, pluginId);
            }
        }

        // 处理 @PostMapping
        org.springframework.web.bind.annotation.PostMapping postMapping =
            method.getAnnotation(org.springframework.web.bind.annotation.PostMapping.class);
        if (postMapping != null) {
            for (String path : postMapping.value()) {
                registerHandlerMethod(controller, method, classPath + path, pluginId);
            }
        }

        // 处理 @PutMapping
        org.springframework.web.bind.annotation.PutMapping putMapping =
            method.getAnnotation(org.springframework.web.bind.annotation.PutMapping.class);
        if (putMapping != null) {
            for (String path : putMapping.value()) {
                registerHandlerMethod(controller, method, classPath + path, pluginId);
            }
        }

        // 处理 @DeleteMapping
        org.springframework.web.bind.annotation.DeleteMapping deleteMapping =
            method.getAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class);
        if (deleteMapping != null) {
            for (String path : deleteMapping.value()) {
                registerHandlerMethod(controller, method, classPath + path, pluginId);
            }
        }

        // 处理 @PatchMapping
        org.springframework.web.bind.annotation.PatchMapping patchMapping =
            method.getAnnotation(org.springframework.web.bind.annotation.PatchMapping.class);
        if (patchMapping != null) {
            for (String path : patchMapping.value()) {
                registerHandlerMethod(controller, method, classPath + path, pluginId);
            }
        }
    }

    /**
     * 注册 HandlerMethod
     */
    private void registerHandlerMethod(Object handler, Method method, String path, String pluginId) {
        try {
            // 创建 RequestMappingInfo
            org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition methodsCondition =
                new org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition();

            org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder builder =
                org.springframework.web.servlet.mvc.method.RequestMappingInfo
                    .paths(path)
                    .options(handlerMapping.getBuilderConfiguration());

            org.springframework.web.servlet.mvc.method.RequestMappingInfo mappingInfo = builder.build();

            handlerMapping.registerMapping(mappingInfo, handler, method);
            log.debug("注册映射: {} -> {}.{}", path, handler.getClass().getSimpleName(), method.getName());
        } catch (Exception e) {
            log.error("注册映射失败: {} -> {}.{}", path, handler.getClass().getSimpleName(), method.getName(), e);
        }
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

                Object bean = entry.getValue().getBean();
                if (bean != null) {
                    // 检查 Bean 是否来自该插件的 ClassLoader
                    ClassLoader beanClassLoader = bean.getClass().getClassLoader();
                    ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();

                    if (beanClassLoader != null && beanClassLoader.equals(pluginClassLoader)) {
                        handlerMapping.unregisterMapping(entry.getKey());
                        log.debug("注销映射: {}", entry.getKey());
                    }
                }
            }

            log.info("插件 [{}] 的 Controller 注销完成", pluginWrapper.getPluginId());
        } catch (Exception e) {
            log.error("注销插件 Controller 失败: {}", pluginWrapper.getPluginId(), e);
        }
    }
}
