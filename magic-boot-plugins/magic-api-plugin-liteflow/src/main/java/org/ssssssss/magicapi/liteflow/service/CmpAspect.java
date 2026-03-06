package org.ssssssss.magicapi.liteflow.service;

import com.yomahub.liteflow.aop.ICmpAroundAspect;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.slot.DefaultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmpAspect implements ICmpAroundAspect {

    private final static Logger logger = LoggerFactory.getLogger(CmpAspect.class);

    @Override
    public void beforeProcess(NodeComponent cmp) {
        //before business
        logger.info("\n============ 执行流程[{}]前 ============\n请求参数: {}\n============ 执行流程[{}]前 ============",
                cmp.getName(), cmp.getRequestData(), cmp.getName());
    }

    @Override
    public void afterProcess(NodeComponent cmp) {
        //after business
        logger.info("\n============ 执行流程[{}]后 ============\n响应结果: {}\n============ 执行流程[{}]后 ============",
                cmp.getName(), cmp.getContextBean(DefaultContext.class).getDataMap(), cmp.getName());
    }

    @Override
    public void onSuccess(NodeComponent cmp) {
        //do sth
    }

    @Override
    public void onError(NodeComponent cmp, Exception e) {
        //do sth
    }
}
