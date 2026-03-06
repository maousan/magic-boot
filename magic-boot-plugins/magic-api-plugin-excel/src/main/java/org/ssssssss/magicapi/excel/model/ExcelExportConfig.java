package org.ssssssss.magicapi.excel.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel 导出配置
 *
 * @author magic-api-plugin-excel
 */
public class ExcelExportConfig {

    /**
     * 文件名（不含扩展名）
     */
    private String fileName;

    /**
     * 表头映射：字段名 -> 列名
     */
    private Map<String, String> headerMap;

    /**
     * 列顺序（可选，用于控制输出顺序）
     */
    private List<String> columnOrder;

    /**
     * 列宽配置：字段名 -> 列宽（字符数）
     */
    private Map<String, Integer> columnWidths;

    /**
     * 是否自动列宽
     */
    private boolean autoWidth = true;

    /**
     * 是否包含默认样式（表头加粗、边框等）
     */
    private boolean defaultStyle = true;

    /**
     * 模板路径（用于模板填充）
     */
    private String templatePath;

    /**
     * 使用模板填充还是动态生成
     */
    private boolean useTemplate = false;

    /**
     * Sheet 名称
     */
    private String sheetName = "Sheet1";

    /**
     * 最大行数（超过后自动创建新 Sheet）
     */
    private int maxRowsPerSheet = 10000;

    /**
     * 自动列宽时的最小列宽（字符数）
     */
    private int minWidth = 15;

    /**
     * 自动列宽时的最大列宽（字符数）
     */
    private int maxWidth = 50;

    public ExcelExportConfig() {
        this.headerMap = new LinkedHashMap<>();
        this.columnWidths = new LinkedHashMap<>();
    }

    public ExcelExportConfig(String fileName) {
        this();
        this.fileName = fileName;
    }

    public ExcelExportConfig(String fileName, Map<String, String> headerMap) {
        this();
        this.fileName = fileName;
        this.headerMap = headerMap;
    }

    // Getters and Setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public List<String> getColumnOrder() {
        return columnOrder;
    }

    public void setColumnOrder(List<String> columnOrder) {
        this.columnOrder = columnOrder;
    }

    public Map<String, Integer> getColumnWidths() {
        return columnWidths;
    }

    public void setColumnWidths(Map<String, Integer> columnWidths) {
        this.columnWidths = columnWidths;
    }

    public boolean isAutoWidth() {
        return autoWidth;
    }

    public void setAutoWidth(boolean autoWidth) {
        this.autoWidth = autoWidth;
    }

    public boolean isDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(boolean defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public boolean isUseTemplate() {
        return useTemplate;
    }

    public void setUseTemplate(boolean useTemplate) {
        this.useTemplate = useTemplate;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getMaxRowsPerSheet() {
        return maxRowsPerSheet;
    }

    public void setMaxRowsPerSheet(int maxRowsPerSheet) {
        this.maxRowsPerSheet = maxRowsPerSheet;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }
}
