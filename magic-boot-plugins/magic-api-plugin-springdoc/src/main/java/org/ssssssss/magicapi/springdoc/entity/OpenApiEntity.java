package org.ssssssss.magicapi.springdoc.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

/**
 * OpenAPI 3.0 实体定义
 *
 * @author mxd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiEntity {

	private String openapi = "3.0.3";

	private String host;

	private String basePath;

	private Info info;

	private List<Map<String, Object>> servers = new ArrayList<>();

	private final Map<String, Object> components = new HashMap<>();

	private final Map<String, PathItem> paths = new LinkedHashMap<>();

	private final Map<String, Object> securitySchemes = new HashMap<>();

	private final List<Map<String, List<String>>> security = new ArrayList<>();

	/**
	 * 标签列表（根级别）
	 */
	private List<Tag> tags = new ArrayList<>();

	/**
	 * 标签定义
	 */
	public static class Tag {
		private String name;
		private String description;
		private ExternalDocs externalDocs;

		public Tag(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public Tag(String name, String description, ExternalDocs externalDocs) {
			this.name = name;
			this.description = description;
			this.externalDocs = externalDocs;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public ExternalDocs getExternalDocs() {
			return externalDocs;
		}

		public void setExternalDocs(ExternalDocs externalDocs) {
			this.externalDocs = externalDocs;
		}
	}

	/**
	 * 外部文档
	 */
	public static class ExternalDocs {
		private String description;
		private String url;

		public ExternalDocs(String url) {
			this.url = url;
		}

		public ExternalDocs(String description, String url) {
			this.description = description;
			this.url = url;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	/**
	 * 添加服务器
	 */
	public void addServer(String url, String description) {
		Map<String, Object> server = new HashMap<>(2);
		server.put("url", url);
		server.put("description", description);
		servers.add(server);
	}

	/**
	 * 添加路径
	 */
	public void addPath(String path, String method, Operation operation) {
		PathItem existingPath = paths.get(path);
		if (existingPath == null) {
			existingPath = new PathItem();
			paths.put(path, existingPath);
		}
		existingPath.setOperation(method.toLowerCase(), operation);
	}

	/**
	 * 添加标签（根级别）
	 */
	public void addTag(String name, String description) {
		// 检查是否已存在同名标签
		boolean exists = tags.stream().anyMatch(t -> t.getName().equals(name));
		if (!exists) {
			tags.add(new Tag(name, description));
		}
	}

	/**
	 * 添加标签（带外部文档）
	 */
	public void addTag(String name, String description, ExternalDocs externalDocs) {
		boolean exists = tags.stream().anyMatch(t -> t.getName().equals(name));
		if (!exists) {
			tags.add(new Tag(name, description, externalDocs));
		}
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * 添加组件定义
	 */
	public void addSchema(String name, Object schema) {
		components.computeIfAbsent("schemas", k -> new HashMap<>());
		((Map<String, Object>) components.get("schemas")).put(name, schema);
	}

	/**
	 * 添加安全方案
	 */
	public void addSecurityScheme(String name, Object scheme) {
		securitySchemes.put(name, scheme);
		if (!components.containsKey("securitySchemes")) {
			components.put("securitySchemes", securitySchemes);
		}
	}

	/**
	 * 添加安全要求
	 */
	public void addSecurity(String name) {
		Map<String, List<String>> sec = new HashMap<>(1);
		sec.put(name, new ArrayList<>());
		security.add(sec);
	}

	public String getOpenapi() {
		return openapi;
	}

	public void setOpenapi(String openapi) {
		this.openapi = openapi;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public List<Map<String, Object>> getServers() {
		return servers;
	}

	public void setServers(List<Map<String, Object>> servers) {
		this.servers = servers;
	}

	public Map<String, Object> getComponents() {
		return components;
	}

	public Map<String, PathItem> getPaths() {
		return paths;
	}

	public Map<String, Object> getSecuritySchemes() {
		return securitySchemes;
	}

	public List<Map<String, List<String>>> getSecurity() {
		return security;
	}

	/**
	 * OpenAPI 3.0 信息
	 */
	public static class Info {

		private String title;

		private String description;

		private String version;

		private Contact contact;

		private License license;

		public Info(String title, String description, String version) {
			this.title = title;
			this.description = description;
			this.version = version;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public Contact getContact() {
			return contact;
		}

		public void setContact(Contact contact) {
			this.contact = contact;
		}

		public License getLicense() {
			return license;
		}

		public void setLicense(License license) {
			this.license = license;
		}
	}

	/**
	 * 联系人信息
	 */
	public static class Contact {

		private String name;

		private String url;

		private String email;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	/**
	 * 许可证
	 */
	public static class License {

		private String name;

		private String url;

		public License(String name, String url) {
			this.name = name;
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	/**
	 * 路径项
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class PathItem {

		private String summary;

		private String description;

		private String operationId;

		private List<String> tags = new ArrayList<>();

		private List<Map<String, Object>> parameters = new ArrayList<>();

		private Operation get;

		private Operation post;

		private Operation put;

		private Operation delete;

		private Operation patch;

		public PathItem() {
		}

		public void setOperation(String method, Operation operation) {
			switch (method.toLowerCase()) {
				case "get":
					this.get = operation;
					break;
				case "post":
					this.post = operation;
					break;
				case "put":
					this.put = operation;
					break;
				case "delete":
					this.delete = operation;
					break;
				case "patch":
					this.patch = operation;
					break;
			}
		}

		public Operation getGet() {
			return get;
		}

		public void setGet(Operation get) {
			this.get = get;
		}

		public Operation getPost() {
			return post;
		}

		public void setPost(Operation post) {
			this.post = post;
		}

		public Operation getPut() {
			return put;
		}

		public void setPut(Operation put) {
			this.put = put;
		}

		public Operation getDelete() {
			return delete;
		}

		public void setDelete(Operation delete) {
			this.delete = delete;
		}

		public Operation getPatch() {
			return patch;
		}

		public void setPatch(Operation patch) {
			this.patch = patch;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getOperationId() {
			return operationId;
		}

		public void setOperationId(String operationId) {
			this.operationId = operationId;
		}

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}

		public void addTag(String tag) {
			this.tags.add(tag);
		}

		public List<Map<String, Object>> getParameters() {
			return parameters;
		}

		public void setParameters(List<Map<String, Object>> parameters) {
			this.parameters = parameters;
		}

		public void addParameter(Map<String, Object> parameter) {
			this.parameters.add(parameter);
		}
	}

	/**
	 * 操作
	 */
	public static class Operation {

		private List<String> tags = new ArrayList<>();

		private String summary;

		private String description;

		private String operationId;

		private List<Map<String, Object>> parameters = new ArrayList<>();

		private RequestBody requestBody;

		private Map<String, Response> responses = new LinkedHashMap<>();

		private List<Map<String, List<String>>> security = new ArrayList<>();

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}

		public void addTag(String tag) {
			this.tags.add(tag);
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getOperationId() {
			return operationId;
		}

		public void setOperationId(String operationId) {
			this.operationId = operationId;
		}

		public List<Map<String, Object>> getParameters() {
			return parameters;
		}

		public void setParameters(List<Map<String, Object>> parameters) {
			this.parameters = parameters;
		}

		public void addParameter(Map<String, Object> parameter) {
			this.parameters.add(parameter);
		}

		public RequestBody getRequestBody() {
			return requestBody;
		}

		public void setRequestBody(RequestBody requestBody) {
			this.requestBody = requestBody;
		}

		public Map<String, Response> getResponses() {
			return responses;
		}

		public void setResponses(Map<String, Response> responses) {
			this.responses = responses;
		}

		public void addResponse(String status, Response response) {
			this.responses.put(status, response);
		}

		public List<Map<String, List<String>>> getSecurity() {
			return security;
		}

		public void setSecurity(List<Map<String, List<String>>> security) {
			this.security = security;
		}

		public void addSecurity(String name) {
			Map<String, List<String>> sec = new HashMap<>(1);
			sec.put(name, new ArrayList<>());
			security.add(sec);
		}
	}

	/**
	 * 请求体
	 */
	public static class RequestBody {

		private String description;

		private Map<String, MediaType> content = new HashMap<>();

		private boolean required = false;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Map<String, MediaType> getContent() {
			return content;
		}

		public void setContent(Map<String, MediaType> content) {
			this.content = content;
		}

		public void addMediaType(String mediaType, MediaType mediaTypeObject) {
			this.content.put(mediaType, mediaTypeObject);
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}
	}

	/**
	 * 媒体类型
	 */
	public static class MediaType {

		private Object schema;

		private Object example;

		public Object getSchema() {
			return schema;
		}

		public void setSchema(Object schema) {
			this.schema = schema;
		}

		public Object getExample() {
			return example;
		}

		public void setExample(Object example) {
			this.example = example;
		}
	}

	/**
	 * 响应
	 */
	public static class Response {

		private String description;

		private Map<String, MediaType> content = new HashMap<>();

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Map<String, MediaType> getContent() {
			return content;
		}

		public void setContent(Map<String, MediaType> content) {
			this.content = content;
		}

		public void addMediaType(String mediaType, MediaType mediaTypeObject) {
			this.content.put(mediaType, mediaTypeObject);
		}
	}

	/**
	 * 参数
	 */
	public static Map<String, Object> createParameter(boolean required, String name, String in, String type, String description, Object example) {
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("name", name);
		parameter.put("in", in);
		parameter.put("required", required);
		parameter.put("description", description);

		Map<String, Object> schema = new HashMap<>();
		schema.put("type", type);
		if (example != null) {
			schema.put("example", example);
		}
		parameter.put("schema", schema);

		return parameter;
	}

	/**
	 * 创建 Body 参数
	 */
	public static Map<String, Object> createBodyParameter(boolean required, String description, Object schema) {
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("required", required);
		parameter.put("description", description);
		parameter.put("content", schema);
		return parameter;
	}

	/**
	 * 引用 Schema
	 */
	public static Map<String, String> createRefSchema(String ref) {
		Map<String, String> schema = new HashMap<>(1);
		schema.put("$ref", "#/components/schemas/" + ref);
		return schema;
	}

	/**
	 * 获取 JavaScript 类型
	 */
	public static String getJavaScriptType(Object object) {
		if (object instanceof Number) {
			if (object instanceof Integer || object instanceof Long) {
				return "integer";
			}
			return "number";
		}
		if (object instanceof String) {
			return "string";
		}
		if (object instanceof Boolean) {
			return "boolean";
		}
		if (object instanceof List) {
			return "array";
		}
		if (object instanceof Map) {
			return "object";
		}
		return "string";
	}
}
