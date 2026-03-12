package org.ssssssss.magicapi.springdoc.entity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.model.*;
import org.ssssssss.magicapi.core.service.MagicResourceService;
import org.ssssssss.magicapi.core.service.impl.RequestMagicDynamicRegistry;
import org.ssssssss.magicapi.utils.JsonUtils;
import org.ssssssss.magicapi.utils.PathUtils;
import org.ssssssss.script.parsing.ast.literal.BooleanLiteral;

import java.util.*;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.ssssssss.magicapi.springdoc.SpringDocConfig;

import static org.ssssssss.magicapi.core.config.Constants.*;

/**
 * 生成 OpenAPI 3.0 格式的 JSON
 *
 * @author mxd
 */
public class OpenApiProvider {

	/**
	 * body 空对象
	 */
	private static final String BODY_EMPTY = "{}";

	private final Map<String, Object> SCHEMA_MAP = new ConcurrentHashMap<>();

	private final RequestMagicDynamicRegistry requestMagicDynamicRegistry;

	private final MagicResourceService magicResourceService;
	/**
	 * 基础路径
	 */
	private final String basePath;
	private final OpenApiEntity.Info info;
	private final boolean persistenceResponseBody;
	private final String prefix;
	private final Map<String, Object> securitySchemesMap;
	private final List<String> securityNames;
	private final SpringDocConfig springDocConfig;

	public OpenApiProvider(RequestMagicDynamicRegistry requestMagicDynamicRegistry, MagicResourceService magicResourceService,
						   String basePath, OpenApiEntity.Info info, boolean persistenceResponseBody, String prefix,
						   Map<String, Object> securitySchemesMap, List<String> securityNames, SpringDocConfig springDocConfig) {
		this.requestMagicDynamicRegistry = requestMagicDynamicRegistry;
		this.magicResourceService = magicResourceService;
		this.basePath = basePath;
		this.info = info;
		this.persistenceResponseBody = persistenceResponseBody;
		this.prefix = StringUtils.defaultIfBlank(prefix, "") + "/";
		this.securitySchemesMap = securitySchemesMap;
		this.securityNames = securityNames;
		this.springDocConfig = springDocConfig;
	}

