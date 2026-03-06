package org.ssssssss.magicapi.springdoc;

import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.ssssssss.magicapi.core.config.MagicAPIProperties;
import org.ssssssss.magicapi.core.config.MagicPluginConfiguration;
import org.ssssssss.magicapi.core.model.Plugin;
import org.ssssssss.magicapi.core.service.MagicResourceService;
import org.ssssssss.magicapi.core.service.impl.RequestMagicDynamicRegistry;
import org.ssssssss.magicapi.springdoc.entity.OpenApiEntity;
import org.ssssssss.magicapi.springdoc.entity.OpenApiProvider;
import org.ssssssss.magicapi.utils.Mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@EnableConfigurationProperties(SpringDocConfig.class)
@ConditionalOnProperty(
		name = {"springdoc.api-docs.enabled"},
		matchIfMissing = true
)
public class MagicSpringDocConfiguration implements MagicPluginConfiguration {

	private final MagicAPIProperties properties;
	private final SpringDocConfig springDocConfig;
	@Autowired
	@Lazy
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	private final ObjectProvider<RequestMagicDynamicRegistry> requestMagicDynamicRegistryObjectProvider;
	private final MagicResourceService magicResourceService;
	private final ServletContext servletContext;

	private final AtomicBoolean createdMapping = new AtomicBoolean(false);

	private static final Logger logger = LoggerFactory.getLogger(MagicSpringDocConfiguration.class);

	public MagicSpringDocConfiguration(MagicAPIProperties properties, SpringDocConfig springDocConfig,
									   ObjectProvider<RequestMagicDynamicRegistry> requestMagicDynamicRegistryObjectProvider,
									   MagicResourceService magicResourceService, ServletContext servletContext) {
		this.properties = properties;
		this.springDocConfig = springDocConfig;
		this.requestMagicDynamicRegistryObjectProvider = requestMagicDynamicRegistryObjectProvider;
		this.magicResourceService = magicResourceService;
		this.servletContext = servletContext;
	}

	@Override
	public Plugin plugin() {
		return new Plugin("SpringDoc");
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(SwaggerUiConfigParameters.class)
	public SwaggerUiConfigProperties magicSwaggerUiConfigProperties(SwaggerUiConfigProperties swaggerUiConfigProperties,
																	SpringDocConfigProperties springDocConfigProperties) {
		Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = swaggerUiConfigProperties.getUrls();
		if (urls == null) {
			urls = new HashSet<>();
			AbstractSwaggerUiConfigProperties.SwaggerUrl url = new AbstractSwaggerUiConfigProperties.SwaggerUrl(
					"default", springDocConfigProperties.getApiDocs().getPath(), null);
			urls.add(url);
		}
		urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(springDocConfig.getGroupName(),
				servletContext.getContextPath() + springDocConfig.getLocation(), null) {
			@Override
			public String getUrl() {
				try {
					if (createdMapping.compareAndSet(false, true)) {
						createOpenApiProvider();
					}
				} catch (Exception e) {
					logger.error("注册 SpringDoc 接口失败", e);
				}
				return super.getUrl();
			}
		});
		swaggerUiConfigProperties.setUrls(urls);
		return swaggerUiConfigProperties;
	}

