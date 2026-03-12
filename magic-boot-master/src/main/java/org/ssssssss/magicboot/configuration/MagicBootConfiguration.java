package org.ssssssss.magicboot.configuration;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.ssssssss.magicapi.core.config.MagicAPIProperties;
import org.ssssssss.magicapi.core.config.MagicPluginConfiguration;
import org.ssssssss.magicapi.core.logging.LoggerManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@AutoConfigureAfter(MagicPluginConfiguration.class)
public class MagicBootConfiguration implements WebMvcConfigurer {

    private final MagicAPIProperties properties;

    public MagicBootConfiguration(MagicAPIProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeSerializer);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String web = properties.getWeb();
        registry.addResourceHandler(web + "/idea/**").addResourceLocations("classpath:/magic-idea/");
    }
}