	@ResponseBody
	public OpenApiEntity openApiJson() {
		this.SCHEMA_MAP.clear();
		List<ApiInfo> infos = requestMagicDynamicRegistry.mappings();
		OpenApiEntity openApiEntity = new OpenApiEntity();
		openApiEntity.setInfo(info);

		// 收集 tagName -> groupName 的映射关系
		Map<String, String> tagGroupMap = new LinkedHashMap<>();

		// 添加服务器信息
		if (StringUtils.isNotBlank(basePath)) {
			openApiEntity.addServer(basePath, "当前服务");
		}

		// 添加安全方案
		for (Map.Entry<String, Object> entry : securitySchemesMap.entrySet()) {
			openApiEntity.addSecurityScheme(entry.getKey(), entry.getValue());
		}

		// 添加全局安全要求
		for (String securityName : securityNames) {
			openApiEntity.addSecurity(securityName);
		}

		for (ApiInfo apiInfo : infos) {
			String groupName = getRootGroupName(apiInfo.getGroupId());
			String requestPath = PathUtils.replaceSlash(this.prefix + magicResourceService.getGroupPath(apiInfo.getGroupId()) + "/" + apiInfo.getPath());
			// 使用 path 的驼峰格式作为 tag name
			String tagName = generateTagFromPath(requestPath);
			// 保存 tagName -> groupName 映射，用于生成描述
			tagGroupMap.putIfAbsent(tagName, groupName);

			OpenApiEntity.Operation operation = new OpenApiEntity.Operation();
			operation.addTag(tagName);
			operation.setSummary(apiInfo.getName());
			operation.setDescription(StringUtils.defaultIfBlank(apiInfo.getDescription(), apiInfo.getName()));
			// 设置 operationId，如果为空则根据 path 和 method 生成驼峰格式
			String operationId = generateCamelCaseOperationId(apiInfo.getMethod(), requestPath);
			operation.setOperationId(operationId);

			try {
				List<Map<String, Object>> parameters = parseParameters(apiInfo);
				boolean hasBody = parameters.stream().anyMatch(it -> "requestBody".equals(it.get("in")));

				// 添加查询参数和路径参数
				parameters.stream()
					.filter(it -> !"requestBody".equals(it.get("in")))
					.forEach(operation::addParameter);

				// 处理请求体
				BaseDefinition baseDefinition = apiInfo.getRequestBodyDefinition();
				if (hasBody && baseDefinition != null) {
					OpenApiEntity.RequestBody requestBody = new OpenApiEntity.RequestBody();
					requestBody.setRequired(baseDefinition.isRequired());
					requestBody.setDescription(baseDefinition.getDescription());

					OpenApiEntity.MediaType mediaType = new OpenApiEntity.MediaType();
					String groupNameForSchema = magicResourceService.getGroupName(apiInfo.getGroupId()).replace("/", "-");
					String voName = buildVoName(groupNameForSchema, apiInfo.getPath(), "request", baseDefinition);

					if (!CollectionUtils.isEmpty(baseDefinition.getChildren())) {
						doProcessDefinition(baseDefinition, apiInfo, groupNameForSchema, "root_", voName, 0);
						mediaType.setSchema(OpenApiEntity.createRefSchema(voName));
					} else {
						mediaType.setSchema(buildSimpleSchema(baseDefinition));
					}

					requestBody.addMediaType("application/json", mediaType);
					operation.setRequestBody(requestBody);
				}

				// 处理响应
				Map<String, OpenApiEntity.Response> responses = parseResponses(apiInfo, tagName);
				responses.forEach(operation::addResponse);

			} catch (Exception e) {
				// 记录异常但不影响其他接口处理
				System.err.println("解析接口失败：" + apiInfo.getPath() + ", 错误：" + e.getMessage());
			}

			openApiEntity.addPath(requestPath, apiInfo.getMethod(), operation);
		}

		// 添加根级别的 tags（支持自定义配置）
		for (Map.Entry<String, String> entry : tagGroupMap.entrySet()) {
			String tagName = entry.getKey();
			String groupName = entry.getValue();
			SpringDocConfig.TagConfig tagConfig = springDocConfig.getTags().get(tagName);
			if (tagConfig != null) {
				OpenApiEntity.ExternalDocs externalDocs = null;
				if (tagConfig.getExternalDocs() != null) {
					externalDocs = new OpenApiEntity.ExternalDocs(
						tagConfig.getExternalDocs().getDescription(),
						tagConfig.getExternalDocs().getUrl()
					);
				}
				openApiEntity.addTag(tagName,
					tagConfig.getDescription() != null ? tagConfig.getDescription() : groupName + "接口",
					externalDocs);
			} else {
				openApiEntity.addTag(tagName, groupName + "接口");
			}
		}

		// 添加 Schema 定义
		if (!this.SCHEMA_MAP.isEmpty()) {
			for (Map.Entry<String, Object> entry : this.SCHEMA_MAP.entrySet()) {
				openApiEntity.addSchema(entry.getKey(), entry.getValue());
			}
		}

		return openApiEntity;
	}

	/**
	 * 创建 PathItem，将参数移到 PathItem 级别
	 */
	private OpenApiEntity.PathItem createPathItem(OpenApiEntity.Operation operation, List<Map<String, Object>> opParams) {
		OpenApiEntity.PathItem pathItem = new OpenApiEntity.PathItem();
		pathItem.setOperation(operation.getTags().isEmpty() ? "get" : operation.getTags().get(0).toLowerCase(), operation);
		return pathItem;
	}

	private String getRootGroupName(String groupId) {
		Group group = magicResourceService.getGroup(groupId);
		if (!Objects.equals(group.getParentId(), "0")) {
			return getRootGroupName(group.getParentId());
		}
		return group.getName();
	}

	/**
	 * 从 path 生成 tag 名称（驼峰格式）
	 * 例如: /api/system/user/list -> systemUser
	 *       /api/data/report/export -> dataReport
	 *
	 * @param path 请求路径
	 * @return 驼峰格式的 tag 名称
	 */
	private String generateTagFromPath(String path) {
		// 移除前缀斜杠，按 / 分割
		String normalizedPath = path.replaceFirst("^/", "");
		String[] parts = normalizedPath.split("/");

		// 取前两级路径作为 tag（排除接口方法名）
		StringBuilder tagBuilder = new StringBuilder();
		int maxParts = Math.min(parts.length >= 3 ? 2 : parts.length, parts.length);

		for (int i = 0; i < maxParts; i++) {
			String part = parts[i];
			if (part != null && !part.isEmpty()) {
				// 第一个部分首字母小写，后续部分首字母大写
				if (tagBuilder.length() == 0) {
					tagBuilder.append(part.substring(0, 1).toLowerCase());
					if (part.length() > 1) {
						tagBuilder.append(part.substring(1));
					}
				} else {
					tagBuilder.append(part.substring(0, 1).toUpperCase());
					if (part.length() > 1) {
						tagBuilder.append(part.substring(1));
					}
				}
			}
		}

		return tagBuilder.length() > 0 ? tagBuilder.toString() : "default";
	}

