package org.ssssssss.magicapi.job.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.job.service.JobLogService;
import org.springframework.beans.factory.annotation.Autowired;

import org.ssssssss.script.MagicScriptContext;
import org.ssssssss.magicapi.utils.ScriptManager;

/**
 * Quartz Job 用于执行 Magic Script 任务
 * 使用 DisallowConcurrentExecution 来处理并发设置
 */
@DisallowConcurrentExecution
public class MagicScriptJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(MagicScriptJob.class);

    @Autowired
    private JobLogService jobLogService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String script = dataMap.getString("script");
        String scriptName = dataMap.getString("scriptName");
        boolean showLog = dataMap.getBoolean("showLog");

        // 记录任务开始
        String jobId = context.getJobDetail().getKey().getName();
        jobLogService.logStart(jobId, scriptName, "CRON", script);

        if (showLog) {
            logger.info("定时任务开始执行: [{}]", scriptName);
        }

        try {
            // 执行脚本
            MagicScriptContext magicScriptContext = new MagicScriptContext();
            magicScriptContext.setScriptName(scriptName);
            Object result = ScriptManager.executeScript(script, magicScriptContext);

            if (showLog) {
                logger.info("定时任务执行完成: [{}], 结果: [{}]", scriptName, result);
            }

            // 记录成功
            jobLogService.logSuccess(jobId, scriptName, result);
        } catch (Exception e) {
            logger.error("定时任务执行失败: [{}]", scriptName, e);

            // 记录失败
            jobLogService.logFailure(jobId, scriptName, e);
        } finally {
            if (showLog) {
                logger.info("定时任务执行完毕: [{}]", scriptName);
            }
        }
    }
}