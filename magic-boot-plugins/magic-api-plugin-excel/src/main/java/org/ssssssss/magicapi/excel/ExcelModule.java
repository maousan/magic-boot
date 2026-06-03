package org.ssssssss.magicapi.excel;

import com.alibaba.excel.EasyExcel;
import org.slf4j.Logger;
import org.ssssssss.magicapi.core.annotation.MagicModule;
import org.ssssssss.magicapi.core.context.RequestContext;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;
import org.ssssssss.magicapi.excel.handler.CustomColumnWidthStyleStrategy;
import org.ssssssss.magicapi.excel.model.ExcelExportConfig;
import org.ssssssss.script.annotation.Comment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Excel 导出模块
 * 支持 List<Map>、List<Entity>、单个对象导出
 * 支持自定义表头、动态文件名、模板填充等功能
 *
 * @author magic-api-plugin-excel
 */
@MagicModule("excel")
public class ExcelModule {

    private static final String CLASSPATH_PREFIX = "classpath:";
    private String templateBasePath = "excel-templates/";
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ExcelModule.class);

    public void setTemplateBasePath(String templateBasePath) {
        this.templateBasePath = templateBasePath;
    }

    // ==================== 基础导出方法（List<Map>） ====================

    @Comment("将 List<Map> 数据导出为 Excel 字节数组（自动表头）")
    public byte[] write(@Comment(name = "data", value = "数据列表，List<Map>类型") List<Map<String, Object>> data) {
        return write(data, (ExcelExportConfig) null);
    }

    @Comment("将 List<Map> 数据导出为 Excel，支持自定义表头（Map 方式：字段名->列名）")
    public byte[] write(@Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                        @Comment(name = "headers", value = "表头映射 Map：字段名->列名") Map<String, String> headers) {
        ExcelExportConfig config = new ExcelExportConfig();
        config.setHeaderMap(headers);
        return write(data, config);
    }

    @Comment("将 List<Map> 数据导出为 Excel，支持列顺序和表头映射")
    public byte[] write(@Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                        @Comment(name = "columnOrder", value = "列顺序列表") List<String> columnOrder,
                        @Comment(name = "headers", value = "表头映射 Map：字段名->列名") Map<String, String> headers) {
        ExcelExportConfig config = new ExcelExportConfig();
        config.setColumnOrder(columnOrder);
        config.setHeaderMap(headers);
        return write(data, config);
    }

    @Comment("将 List<Map> 数据导出为 Excel（使用二维数组配置表头：[[字段，列名], ...]）")
    public byte[] write(@Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                        @Comment(name = "headers", value = "二维数组表头：[[字段名，列名], ...]") List<List<String>> headers) {
        ExcelExportConfig config = new ExcelExportConfig();
        Map<String, String> headerMap = new LinkedHashMap<>();
        List<String> columnOrder = new ArrayList<>();
        for (List<String> header : headers) {
            if (header.size() >= 2) {
                headerMap.put(header.get(0), header.get(1));
                columnOrder.add(header.get(0));
            }
        }
        config.setColumnOrder(columnOrder);
        config.setHeaderMap(headerMap);
        return write(data, config);
    }

    @Comment("将 List<Map> 数据导出为 Excel（完整配置）")
    public byte[] write(@Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                        @Comment(name = "config", value = "导出配置对象") ExcelExportConfig config) {
        if (data == null || data.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 获取表头（始终返回非空列表）
            List<List<String>> head = createHead(config, data.get(0));
            String sheetName = config != null && config.getSheetName() != null ? config.getSheetName() : "Sheet1";

            // 将 List<Map> 转换为 List<List<Object>> 格式
            List<List<Object>> dataToWrite = processData(data, config);

            // 使用 EasyExcel 直接写入数据
            EasyExcel.write(out)
                    .head(head)
                    .sheet(0, sheetName)
                    .registerWriteHandler(createColumnWidthStrategy(config))
                    .doWrite(dataToWrite);

            logger.info("Excel 导出完成，数据条数：{}", dataToWrite.size());

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel 导出失败：" + e.getMessage(), e);
        }
    }

    // ==================== 对象列表导出 ====================

    @Comment("将 List<Entity> 对象列表导出为 Excel 字节数组（自动表头）")
    public byte[] writeEntities(@Comment(name = "data", value = "实体对象列表") List<?> data) {
        return writeEntities(data, (ExcelExportConfig) null);
    }

    @Comment("将 List<Entity> 对象列表导出为 Excel，支持自定义表头")
    public byte[] writeEntities(@Comment(name = "data", value = "实体对象列表") List<?> data,
                                @Comment(name = "headers", value = "表头映射 Map：字段名->列名") Map<String, String> headers) {
        ExcelExportConfig config = new ExcelExportConfig();
        config.setHeaderMap(headers);
        return writeEntities(data, config);
    }

    @Comment("将 List<Entity> 对象列表导出为 Excel（完整配置）")
    public byte[] writeEntities(@Comment(name = "data", value = "实体对象列表") List<?> data,
                                @Comment(name = "config", value = "导出配置对象") ExcelExportConfig config) {
        if (data == null || data.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String sheetName = config != null && config.getSheetName() != null ? config.getSheetName() : "Sheet1";

            EasyExcel.write(out, data.get(0).getClass())
                    .sheet(0, sheetName)
                    .registerWriteHandler(createColumnWidthStrategy(config))
                    .doWrite(data);

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel 导出失败：" + e.getMessage(), e);
        }
    }

    // ==================== 单个对象导出 ====================

    @Comment("将单个对象导出为 Excel（单行数据）")
    public byte[] writeObject(@Comment(name = "obj", value = "单个对象") Object obj) {
        return writeObject(obj, (ExcelExportConfig) null);
    }

    @Comment("将单个对象导出为 Excel，支持自定义表头")
    public byte[] writeObject(@Comment(name = "obj", value = "单个对象") Object obj,
                              @Comment(name = "headers", value = "表头映射 Map：字段名->列名") Map<String, String> headers) {
        ExcelExportConfig config = new ExcelExportConfig();
        config.setHeaderMap(headers);
        return writeObject(obj, config);
    }

    @Comment("将单个对象导出为 Excel（完整配置）")
    public byte[] writeObject(@Comment(name = "obj", value = "单个对象") Object obj,
                              @Comment(name = "config", value = "导出配置对象") ExcelExportConfig config) {
        if (obj == null) {
            return new byte[0];
        }

        List<Object> data = Collections.singletonList(obj);
        return writeEntities(data, config);
    }

    // ==================== 模板填充导出 ====================

    @Comment("使用模板填充导出 Excel（List 数据）")
    public byte[] writeWithTemplate(@Comment(name = "templateName", value = "模板文件名（不含路径）") String templateName,
                                    @Comment(name = "data", value = "数据列表") List<Map<String, Object>> data) {
        return writeWithTemplate(templateName, data, null);
    }

    @Comment("使用模板填充导出 Excel（List 数据，带配置）")
    public byte[] writeWithTemplate(@Comment(name = "templateName", value = "模板文件名（不含路径）") String templateName,
                                    @Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                                    @Comment(name = "config", value = "导出配置对象") ExcelExportConfig config) {
        String templatePath = getTemplatePath(templateName);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templatePath);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if (inputStream == null) {
                throw new RuntimeException("模板文件不存在：" + templatePath);
            }

            EasyExcel.write(out)
                    .withTemplate(inputStream)
                    .sheet()
                    .doWrite(data);

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel 模板导出失败：" + e.getMessage(), e);
        }
    }

    @Comment("使用模板填充导出 Excel（单个对象填充）")
    public byte[] fillTemplate(@Comment(name = "templateName", value = "模板文件名（不含路径）") String templateName,
                               @Comment(name = "data", value = "填充数据（Map 或对象）") Object data) {
        return fillTemplate(templateName, data, null);
    }

    @Comment("使用模板填充导出 Excel（单个对象填充，带配置）")
    public byte[] fillTemplate(@Comment(name = "templateName", value = "模板文件名（不含路径）") String templateName,
                               @Comment(name = "data", value = "填充数据（Map 或对象）") Object data,
                               @Comment(name = "config", value = "导出配置对象") ExcelExportConfig config) {
        String templatePath = getTemplatePath(templateName);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templatePath);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if (inputStream == null) {
                throw new RuntimeException("模板文件不存在：" + templatePath);
            }

            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("data", data);
            EasyExcel.write(out)
                    .withTemplate(inputStream)
                    .sheet()
                    .doWrite(Collections.singletonList(wrapper));

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel 模板填充失败：" + e.getMessage(), e);
        }
    }

    // ==================== 响应式下载方法 ====================

    @Comment("导出 Excel 并自动设置响应头下载（List<Map> 数据）")
    public void download(@Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                         @Comment(name = "fileName", value = "文件名（不含扩展名）") String fileName) {
        download(data, fileName, null);
    }

    @Comment("导出 Excel 并自动设置响应头下载（List<Map> 数据，带表头）")
    public void download(@Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                         @Comment(name = "fileName", value = "文件名（不含扩展名）") String fileName,
                         @Comment(name = "headers", value = "表头映射 Map：字段名->列名") Map<String, String> headers) {
        ExcelExportConfig config = new ExcelExportConfig(fileName);
        config.setHeaderMap(headers);
        download(data, config);
    }

    @Comment("导出 Excel 并自动设置响应头下载（完整配置）")
    public void download(@Comment(name = "data", value = "数据列表") List<Map<String, Object>> data,
                         @Comment(name = "config", value = "导出配置对象") ExcelExportConfig config) {
        MagicHttpServletResponse response = getResponse();
        if (response == null) {
            throw new RuntimeException("无法获取 HttpServletResponse，请确保在 Web 请求环境中调用");
        }

        String fileName = config.getFileName();
        if (fileName == null || fileName.isEmpty()) {
            fileName = "export_" + System.currentTimeMillis();
        }

        try {
            setDownloadHeaders(response, fileName);
            byte[] excelBytes = write(data, config);
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("Excel 下载失败：" + e.getMessage(), e);
        }
    }

    @Comment("导出 Excel 并自动设置响应头下载（List<Entity> 数据）")
    public void downloadEntities(@Comment(name = "data", value = "实体对象列表") List<?> data,
                                 @Comment(name = "fileName", value = "文件名（不含扩展名）") String fileName) {
        downloadEntities(data, fileName, null);
    }

    @Comment("导出 Excel 并自动设置响应头下载（List<Entity> 数据，带表头）")
    public void downloadEntities(@Comment(name = "data", value = "实体对象列表") List<?> data,
                                 @Comment(name = "fileName", value = "文件名（不含扩展名）") String fileName,
                                 @Comment(name = "headers", value = "表头映射 Map：字段名->列名") Map<String, String> headers) {
        MagicHttpServletResponse response = getResponse();
        if (response == null) {
            throw new RuntimeException("无法获取 HttpServletResponse，请确保在 Web 请求环境中调用");
        }

        if (fileName == null || fileName.isEmpty()) {
            fileName = "export_" + System.currentTimeMillis();
        }

        try {
            setDownloadHeaders(response, fileName);
            byte[] excelBytes = writeEntities(data, new ExcelExportConfig(fileName));
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("Excel 下载失败：" + e.getMessage(), e);
        }
    }

    @Comment("使用模板填充并下载 Excel")
    public void downloadWithTemplate(@Comment(name = "templateName", value = "模板文件名（不含路径）") String templateName,
                                     @Comment(name = "data", value = "填充数据") Object data,
                                     @Comment(name = "fileName", value = "文件名（不含扩展名）") String fileName) {
        MagicHttpServletResponse response = getResponse();
        if (response == null) {
            throw new RuntimeException("无法获取 HttpServletResponse，请确保在 Web 请求环境中调用");
        }

        if (fileName == null || fileName.isEmpty()) {
            fileName = "export_" + System.currentTimeMillis();
        }

        try {
            setDownloadHeaders(response, fileName);
            byte[] excelBytes;
            if (data instanceof List) {
                excelBytes = writeWithTemplate(templateName, (List<Map<String, Object>>) data, null);
            } else {
                excelBytes = fillTemplate(templateName, data, null);
            }
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("Excel 模板下载失败：" + e.getMessage(), e);
        }
    }

    // ==================== 便捷方法 ====================

    @Comment("生成带时间戳的文件名")
    public String generateFileName(@Comment(name = "prefix", value = "文件名前缀") String prefix) {
        return prefix + "_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
    }

    @Comment("创建导出配置（流式构建）")
    public ExcelExportConfig createConfig(@Comment(name = "fileName", value = "文件名") String fileName) {
        return new ExcelExportConfig(fileName);
    }

    // ==================== 内部辅助方法 ====================

    /**
     * 创建表头
     * EasyExcel 表头格式：[[列名1], [列名2], ...]
     */
    private List<List<String>> createHead(ExcelExportConfig config, Map<String, Object> sampleData) {
        List<List<String>> head = new ArrayList<>();
        List<String> keys = new ArrayList<>();  // 保存列顺序

        // 表头配置
        if (config != null && config.getHeaderMap() != null && !config.getHeaderMap().isEmpty()) {
            List<String> columnOrder = config.getColumnOrder();

            if (columnOrder != null && !columnOrder.isEmpty()) {
                // 按指定顺序
                for (String key : columnOrder) {
                    String columnName = config.getHeaderMap().getOrDefault(key, key);
                    head.add(Collections.singletonList(columnName));
                    keys.add(key);
                }
            } else {
                // 按 Map 顺序
                for (Map.Entry<String, String> entry : config.getHeaderMap().entrySet()) {
                    head.add(Collections.singletonList(entry.getValue()));
                    keys.add(entry.getKey());
                }
            }
        } else if (sampleData != null && !sampleData.isEmpty()) {
            // 没有配置表头时，自动从数据样本中推断表头（自动表头）
            for (String key : sampleData.keySet()) {
                head.add(Collections.singletonList(key));
                keys.add(key);
            }
        }

        // 保存列顺序到 config 中，用于后续数据处理
        if (config != null && config.getColumnOrder() == null) {
            config.setColumnOrder(keys);
        }

        return head;
    }

    /**
     * 处理数据（将 List<Map> 转换为 List<List<Object>>）
     * EasyExcel 需要 List<List<Object>> 格式的数据
     */
    private List<List<Object>> processData(List<Map<String, Object>> data, ExcelExportConfig config) {
        List<List<Object>> result = new ArrayList<>();
        List<String> columnOrder = config != null ? config.getColumnOrder() : null;

        // 如果没有列顺序，从第一条数据获取
        if (columnOrder == null || columnOrder.isEmpty()) {
            if (!data.isEmpty()) {
                columnOrder = new ArrayList<>(data.get(0).keySet());
            } else {
                return result;
            }
        }

        for (Map<String, Object> row : data) {
            List<Object> rowData = new ArrayList<>();
            for (String key : columnOrder) {
                rowData.add(row.get(key));
            }
            result.add(rowData);
        }
        return result;
    }

    /**
     * 获取 HttpServletResponse
     */
    private MagicHttpServletResponse getResponse() {
        return RequestContext.getHttpServletResponse();
    }

    /**
     * 设置下载响应头
     */
    private void setDownloadHeaders(MagicHttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + encodedFileName + ".xlsx");
        } catch (Exception e) {
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        }
    }

    /**
     * 创建列宽策略
     */
    private CustomColumnWidthStyleStrategy createColumnWidthStrategy(ExcelExportConfig config) {
        if (config == null) {
            return new CustomColumnWidthStyleStrategy();
        }
        return new CustomColumnWidthStyleStrategy(config.getMinWidth(), config.getMaxWidth());
    }

    /**
     * 获取模板文件路径
     */
    private String getTemplatePath(String templateName) {
        if (templateName.startsWith(CLASSPATH_PREFIX)) {
            return templateName.substring(CLASSPATH_PREFIX.length());
        }
        return templateBasePath + templateName;
    }
}