	private List<Map<String, Object>> parseParameters(ApiInfo info) {
		List<Map<String, Object>> parameters = new ArrayList<>();

		// 查询参数
		info.getParameters().forEach(it ->
			parameters.add(OpenApiEntity.createParameter(it.isRequired(), it.getName(), "query",
				it.getDataType().getJavascriptType(), it.getDescription(), it.getValue()))
		);

		// Header 参数
		info.getHeaders().forEach(it ->
			parameters.add(OpenApiEntity.createParameter(it.isRequired(), it.getName(), "header",
				it.getDataType().getJavascriptType(), it.getDescription(), it.getValue()))
		);

		// 路径参数
		List<Path> paths = new ArrayList<>(info.getPaths());
		MagicConfiguration.getMagicResourceService().getGroupsByFileId(info.getId())
				.stream()
				.flatMap(it -> it.getPaths().stream())
				.filter(it -> !paths.contains(it))
				.forEach(paths::add);
		paths.forEach(it ->
			parameters.add(OpenApiEntity.createParameter(it.isRequired(), it.getName(), "path",
				it.getDataType().getJavascriptType(), it.getDescription(), it.getValue()))
		);

		// 请求体
		try {
			BaseDefinition baseDefinition = info.getRequestBodyDefinition();
			if (baseDefinition != null && !CollectionUtils.isEmpty(baseDefinition.getChildren())) {
				Map<String, Object> requestBody = new HashMap<>();
				String groupName = magicResourceService.getGroupName(info.getGroupId()).replace("/", "-");
				String voName = buildVoName(groupName, info.getPath(), "request", baseDefinition);

				Map<String, Object> content = new HashMap<>(1);
				Map<String, Object> mediaType = new HashMap<>(1);
				mediaType.put("schema", OpenApiEntity.createRefSchema(voName));
				content.put("application/json", mediaType);

				requestBody.put("required", baseDefinition.isRequired());
				requestBody.put("description", baseDefinition.getDescription());
				requestBody.put("content", content);
				requestBody.put("in", "requestBody");
				parameters.add(requestBody);
			} else if (StringUtils.isNotBlank(info.getRequestBody())) {
				Object object = JsonUtils.readValue(info.getRequestBody(), Object.class);
				boolean isListOrMap = (object instanceof List || object instanceof Map);
				if (isListOrMap && BooleanLiteral.isTrue(object)) {
					Map<String, Object> requestBody = new HashMap<>();
					requestBody.put("required", false);
					requestBody.put("in", "requestBody");
					Map<String, Object> content = new HashMap<>(1);
					Map<String, Object> mediaType = new HashMap<>(1);
					Map<String, Object> schema = new HashMap<>(2);
					if (object instanceof List) {
						schema.put("type", "array");
						schema.put("items", new HashMap<>());
					} else {
						schema.put("type", "object");
						schema.put("properties", new HashMap<>());
					}
					mediaType.put("schema", schema);
					mediaType.put("example", object);
					content.put("application/json", mediaType);
					requestBody.put("content", content);
					parameters.add(requestBody);
				}
			}

		} catch (Exception e) {
			System.err.println("解析请求参数失败：" + e.getMessage());
		}
		return parameters;
	}

	private Map<String, OpenApiEntity.Response> parseResponses(ApiInfo info, String groupName) {
		Map<String, OpenApiEntity.Response> result = new LinkedHashMap<>();
		OpenApiEntity.Response response = new OpenApiEntity.Response();
		response.setDescription("OK");

		BaseDefinition baseDefinition = info.getResponseBodyDefinition();
		if (baseDefinition != null && !CollectionUtils.isEmpty(baseDefinition.getChildren())) {
			OpenApiEntity.MediaType mediaType = new OpenApiEntity.MediaType();
			String groupNameForSchema = groupName.replace("/", "-");
			String voName = buildVoName(groupNameForSchema, info.getPath(), "response", baseDefinition);

			// 生成 schema 定义
			doProcessDefinition(baseDefinition, info, groupNameForSchema, "root_", voName, 0);
			mediaType.setSchema(OpenApiEntity.createRefSchema(voName));
			response.addMediaType("application/json", mediaType);
		} else if (this.persistenceResponseBody && StringUtils.isNotBlank(info.getResponseBody())) {
			try {
				Object object = JsonUtils.readValue(info.getResponseBody(), Object.class);
				OpenApiEntity.MediaType mediaType = new OpenApiEntity.MediaType();
				mediaType.setSchema(buildSimpleSchema(object));
				mediaType.setExample(object);
				response.addMediaType("application/json", mediaType);
			} catch (Exception ignored) {
			}
		}

		result.put("200", response);
		return result;
	}

