package org.ssssssss.magicboot.configuration;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.dtflys.forest.converter.json.ForestFastjson2Converter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import org.ssssssss.magicboot.model.Global;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Global.USER_FILES_BASE_URL + "**").addResourceLocations("file:" + Global.getDir() + Global.USER_FILES_BASE_URL);
    }

    // 兼容接口多个"/"，比如//system/xxx
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUrlPathHelper(new UrlPathHelper());
    }

//    @Bean
//    public ForestJsonConverter forestFastjson2Converter() {
//        ForestFastjson2Converter converter = new ForestFastjson2Converter();
//        // 设置日期格式
//        converter.setDateFormat("yyyy-MM-dd HH:mm:ss");
//        // 设置序列化特性
//        converter.addWriterFeature(JSONWriter.Feature.IgnoreNoneSerializable);
//        // 设置反序列化特性
//        converter.addReadFeature(JSONReader.Feature.ErrorOnNoneSerializable);
//        return converter;
//    }

}
