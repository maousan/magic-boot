package org.ssssssss.magicapi.job.quartz;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.job.service.MagicJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.ssssssss.script.MagicScriptContext;
import org.ssssssss.magicapi.utils.ScriptManager;
import org.ssssssss.magicapi.job.model.JobInfo;

import java.lang.reflect.Method;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * Quartz Job 用于执行 Magic Script 任务或 Class 任务
 * 使用 DisallowConcurrentExecution 来处理并发设置
 */
@DisallowConcurrentExecution
public class MagicScriptJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(MagicScriptJob.class);
    @Autowired
    private MagicJobLogService jobLogService;
    @Autowired
    private ApplicationContext applicationContext;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String script = dataMap.getString("script");
        String scriptName = dataMap.getString("scriptName");
        boolean showLog = dataMap.getBoolean("showLog");
        String jobType = dataMap.getString("jobType");
        String params = dataMap.getString("params");
        String clazz = dataMap.getString("clazz");
        // 记录任务开始
        String jobId = context.getJobDetail().getKey().getName();
        Long jobLogId = jobLogService.logStart(jobId, scriptName, "CRON");
        if (showLog) {
            logger.info("定时任务开始执行: [{}]", scriptName);
        }
        long startTime = System.currentTimeMillis();
        try {
            Object result;
            // 根据 jobType 执行不同的任务类型
            if ("clazz".equalsIgnoreCase(jobType)) {
                // 执行类方法
                result = executeClass(clazz, params, showLog, scriptName);
            } else {
                // 默认执行脚本
                MagicScriptContext magicScriptContext = new MagicScriptContext();
                magicScriptContext.setScriptName(scriptName);
                if (params != null && !params.isEmpty()) {
                    try {
                        Map<String, Object> paramsMap = JSON.parseObject(params, Map.class);
                        paramsMap.forEach(magicScriptContext::set);
                    } catch (Exception e) {
                        logger.warn("解析参数失败， 将使用原始参数");
                    }
                }
                result = ScriptManager.executeScript(script, magicScriptContext);
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            if (showLog) {
                logger.info("定时任务执行完成: [{}], 结果: [{}], 耗时: [{}] ms", scriptName, result, duration);
            }
            // 记录成功
            jobLogService.logSuccess(jobLogId, result, duration, LocalDateTimeUtil.of(endTime));
        } catch (Exception e) {
            logger.error("定时任务执行失败: [{}]", scriptName, e);
            // 记录失败
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            jobLogService.logFailure(jobLogId, duration, LocalDateTimeUtil.of(endTime), e);
        } finally {
            if (showLog) {
                logger.info("定时任务执行完毕: [{}]", scriptName);
            }
        }
    }
    /**
     * 执行类的 execute 方法
     */
    private Object executeClass(String className, String params, boolean showLog, String scriptName) throws Exception {
        if (showLog) {
            logger.info("执行类任务: [{}], 参数: [{}]", className, params);
        }
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
            if (showLog) {
                logger.info("类任务执行成功: [{}]", scriptName);
            }
            return result;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到类: " + className, e);
        } catch (Exception e) {
            throw new RuntimeException("执行类任务失败: " + e.getMessage(), e);
        }
    }
}