	/**
	 * 构建 Schema
	 */
	private Object buildSimpleSchema(Object value) {
		if (value == null) {
			return null;
		}
		Map<String, Object> schema = new HashMap<>(2);
		String type = OpenApiEntity.getJavaScriptType(value);
		schema.put("type", type);
		if (value instanceof List) {
			List<?> list = (List<?>) value;
			if (!list.isEmpty()) {
				Map<String, Object> items = new HashMap<>(2);
				items.put("type", OpenApiEntity.getJavaScriptType(list.get(0)));
				schema.put("items", items);
			}
		}
		schema.put("example", value);
		return schema;
	}

	/**
	 * 构建 VO 名称
	 */
	private String buildVoName(String groupName, String path, String definitionType, BaseDefinition baseDefinition) {
		String sanitizedPath = path.replaceFirst("/", "").replaceAll("/", "_");
		String typePrefix = definitionType.equals("response") ? "response" : "request";
		String fieldName = StringUtils.isNotBlank(baseDefinition.getName()) ? baseDefinition.getName() : "root";
		return groupName + "_" + sanitizedPath + "_" + typePrefix + "_" + fieldName;
	}

	private Map<String, Object> doProcessDefinition(BaseDefinition target, ApiInfo info, String groupName,
			 String parentName, String voName, int level) {
		Map<String, Object> result = new HashMap<>(4);
		result.put("description", target.getDescription());

		if (DataType.Array == target.getDataType()) {
			result.put("type", "array");
			if (!CollectionUtils.isEmpty(target.getChildren())) {
				result.put("items", doProcessDefinition(target.getChildren().get(0), info, groupName,
					parentName + target.getName() + "_", voName + "_item", level + 1));
			} else {
				result.put("items", new HashMap<>());
			}
		} else if (DataType.Object == target.getDataType() || DataType.Any == target.getDataType()) {
			// 检查是否已存在同名 Schema
			String finalVoName = voName;
			if (this.SCHEMA_MAP.containsKey(voName)) {
				finalVoName = voName + "_" + level;
			}

			Map<String, Object> definition = new HashMap<>(4);
			Map<String, Map<String, Object>> properties = new HashMap<>(target.getChildren().size());
			List<String> required = new ArrayList<>();

			for (BaseDefinition obj : target.getChildren()) {
				properties.put(obj.getName(), doProcessDefinition(obj, info, groupName,
					parentName + target.getName() + "_", finalVoName + "_" + obj.getName(), level + 1));
				if (obj.isRequired()) {
					required.add(obj.getName());
				}
			}

			definition.put("type", "object");
			definition.put("properties", properties);
			definition.put("description", target.getDescription());
			if (!required.isEmpty()) {
				definition.put("required", required);
			}

			this.SCHEMA_MAP.put(finalVoName, definition);
			result.put("$ref", "#/components/schemas/" + finalVoName);

		} else {
			result.put("type", target.getDataType().getJavascriptType());
			result.put("example", target.getValue());
		}
		return result;
	}

	/**
	 * 生成驼峰格式的 operationId
	 * 例如: GET /api/user/list -> getApiUserList
	 *      POST /api/user/{id} -> postApiUserById
	 *      DELETE /api/user/{id} -> deleteApiUserById
	 *
	 * @param method HTTP 方法
	 * @param path 请求路径
	 * @return 驼峰格式的 operationId
	 */
	private String generateCamelCaseOperationId(String method, String path) {
		// 移除路径中的路径参数标记 {}, 保留参数名
		String normalizedPath = path.replaceAll("[{}]", "");

		// 按分隔符分割路径
		String[] parts = normalizedPath.split("[/:_]");

		// 构建 operationId
		StringBuilder operationId = new StringBuilder();
		operationId.append(method.toLowerCase());

		// 处理路径各部分，转换为驼峰格式
		for (String part : parts) {
			if (part != null && !part.isEmpty()) {
				// 首字母大写，其余小写
				operationId.append(Character.toUpperCase(part.charAt(0)));
				if (part.length() > 1) {
					operationId.append(part.substring(1).toLowerCase());
				}
			}
		}

		return operationId.toString();
	}
}
