package org.ssssssss.magicapi.liteflow;

import com.yomahub.liteflow.core.FlowExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.core.annotation.MagicModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.ssssssss.script.annotation.Comment;

import java.util.List;

/**
 * LiteFlow integration module for Magic API.
 * Provides workflow and rule engine capabilities through magic-api platform.
 */
@MagicModule("lf") // Using "lf" as shorthand for "liteflow"
public class LiteflowModule {

    private static final Logger logger = LoggerFactory.getLogger(LiteflowModule.class.getName());

    private final FlowExecutor flowExecutor;

    public LiteflowModule(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor;
        logger.info("LiteFlow module initialized."); // Changed from logger.info("LiteFlow module initialized") to logger.info("LiteFlow module initialized.")
    }

    @Comment("获取executor")
    public FlowExecutor getExecutor() {
        return flowExecutor;
    }

    @Comment("执行工作流链")
    public Object execute(@Comment(name = "chainId", value = "chainId") String chainId) {
        try {
            // Execute the corresponding flow chain
            return flowExecutor.execute2Resp(chainId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute flow: " + e.getMessage(), e);
        }
    }

    @Comment("执行工作流链")
    public Object execute(@Comment(name = "chainId", value = "chainId") String chainId,
                          @Comment(name = "args", value = "参数") Object params) {
        try {
            // Execute the corresponding flow chain
            return flowExecutor.execute2Resp(chainId, params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute flow: " + e.getMessage(), e);
        }
    }

}