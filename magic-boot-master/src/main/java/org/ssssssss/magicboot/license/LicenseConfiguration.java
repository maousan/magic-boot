package org.ssssssss.magicboot.license;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * License 组件装配：闸门过滤器注册（order 最前，先于一切过滤器与 DispatcherServlet）。
 */
@Configuration
@EnableConfigurationProperties(LicenseProperties.class)
public class LicenseConfiguration {

    @Bean
    public FilterRegistrationBean<LicenseGateFilter> licenseGateFilter(LicenseManager licenseManager,
                                                                       LicenseProperties properties) {
        FilterRegistrationBean<LicenseGateFilter> registration =
                new FilterRegistrationBean<>(new LicenseGateFilter(licenseManager, properties.getPermitPatterns()));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        registration.setName("licenseGateFilter");
        return registration;
    }
}
