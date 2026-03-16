package org.ssssssss.magicapi.job.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.job.service.IJob;

@Component
public class TestJob implements IJob {

    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);

    @Override
    public Object execute(String param) {
        logger.info("TestJob execute param: " + param);
        return "";
    }

}
