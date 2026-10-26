package org.ssssssss.magicboot.provider;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.core.context.RequestEntity;
import org.ssssssss.magicapi.core.interceptor.ResultProvider;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicboot.config.LogSwitchConfig;
import org.ssssssss.magicboot.entity.MagicErrorLog;
import org.ssssssss.magicboot.service.MagicErrorLogService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionResultProvider implements ResultProvider {

	private static final Logger log = LoggerFactory.getLogger(ExceptionResultProvider.class);

	@Autowired
	private MagicErrorLogService magicErrorLogService;

	// 系统配置开关：error-log.enable=false 时不落库（配置页可切，保存即时生效）
	@Autowired
	private LogSwitchConfig logSwitchConfig;

	@Override
	public Object buildResult(RequestEntity requestEntity, int code, String message, Object data) {
		long timestamp = System.currentTimeMillis();
        return new JsonBean<>(code, message, data, (int) (timestamp - requestEntity.getRequestTime()));
	}

	@Override
	public Object buildException(RequestEntity requestEntity, Throwable throwable) {
		// 记录错误日志到数据库（error-log.enable=false 时跳过，响应不再带错误ID）
		boolean saveAttempted = false;
		String errorLogId = null;
		if (logSwitchConfig.isErrorLogEnabled()) {
			saveAttempted = true;
			errorLogId = saveErrorLog(requestEntity, throwable);
		}

		if(throwable.getCause() instanceof DisableServiceException){
			return buildResult(requestEntity, 500, "此账号已被临时封禁，请联系管理员");
		}

		// 落库失败（尝试过且无ID）时返回原始异常信息（空则类名），客户端可诊断；
		// 不再返回既查不了库也无意义的 ERROR_LOG_SAVE_FAILED 占位符，失败原因由 saveErrorLog 留服务端日志
		if (errorLogId == null && saveAttempted) {
			Throwable actualException = throwable.getCause() != null ? throwable.getCause() : throwable;
			String originalMessage = actualException.getMessage();
			if (originalMessage == null || originalMessage.trim().isEmpty()) {
				originalMessage = actualException.getClass().getSimpleName();
			}
			return buildResult(requestEntity, 500, "系统内部出现错误: " + originalMessage);
		}

		// 返回包含错误日志ID的信息
		String errorMessage = errorLogId != null ? "系统内部出现错误，错误ID: " + errorLogId : "系统内部出现错误";
		return buildResult(requestEntity, 500, errorMessage, errorLogId);
	}

	/**
	 * 保存错误日志到数据库
	 */
	private String saveErrorLog(RequestEntity requestEntity, Throwable throwable) {
		try {
			MagicErrorLog errorLog = new MagicErrorLog();

			// 生成唯一ID
			String errorLogId = IdUtil.fastSimpleUUID();
			errorLog.setId(errorLogId);

			// 请求信息
			if (requestEntity.getApiInfo() != null) {
				// 2026-10-21 改因：记录完整请求 URI（含分组前缀，如 /api/location/batch-epc/bind），
				// 脚本段路径在多分组下定位困难；与访问日志（magic_access_log）口径一致
				errorLog.setRequestPath(requestEntity.getRequest() != null
						? requestEntity.getRequest().getRequestURI()
						: requestEntity.getApiInfo().getPath());
				errorLog.setRequestMethod(requestEntity.getApiInfo().getMethod());
			}

			// 请求参数（合并URL参数和POST body）
			try {
				Map<String, Object> allParams = new HashMap<>();

				// 获取 URL 参数
				Map<String, Object> parameters = requestEntity.getParameters();
				if (parameters != null && !parameters.isEmpty()) {
					allParams.putAll(parameters);
				}

				// 获取 POST body
				Object requestBody = requestEntity.getRequestBody();
				if (requestBody != null) {
					// 如果 request body 是 Map 类型，合并到参数中
					if (requestBody instanceof Map) {
						allParams.put("_requestBody", requestBody);
					} else {
						// 其他类型（如字符串、对象等），转为 JSON 字符串存储
						allParams.put("_requestBody", JSONUtil.parse(requestBody));
					}
				}

				// 保存所有参数
				if (!allParams.isEmpty()) {
					errorLog.setRequestParams(JSONUtil.toJsonStr(allParams));
				}
			} catch (Exception e) {
				// 忽略参数序列化异常
			}

			// 请求头信息
			try {
				Map<String, Object> headers = requestEntity.getHeaders();
				if (headers != null && !headers.isEmpty()) {
					errorLog.setRequestHeaders(JSONUtil.toJsonStr(headers));
				}
			} catch (Exception e) {
				// 忽略请求头序列化异常
			}

			// 用户信息
			try {
				if (StpUtil.isLogin()) {
					errorLog.setUserId(StpUtil.getLoginIdAsString());
				}
			} catch (Exception e) {
				// 忽略获取用户信息异常
			}

			// IP地址
			if (requestEntity.getRequest() != null) {
				errorLog.setIpAddress(requestEntity.getRequest().getRemoteAddr());
			}

			// 异常信息
			Throwable actualException = throwable.getCause() != null ? throwable.getCause() : throwable;
			errorLog.setExceptionClass(actualException.getClass().getName());
			errorLog.setExceptionMessage(actualException.getMessage());

			// 堆栈跟踪
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			actualException.printStackTrace(printWriter);
			errorLog.setStackTrace(stringWriter.toString());

			// 创建时间
			errorLog.setCreateTime(LocalDateTime.now());

			// 保存到数据库
			magicErrorLogService.save(errorLog);

			return errorLogId;
		} catch (Exception e) {
			// 落库失败必须留服务端日志（否则无法排查），返回 null 由调用方改走原始异常信息分支
			log.error("保存错误日志到数据库失败", e);
			return null;
		}
	}
}
