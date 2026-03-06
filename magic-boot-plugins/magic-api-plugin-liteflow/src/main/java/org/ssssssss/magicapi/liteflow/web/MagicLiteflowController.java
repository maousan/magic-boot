package org.ssssssss.magicapi.liteflow.web;

import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.common.entity.ValidationResp;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.core.FlowExecutorHolder;
import com.yomahub.liteflow.enums.ScriptTypeEnum;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.flow.element.Chain;
import com.yomahub.liteflow.flow.element.Node;
import com.yomahub.liteflow.meta.LiteflowMetaOperator;
import com.yomahub.liteflow.script.validator.ScriptValidator;
import com.yomahub.liteflow.slot.DefaultContext;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.ssssssss.magicapi.liteflow.model.FlowComponentInfo;
import org.ssssssss.magicapi.liteflow.model.FlowInfo;
import org.ssssssss.magicapi.utils.JsonUtils;
import org.ssssssss.script.MagicScriptDebugContext;

import java.util.List;
import java.util.Map;

public class MagicLiteflowController extends MagicController implements MagicExceptionHandler {


	public MagicLiteflowController(MagicConfiguration configuration) {
		super(configuration);
	}

	@PostMapping("/liteflow/execute")
	@ResponseBody
	public JsonBean<Object> execute(String id, MagicHttpServletRequest request){
		String type = request.getHeader("Magic-Data-Type");
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
			if ("chain".equalsIgnoreCase(type)) {
				return handleChain(script, entity);
			} else {
				return handleNode(script, entity);
			}
		} finally {
			WebSocketSessionManager.removeMagicScriptContext(sessionAndScriptId);
			MagicLoggerContext.SESSION.remove();
		}
	}

	private JsonBean<Object> handleChain(String script, MagicEntity entity) {
		var validResult = LiteFlowChainELBuilder.validateWithEx(script);
		boolean success = validResult.isSuccess();
		FlowInfo flowInfo = (FlowInfo) entity;
		if(success){
			Chain chain = LiteflowMetaOperator.getChain(flowInfo.getChainId());
			if (chain != null) {
				LiteflowMetaOperator.reloadOneChain(flowInfo.getChainId(), script);
			} else {
				LiteFlowChainELBuilder.createChain()
						.setChainId(flowInfo.getChainId())
						.setEL(script).build();
			}
		}
		var response = FlowExecutorHolder.loadInstance().execute2Resp(flowInfo.getChainId(),
				StringUtils.isBlank(flowInfo.getParams()) ? null : JsonUtils.readValue(flowInfo.getParams(), Map.class));
		String message = !success ? validResult.getCause().getLocalizedMessage() :
				(response.isSuccess() ? "验证通过" : "验证通过，但执行失败");
		return new JsonBean<>(success ? 1 : 0, message, response.getCause());
	}

	private JsonBean<Object> handleNode(String script, MagicEntity entity) {
		FlowComponentInfo componentInfo = (FlowComponentInfo) entity;
		ScriptTypeEnum scriptTypeEnum = ScriptTypeEnum.getEnumByDisplayName(componentInfo.getScriptType());
		ValidationResp resp = ScriptValidator.validateWithEx(script, scriptTypeEnum);
		boolean isSuccess = resp.isSuccess();
		Exception e = resp.getCause();
		if (!isSuccess) {
			logger.error("脚本验证失败: {}", ExceptionUtils.getStackTrace(e));
			return new JsonBean<>(0, e.getLocalizedMessage(), e);
		}
		logger.info("脚本验证通过: {}[{}]", componentInfo.getNodeId(), componentInfo.getScriptType());
		List<Node> nodes = LiteflowMetaOperator.getNodesInAllChain(componentInfo.getNodeId());
		if (nodes.isEmpty()) {
			LiteFlowNodeBuilder.createScriptNode().setId(componentInfo.getNodeId())
					.setName(componentInfo.getName())
					.setScript(script)
					.build();
			logger.info("创建脚本节点: {}[{}]", componentInfo.getNodeId(), componentInfo.getScriptType());
		} else {
			LiteflowMetaOperator.reloadScript(componentInfo.getNodeId(), script);
			logger.info("重新加载脚本节点: {}[{}]", componentInfo.getNodeId(), componentInfo.getScriptType());
		}
		//执行脚本
		LiteflowResponse response = FlowExecutorHolder.loadInstance().execute2RespWithEL(String.format("THEN(%s)", componentInfo.getNodeId()), null);
		boolean success = response.isSuccess();
		if (success) {
			logger.info("脚本执行成功: {}", JsonUtils.toJsonString(response.getContextBean(DefaultContext.class).getDataMap()));
		}  else {
			logger.error("脚本执行失败: {}", ExceptionUtils.getStackTrace(response.getCause()));
		}
		return new JsonBean<>(success ? 1 : 0, success ? "执行成功" : response.getMessage(), response.getContextBean(DefaultContext.class).getDataMap());
	}
}
