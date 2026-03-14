package org.ssssssss.magicapi.job.web;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.config.WebSocketSessionManager;
import org.ssssssss.magicapi.core.logging.MagicLoggerContext;
import org.ssssssss.magicapi.core.model.DebugRequest;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.core.model.MagicEntity;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.web.MagicController;
import org.ssssssss.magicapi.core.web.MagicExceptionHandler;
import org.ssssssss.magicapi.job.model.JobInfo;
import org.ssssssss.magicapi.utils.ScriptManager;
import org.ssssssss.script.MagicScriptDebugContext;

import java.lang.reflect.Method;
import java.util.Map;


public class MagicJobController extends MagicController implements MagicExceptionHandler {

    @Autowired
    private ApplicationContext applicationContext;

    public MagicJobController(MagicConfiguration configuration) {
        super(configuration);
    }

    @PostMapping("/job/execute")
    @ResponseBody
    public JsonBean<Object> execute(String id, MagicHttpServletRequest request){
        MagicEntity entity = MagicConfiguration.getMagicResourceService().file(id);
        notNull(entity, FILE_NOT_FOUND);
        String script = entity.getScript();
        DebugRequest debugRequest = DebugRequest.create(request);
        MagicLoggerContext.SESSION.set(debugRequest.getRequestedClientId());
        String sessionAndScriptId = debugRequest.getRequestedClientId() + debugRequest.getRequestedScriptId();
        try {
            MagicScriptDebugContext magicScriptContext = debugRequest.createMagicScriptContext(configuration.getDebugTimeout());
            WebSocketSessionManager.addMagicScriptContext(sessionAndScriptId, magicScriptContext);
            magicScriptContext.setScriptName(MagicConfiguration.getMagicResourceService().getScriptName(entity));

            JobInfo jobInfo = (JobInfo) entity;
            String jobType = jobInfo.getJobType();
            String params = jobInfo.getParams();

            Object result;
            if ("clazz".equalsIgnoreCase(jobType)) {
                // 执行类方法
                result = executeClass(jobInfo.getClazz(), params);
            } else {
                // 默认执行脚本
                if (params != null && !params.isEmpty()) {
                    try {
                        Map<String, Object> paramsMap = JSON.parseObject(params, Map.class);
                        paramsMap.forEach(magicScriptContext::set);
                    } catch (Exception e) {
                        logger.warn("解析参数失败: {}", e.getMessage());
                    }
                }
                result = ScriptManager.executeScript(script, magicScriptContext);
            }
            return new JsonBean<>(result);
        } catch(Exception e) {
            logger.error("执行脚本出错: {}", ExceptionUtils.getStackTrace(e));
            return new JsonBean<>(0, "执行脚本出错: " + e.getMessage());
        } finally {
            WebSocketSessionManager.removeMagicScriptContext(sessionAndScriptId);
            MagicLoggerContext.SESSION.remove();
        }
    }

    /**
     * 执行类的 execute 方法
     */
    private Object executeClass(String className, String params) throws Exception {
        try {
            // 加载类
            Class<?> clazz = Class.forName(className);

            // 尝试从 Spring 容器获取 Bean，如果不存在则创建新实例
            Object instance;
            try {
                instance = applicationContext.getBean(clazz);
            } catch (Exception e) {
                // 如果无法从 Spring 容器获取，则创建新实例
                instance = clazz.getDeclaredConstructor().newInstance();
            }

            // 查找 execute 方法，优先查找带参数的方法
            Method executeMethod;
            Object result;

            try {
                // 尝试查找带 String 参数的 execute 方法
                executeMethod = clazz.getMethod("execute", String.class);
                result = executeMethod.invoke(instance, params);
            } catch (NoSuchMethodException e) {
                // 如果没有带参数的方法，尝试查找无参方法
                executeMethod = clazz.getMethod("execute");
                result = executeMethod.invoke(instance);
            }
            return result;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到类: " + className, e);
        } catch (Exception e) {
            throw new RuntimeException("执行类任务失败: " + e.getMessage(), e);
        }
    }
}
