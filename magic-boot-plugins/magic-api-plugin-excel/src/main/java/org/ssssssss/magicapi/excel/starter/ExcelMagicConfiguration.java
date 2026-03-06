package org.ssssssss.magicapi.excel.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ssssssss.magicapi.core.config.MagicPluginConfiguration;
import org.ssssssss.magicapi.core.model.Plugin;
import org.ssssssss.magicapi.excel.ExcelModule;

/**
 * Excel 插件自动配置
 *
 * @author magic-api-plugin-excel
 */
@Configuration
@ConfigurationProperties(prefix = "magic-api.plugin.excel")
public class ExcelMagicConfiguration implements MagicPluginConfiguration {

    /**
     * 模板基础路径
     */
    private String templateBasePath = "excel-templates/";

    @Override
    public Plugin plugin() {
        return new Plugin("Excel 导出", "excel", null);
    }

    @Bean(name = "magicExcelModule")
    @ConditionalOnMissingBean
    public ExcelModule magicExcelModule() {
        ExcelModule excelModule = new ExcelModule();
        excelModule.setTemplateBasePath(templateBasePath);
        return excelModule;
    }

    public String getTemplateBasePath() {
        return templateBasePath;
    }

    public void setTemplateBasePath(String templateBasePath) {
        this.templateBasePath = templateBasePath;
    }
}
