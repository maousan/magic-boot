package org.ssssssss.magicapi.springdoc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenAPI 3.0 配置
 *
 * @author mxd
 */
@ConfigurationProperties(prefix = "magic-api.springdoc")
public class SpringDocConfig {

	/**
	 * 资源名称
	 */
	private String name = "MagicAPI 接口";

	/**
	 * 资源位置
	 */
	private String location = "/v3/api-docs/magic-api/openapi.json";

	/**
	 * 分组名称
	 */
	private String groupName = "magic-api";

	/**
	 * 文档标题
	 */
	private String title = "MagicAPI OpenAPI Docs";

	/**
	 * 文档描述
	 */
	private String description = "MagicAPI 接口信息";

	/**
	 * 联系人信息
	 */
	@NestedConfigurationProperty
	private Contact contact = new Contact();

	/**
	 * 许可证
	 */
	@NestedConfigurationProperty
	private License license;

	/**
	 * 基本认证
	 */
	@NestedConfigurationProperty
	private SecurityScheme basicAuth;

	/**
	 * API 密钥认证
	 */
	@NestedConfigurationProperty
	private SecurityScheme apiKeyAuth;

	/**
	 * OAuth2 认证
	 */
	@NestedConfigurationProperty
	private OAuth2 oauth2;

	/**
	 * 标签配置（用于自定义分组描述）
	 */
	@NestedConfigurationProperty
	private Map<String, TagConfig> tags = new HashMap<>();

	/**
	 * 文档版本
	 */
	private String version = "1.0";

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public SecurityScheme getBasicAuth() {
		return basicAuth;
	}

	public void setBasicAuth(SecurityScheme basicAuth) {
		this.basicAuth = basicAuth;
	}

	public SecurityScheme getApiKeyAuth() {
		return apiKeyAuth;
	}

	public void setApiKeyAuth(SecurityScheme apiKeyAuth) {
		this.apiKeyAuth = apiKeyAuth;
	}

	public OAuth2 getOauth2() {
		return oauth2;
	}

	public void setOauth2(OAuth2 oauth2) {
		this.oauth2 = oauth2;
	}

	public Map<String, TagConfig> getTags() {
		return tags;
	}

	public void setTags(Map<String, TagConfig> tags) {
		this.tags = tags;
	}

	/**
	 * 标签配置
	 */
	public static class TagConfig {

		/**
		 * 标签描述
		 */
		private String description;

		/**
		 * 外部文档
		 */
		private ExternalDoc externalDocs;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public ExternalDoc getExternalDocs() {
			return externalDocs;
		}

		public void setExternalDocs(ExternalDoc externalDocs) {
			this.externalDocs = externalDocs;
		}
	}

	/**
	 * 外部文档配置
	 */
	public static class ExternalDoc {

		private String description;

		private String url;

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
	 * 联系人
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
	 * 安全方案配置
	 */
	public static class SecurityScheme {

		private String type;

		private String description;

		private String name;

		private String in;

		private String scheme;

		private String bearerFormat;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIn() {
			return in;
		}

		public void setIn(String in) {
			this.in = in;
		}

		public String getScheme() {
			return scheme;
		}

		public void setScheme(String scheme) {
			this.scheme = scheme;
		}

		public String getBearerFormat() {
			return bearerFormat;
		}

		public void setBearerFormat(String bearerFormat) {
			this.bearerFormat = bearerFormat;
		}
	}

	/**
	 * OAuth2 配置
	 */
	public static class OAuth2 {

		private String type = "oauth2";

		private Map<String, OAuth2Flow> flows = new HashMap<>();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Map<String, OAuth2Flow> getFlows() {
			return flows;
		}

		public void setFlows(Map<String, OAuth2Flow> flows) {
			this.flows = flows;
		}

		public static class OAuth2Flow {

			private String authorizationUrl;

			private String tokenUrl;

			private Map<String, String> scopes = new HashMap<>();

			public String getAuthorizationUrl() {
				return authorizationUrl;
			}

			public void setAuthorizationUrl(String authorizationUrl) {
				this.authorizationUrl = authorizationUrl;
			}

			public String getTokenUrl() {
				return tokenUrl;
			}

			public void setTokenUrl(String tokenUrl) {
				this.tokenUrl = tokenUrl;
			}

			public Map<String, String> getScopes() {
				return scopes;
			}

			public void setScopes(Map<String, String> scopes) {
				this.scopes = scopes;
			}
		}
	}

}