	@Bean
	@Primary
	@Lazy
	public SwaggerUiConfigParameters magicSwaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
		return new SwaggerUiConfigParameters(swaggerUiConfigProperties) {
			@Override
			public Map<String, Object> getConfigParameters() {
				Map<String, Object> params = super.getConfigParameters();
				if (createdMapping.compareAndSet(false, true)) {
					try {
						createOpenApiProvider();
					} catch (NoSuchMethodException e) {
						logger.error("注册 SpringDoc 接口失败", e);
						return params;
					}
				}
				Set<SwaggerUrl> urls = (Set<SwaggerUrl>) params.get("urls");
				if (urls == null) {
					urls = new HashSet<>();
					SwaggerUrl url = new SwaggerUrl("default", (String) params.remove("url"), null);
					urls.add(url);
				} else {
					urls = new HashSet<>(urls);
				}
				urls.add(new SwaggerUrl(springDocConfig.getGroupName(),
						servletContext.getContextPath() + springDocConfig.getLocation(), null));
				params.put("urls", urls);
				return params;
			}
		};
	}

	private void createOpenApiProvider() throws NoSuchMethodException {
		Mapping mapping = Mapping.create(requestMappingHandlerMapping);
		RequestMappingInfo requestMappingInfo = mapping.paths(springDocConfig.getLocation()).build();

		// 构建文档信息
		OpenApiEntity.Info info = new OpenApiEntity.Info(
				springDocConfig.getTitle(),
				springDocConfig.getDescription(),
				springDocConfig.getVersion()
		);

		// 设置联系人
		SpringDocConfig.Contact contactConfig = springDocConfig.getContact();
		if (contactConfig != null) {
			OpenApiEntity.Contact contact = new OpenApiEntity.Contact();
			contact.setName(contactConfig.getName());
			contact.setUrl(contactConfig.getUrl());
			contact.setEmail(contactConfig.getEmail());
			info.setContact(contact);
		}

		// 设置许可证
		if (springDocConfig.getLicense() != null) {
			SpringDocConfig.License licenseConfig = springDocConfig.getLicense();
			info.setLicense(new OpenApiEntity.License(licenseConfig.getName(), licenseConfig.getUrl()));
		}

		// 构建安全方案
		Map<String, Object> securitySchemesMap = new HashMap<>();
		Set<String> securityNames = new HashSet<>();

		// Basic Auth
		if (springDocConfig.getBasicAuth() != null) {
			SpringDocConfig.SecurityScheme basicAuth = springDocConfig.getBasicAuth();
			Map<String, Object> scheme = new HashMap<>();
			scheme.put("type", "http");
			scheme.put("scheme", "basic");
			if (basicAuth.getDescription() != null) {
				scheme.put("description", basicAuth.getDescription());
			}
			securitySchemesMap.put("BasicAuth", scheme);
			securityNames.add("BasicAuth");
		}

		// API Key Auth
		if (springDocConfig.getApiKeyAuth() != null) {
			SpringDocConfig.SecurityScheme apiKeyAuth = springDocConfig.getApiKeyAuth();
			Map<String, Object> scheme = new HashMap<>();
			scheme.put("type", "apiKey");
			scheme.put("in", apiKeyAuth.getIn() != null ? apiKeyAuth.getIn() : "header");
			scheme.put("name", apiKeyAuth.getName() != null ? apiKeyAuth.getName() : "X-API-Key");
			if (apiKeyAuth.getDescription() != null) {
				scheme.put("description", apiKeyAuth.getDescription());
			}
			securitySchemesMap.put("ApiKeyAuth", scheme);
			securityNames.add("ApiKeyAuth");
		}

		// OAuth2
		if (springDocConfig.getOauth2() != null) {
			SpringDocConfig.OAuth2 oauth2Config = springDocConfig.getOauth2();
			Map<String, Object> scheme = new HashMap<>();
			scheme.put("type", "oauth2");
			Map<String, Object> flows = new HashMap<>();

			if (oauth2Config.getFlows() != null) {
				for (Map.Entry<String, SpringDocConfig.OAuth2.OAuth2Flow> entry : oauth2Config.getFlows().entrySet()) {
					Map<String, Object> flow = new HashMap<>();
					SpringDocConfig.OAuth2.OAuth2Flow flowConfig = entry.getValue();
					if (flowConfig.getAuthorizationUrl() != null) {
						flow.put("authorizationUrl", flowConfig.getAuthorizationUrl());
					}
					if (flowConfig.getTokenUrl() != null) {
						flow.put("tokenUrl", flowConfig.getTokenUrl());
					}
					if (flowConfig.getScopes() != null) {
						flow.put("scopes", flowConfig.getScopes());
					}
					flows.put(entry.getKey(), flow);
				}
			}
			scheme.put("flows", flows);
			securitySchemesMap.put("OAuth2", scheme);
			securityNames.add("OAuth2");
		}

		// 默认添加 Token 认证（适配 Sa-Token）
		Map<String, Object> tokenScheme = new HashMap<>();
		tokenScheme.put("type", "apiKey");
		tokenScheme.put("in", "header");
		tokenScheme.put("name", "token");
		tokenScheme.put("description", "Sa-Token 认证");
		securitySchemesMap.put("TokenAuth", tokenScheme);
		securityNames.add("TokenAuth");

		// 构建 OpenAPI Provider
		OpenApiProvider openApiProvider = new OpenApiProvider(
				requestMagicDynamicRegistryObjectProvider.getObject(),
				magicResourceService,
				servletContext.getContextPath(),
				info,
				properties.isPersistenceResponseBody(),
				properties.getPrefix(),
				securitySchemesMap,
				new java.util.ArrayList<>(securityNames)
		);

		// 注册 OpenAPI JSON 端点
		mapping.register(requestMappingInfo, openApiProvider, OpenApiProvider.class.getDeclaredMethod("openApiJson"));
	}
}
