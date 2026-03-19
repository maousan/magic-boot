package org.ssssssss.magicboot.provider;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.context.RequestEntity;
import org.ssssssss.magicapi.core.interceptor.ResultProvider;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicboot.entity.MagicErrorLog;
import org.ssssssss.magicboot.service.MagicErrorLogService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionResultProvider implements ResultProvider {

	@Autowired
	private MagicErrorLogService magicErrorLogService;

	@Override
	public Object buildResult(RequestEntity requestEntity, int code, String message, Object data) {
		long timestamp = System.currentTimeMillis();
        return new JsonBean<>(code, message, data, (int) (timestamp - requestEntity.getRequestTime()));
	}

	@Override
	public Object buildException(RequestEntity requestEntity, Throwable throwable) {
		// 记录错误日志到数据库
		String errorLogId = saveErrorLog(requestEntity, throwable);

		if(throwable.getCause() instanceof DisableServiceException){
			return buildResult(requestEntity, 500, "此账号已被临时封禁，请联系管理员");
		}

		// 返回包含错误日志ID的信息
		String errorMessage = "系统内部出现错误，错误ID: " + errorLogId;
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
				errorLog.setRequestPath(requestEntity.getApiInfo().getPath());
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
			// 如果保存日志失败，返回一个默认ID
			return "ERROR_LOG_SAVE_FAILED";
		}
	}
}
